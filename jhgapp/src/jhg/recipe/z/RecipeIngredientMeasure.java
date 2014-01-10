package jhg.recipe.z;

import jhg.recipe.Recipe;

@SuppressWarnings("unused")
public class RecipeIngredientMeasure {//extends Model {

	private Recipe recipe;
	private Ingredient ingredient;
	private UnitOfMeasure uom;
	private Double amount;
	
	private Double calculateCalories()
	{
		return null;//TODO impl.
	}
}
