package ch.simonf.rezepte.recipe;

import ch.simonf.rezepte.R;

public class Amount {
	public float amount;
	private String number;
	
	public Amount(float amount)
	{
		set_number(amount);
	}
	
	public void set_number(float amount)
	{
		// e.g. 3.5 to "3"
		if(((int) amount) != 0)
		{
			number = Integer.toString((int) amount);
			number += " ";
		}
		else
			number = "";
		
		float frac = amount - ((int) amount);
		
		// e.g. 0.5 to "1/2" => 3.5 to "3 1/2"
		if(frac == 0.25)
			number += "1/4";
		
		if(frac == 0.5)
			number += "1/2";
		
		if(frac == 0.75)
			number += "3/4";
	}
	
	public String get_number()
	{
		return number;
	}
}
