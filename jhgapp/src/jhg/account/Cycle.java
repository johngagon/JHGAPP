package jhg.account;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

import jhg.ApplicationException;
import jhg.ImportResults;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.Result;
import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.field.DateTime;
import jhg.model.field.DateTime.DateTimeField;
import jhg.model.field.Decimal;
import jhg.model.field.Flag;
import jhg.model.field.Text;

public class Cycle extends Model {
	public static final String CYCLE_LABEL = "Cycle";
	//public static final CycleManager manager = new CycleManager(CYCLE);
	
	public static final String BK = "periodbk";
	public static final String PERIOD = "period";
	public static final String START = "start";
	public static final String END = "end";
	public static final String DEBIT = "debit";
	public static final String CREDIT = "credit";
	public static final String IS_BALANCED = "is_balanced";
	public static final String PROFIT = "profit";
	
	public static class CycleManager extends Manager<Cycle>{
		public CycleManager(Application app) {
			super(app,Accounting.CYCLE);//TODO unify using constants
			setLabel(CYCLE_LABEL);
		}
		
		@Override
		public final void initManager() {
			//Period
			Text.TextField period = new Text.TextField(this,PERIOD);
			period.setLength(30);
			period.setRequired();
			period.setValidationRegex(Pattern.compile("^[a-zA-Z\\. ]{4,30}$"),
					"upper and lower case alphabetical characters, space and dot between 4 and 30 characters long");
			period.setLabel("Period");//period.setPrompt("");period.setHelp("");
			
			//Start
			DateTime.DateTimeField start = new DateTime.DateTimeField(this, START, DateTimeField.Type.DATE);
			start.setRequired();
			start.setLabel("Period Start");			
			
			//End
			DateTime.DateTimeField end = new DateTime.DateTimeField(this, END, DateTimeField.Type.DATE);
			end.setRequired();
			end.setLabel("Period End");		
			
			//Debit
			Decimal.DecimalField debit = new Decimal.DecimalField(this,DEBIT,9,2,Accounting.DEFAULT_ROUNDING);
			//debit.setAggregateCalculated(Account.manager.getField(Account.CURRENT_BALANCE));//should set the default value to blank when a balance is created.
			debit.setFormatter(DecimalFormat.getCurrencyInstance());
			debit.setLabel("Debit");
			
			//Credit
			Decimal.DecimalField credit = new Decimal.DecimalField(this,CREDIT,9,2,Accounting.DEFAULT_ROUNDING);
			//credit.setAggregateCalculated(Account.manager.getField(Account.CURRENT_BALANCE));//should set the default value to blank when a balance is created.
			credit.setFormatter(DecimalFormat.getCurrencyInstance());
			credit.setLabel("Credit");
			
			//Is Balanced
			Flag.FlagField is_balanced = new Flag.FlagField(this,IS_BALANCED);
			is_balanced.setCalculated();
			is_balanced.setFormat(Flag.FlagField.Format.YN);
			is_balanced.setLabel("Balanced?");

			//
			Decimal.DecimalField profit = new Decimal.DecimalField(this,PROFIT,9,2,Accounting.DEFAULT_ROUNDING);
			profit.setCalculated();//should set the default value to blank when a balance is created.
			credit.setFormatter(DecimalFormat.getCurrencyInstance());
			credit.setLabel("Profit");			
			
			//do this altogether in case we have a problem.
			fields.add(period);
			fields.add(start);
			fields.add(end);
			fields.add(debit);
			fields.add(credit);
			fields.add(is_balanced);
			fields.add(profit);
			setUnique(BK,new Field[]{period});
		}

		public ModelResult createCycle(String _period, String _start, String _end) throws ApplicationException {
			Cycle newCycle = new Cycle(this);
			ModelResult mr = new ModelResult();
			mr.addResult(newCycle.setValue(PERIOD,_period));
			mr.addResult(newCycle.setValue(START,_start));
			mr.addResult(newCycle.setValue(END,_end));
			verifyUnique(newCycle,mr);
			if(mr.isSuccessful()){
				mr.setModel(newCycle);
				add(newCycle);
			}
			return mr;
		}

		@Override
		public void initDependent() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ModelsResult performImport(List<String> rows) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Model makeModel() {
			return new Cycle(this);
		}
	}
	
	public Cycle(CycleManager manager) {
		super(manager);
	}

	@Override
	public String getIdentifyingValue(){
		return getValue(PERIOD).format();
	}	
	
	protected void doCalculation(List<Field> notifying, Field f){

		
		if(f.is(IS_BALANCED) && haveValue(CREDIT) && haveValue(DEBIT)){
			Decimal credit = (Decimal)getValue(CREDIT);
			Decimal debit = (Decimal)getValue(DEBIT);
			BigDecimal bal = new BigDecimal(Decimal.Util.subtract(credit,debit));
			Boolean isBal = bal.equals(Decimal.ZERO_BD);
			Result r = setCalc(notifying, getField(IS_BALANCED),isBal.toString());
			if(r.isSuccessful()){
				setCalc(notifying, getField(PROFIT),String.valueOf(bal.doubleValue()));
			}
		}
	}

	@SuppressWarnings("unused")
	private static void debug(String s){
		boolean debug = true;
		if(debug){
			System.out.println("Cycle."+s);
		}
	}

}
