package ch.simonf.rezepte.recipe;

import ch.simonf.rezepte.R;

public class Arrangement {
	public Ingredient ingredient;
	public Measurement measurement;
	public Amount amount;
	public String comment;
	
	public Arrangement()
	{
		
	}
	
	// simulates a constructor with default parameter: comment = null
	public Arrangement(float amount, Measurement measurement, Ingredient ingredient)
	{
		this(amount, measurement, ingredient, null);
	}
	
	public Arrangement(float amount, Measurement measurement, Ingredient ingredient, String comment)
	{
		this.ingredient = ingredient;
		this.measurement = measurement;
		this.amount = new Amount(amount);
		this.comment = comment;
	}
}
