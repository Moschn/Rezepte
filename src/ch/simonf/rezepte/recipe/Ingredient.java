package ch.simonf.rezepte.recipe;

import ch.simonf.rezepte.R;
import java.sql.Timestamp;

public class Ingredient {
	private int id;
	
	public String name;
	public String plural;
	public Ingredient parent;
	public Timestamp created_at;
	public Timestamp updated_at;
	
	public Ingredient()
	{
		
	}
	
	public Ingredient(int id)
	{
		this.id = id;
	}
	
	public int get_id()
	{
		return id;
	}
}
