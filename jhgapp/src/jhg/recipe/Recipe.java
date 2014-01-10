package jhg.recipe;

import java.util.List;
import java.util.regex.Pattern;

import jhg.ModelsResult;
import jhg.model.Application;
import jhg.model.Field;
import jhg.model.Manager;
import jhg.model.Model;
import jhg.model.field.Text;
import jhg.model.field.special.Ssn;

@SuppressWarnings("rawtypes")
public class Recipe extends Model {
	
	//all models have an id, do a complete definition here based on a DD
	//any modifying requires readwrite.

	
	
	public Recipe(Manager _manager) {
		super(_manager);

		// TODO Auto-generated constructor stub
	}




	@SuppressWarnings("unused")
	private static class RecipeManager extends Manager
	{
		public RecipeManager(Application app,String _entityname) {
			super(app,_entityname);
			//init();
			setLabel("recipe");			
		}

		@SuppressWarnings("unchecked")
		@Override
		public final void initManager() {
			Text.TextField name = new Text.TextField(this,"name");
			name.setValidationRegex(Pattern.compile("^[a-zA-Z ]{4,50}$"),
					"upper and lower case alphabetical characters between 4 and 50 characters long");
			
			Ssn.SsnField ssn = new Ssn.SsnField(this,"social_security_number");
			
			//Field name only allows ...
			
			//TODO add field defining characteristics
			
			fields.add(name);
			//...
		}

		@Override
		public void initDependent() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ModelsResult performImport(List rows) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Model makeModel() {
			// TODO Auto-generated method stub
			return null;
		}

		
	}
	//private static Manager manager = new RecipeManager("recipe");
	
	
	
	/*
	
	private String description;
	private Author contributor;
	private Genre genre;
	private List<Direction> directions;
	private List<RecipeIngredientMeasure> ingredients;
	private Author author;
	private Date dateAuthored;
	private Money cost;
	*/
	
	
	public Double calculateCalories()
	{
		return null;//TODO impl
	}




	@Override
	public void preSave() {
		// TODO Auto-generated method stub
		
	}






	@Override
	public String getFormattedValue(Field f) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public String getIdentifyingValue() {
		// TODO Auto-generated method stub
		return null;
	}




	
}
