package ch.simonf.rezepte.recipe;

import java.sql.Timestamp;
import java.util.ArrayList;

import android.util.SparseArray;

public class Recipe{
	private final int id;
	
	public String name;
	public int servings;
	public int prepare_time;
	public int cooking_time;
	public ArrayList<String> instructions;
	public ArrayList<Arrangement> arrangements;
	public int created_by;
	public int updated_by;
	public Timestamp created_at;
	public Timestamp updated_at;
	public float match;
	
	public Recipe(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public SparseArray<Ingredient> getIngredients()
	{
		if(arrangements.size() == 0)
			return null;
		
		SparseArray<Ingredient> ingredients = new SparseArray<Ingredient>();
		for(int i = 0; i < arrangements.size(); i++)
		{
			Ingredient ingredient = arrangements.get(i).ingredient;
			ingredients.put(ingredient.get_id(), ingredient);
		}
		return ingredients;
	}
	
	public int get_id()
	{
		return id;
	}
	
	public String toString()
	{
		return name;
	}
	
}
