package ch.simonf.rezepte.recipe;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Recipe{
	final int ID;
	
	public String name;
	public int servings;
	public int prepare_time;
	public int cooking_time;
	public ArrayList<Ingredient> ingredients;
	public ArrayList<String> instructions;
	public ArrayList<Arrangement> arrangements;
	public int created_by;
	public int updated_by;
	public Timestamp created_at;
	public Timestamp updated_at;
	
	public Recipe(int id, String name)
	{
		this.ID = id;
		this.name = name;
	}
	
	public void getIngredients()
	{
		
	}
}
