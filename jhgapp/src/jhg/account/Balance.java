package jhg.account;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jhg.ApplicationException;
import jhg.ImportResults;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.Result;
import jhg.account.AccountType.Side;
import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.Value;
import jhg.model.field.Decimal;
import jhg.model.field.Flag;
import jhg.model.field.Reference;
import jhg.model.field.Text;

public class Balance extends Model {
	
	//public static BalanceManager manager = new BalanceManager("balance");
	public static final String BALANCE = "balance";
	public static final String BK = "ownerbk";
	public static final String OWNER = "owner";
	public static final String CODE = "code";
	public static final String DEBIT = "debit";
	public static final String CREDIT = "credit";
	public static final String IS_BALANCED = "is_balanced";
	public static final String INSOLVENT = "insolvent";
	
	public static class BalanceManager extends Manager<Balance>{
		
		public BalanceManager(Application app) {
			super(app, Accounting.BALANCE);
			setLabel("Balance");
			//NEVER call init() from in the constructor.
		}
		
		@Override
		public final void initManager() {
			
			//Owner
			Text.TextField owner = new Text.TextField(this,OWNER);//TODO use constants is better.
			owner.setLength(30);
			owner.setRequired();
			owner.setValidationRegex(Pattern.compile("^[a-zA-Z\\. ]{4,30}$"),
					"upper and lower case alphabetical characters, space and dot between 4 and 30 characters long");
			owner.setLabel("Owner");//owner.setPrompt("");owner.setHelp("");
			
			//Code
			Text.TextField code = new Text.TextField(this, CODE);
			code.setLength(3);
			code.setValidationRegex(Pattern.compile("^[A-Z]{3}$"),
					"error, uppercase only.");
			code.setCalculated();
			code.setLabel("Code");
			
			//Debit
			Decimal.DecimalField debit = new Decimal.DecimalField(this,DEBIT,9,2,Accounting.DEFAULT_ROUNDING);
			//debit.setFormatter(DecimalFormat.getCurrencyInstance());
			debit.setLabel("Debit");
			
			//Credit
			Decimal.DecimalField credit = new Decimal.DecimalField(this,CREDIT,9,2,Accounting.DEFAULT_ROUNDING);
			//credit.setFormatter(DecimalFormat.getCurrencyInstance());
			credit.setLabel("Credit");
			
			//Is Balanced
			Flag.FlagField is_balanced = new Flag.FlagField(this,IS_BALANCED);
			is_balanced.setCalculated();
			is_balanced.setFormat(Flag.FlagField.Format.YN);
			is_balanced.setLabel("Balanced?");

			//
			Decimal.DecimalField insolvent = new Decimal.DecimalField(this,INSOLVENT,9,2,Accounting.DEFAULT_ROUNDING);
			insolvent.setCalculated();//should set the default value to blank when a balance is created.
			//insolvent.setFormatter(DecimalFormat.getCurrencyInstance());
			insolvent.setLabel("Insolvent");			
			
			//do this altogether in case we have a problem.
			fields.add(owner);
			fields.add(code);
			fields.add(debit);
			fields.add(credit);
			fields.add(is_balanced);
			fields.add(insolvent);
			setUnique(BK,new Field[]{owner});
			
			/*
			 * x Balance: 
			 * x  (int id)
			 * x  owner:  Text 30, Alpha whitesp
			 * x  debit:  Decimal 9,2 digits, format Currency.  $9,999,999.99. no range, def:blank if(exist dr bal accounts) total
			 * x  credit: Decimal 9,2 Money credit.                                      def:blank if(exist cr bal accounts) total
			 * x  Bal?  : Flag, calculated (req:debit,credit) debit-credit=0
			 * x  Insolv: Decimal, calc (req: debit,credit) credit-debit. if(!bal) else blank
			 */	
			
		}

		@Override
		public final void initDependent(){
			getField(DEBIT).setAggregateCalculated(application.getManager(Accounting.ACCOUNT).getField(Account.CURRENT_BALANCE));//should set the default value to blank when a balance is created.
			//TODO the above long string of calls violates some law of demeter and has some null vulnerability but this would be programmer error.
			getField(CREDIT).setAggregateCalculated(application.getManager(Accounting.ACCOUNT).getField(Account.CURRENT_BALANCE));//should set the default value to blank when a balance is created.
		}
		
		public ModelResult createBalance(String val) throws ApplicationException {
			Balance newBalance = new Balance(this);
			Field owner = getField(OWNER);
			if(owner==null)throw new IllegalStateException("owner field not found.");
			Result setOwner = newBalance.setValue(owner,val);
			ModelResult mr = new ModelResult();
			mr.addResult(setOwner);
			verifyUnique(newBalance,mr);
			if(mr.isSuccessful()){
				mr.setModel(newBalance);
				add(newBalance);
			}
			return mr;
		}

		@Override
		public ModelsResult performImport(List<String> rows) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Model makeModel() {
			return new Balance(this);
		}

		
	}
	
	public Balance(BalanceManager manager) {
		super(manager);
	}
	
	@Override
	public String getIdentifyingValue(){
		System.out.println("Balance getIdentifyingValue");
		return getValue(OWNER).format();
	}	

	protected void doCalculation(List<Field> notifying, Field f){
		if(f.is(CODE) && haveValue(OWNER)){//prevents circular notifications.
			String codeValue = calcCode((Text)getValue(OWNER));
			setCalc(notifying, getField(CODE),codeValue);
		}
		
		if(f.is(IS_BALANCED) && haveValue(CREDIT) && haveValue(DEBIT)){
			
			Decimal credit = (Decimal)getValue(CREDIT);
			Decimal debit = (Decimal)getValue(DEBIT);
			BigDecimal bal = new BigDecimal(Decimal.Util.subtract(credit,debit));
			Boolean isBal = bal.doubleValue()==0.0;
			debug("Is Balanced "+bal+" update:"+ isBal+". CR "+credit+",  DR "+debit);
			Result r = setCalc(notifying, getField(IS_BALANCED),isBal.toString());
			if(r.isSuccessful()){
				setCalc(notifying, getField(INSOLVENT),String.valueOf(bal.doubleValue()));
			}
		}

	}
	


	private String calcCode(Text vOwner){
		StringBuilder sb = new StringBuilder();
		String owner = vOwner.toString();
		for(int i=0;i<owner.length();i++){
			if(Character.isUpperCase(owner.charAt(i))){
				sb.append(owner.charAt(i));
			}
		}
		String result = sb.toString();
		//debug("calcCode():result:"+result);
		return result;
	}
	
	private static void debug(String s){
		boolean debug = true;
		if(debug){
			System.out.println("Balance."+s);
		}
	}
	
	@SuppressWarnings("unused")
	public String sumDebits() {
		String result = "0";
		List<Decimal> debitAcctBals = new ArrayList<Decimal>();
		for(Account _account:((Account.AccountManager)getApplication().getManager(Accounting.ACCOUNT)).getModels().values()){
			//TODO fix the above long call chain
			Reference acctBalanceID = (Reference)_account.getValue(Account.BALANCE_ID);
			AccountType.Side side = _account.getSide();
			if(acctBalanceID.intValue().equals(this.getId()) && side.equals(Side.DR) ){ //TODO refactor with .is(
				Value currbal = _account.getValue(Account.CURRENT_BALANCE); //TODO add some parametric perhaps.
				if(currbal.isNotBlank()){
					Decimal decimalBal = (Decimal)currbal;
					debitAcctBals.add(decimalBal);
				}
			}
		}
		return Decimal.Util.sum(debitAcctBals);
	}
	
	@SuppressWarnings("unused")
	public String sumCredits() {
		String result = "0";
		List<Decimal> creditAcctBals = new ArrayList<Decimal>();
		for(Account _account:((Account.AccountManager)getApplication().getManager(Accounting.ACCOUNT)).getModels().values()){
			Reference acctBalanceID = (Reference)_account.getValue(Account.BALANCE_ID);
			AccountType.Side side = _account.getSide();
			if(acctBalanceID.intValue().equals(this.getId()) && side.equals(Side.CR) ){ //TODO refactor with .is(
				Value currbal = _account.getValue(Account.CURRENT_BALANCE); //TODO add some parametric perhaps.
				if(currbal.isNotBlank()){
					Decimal decimalBal = (Decimal)currbal;
					creditAcctBals.add(decimalBal);
				}				
			}
		}
		return Decimal.Util.sum(creditAcctBals);
	}	
	
}
