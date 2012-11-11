package ch.simonf.rezepte.recipe;

import ch.simonf.rezepte.R;
import java.sql.Timestamp;

public class Measurement {
	private int id;
	
	public String name;
	public String abbr;
	public String plural;
	public float conversion;
	public Timestamp created_at;
	public Timestamp updated_at;
	
	
	public Measurement()
	{
		
	}
	
	public Measurement(int id)
	{
		this.id = id;
	}
	
	public int get_id()
	{
		return id;
	}
}
