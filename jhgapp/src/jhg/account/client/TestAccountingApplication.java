package jhg.account.client;

import java.util.List;

import jhg.FileUtil;
import jhg.account.Accounting;


public class TestAccountingApplication {

	@SuppressWarnings("unused")
	public static void main(String[] args){
		Accounting accounting = new Accounting();
		debug("Loading "+accounting);
		accounting.initialize();
		try{
			
			String filename = "bank.txt";
			System.out.println("Reading:"+filename);
			List<String> contents = FileUtil.read(filename);
			for(String line:contents){
				//parseline
				//RESUME here (import poc)
			}
			
			//final int JUNE = 6;//2013
			//final String JUNEFILE = "";
			/*
			createBalance();
			//createAccountMonthly(JUNE);//June
			createAccountTypes();
			createAccounts();
			openingEntries();         //summaries(); 
			//import(JUNEFILE,JUNE);  //summaries();
			//validate(JUNE);        //summaries();
			//postAccounts(JUNE);    //summaries();
			//enterOutstanding(JUNE);//summaries();
			//reconcile(JUNE);       //summaries();
			//close(JUNE);           //summaries + statements();
			 * 
			 */
		
		}catch(Exception e){
			debug("Exception: "+e.getMessage());
			e.printStackTrace();
		}

		

	}
	/*
	private static void openingEntries() throws Exception {
		Account checking1 = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Capital One Checking 1 6281"}));
		Account checking2 = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Capital One Checking 2 1579"}));
		Account hardAssets = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Hard Assets"}));
		Account retirement = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Retirement"}));
		Account savings = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Savings"}));
		Account dental = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Dental"}));
		Account netWorth = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Net Worth"}));
		Account salary = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Salary"}));
		Account taxes = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Taxes"}));
		Account deductible = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Deductible"}));
		Account childcare = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Childcare"}));
		Account bills = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Bills"}));
		Account necessities = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Necessities"}));
		Account essentials = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Essentials"}));
		Account discretionary = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Discretionary"}));
		Account incidental = (Account) verify(Account.manager.lookup(Account.BK, new String[]{"Incidental"}));
		final String O = "Opening";
		final String C = "open";//check no default
		final String P = "8";
		final String Z = "0.00";
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.001-0400",O,C,"38.54",checking1,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.002-0400",O,C,"0.00",checking2,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.003-0400",O,C,Z,hardAssets,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.004-0400",O,C,Z,retirement,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.005-0400",O,C,Z,savings,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.006-0400",O,C,Z,netWorth,dental,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.007-0400",O,C,Z,netWorth,salary,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.008-0400",O,C,Z,taxes,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.009-0400",O,C,Z,deductible,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.010-0400",O,C,Z,childcare,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.011-0400",O,C,Z,bills,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.012-0400",O,C,Z,necessities,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.013-0400",O,C,Z,essentials,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.014-0400",O,C,Z,discretionary,netWorth,P) );
		check ( Entry.manager.createEntry("2013-07-27 05:26:33.015-0400",O,C,Z,incidental,netWorth,P) );
		//savings.setValue(Account.NAME, "Savings M and T Bank");
		
		//Verify that the balance shows the right balance sheet.
		//Verify that the accounts have a balance.
		debug(Account.manager);
		debug("-----");
		debug(Balance.manager);
		debug("Opening Entries finished. ");			
		
		//Entry.manager.createEntry(Account.manager.)
	}

	private static void createAccounts() throws Exception {
		Balance balance = (Balance) verify(Balance.manager.lookup(Balance.BK,new String[]{"John H. Gagon"}));
		AccountType asset = (AccountType) verify(AccountType.manager.lookup(AccountType.BK,new String[]{"Asset"}));
		AccountType liability = (AccountType) verify(AccountType.manager.lookup(AccountType.BK,new String[]{"Liability"}));
		AccountType worth = (AccountType) verify(AccountType.manager.lookup(AccountType.BK,new String[]{"Worth"}));
		AccountType income = (AccountType) verify(AccountType.manager.lookup(AccountType.BK,new String[]{"Income"}));
		AccountType expense = (AccountType) verify(AccountType.manager.lookup(AccountType.BK,new String[]{"Expense"}));
		
		check( Account.manager.createAccount("Capital One Checking 1 6281",balance,asset) );
		check( Account.manager.createAccount("Capital One Checking 2 1579",balance,asset) );
		check( Account.manager.createAccount("Hard Assets",balance,asset) );
		check( Account.manager.createAccount("Retirement",balance,asset) );
		check( Account.manager.createAccount("Savings",balance,asset) );
		check( Account.manager.createAccount("Dental",balance,liability) );
		check( Account.manager.createAccount("Net Worth",balance,worth) );
		check( Account.manager.createAccount("Salary",balance,income) );
		check( Account.manager.createAccount("Taxes",balance,expense) );        //                                             absolute, yearly recurring
		check( Account.manager.createAccount("Deductible",balance,expense) );   //                                             can claim on itemized: work travel, work supplies, work phone, charity, mortgage, childcare, healthcare
		check( Account.manager.createAccount("Childcare",balance,expense) );    //                                             life insurance, child support
		check( Account.manager.createAccount("Bills",balance,expense) );        //rent                                         scheduled, monthly recurring
		check( Account.manager.createAccount("Necessities",balance,expense) );  //grocery, gas, parking, meds,                 essential-beneficial 
		check( Account.manager.createAccount("Mandatory",balance,expense) );    //fines                                        essential-unbeneficial  - not avoidable, legal
		check( Account.manager.createAccount("Discretionary",balance,expense) );//entertainment                                inessential-beneficial
		check( Account.manager.createAccount("Incidental",balance,expense) );   //donations, cigs, overdraft, interest         inessential-unbeneficial but avoidable
		debug(Account.manager);
		debug("CreateAccounts finished. ");		
	}
	
	
	public static Model verify(ModelResult mr)throws Exception{
		if(mr.hasModel()){
			return mr.getModel();
		}else{
			check(mr);
			throw new Exception("Failed. See output.");
		}
	}
	
	public static void check(ModelResult mr){
		if(!mr.hasModel()){
			
			debug("Reason:"+mr.getReason().name());
			
			List<Result> results = mr.getResults();
			for(Result r:results){
				if(r.notSuccessful()){
					debug("Result:"+r.name());
					if(r.hasMessages()){
						Messages m = r.getMessages();
						List<String> messages = m.get();
						for(String s:messages){
							debug(s);
						}
					}
					if(r.hasException()){
						Exception e = r.exception();
						debug(" Exception:");
						e.printStackTrace();
					}
					if(r.hasValue()){
						Value v = r.value();
						debug(" Value:'"+v+"'");
					}
					
					debug("-------------------------");
				}
			}
			debug("--------------------------------------------------");
		}	
	}
	

	
	private static void createAccountTypes() throws Exception {
		//Create 5: Asset, Liability, Worth, Income, Expense
		String[] accountTypeData = {"Asset,1,1","Liability,1,2","Worth,1,2","Income,2,2","Expense,2,1"};
		for(String accountTypeDatum:accountTypeData){
			ModelResult mr = AccountType.manager.createAccountType(accountTypeDatum);
			if(!mr.hasModel()){
				throw new Exception("Create Account Type unsuccessful.");
			}			
		}
		debug(AccountType.manager);
		debug("CreateAccountTypes finished. ");
	}
	
//	private static void createAccountMonthly() throws Exception {
//	}
	

	private static void createBalance() throws Exception {
		ModelResult mr = Balance.manager.createBalance("John H. Gagon");
		if(!mr.hasModel()){throw new Exception("CreateBalance unsuccessful.");
		}else{
			debug(Balance.manager);
			debug("CreateBalance finished. ");
		}
	}
	
	
	
	
	private static void debug(Manager manager) {
		Map<Integer,Model> models = manager.getModels();
		for(Integer i:models.keySet()){
			Model m = models.get(i);
			debug("Model("+m.getId()+"): "+m);
		}
	}
	*/

	private static void debug(String s){
		System.out.println(s);
	}
	
}
/*
 * Data Definition:
 * 
 * Balance: 
 *   (int id)
 *   owner:  Text 30, Alpha whitesp
 *   debit:  Decimal 9,2 digits, format Currency.  $9,999,999.99. no range, def:blank if(exist dr bal accounts) total
 *   credit: Decimal 9,2 Money credit.                                      def:blank if(exist cr bal accounts) total
 *   Bal?  : Flag, calculated (req:debit,credit) debit-credit=0
 *   Insolv: Decimal, calc (req: debit,credit) credit-debit. if(!bal) else blank
 *   
 * AccountType:
 *   name:
 *   enum: DR,CR
 *   enum: Balance,Cycle
 *   
 * Account
 *   balance fk id.
 *   type
 *   name
 *   current amount
 *   
 * AccountMonthly
 *   month
 *   year
 *   account id
 *   amount
 *   
 * Entry   (see capital one)
 *   date
 *   account id
 *   
 */

/*
 * Use case:
 * 
 * Account Types:
 * 1. Asset
 * 2. Liability
 * 3. Capital
 * 4. Income
 * 5. Expenses
 * 
 * Account:        assumption: sales tax not counted separately. All grocery is essential.
 *                 certain: amount known. absolute: no choice. intentional-desired/control over. 
 *  1. Checking 1
 *  2. Savings  1
 *  3. Dental   2
 *  4. Worth    3
 *  5. Salary   4
 *  6. Tax      5   (absolute,(scheduled, certain, essential, intentional))  
 *  7. Bills    5   (nonabslt,(scheduled, certain, essential, intentional)) Child Supp, Rent, Power, Cell, Car Ins (tweakable but controlled by prices/needs but known in advance) 
 *  8. Food     5   (nonabslt, unschdled, uncertn, essential, intentional ) Grocery,Gas, Medical   (tweakable but controlled by prices/needs)
 *  9. Fines    5   (nonabslt, unschdled, uncertn, essential, unintentnal ) Fines, Late fees, Tickets, (not controllable but essential)
 * 10. Dscrtnry 5   (nonabslt,(unschdled, uncertn) inessentl, intentional ) Dining, Movies, Tolls, Gifts             (biggest controlled source of waste, useful but not essential), Latest Greatest book/cell phone. 
 * 11. Incidntl 5   (nonabslt,(unschdled, uncertn) inessentl, unintentnal ) Donations,Tips,Loss/Theft,Handout,Cigs,Replace Damage,Accidents  (biggest but less controllable source of waste) not obligatory.
 *                  absolute, unschdled, uncertn? inessentl? unintentnal? No
 *                  nonabslt, scheduled, uncertn? inessentl? unintentnal? No
 *                  nonabslt, unschdled, certain, essential, Unintentnal? No
 *                    
 *                  
 * 
 * I. Initialize Accounts.
 *    Checking Balance & Worth - March
 *    Salary, Food, Tax = zero                 STATUS: Statement
 * II. Monthly Recording: March (April,May,June) 
 * 
 * a. Import March checkbook entries from file STATUS: Imported/Error
 *     EntryManager of Entry., 
 *     default source: bank. import date: import usr: jhg
 *    Get summary of Entry                     
 * b. Validate using a Manager group action    STATUS: Valid/NotPosted  / Invalid
 *    Get summary of Entry
 * c. Assign accounts for each.                STATUS: Posted, Reconciled
 *    Get summaries of Entry and Accounts
 * d. Add Outstanding Entries                  STATUS: Valid/NotPosted, Not Reconciled
 *    Get summaries
 * e. Reconcile Outstanding                    STATUS: Reconciled    
 *    (add reconciling entries)
 *    Get summaries 
 * f. Perform Monthly Closing                  STATUS: Closed, Statement    
 *    Get summaries - (Statement, Balance Sheet, Final Journal)
 *    
 *  IMPORTED (not valid) - in memory
 *  VALIDATED (but not complete, record is unique. (business keys are required)/assigned accounts)  best not to rely excessively on required fields and use completion codes instead.
 *  POSTED    (assigned accounts but maybe not reconciled, outstanding hand entries might be needed.  
 *  	will show up on accounts. (add rule)
 *  	won't show up on cycle until reconciled
 *  	won't show up on balance sheet until closed    
 *  RECONCILED (hand entered entries do require immediate validation and posting)   
 *    	will show up on accounts and cycle but not balance sheet.
 *      the cycle will show the current income statement unclosed (and balance sheet unclosed?)
 *  CLOSED
 *      the entries for closing are entered, the cycle is marked closed and locked, the entries are now marked closed and locked. 
 *      the cycle shows the current income statement (with sections for the credits on income and debits on expenses subtotalled and another section for the closeout)
 *      the cycle can also show the changes in assets, liabilities and capital for a period.
 *      
 *      the balance sheet will be updated with the closed entries. (income and expense are always zero'd so no use showing Cycle level accounts). 
 *             
 *  Archiving
 *      to prevent constant recalculations during load up, when archiving, the opening entries are updated
 *      using the summations of the entries to be deleted.    
 *    
 * III. Goals: Earning goal/new debt goal, Expense Budget, Debt Reduction, Worth goal, Savings goal
 *    Tax calculator  
 *    
 * Use all the basic types and the relationship     
 */