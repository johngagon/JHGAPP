package jhg.deploy;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * A deploy tool that will do backups based on location.
 * This class avoids null and checked exceptions at all costs and will throw only runtime
 * exceptions when fatal problems occur.
 * 
 * Deletions from the deployment must be backed up and performed manually at this time.
 * It is discouraged to do deletions that are not part of jars.  It might be preferable 
 * to program so as to ignore unless there are security breaches in which case, a restore
 * is not desirable so much without much consideration and if so, configuration management
 * will be responsible for restoring the lost file instead of the backup.
 * 
 * If this fails, you should restore your backup and fix the problem.
 * 
 * @author jgagon
 *
 */
public class DeployTool {

	/*
	 * Developer configs
	 */
	private static final boolean DEBUG = true;//I turn this on to true to use the dummied values defaulted in code per my local dev vs commandline.
	
	/*
	 * Unexpected fatal errors.
	 */
	public static final String FILE_IOEXCEPTION = "Unexpected File I/O problem occurred.  Here is the exception message:";
	
	/*
	 * Runtime messages caused by programmer errors.
	 */
	public static final String NULL_PARAMS = "Null parameters provided. These cannot be null.";
	public static final String COLLECTION_NOT_EMPTY = "Collection parameters must be empty.";
	public static final String MAP_NOT_EMPTY = "Map parameters must be empty.";
	
	/*
	 * Error messages caused by bad client values or system states.
	 */
	public static final String DIRECTORY_NOTEXIST = " directory doesn't exist.";
	public static final String DIRECTORY_NOTDIRECTORY = " is not a directory.";
	public static final String BACKUP_DIRECTORY_NOTEMPTY = " - backup directory is not empty.";
	public static final String EMPTY = "";//no error message.
	
	/*
	 * Not copied reasons.
	 */
	public static final String NOTCOPIED_YET = "Copy process not performed yet.";
	public static final String DEST_CANT_READ = "Destination file could not be read. Backup could not be performed.";
	public static final String DEST_CANT_WRITE = "Destination file is read only. Skipping this file for deployment.";
	public static final String BACKUP_DIR_CANT_CREATE = "Backup directory could not be created.";
	public static final String BACKUP_NOT_PRESENT = "Backup file not present.";
	public static final String CANT_CREATE = "Could not create ";
	public static final String FILE_OPEN = "File is open by another process.";
	
	/**
	 * Results from deploySet.
	 * The solution for any errors is to do it manually.
	 * Anything fatal due to bad backup may be more serious but every care is taken
	 * to only restore files properly backed up. 
	 */
	public static enum Result{
		COMPLETE, 
		INCOMPLETE, 
		NO_FILES_FOUND, 
		NOTHING_COPIED, 
		ERROR, 
		FATAL,
		NOTRUN;
	}

	/**
	 * Main entry method.  Right now, the folders should not contain spaces.
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		//System.out.println("Start.");
		boolean deploy = true;
		String backupFolder = "D:\\JG\\testdeploy\\backup";// = "D:\\Backup";
		String releaseFolder = "D:\\JG\\testdeploy\\release";// = "D:\\";
		String destinationFolder = "D:\\JG\\testdeploy\\destination";// = "D:\\";		
		//List<File> filesFoundInBackup = new ArrayList<File>();
		List<File> filesFoundInRelease = new ArrayList<File>();
		Map<File,File> filesNotInDestination = new TreeMap<File,File>();
		Map<File,File> filesInDestination = new TreeMap<File,File>();
		List<File> filesBackedUp = new ArrayList<File>();
		List<File> filesOverwritten = new ArrayList<File>();
		List<File> filesCopiedNotOverwritten = new ArrayList<File>();
		Map<File,String> filesNotCopied = new HashMap<File,String>();
		List<String> errorMessages = new ArrayList<String>();		
		if(!DEBUG){
			if( args.length!=3){
				showSyntax();
				return;
			}
			backupFolder = args[0];
			releaseFolder = args[1];
			destinationFolder = args[2];
			if(backupFolder==null 
				|| backupFolder.indexOf(' ')!=-1
				|| releaseFolder==null 
				|| releaseFolder.indexOf(' ')!=-1
				|| destinationFolder==null 
				|| destinationFolder.indexOf(' ')!=-1				
				){
				showSyntax();
				return;
			}
		}
		if(deploy){
			
			Result result = Result.NOTRUN;
			try {
				result = deploySet(backupFolder, releaseFolder,
						destinationFolder, filesFoundInRelease,
						filesNotInDestination, filesInDestination,
						filesBackedUp, filesOverwritten,
						filesCopiedNotOverwritten, filesNotCopied,
						errorMessages);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			debug("Result:"+result);
			if(errorMessages.size()>0){
				for(String errorMsg:errorMessages){
					debug(errorMsg);
				}
			}
			if(!Result.ERROR.equals(result)){
				debug("");
				debug("");
				debug("Files Found In Release - ",filesFoundInRelease);
				debugFileMap("Files Not In Destination - ",filesNotInDestination);
				debugFileMap("Files In Destination",filesInDestination);
				debug("Files Backed Up - ",filesBackedUp);
				debug("Files Overwritten - ",filesOverwritten);
				debug("Files Copied Without Overwriting - ",filesCopiedNotOverwritten);
				debug("Files Not Copied - ",filesNotCopied);
			}
		}
		System.out.println("Done.");
	}

	/**
	 * Deploys files found in a release folders to destination folders backuping first.
	 * The release folder should mimic the destination it is overlaying on.  
	 * Files found in destination are backed up to backup folder first.
	 * 
	 * Preconditions. No parameter may be null. Lists and maps must be empty for filling.
	 * Folders must exist. Backup folder must be empty.
	 * 
	 * 
	 * 1. Find all files in the release and register filesFoundInRelease
	 * 2. Look in the destination for matches
	 * 2a. Register files not found in filesNotInDestination
	 * 2b. Register files found in filesInDestination
	 * 3. Iterate filesInDestination and copy to backup, creating directories needed; register filesBackedUp
	 * 4. If backed up, overwrite the destination; register filesOverwritten
	 * 5. Iterate filesNotInDestination and copy without allowing overwrite; register filesCopied
	 * 
	 * Post conditions:
	 * 
	 * filesFoundInRelease = filesNotInDestination + filesInDestination
	 * filesFoundInRelease = filesNotCopied + filesOverwritten + filesCopied
	 * filesOverwritten <= filesBackedUp
	 * return value will not be null.
	 * 
	 * @param backupFolder  the path of a backup folder
	 * @param releaseFolder  the path of the folder containing a deployment release.
	 * @param destinationFolder  the path to the target container folder root
	 * @param filesFoundInRelease  a list to hold files discovered by crawling release directory
	 * @param filesNotInDestination  a map to hold the release files:destination sought file not found in destination files.
	 * @param filesInDestination  a map to hold the release files:found destination files that had matches that will be overwritten in destination after backup.
	 * @param filesBackedUp  a list to hold the files backed up (backup point of view, should correspond fairly obviously to destination and release.
	 * @param filesOverwritten  a list to hold files in destination that were overwritten
	 * @param filesCopiedNotOverwritten  a list to hold files that were copied since the destination file didn't have the new file
	 * @param filesNotCopied  a map to hold files:reasons from the release that could not be copied for whatever reason
	 * @param errorMessages a list to hold validation error messages that might arise before attempting deployment
	 * @return a result code that indicates the result of the deployment
	 */
	public static Result deploySet(String backupFolder, 
			String releaseFolder, 
			String destinationFolder, 
			List<File> filesFoundInRelease,
			Map<File,File> filesNotInDestination,
			Map<File,File> filesInDestination,
			List<File> filesBackedUp,
			List<File> filesOverwritten,
			List<File> filesCopiedNotOverwritten,
			Map<File, String> filesNotCopied, 
			List<String> errorMessages
			){
		debug("Starting deployement.");
		
		// Check preconditions
		checkParams(new Object[]{backupFolder,releaseFolder,destinationFolder,filesFoundInRelease,filesNotInDestination, 
				filesInDestination, filesBackedUp, filesOverwritten, filesCopiedNotOverwritten, filesNotCopied, errorMessages });
		checkListSizes(new Collection[]{filesFoundInRelease, filesBackedUp, filesOverwritten, filesCopiedNotOverwritten, errorMessages});
		checkMapSizes(new Map[]{filesInDestination, filesNotInDestination, filesNotCopied});
		
		File backupDirectory = new File(backupFolder);
		File releaseDirectory = new File(releaseFolder);
		File destinationDirectory = new File(destinationFolder);
		String errorMessage = (!backupDirectory.exists())? backupFolder+DIRECTORY_NOTEXIST
			:(!backupDirectory.isDirectory())? backupFolder+DIRECTORY_NOTDIRECTORY
			:(!releaseDirectory.exists())? releaseFolder+DIRECTORY_NOTEXIST
			:(!releaseDirectory.isDirectory())? releaseFolder+DIRECTORY_NOTDIRECTORY
			:(!destinationDirectory.exists())? destinationFolder+DIRECTORY_NOTEXIST
			:(!destinationDirectory.isDirectory())? destinationFolder+DIRECTORY_NOTDIRECTORY					
			:EMPTY;
		if(!errorMessage.isEmpty()){
			errorMessages.add(errorMessage);	
			return Result.ERROR;
		}
		File[] backupContents = backupDirectory.listFiles();
		if(backupContents.length>0){
			errorMessages.add(BACKUP_DIRECTORY_NOTEMPTY);
			return Result.ERROR;
		}
		debug("  Using release files in: "+releaseFolder);
		debug("  Backing up to: "+backupFolder);
		debug("  Deploying to: "+destinationFolder+"\n");
		
		// Initialize
		getFiles(releaseDirectory,filesFoundInRelease);
		if(filesFoundInRelease.size()==0){
			return Result.NO_FILES_FOUND;
		}
		for(File file:filesFoundInRelease){
			filesNotCopied.put(file,"Copy process not performed yet.");
		}
		
		// Determine if files in release are in destination or not.
		for(File file:filesFoundInRelease){
			String fullFileName = file.getPath();
			String seekFileName = fullFileName.replace(releaseFolder, destinationFolder);
			File destinationFile = new File(seekFileName);
			if(destinationFile.exists()){
				filesInDestination.put(file,destinationFile);
			}else{
				filesNotInDestination.put(file,destinationFile);
			}
		}
		
		// Do the backup and overwrite.
		for(File file:filesInDestination.keySet()){
			File destFile = filesInDestination.get(file);
			if(!destFile.canRead()){
				//Destination file could not be read so it cannot be backed up.
				filesNotCopied.put(file,DEST_CANT_READ);
			}else if(!destFile.canWrite()){
				//Destination file is read only so it cannot be overwritten.
				filesNotCopied.put(file,DEST_CANT_WRITE);
			}else{
				String fullFileName = file.getPath();
				String backupFileName = fullFileName.replace(releaseFolder,backupFolder);
				
				File backupFile = new File(backupFileName);
				debug("\nBackup file to be created:"+backupFile.getPath());
				if(backupFile.getParentFile()==null){
					filesNotCopied.put(file,BACKUP_DIR_CANT_CREATE);
				}else{
					try {
						performCopy(filesBackedUp, filesOverwritten,
								filesNotCopied, file, destFile, backupFile);
					} catch (IOException e) {
						errorMessages.add("Could not restore file after failed destination copy.");
						return Result.FATAL;
					}//try
				}//else backup directory available.
			}//else destination is backupable.
		}//for filesInDestination
		
		// Copy files not in destination over.
		for(File file:filesNotInDestination.keySet()){
			File destFile = filesNotInDestination.get(file);
			boolean destcopied = false;
			try {
				destcopied = copy(filesNotCopied,file,file,destFile);
			} catch (IOException e) {
				e.printStackTrace();//should not happen.
			}
			if(destcopied){
				filesCopiedNotOverwritten.add(destFile);
				filesNotCopied.remove(file);
			}
		}
		
		// Post-condition check and determine if complete or not done or not.
		checkPostConditions(filesFoundInRelease,filesNotInDestination,filesInDestination,
				filesBackedUp,filesOverwritten,filesCopiedNotOverwritten,filesNotCopied);
		
		if(filesNotCopied.size()>0){
			if(filesNotCopied.size()==filesFoundInRelease.size()){
				return Result.NOTHING_COPIED;
			}
			return Result.INCOMPLETE;
		}
		debug("Finished deployment. See results below.");
		return Result.COMPLETE;
	}

	private static void performCopy(List<File> filesBackedUp,
			List<File> filesOverwritten, Map<File, String> filesNotCopied,
			File file, File destFile, File backupFile) throws IOException {
		backupFile.getParentFile().mkdirs();
		boolean backupcopied = copy(filesNotCopied, file,
				destFile, backupFile);
		if (!backupcopied) {
			backupFile.delete();
		}
		if (!backupFile.exists()) {
			filesNotCopied.put(file, BACKUP_NOT_PRESENT);
		} else {
			filesBackedUp.add(backupFile);
			//copy and overwrite files found in destination with release file.
			boolean destcopied = copy(filesNotCopied, file,
					file, destFile);
			if (!destcopied) {
				//try restore!
				copy(null, null,backupFile, destFile);//will throw if unsuccessful.
			}else{
				filesOverwritten.add(destFile);
				filesNotCopied.remove(file);
			}
		}
	}

	private static boolean copy(Map<File, String> filesNotCopied, File release, File from,
			File to) throws IOException {
		debug("  Copying file:"+from.getPath()+" \n\t to:"+to.getPath());
		try{
			if(!to.exists()){
				boolean created = to.createNewFile();
				if(!created){
					filesNotCopied.put(release, CANT_CREATE+" "+to.getPath());
				}
			}
			InputStream in = new FileInputStream(from);
			OutputStream out = new FileOutputStream(to);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			} 
			in.close();
			out.close();
			return true;
		}catch(IOException e){
			if(filesNotCopied!=null){
				filesNotCopied.put(release,e.getMessage());
				return false;
			}
			throw e;
		}
	}	

	private static void checkMapSizes(@SuppressWarnings("rawtypes") Map[] maps) {
		for(@SuppressWarnings("rawtypes") Map map:maps){
			if(map.size()!=0)throw new IllegalArgumentException(MAP_NOT_EMPTY);
		}
	}

	private static void checkPostConditions(List<File> filesFoundInRelease,
			Map<File,File> filesNotInDestination, Map<File,File> filesInDestination,
			List<File> filesBackedUp, List<File> filesOverwritten,
			List<File> filesCopied, Map<File, String> filesNotCopied) {
		assertTrue(filesFoundInRelease.size() == filesNotInDestination.size() + filesInDestination.size(),"Release and Destination mismatch"+(filesFoundInRelease.size()+"="+filesNotInDestination.size() +"+"+ filesInDestination.size()));
		assertTrue(filesFoundInRelease.size() == filesNotCopied.size() + filesOverwritten.size() + filesCopied.size(), "Release and copy totals do not match:"+(filesFoundInRelease.size()+"="+filesNotCopied.size() +"+"+ filesOverwritten.size() +"+"+ filesCopied.size()));
		assertTrue(filesOverwritten.size() <= filesBackedUp.size(),"Overwritten greater than backed up:"+(filesOverwritten.size()+" <= "+filesBackedUp.size()));
	}

	private static void assertTrue(boolean b, String string) {
		if(!b){
			if(DEBUG)
				System.out.println(string);
			else
				throw new IllegalStateException(string);
		}
	}

	private static void checkListSizes(@SuppressWarnings("rawtypes") Collection[] collections) {
		for(@SuppressWarnings("rawtypes") Collection c:collections){
			if(c.size()!=0)throw new IllegalArgumentException(COLLECTION_NOT_EMPTY);
		}
	}	
	
	private static void checkParams(Object[] params){
		for(Object o:params){
			if(o==null) throw new IllegalArgumentException(NULL_PARAMS);
		}
	}
	
	private static void debug(String msg) {
		System.out.println(msg);
	}
	
	private static void debug(String title,List<File> files) {
		System.out.println(title + " Count: "+files.size());
		System.out.println("-------------");
		for(File file:files){
			System.out.println("File:"+file.getPath());
		}
		System.out.println("");
	}
	
	private static void debug(String title, Map<File, String> files) {
		System.out.println(title + " Count: "+files.size());
		System.out.println("-------------");
		for(File file:files.keySet()){
			System.out.println("File:"+file.getPath() + " Reason:"+files.get(file));
		}
		System.out.println("");
	}	
	
	private static void debugFileMap(String title,
			Map<File, File> files) {
		System.out.println(title + " Count: "+files.size());
		System.out.println("-------------");
		for(File file:files.keySet()){
			System.out.println("File:"+files.get(file).getPath());
		}
		System.out.println("");
	}	

	private static void getFiles(File releaseDirectory,
			List<File> filesFoundInRelease) {
		
		File[] files = releaseDirectory.listFiles();
		for(File file: files){
			if(file.isDirectory()){
				getFiles(file,filesFoundInRelease);
			}else{
				filesFoundInRelease.add(file);
			}
		}
	}

	private static void showSyntax() {
		System.out.println("Syntax: "//-d|-u 
				+"<backupdir> <releasedir> <destinationdir>  \n\n These currently cannot have whitespace.");
	}	

}
