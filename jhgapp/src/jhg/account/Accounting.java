package jhg.account;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jhg.PageResult;
import jhg.model.Application;
import jhg.model.Manager;
import jhg.model.Value;
import jhg.model.field.Decimal;
import jhg.model.field.Reference;

public class Accounting extends Application {

	public static RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
	//public static AccountTypeManager manager = new AccountTypeManager();
	
	public static final String ACCOUNT_TYPE = "account_type";
	public static final String ACCOUNT = "account";
	public static final String CYCLE = "cycle";
	public static final String BALANCE = "balance";
	public static final String ENTRY = "entry";
	
	public Accounting() {
		super("accounting");//Note: we must have this constructor be no arg due to reflection based loading and instantiation.
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void initialize() {
		managers.add(new Balance.BalanceManager(this));
		managers.add(new AccountType.AccountTypeManager(this));
		managers.add(new Account.AccountManager(this));
		managers.add(new Entry.EntryManager(this));
		managers.add(new Cycle.CycleManager(this));
		for(Manager manager:managers){
			manager.initManager();
		}
		for(Manager manager:managers){
			manager.initDependent();
		}
		//setup data
		
	}
	
	public PageResult<Entry> getEntries(Integer balanceId, int pageSize){
		List<Entry> entries = new ArrayList<Entry>();
		Manager<Entry> manager = getManager(ENTRY);
		Map<Integer,Entry> models = manager.getModels();
		for(Integer entryId:models.keySet()){
			Entry entry = models.get(entryId);
			Account dr = entry.debitAccount();
			Account cr = entry.creditAccount();
			Reference drAcctBalId = (Reference)dr.getValue(Account.BALANCE_ID);
			Reference crAcctBalId = (Reference)cr.getValue(Account.BALANCE_ID);
			if(drAcctBalId.intValue().equals(balanceId) && crAcctBalId.intValue().equals(balanceId)){			
				entries.add(entry);
			}
		}
		Collections.sort(entries,new Entry.EntryDateComparator());
		PageResult<Entry> entryList = new PageResult<Entry>(entries, pageSize);

		return entryList;
	}	
	
	public String getAccountTotal(Integer balanceId, AccountType accountType){
		//get accounts but filter by account types of given level and balance.
		Manager<Account> manager = getManager(ACCOUNT);
		Map<Integer,Account> models =  manager.getModels();
		List<Decimal> balances = new ArrayList<Decimal>();
		for(Integer acctId:models.keySet()){
			Account acct = models.get(acctId);
			AccountType accttype = acct.getType();
			Reference acctBalId = (Reference)acct.getValue(Account.BALANCE_ID);
			//NOTE Filter
			if(accttype.equals(accountType) && acctBalId.intValue().equals(balanceId)){
				Value currbal = acct.getValue(Account.CURRENT_BALANCE);
				if(currbal.isNotBlank()){
					Decimal decimalBal = (Decimal)currbal;
					balances.add(decimalBal);
				}
			}
			
		}
		return Decimal.Util.sum(balances);
	}

	@Override
	public String getName() {
		return "Accounting";
	}
	
	

}
