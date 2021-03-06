package ch.simonf.rezepte.activities;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import ch.simonf.rezepte.recipe.Ingredient;
import ch.simonf.rezepte.recipe.Recipe;
import ch.simonf.rezepte.utils.Globals;

public class User {
	private String id;
	public SparseArray<Ingredient> ingredients;
	
	User(String id)
	{
		ingredients = new SparseArray<Ingredient>();
		this.id = id;
		pullIngredients();
	}
	
	public String get_id()
	{
		return id;
	}
	
	public boolean inExistence ()
	{
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				
				try {
					return Boolean.valueOf(Globals.mysql.getUserDetails());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Boolean results)
			{
				// if this user does not exist yet
				// add the id to our DB
				if(!results)
					createUser();
			}
			
		}.execute();
		return true;
	}
	
	private void createUser()
	{
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				Globals.mysql.createUser(id);
				return null;
			}
			
		}.execute();
	}
	
	public void addIngredient(JSONArray ids) throws NumberFormatException, JSONException
	{
		for(int i = 0; i < ids.length(); i++)
		{
			int key = Integer.parseInt(ids.getString(i));
			Ingredient mIngredient = Globals.mysql.ingredients.get(key);
			if(mIngredient != null)
				ingredients.append(mIngredient.get_id(), mIngredient);
		}
	}
	
	public void toggleIngredient(Ingredient ingredient, Context context)
	{

	}
	
	public void toggleIngredient(Ingredient ingredient)
	{
		// add ingredient if not already connected to user
		if(ingredients.get(ingredient.get_id()) == null)
			ingredients.append(ingredient.get_id(), ingredient);
		
		// remove otherwise
		else
			ingredients.remove(ingredient.get_id());
			
		pushIngredients();
		
	}
	
	private void pushIngredients()
	{
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Globals.mysql.updateUserDetails();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute();

	}
	
	private void pullIngredients()
	{
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					Globals.mysql.getUserDetails();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute();

	}
	
	public float getRecipeMatch(Recipe recipe)
	{
		if(this.ingredients.size() == 0)
			return 0;
		
		SparseArray<Ingredient> recipeIngredients = recipe.getIngredients();
		int total = recipeIngredients.size();
		int mutual = 0;
		
		for(int i = 0; i < total; i++)
		{
			int key = recipeIngredients.valueAt(i).get_id();
			
			if(this.ingredients.get(key) != null)
				mutual++;
		}
		
		float percentage = ((float) mutual) / ((float) total) * 100;
		
		// save the current match into the particular recipe instance
		recipe.match = percentage;
		
		return (float) percentage;
	}
}
