package ch.simonf.rezepte.recipe;

import ch.simonf.rezepte.R;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

public class Ingredient {
	private int id;
	
	public String name;
	public String plural;
	public Boolean spice;
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
	
	public ArrayList<BasicNameValuePair> toBasicNameValuePair()
	{
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if(name != null)
			params.add(new BasicNameValuePair("name", name));
		
		if(plural != null)
			params.add(new BasicNameValuePair("plural", plural));
		
		if(spice != null)
			params.add(new BasicNameValuePair("spice", Integer.toString((spice? 1 : 0))));
		
		if(parent != null)
			params.add(new BasicNameValuePair("parent", Integer.toString(parent.get_id())));
		
		if(created_at != null)
			params.add(new BasicNameValuePair("created_at", Long.toString(created_at.getTime())));
		
		if(updated_at != null)
			params.add(new BasicNameValuePair("updated_at", Long.toString(updated_at.getTime())));
		
		return params;
	}
	
	public String toString()
	{
		return plural;
	}
}
