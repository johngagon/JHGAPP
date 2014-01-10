package jhg;

import java.io.*;
import java.util.*;

public class FileUtil {

	public static final String IMPORT_DIR = "./import/";
	//date,desc,checkno,amt
	public static List<String> read(String _fileName){
		List<String> result = new ArrayList<String>();
		String filename = IMPORT_DIR+_fileName;
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(filename));
	        //StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            result.add(line);
	            //sb.append('\n');
	            line = br.readLine();
	        }
	        //String everything = sb.toString();
	        //System.out.println(everything);
	    }catch(Exception e){
	    	e.printStackTrace();
	    } finally {
	        if(br!=null){
	        	try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	    return result;
	}
	public static void main(String[] args){
		String filename = "bank.txt";
		System.out.println("Reading:"+filename);
		List<String> contents = read(filename);
		int i=0;
		for(String s:contents){
			i++;
			System.out.println(i+":"+s);
		}
		/*
    	//System.out.println(new java.io.File("").getAbsolutePath());
    	//System.out.println(FileUtil.class.getClassLoader().getResource("").getPath());		 
		String filename = "./import/bank.txt";
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(filename));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            sb.append('\n');
	            line = br.readLine();
	        }
	        String everything = sb.toString();
	        System.out.println(everything);
	    }catch(Exception e){
	    	e.printStackTrace();
	    } finally {
	        if(br!=null){
	        	try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
		//InputStream inputStream = FileUtil.class.getResourceAsStream(filename);
		*/
	}
}
/*
package test;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Raf {
    private static class Record{
    	private final double price;
    	private final int id;
    	private final int stock;

    	public Record(int id, int stock, double price){
    		this.id = id;
    		this.stock = stock;
    		this.price = price;
    	}

    	public void pack(int n, int offset, byte[] array){
    		array[offset + 0] = (byte)(n & 0xff);
    		array[offset + 1] = (byte)((n >> 8) & 0xff);
    		array[offset + 2] = (byte)((n >> 16) & 0xff);
    		array[offset + 3] = (byte)((n >> 24) & 0xff);
    	}

    	public void pack(double n, int offset, byte[] array){
    		long bytes = Double.doubleToRawLongBits(n);
    		pack((int) (bytes & 0xffffffff), offset, array);
    		pack((int) ((bytes >> 32) & 0xffffffff), offset + 4, array);
    	}

    	public byte[] getBytes() {
    		byte[] record = new byte[16];
    		pack(id, 0, record);
    		pack(stock, 4, record);
    		pack(price, 8, record);
    		return record;
    	}
    }

    private static final int RECORD_SIZE = 16;
    private static final int N_RECORDS = 1024;

    /**
     * @param args
     * @throws IOException 
     *
    public static void main(String[] args) throws IOException {
    	RandomAccessFile raf = new RandomAccessFile(args[0], "rw");
    	try{
    		raf.seek(RECORD_SIZE * N_RECORDS);

    		raf.seek(0);

    		raf.write(new Record(1001, 476, 28.35).getBytes());
    		raf.write(new Record(1002, 240, 32.56).getBytes());
    	} finally {
    		raf.close();
    	}
    }
}
 */
