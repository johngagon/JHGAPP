package jhg.account;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import jhg.ApplicationException;
import jhg.ImportResults;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.account.AccountType.Level;
import jhg.account.AccountType.Side;
import jhg.account.Balance.BalanceManager;
import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.Value;
import jhg.model.field.Decimal;
import jhg.model.field.Reference;
import jhg.model.field.Text;


public class Account extends Model {
	//public static AccountManager manager = new AccountManager("account");
	
	//INDEX NAMES
	public static final String BK = "account_bk";
	
	//FIELD NAMES
	public static final String TYPE_ID = "account_type_id";
	public static final String BALANCE_ID = "balance_id";
	public static final String NAME = "name";
	public static final String CURRENT_BALANCE = "current_balance";
	
	public static class AccountManager extends Manager<Account>{

		public AccountManager(Application app) {
			super(app,Accounting.ACCOUNT);
			setLabel("Account");
			this.isVersioned = true;
		}

		@Override
		public final void initManager() {
			//Name
			Text.TextField name = new Text.TextField(this,NAME);
			name.setLength(50);//repeat 1
			name.setRequired();
			name.setValidationRegex(Pattern.compile("^[a-zA-Z0-9 ]{4,50}$"),//repeat 2
					"upper and lower case alphabetical characters with spaces between 4 and 50 characters long");//repeat 3  TODO stop the repeat
			name.setLabel("Account Name");//owner.setPrompt("");owner.setHelp("");
			
			//Balance
			Reference.ReferenceField balanceId = new Reference.ReferenceField(this,BALANCE_ID,application.getManager(Accounting.BALANCE));
			balanceId.setLabel("Balance Name");
			
			//Account Type
			Reference.ReferenceField typeId = new Reference.ReferenceField(this,TYPE_ID,application.getManager(Accounting.ACCOUNT_TYPE));
			typeId.setLabel("Account Type");
			
			//Current Balance
			Decimal.DecimalField currentBalance = new Decimal.DecimalField(this,CURRENT_BALANCE,9,2,Accounting.DEFAULT_ROUNDING);
			currentBalance.setFormatter(DecimalFormat.getCurrencyInstance());
			currentBalance.setLabel("Current Balance");			
			
			fields.add(name);
			fields.add(balanceId);
			fields.add(typeId);
			fields.add(currentBalance);
			setUnique(BK,new Field[]{name});
		}
		
		@Override
		public final void initDependent(){
			Field entryAmount = application.getManager(Accounting.ENTRY).getField(Entry.AMOUNT);
			getField(CURRENT_BALANCE).setAggregateCalculated(entryAmount);//should set the default value to blank when a balance is created.
		}		

		//NOTE: this uses "dd() which uses an in memory integer key.
		public ModelResult createAccount(String _name,Balance _balance, AccountType _type) throws ApplicationException {
			ModelResult mr = new ModelResult();
			Account newAcct = new Account(this);
			mr.addResult(newAcct.setValue(getField(NAME), _name));
			mr.addResult(newAcct.setReference(getField(BALANCE_ID),_balance));
			mr.addResult(newAcct.setReference(getField(TYPE_ID),_type));
			verifyUnique(newAcct,mr);
			if(mr.isSuccessful()){
				mr.setModel(newAcct);
				add(newAcct);
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
			return new Account(this);
		}
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public Account(Manager manager) {
		super(manager);
	}
	
	@Override
	public String getIdentifyingValue(){
		return getValue(NAME).format();
	}
	
	public AccountType getType(){
		Reference accountTypeID = (Reference)getValue(TYPE_ID);
		AccountType type = (AccountType)accountTypeID.getReferencedModel();	
		return type;
	}

	public Side getSide(){
		return getType().getSide();
	}
	
	public Level getLevel(){
		return getType().getLevel();
	}	
	
	//This model has fields that are being listened to by an aggregating listener.
	protected void doAggregateCalculations(Field foreignListener, Field notifier, Value _value) throws ApplicationException{

		/*
		 * When calculating the entries, the CURRENT_BALANCE field is set and checks who is listening.
		 * In this case, there are two fields in Balance, CREDIT AND DEBIT but only if this Account's AccountType LEVEL is BALANCE. (we dont' need income expense)
		 * 
		 * This usually won't result in an even balance without debiting income for the credit on worth, crediting expense for the debit on worth (the monthly will examine without this)
		 *    we could look at entries and only include those which are closed.
		 * 
		 * 1. when calculating we are only looking at the Level.BALANCE accounts.
		 * 2. we are only looking at trial and up.  
		 */
		//For now, we're just using the opening entries and we can fine tune it later.
		if(foreignListener.managerNameEquals(Balance.BALANCE) && foreignListener.getName().equals(Balance.DEBIT)){
			//TODO this fails because the balances are already accumulative. 
			//if there's a way to make the Balance just read the sum each time.
			Balance balanceInstance = balanceInstance();
			if(getLevel().equals(Level.BALANCE) && getSide().equals(Side.DR)){
				String sum = balanceInstance.sumDebits();//sum the accounts having the same balance id.
				balanceInstance.setValue(foreignListener, sum);
				debug("Balance debit:"+balanceInstance.getValue(foreignListener));
			}
		}else if(foreignListener.managerNameEquals(Balance.BALANCE) && foreignListener.getName().equals(Balance.CREDIT)){
			Balance balanceInstance = balanceInstance();
			if(getLevel().equals(Level.BALANCE) && getSide().equals(Side.CR)){
				String sum = balanceInstance.sumCredits();//sum the accounts having the same balance id.
				balanceInstance.setValue(foreignListener, sum);
				debug("Balance credit:"+balanceInstance.getValue(foreignListener));
			}
		}
		/*
		else if(foreignListener.managerNameEquals(Cycle.CYCLE) && foreignListener.getName().equals(Cycle.DEBIT)){
			Decimal amount = (Decimal)_value;
			Cycle currentCycle = getCurrentCycle();
			if(getLevel().equals(Level.PERIODIC) && getSide().equals(Side.DR)){
				if(currentCycle.getValue(foreignListener).isBlank()){
					currentCycle.setValue(foreignListener, _value.toString());
				}else{
					Decimal debit = (Decimal)currentCycle.getValue(foreignListener);
					currentCycle.setValue(foreignListener,Decimal.Util.add(debit, amount));
				}
			}			
		}else if(foreignListener.managerNameEquals(Cycle.CYCLE) && foreignListener.getName().equals(Cycle.CREDIT)){
			Decimal amount = (Decimal)_value;
			Cycle currentCycle = getCurrentCycle();
			if(getLevel().equals(Level.PERIODIC) && getSide().equals(Side.CR)){
				if(currentCycle.getValue(foreignListener).isBlank()){
					currentCycle.setValue(foreignListener, _value.toString());
				}else{
					Decimal credit = (Decimal)currentCycle.getValue(foreignListener);
					currentCycle.setValue(foreignListener,Decimal.Util.add(credit, amount));
				}
			}			
		}
		*/
		
	}	
	
	@SuppressWarnings("unused")
	private Cycle getCurrentCycle() {
		// TODO Auto-generated method stub
		return null;
	}

	public Balance balanceInstance() throws ApplicationException{
		Reference balanceID = (Reference)getValue(BALANCE_ID);
		BalanceManager balanceManager = (BalanceManager) getApplication().getManager(Balance.BALANCE);
		Balance _balance = balanceManager.getModel(balanceID.intValue());
		return _balance;
	}	
	
	private static void debug(String s){//TODO refactor to base and use name.
		boolean debug = true;
		if(debug){
			System.out.println("Account."+s);
		}
	}	
	
}

/*

Don't add, just update the value.

if(foreignListener.managerNameEquals(Balance.BALANCE) && foreignListener.getName().equals(Balance.DEBIT)){
Decimal amount = (Decimal)_value;
Balance balanceInstance = balanceInstance();
if(getLevel().equals(Level.BALANCE) && getSide().equals(Side.DR)){
	if(balanceInstance.getValue(foreignListener).isBlank()){ 
		debug("Setting balance.debit due to setting of account["+getValue(NAME)+"]."+notifier.getName()+":"+_value.toString());
		balanceInstance.setValue(foreignListener, _value.toString());
	}else{
		Decimal debit = (Decimal)balanceInstance.getValue(foreignListener);
		debug("Increasing balance.debit from "+debit+" using account["+getValue(NAME)+"]."+notifier.getName()+":"+_value.toString());
		balanceInstance.setValue(foreignListener,Decimal.Util.add(debit, amount));
	}
	debug("Balance debit:"+balanceInstance.getValue(foreignListener));
}
}else if(foreignListener.managerNameEquals(Balance.BALANCE) && foreignListener.getName().equals(Balance.CREDIT)){
Decimal amount = (Decimal)_value;
Balance balanceInstance = balanceInstance();
if(getLevel().equals(Level.BALANCE) && getSide().equals(Side.CR)){
	if(balanceInstance.getValue(foreignListener).isBlank()){
		debug("Setting balance.credit due to setting of account["+getValue(NAME)+"]."+notifier.getName()+":"+_value.toString());
		balanceInstance.setValue(foreignListener, _value.toString());
	}else{
		Decimal credit = (Decimal)balanceInstance.getValue(foreignListener);
		debug("Increasing balance.credit from "+credit+" using account["+getValue(NAME)+"]."+notifier.getName()+":"+_value.toString());
		balanceInstance.setValue(foreignListener,Decimal.Util.add(credit, amount));
	}
	debug("Balance credit:"+balanceInstance.getValue(foreignListener));
}
}
*/