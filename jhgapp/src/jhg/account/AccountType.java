package jhg.account;

import java.util.List;
import java.util.regex.Pattern;

import jhg.ApplicationException;
import jhg.ImportResults;
import jhg.ModelResult;
import jhg.ModelsResult;
import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.field.Numeric;
import jhg.model.field.Text;

import org.apache.commons.lang3.StringUtils;

public class AccountType extends Model {
	public static final String BK = "accounttype_bk";
	public static final String NAME = "name";
	public static final String LEVEL = "level";
	public static final String SIDE  = "side";
	
	public static enum Level{BALANCE,PERIODIC}
	public static enum Side{DR,CR}
	
	//This is a complex static lookup. complex due to having other enums with it but static list like Asset, Expense, etc.
	public static class AccountTypeManager extends Manager<AccountType>{
		
		

		public AccountTypeManager(Application app) {
			super(app,Accounting.ACCOUNT_TYPE);
			setLabel("AccountType");
		}

		@Override
		public final void initManager() {
			
			//Name
			Text.TextField name = new Text.TextField(this,NAME);
			name.setLength(15);
			name.setRequired();
			
			name.setValidationRegex(Pattern.compile("^[a-zA-Z]{4,15}$"),
					"upper and lower case alphabetical characters between 4 and 15 characters long");
			name.setLabel("Type");//owner.setPrompt("");owner.setHelp("");
			
			//Level
			Numeric.NumberField level = new Numeric.NumberField(this,LEVEL,Numeric.NumberField.Type.TINYINT);
			level.setRequired();
			level.setBounds(1L,2L);
			level.setLabel("Account Cycle Type");
			
			//Side
			Numeric.NumberField side = new Numeric.NumberField(this,SIDE,Numeric.NumberField.Type.TINYINT);
			side.setRequired();
			side.setBounds(1L,2L);
			side.setLabel("Account Balance Side");	
			
			fields.add(name);
			fields.add(level);
			fields.add(side);
			setUnique(BK,new Field[]{name});
		}
		
		public ModelResult createAccountType(String csv) throws ApplicationException {
			ModelResult mr = new ModelResult();
			AccountType newType = new AccountType(this);
			String[] values = StringUtils.split(csv, ',');
			mr.addResult(newType.setValue(getField(NAME), values[0]));
			mr.addResult(newType.setValue(getField(LEVEL),values[1]));//FIXME use another class
			mr.addResult(newType.setValue(getField(SIDE),values[2]));//FIXME use another class
			verifyUnique(newType,mr);
			if(mr.isSuccessful()){
				mr.setModel(newType);
				add(newType);
			}
			return mr;
		}

		@Override
		public void initDependent() {
			;//no aggregate types,etc... to do.
		}

		@Override
		public ModelsResult performImport(List<String> rows) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Model makeModel() {
			return new AccountType(this);
		}



	}
	
	@Override
	public String getIdentifyingValue(){
		return getValue(NAME).format();
	}	
	
	public Level getLevel(){
		Field _field = getManager().getField(LEVEL);
		Numeric levelValue = (Numeric)fieldvalues.get(_field);
		Level[] levels = Level.values();
		return levels[levelValue.intValue()-1];
	}
	
	public Side getSide(){
		Field _field = getManager().getField(SIDE);
		Numeric sideValue = (Numeric)fieldvalues.get(_field);
		Side[] sides = Side.values();
		return sides[sideValue.intValue()-1];
	}	
	
	public AccountType(AccountTypeManager manager) {
		super(manager);
	}

	
}
