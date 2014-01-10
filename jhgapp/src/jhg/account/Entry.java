package jhg.account;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jhg.ApplicationException;
import jhg.ImportResults;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.account.Account.AccountManager;
import jhg.account.AccountType.Side;
import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.Value;
import jhg.model.field.DateTime;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Decimal;
import jhg.model.field.Numeric;
import jhg.model.field.Reference;
import jhg.model.field.Text;

public class Entry extends Model {
	//public static EntryManager manager = new EntryManager("entry");
	
	//INDEX NAMES
	public static final String BK = "entry_bk";
	//FIELD NAMES
	public static final String ENTRY_DATE = "entry_date";
	public static final String DESCRIPTION = "description";
	public static final String CHECK_NO = "check_no";      //source doc number, receipt no.
	public static final String AMOUNT = "amount";
	public static final String ACCOUNT_DR = "account_dr";	
	public static final String ACCOUNT_CR = "account_cr";
	public static final String POSTING = "posting";
	                                                     
	public static enum Posting{
		STARTED,                  //1   user opened but is not saved since user has not set value on required minimum/bk.
		ENTERED,                  //2   ready for save from user due to having minimum required fields and business key.
		IMPORTED,                 //3   ready for save from file due to having minimum required fields and business key. when save action occurs, it is recorded
		STORED,                   //4   valid just enough to store in database so import record is not lost but could have errors saved as well.
		VALIDATED,                //5   considered a valid record useful for work and without corrections.
		RECORDED,                 //6   proven valid and saved for first time and now persisted but not necessarily considered complete enough for review
		POSTED,                   //7   functionally complete entry that continues to be valid and also saved.
		RECONCILED,               //8   this is found valid externally and consistent with public record, bank etc.
		TRIAL,                    //9   the record is closed but not proven error free yet with other records.
		CLOSE }                   //10  the record is locked and made part of official statement. (a proposed republish may happen if this ever proves insufficiently final)
	
	/**
	 * DOC
	 */
	public static class EntryDateComparator implements Comparator<Entry> {
		@Override
		public int compare(Entry e1, Entry e2) {
			if(e1==e2 || e1.equals(e2)){
				return 0;
			}
			DateTime d1 = e1.getEntryDate();
			DateTime d2 = e2.getEntryDate();
			return d1.compareTo(d2);
		}
	}
	
	/**
	 * 
	 * @author John
	 *
	 */
	public static class EntryManager extends Manager<Entry>{

		
		public EntryManager(Application app) {
			super(app,Accounting.ENTRY);
			setLabel("Entry");
		}

		@Override
		public final void initManager() {
			//Date
			DateTime.DateTimeField date = new DateTime.DateTimeField(this, ENTRY_DATE, DateTimeField.Type.TIMESTAMP);
			date.setPrecision(0);
			date.setRequired();
			date.setFormat("yyyy-MM-dd HH:mm:ss");//for storage
			date.setLabel("Entry Date");
			
			//Description
			Text.TextField description = new Text.TextField(this,DESCRIPTION);
			description.setLength(500);//repeat 1
			description.setRequired();			
			description.setLabel("Description");

			//Check No
			Text.TextField checkNo = new Text.TextField(this,CHECK_NO);
			checkNo.setLength(15);//repeat 1
			checkNo.setValidationRegex(Pattern.compile("^[a-zA-Z0-9 ]{4,15}$"),//repeat 2
					"upper and lower case alphabetical characters with spaces between 4 and 15 characters long");//repeat 3  TODO stop the repeat
			checkNo.setLabel("Check No.");			
			
			
			//Account DR
			Reference.ReferenceField accountDr = new Reference.ReferenceField(this,ACCOUNT_DR,application.getManager(Accounting.ACCOUNT));
			accountDr.setLabel("Account Debited");
			//Account CR
			Reference.ReferenceField accountCr = new Reference.ReferenceField(this,ACCOUNT_CR,application.getManager(Accounting.ACCOUNT));
			accountCr.setLabel("Account Credited");

			
			//Amount
			Decimal.DecimalField amount = new Decimal.DecimalField(this,AMOUNT,9,2,Accounting.DEFAULT_ROUNDING);
			amount.setRequired();
			//amount.setFormatter(DecimalFormat.getInstance());//amount.setFormatter(DecimalFormat.getCurrencyInstance());//don't use currency.
			amount.setLabel("Amount");			
			
			//Posting
			Numeric.NumberField posting = new Numeric.NumberField(this,POSTING,Numeric.NumberField.Type.TINYINT);
			posting.setRequired();
			posting.setBounds(1L,10L);
			posting.setLabel("Post Status");
						
			
			fields.add(date);
			fields.add(description);
			fields.add(checkNo);
			fields.add(accountDr);
			fields.add(accountCr);
			fields.add(amount);
			fields.add(posting);
			setUnique(BK,new Field[]{date});
		}
		
		@Override
		public void initDependent() {
			//nothing to do. 
		}		
		
		public ModelResult createEntry(String date,String description,String checkNo,String amount,Account dr,Account cr, String posting) throws ApplicationException{
			ModelResult mr = new ModelResult();
			Entry newEntry = new Entry(this);
			mr.addResult(newEntry.setValue(ENTRY_DATE, date));
			mr.addResult(newEntry.setValue(DESCRIPTION, description));
			mr.addResult(newEntry.setValue(CHECK_NO, checkNo));
			mr.addResult(newEntry.setReference(ACCOUNT_DR, dr));
			mr.addResult(newEntry.setReference(ACCOUNT_CR, cr));
			mr.addResult(newEntry.setValue(POSTING, posting));   //this field is likely going to be pre-requisite for the aggregate calc.
			mr.addResult(newEntry.setValue(AMOUNT, amount));     //note, this relies on the accounts, moved the two account references up even though not in argument order.
			verifyUnique(newEntry,mr);
			if(mr.isSuccessful()){
				mr.setModel(newEntry);
				add(newEntry);
			}
			return mr;	
		}

		//ENTRY_DATE,DESCRIPTION,CHECK_NO,AMOUNT
		public ModelResult createEntryFromImport(String date, String desc, String checkno, String amt) throws ApplicationException{
			AccountManager acctMgr = (AccountManager)application.getManager(Accounting.ACCOUNT);
			//RESUME here finish testing import format - move to the importer
			String checkingAcctName = "Capital One Checking 1 6281"; //TODO hardcoded, initialize these defaults somehow.
			String netWorthAcctName = "Net Worth";
			
			ModelResult drR = acctMgr.lookup(Account.BK, new String[]{checkingAcctName});
			Account dr = (Account)drR.getModel();
			ModelResult crR = acctMgr.lookup(Account.BK, new String[]{netWorthAcctName});
			Account cr = (Account)crR.getModel();
			
			String p = String.valueOf(Posting.STARTED.ordinal());
			
			
			return createEntry(date,desc,checkno,amt,dr,cr,p);
		}

		@Override
		public ModelsResult performImport(List<String> rows) throws ApplicationException {
			ModelsResult msr = new ModelsResult();
			char delimiter = '\t';
			ModelResult mr = new ModelResult();
			for(String line: rows){
				String[] parsed = StringUtils.split(line,delimiter);
				//TODO verify length
				mr = createEntryFromImport(parsed[0],parsed[1],parsed[2],parsed[3]);
				if(mr.isSuccessful()){
					this.verifyUnique((Entry)mr.getModel(),mr);	
				}
				msr.addResult(mr);
			}
			return msr;
		}

		@Override
		public Model makeModel() {
			return new Entry(this);
		}
		
		
	}

	@Override
	protected void doAggregateCalculations(Field foreignListener, Field notifier, Value _value) throws ApplicationException{
		/*
		 * 1. go through the reference fields and get any whose managers are the same as the foreign listener (make method for this to check)
		 *      or just grab the values on both accounts 
		 * 2. get the value of reference field and find the model with that manager by id.
		 * 
		 * 3. IF   the account model referenced is via the  debit reference and the account is of type  debit OR (opening asset balance:asset side)
		 *         the account model referenced is via the credit reference and the account is of type credit    (opening asset balance:worth side)
		 *      	  if there is no value, set this one. if there is, add the logic to add this value to that one.
		 *    ELSE the account model referenced is via the  debit reference but the account is of type credit OR (paying a loan:liability side)
		 *         the account model referenced is via the credit reference but the account is of type  debit    (paying a loan:asset side) 
		 *            there should be a value, subtract the amount from the balance value. 
		 */
		Decimal amount = (Decimal)_value;
		Account debitAccount = debitAccount();
		Account creditAccount = creditAccount();
		if(debitAccount.getValue(foreignListener).isBlank()){
			debitAccount.setValue(foreignListener, _value.toString());
		}else{
			Decimal currentBalance = (Decimal)debitAccount.getValue(foreignListener);
			if(debitAccount.getSide().equals(Side.DR)){
				debitAccount.setValue(foreignListener,Decimal.Util.add(currentBalance, amount));
			}else{
				debitAccount.setValue(foreignListener,Decimal.Util.subtract(currentBalance, amount));
			}
		}
		if(creditAccount.getValue(foreignListener).isBlank()){
			creditAccount.setValue(foreignListener, _value.toString());
		}else{
			Decimal currentBalance = (Decimal)creditAccount.getValue(foreignListener);
			if(creditAccount.getSide().equals(Side.CR)){
				creditAccount.setValue(foreignListener,Decimal.Util.add(currentBalance, amount));
			}else{
				creditAccount.setValue(foreignListener,Decimal.Util.subtract(currentBalance, amount));
			}
		}		
	}
	
	public Account debitAccount(){
		Reference debitAccountID = (Reference)getValue(ACCOUNT_DR);
		Account debitAccount = (Account)debitAccountID.getReferencedModel();
		return debitAccount;
	}
	
	public Account creditAccount(){
		Reference credictAccountID = (Reference)getValue(ACCOUNT_CR);
		Account creditAccount = (Account)credictAccountID.getReferencedModel();
		return creditAccount;
	}
	
	public Entry(EntryManager manager) {
		super(manager);
	}

	@Override
	public String getIdentifyingValue(){
		System.out.println("Entry.getIdentifyingValue getValue format:'"+getValue(ENTRY_DATE).format()+"'");
		System.out.println("Entry.getIdentifyingValue: getValue'"+getValue(ENTRY_DATE)+"'");
		System.out.println("Entry.getIdentifyingValue: Entry toString()'"+this+"'");
		return getValue(ENTRY_DATE).format()+" ["+getValue(ACCOUNT_DR).format()+"]["+getValue(ACCOUNT_CR).format()+"] "+getValue(AMOUNT).format();
		//return getValue(ENTRY_DATE).format()+":"+getValue(DESCRIPTION).format().substring(0,10)+"...";
	}	
	
	public DateTime getEntryDate(){
		Value entryDateValue = getValue(ENTRY_DATE);
		if(entryDateValue instanceof DateTime){
			DateTime entryDate = (DateTime)entryDateValue;
			return entryDate;
		}else{
			throw new IllegalStateException("Entry date could not store as date time");
		}
	}
	
}
/*
 * Assets = Liabilities + Capital.
 * 
 * Cash
 * 
 * 
 */
/*                          //for import
 * id
 * Date
 * Description
 * Check
 * Amount                       //during import this is which ever column has the value as an import rule.
 * AccountToDebit (deposit)    //import rule, this is default to the account id selected to default.
 * AccountTOCredit              
 * 
 * Currently only supporting single parts
 */