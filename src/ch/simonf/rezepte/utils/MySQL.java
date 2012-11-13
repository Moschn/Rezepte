package ch.simonf.rezepte.utils;

import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.activities.User;
import ch.simonf.rezepte.activities.Ingredients.AllIngredientsActivity;
import ch.simonf.rezepte.recipe.Arrangement;
import ch.simonf.rezepte.recipe.Ingredient;
import ch.simonf.rezepte.recipe.Measurement;
import ch.simonf.rezepte.recipe.Recipe;

public class MySQL {
	
	public SparseArray<Recipe> recipes = new SparseArray<Recipe>();
	public SparseArray<Ingredient> ingredients = new SparseArray<Ingredient>();
	public SparseArray<Measurement> measurements = new SparseArray<Measurement>();
	
	public User user;
	
	private Context context;
	private Handler handler;
	
	private static final String url_folder = "http://lv-studios.ch/projects/android_connect/";
	
	// url to get all products list
	String url_all_recipes = "http://lv-studios.ch/projects/android_connect/get_all_recipes.php";
	// single product url
	private static final String url_recipe_detials = "http://lv-studios.ch/projects/android_connect/get_recipe_details.php";
	// url to update product
	private static final String url_update_product = "http://lv-studios.ch/projects/android_connect/update_product.php";	
	// url to delete product
	private static final String url_delete_product = "http://lv-studios.ch/projects/android_connect/delete_product.php";	
	private static final String url_ingredient_details = "http://lv-studios.ch/projects/android_connect/get_ingredient_details.php";
	private static final String url_measurement_details = "http://lv-studios.ch/projects/android_connect/get_measurement_details.php";
	
	private static final String url_get_all_columns = url_folder + "select.php";
	private static final String url_insert = url_folder + "insert.php";
	
	private static final String TAG_SUCCESS = "success";
	
	// Progress Dialog
	public ProgressDialog pDialog;
	
	public void setupDialog(Context ctx, String message)
	{
		// setup dialog
		pDialog = new ProgressDialog(ctx);
		pDialog.setMessage(message);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();

	}
	
	public void dismissDialog() 
	{
		// dismisses the current dialog
		pDialog.dismiss();
	}

	public void cacheRecipes(Context ctx)
	{
		String FILENAME = "hello_file";
		String string = "hello world!";
		FileOutputStream fos;
		
		try {
			   fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			   fos.write(string.getBytes());
			   fos.close();
			}
			catch(Exception e) {
			      // do something
			}
	}
	
	public void getRecipes () {
    	
    	// Creating JSON Parser object
		JSONParser jParser = new JSONParser();

		// JSON Node names
		final String TAG_SUCCESS = "success";
		final String TAG_ITEM = "recipes";
		final String TAG_PID = "id";
		final String TAG_NAME = "name";

		// products JSONArray
		JSONArray items = null;
			
		
		// Building Parameters
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_all_recipes, "GET", list);
		
		// Check your log cat for JSON reponse
		Log.d("All Recipes: ", json.toString());

		try {
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				// products found
				// Getting Array of Products
				items = json.getJSONArray(TAG_ITEM);

				// looping through All Products
				for (int i = 0; i < items.length(); i++) {
					JSONObject c = items.getJSONObject(i);

					// Storing each json item in variable
					String id = c.getString(TAG_PID);
					String name = c.getString(TAG_NAME);

					recipes.put(Integer.parseInt(id), new Recipe(Integer.parseInt(id),name));
				}
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}     
	
	public void getIngredients()
	{
		JSONArray items = getAllColumns("ingredients", null, null);
		try {
			getIngredientDetails(items);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getRecipeDetails(int id) 
	{
    	
		//Integer id = params[0];
		Recipe recipe = recipes.get(id);
		JSONObject jRecipe;

		
		// Creating JSON Parser object
		JSONParser jParser = new JSONParser();
		
		int success;
		
		try {
			// Building Parameters
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("id", Integer.toString(id)));

			// getting product details by making HTTP request
			// Note that product details url will use GET request
			JSONObject json = jParser.makeHttpRequest(url_recipe_detials, "GET", list);

			// check your log for json response
			Log.d("Single Product Details", json.toString());
			
			// json success tag
			success = json.getInt(TAG_SUCCESS);
			if (success == 1) {
				// successfully received product details
				JSONArray productObj = json.getJSONArray("recipe"); // JSON Array
				
				// get first product object from JSON Array
				jRecipe = productObj.getJSONObject(0);
				
				recipe.servings = Integer.parseInt(jRecipe.getString("servings"));
				recipe.prepare_time = Integer.parseInt(jRecipe.getString("prepare_time"));
				recipe.cooking_time = Integer.parseInt(jRecipe.getString("cooking_time"));
				recipe.created_by = Integer.parseInt(jRecipe.getString("created_by"));
				recipe.updated_by = Integer.parseInt(jRecipe.getString("updated_by"));
				recipe.created_at = Timestamp.valueOf(jRecipe.getString("created_at"));
				recipe.updated_at = Timestamp.valueOf(jRecipe.getString("updated_at"));
				
				recipe.instructions = parseInstructions(jRecipe.getString("instructions"));
				
				// parse ingredient string into an ArrayList:
				//e.g. ingredients[i] = {amount_i, measurment_i, ingredient_i} 
				recipe.arrangements = parseArrangement(jRecipe.getString("ingredients"));
				
				getIngredientDetails(recipe);
				getMeasurementDetails(recipe);
				
				for(int i = 0; i < 0/*recipe.arrangements.size()*/; i++)
				{
					// never get details for an instance that is already complete
					// isEmpty() requires at least API 9
					if((recipe.arrangements.get(i).ingredient.name) == null)
						getIngredientDetails(recipe);
					
					if((recipe.arrangements.get(i).measurement.name) == null)
						getMeasurementDetails(recipe);
				}
				
				
				//product = productObj.getJSONObject(0);

			}else{
				// product with pid not found
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}			
	}
	
	public void createUser(String id)
	{
		insert("users", "facebookId", id);
	}
	
	public boolean getUserDetails()
	{
		JSONArray response = getAllColumns("users", "facebookId", "("+user.get_id()+")");
		JSONObject jUser = null;
		try {
			jUser = response.getJSONObject(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(jUser == null)
			return false;
					
		else
		{
			// TODO Add details to user instance
			return true;
		}
	}
	
	private void insert(String table, String names, String values)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("table", table));
		params.add(new BasicNameValuePair("names", names));
		params.add(new BasicNameValuePair("values", values));
		
		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.makeHttpRequest(url_insert, "POST", params);
		
		// check log cat fro response
		Log.d("Create User: ", json.toString());

		// check for success tag
		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				// successfully created user
			} else {
				// failed to create product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private JSONArray getAllColumns(String table, String key, String needle)
	{
		// the query will look like this: "SELECT * FROM table WHERE key IN needle;"
		
		if(key == null)
			key = "";
		if(needle == null)
			needle = "";
		
		// Creating JSON Parser object
		JSONParser jParser = new JSONParser();
	
		// products JSONArray
		JSONArray items = null;
			
		
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("table", table));
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("needle", needle));
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_get_all_columns, "GET", params);
		
		// Check your log cat for JSON reponse
		Log.d("All columns: ", json.toString());
	
		try {
			// Checking for SUCCESS TAG
			int success = json.getInt("success");
	
			if (success == 1) {
				// products found
				// Getting Array of Products
				items = json.getJSONArray("values");
	
				return items;
			} else {
	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<Arrangement> parseArrangement(String values)
	{
		ArrayList<Arrangement> arrangements = new ArrayList<Arrangement>();
		// e.g. "2<val>4<val>1<arr>4<val>5<val>2<arr>..." becomes "2<val>4<val>1","4<val>5<val>2",...
		String[] temp = values.split("<arr>");
		for(int i = 0; i < temp.length; i++)
		{
			
			ArrayList<String> tempSubList = new ArrayList<String>();
			
			// e.g. "2<val>4<val>1","4<val>5<val>2",... becomes ["2","4","1"],["4","5","2"],...
			String[] tempSubArray = temp[i].split("<val>");
			
			// copy String array over to ArrayList
			for(int k = 0; k < tempSubArray.length; k++)
			{
				tempSubList.add(tempSubArray[k]);
			}
			
			
			int ingredient_id = Integer.parseInt(tempSubList.get(2));
			int measurement_id = Integer.parseInt(tempSubList.get(1));
			
			Ingredient ingredient = new Ingredient(ingredient_id);
			Measurement measurement = new Measurement(measurement_id);
			String comment = null;
			if(tempSubList.size() >= 4)
				comment = tempSubList.get(3);
			
			// making sure not to have several ingredients/measurements with the same id
			for(int k = 0; k < arrangements.size(); k++)
			{
				if(arrangements.get(k).ingredient.get_id() == ingredient_id)
					ingredient = arrangements.get(k).ingredient;
				
				if(arrangements.get(k).measurement.get_id() == measurement_id)
					measurement = arrangements.get(k).measurement;
			}
			
			arrangements.add(new Arrangement(								
					Float.parseFloat(tempSubList.get(0)),	// 4
					measurement,							// teaspoons
					ingredient,								// sugar
					comment									// comment
					));
		}
		return arrangements;
	}
	
	private void getIngredientDetails(JSONArray items) throws JSONException{
		
		// looping through All Products
		for (int i = 0; i < items.length(); i++) {
			JSONObject jIngredient = items.getJSONObject(i);
			
			// set ingredient id through the constructor
			Ingredient ingredient = new Ingredient(Integer.parseInt(jIngredient.getString("id")));
			
			transferIngredients(ingredient, jIngredient);
			
			this.ingredients.put(ingredient.get_id(), ingredient);
		}
	}

	private void getIngredientDetails(Recipe recipe) throws JSONException
	{
		// will contain the already listed ids
		ArrayList<String> ids = new ArrayList<String>();
		
		// preparing ids in the form of "(2,6,24,3,...)"
		String idsString = "(";
		
		for(int i = 0; i < recipe.arrangements.size(); i++)
		{	
			String id = Integer.toString(recipe.arrangements.get(i).ingredient.get_id());
			
			// never get details for an instance that is already complete
			if(!ids.contains(id))
			{
				ids.add(id);
				idsString += id;
				if(i < (recipe.arrangements.size() - 1))
					idsString += ",";
			}
		}
		
		idsString += ")";
		
		JSONArray items = getAllColumns("ingredients","id",idsString);
		
		// looping through All Products
		for (int i = 0; i < items.length(); i++) {
			JSONObject jIngredient = items.getJSONObject(i);
			
			// Storing each json item in variable
			Ingredient ingredient = getIngredientById(recipe, Integer.parseInt(jIngredient.getString("id")));
			
			transferIngredients(ingredient, jIngredient);
			
			this.ingredients.put(ingredient.get_id(), ingredient);
		}
	}
	
	private void getIngredientDetails(ArrayList<Ingredient> ingredient)
	{
		
	}

	private Ingredient getIngredientById(Recipe recipe, int id) {
		for(int i = 0; i < recipe.arrangements.size(); i++)
		{
			if(recipe.arrangements.get(i).ingredient.get_id() == id)
				return recipe.arrangements.get(i).ingredient;
		}
		return null;
	}

	// transfers ingredients from a JSONObject to the already existing ingredient instance
	private void transferIngredients(Ingredient ingredient, JSONObject jIngredient) throws JSONException {
		ingredient.name = jIngredient.getString("name");
		ingredient.plural = jIngredient.getString("plural");
		ingredient.created_at = Timestamp.valueOf(jIngredient.getString("created_at"));
		ingredient.updated_at = Timestamp.valueOf(jIngredient.getString("updated_at"));
		
		if(jIngredient.getString("parent") != "0")
			ingredient.parent = new Ingredient(Integer.parseInt(jIngredient.getString("parent")));
	}

	private void getMeasurementDetails(Recipe recipe) throws JSONException
	{
		// will contain the already listed ids
		ArrayList<String> ids = new ArrayList<String>();
		
		// preparing ids in the form of "(2,6,24,3,...)"
		String idsString = "(";
		
		for(int i = 0; i < recipe.arrangements.size(); i++)
		{	
			String id = Integer.toString(recipe.arrangements.get(i).measurement.get_id());
			
			// never get details for an instance that is already complete
			if(!ids.contains(id))
			{
				// dont add a "," the first time
				if(idsString != "(")
					idsString += ",";
				ids.add(id);
				idsString += id;
			}
			
			// remove instance if id = 0
			if(recipe.arrangements.get(i).measurement.get_id() == 0)
				recipe.arrangements.get(i).measurement = null;
		}
		
		idsString += ")";
		
		JSONArray items = getAllColumns("measurements","id",idsString);
		
		// looping through All Products
		for (int i = 0; i < items.length(); i++) {
			JSONObject jMeasurement = items.getJSONObject(i);
			
			// Storing each json item in variable
			Measurement measurement = getMeasurementById(recipe, Integer.parseInt(jMeasurement.getString("id")));
			
			transferMeasurements(measurement, jMeasurement);		
		}
	}

	private Measurement getMeasurementById(Recipe recipe, int id) {
		for(int i = 0; i < recipe.arrangements.size(); i++)
		{
			if(recipe.arrangements.get(i).measurement.get_id() == id)
				return recipe.arrangements.get(i).measurement;
		}
		return null;
	}

	private void transferMeasurements(Measurement measurement, JSONObject jMeasurement) throws JSONException {
		measurement.name = jMeasurement.getString("name");
		measurement.plural = jMeasurement.getString("plural");
		measurement.abbr = jMeasurement.getString("abbr");
		measurement.conversion = Float.parseFloat(jMeasurement.getString("conversion"));
		measurement.created_at = Timestamp.valueOf(jMeasurement.getString("created_at"));
		measurement.updated_at = Timestamp.valueOf(jMeasurement.getString("updated_at"));
	}

	private ArrayList<String> parseInstructions(String value)
	{
		ArrayList<String> instructions = new ArrayList<String>();
		String[] temp = value.split("<step>");
		
		// move the values from array to ArraList
		for(int i = 0; i < temp.length; i++)
		{
			instructions.add(temp[i]);
		}
		return instructions;
	}
	
	// getter & setter
	
	public void set_context(Context context)
	{
		this.context = context;
	}
	
	public Context get_context()
	{
		return context;
	}
	
	public void set_handler(Handler handler)
	{
		this.handler = handler;
	}

	
	public ArrayList<HashMap<String, String>> recipes_to_hashmap()
	{
		ArrayList<HashMap<String, String>> returnValue = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> temp;
		for(int i = 0; i < recipes.size(); i++)
		{
			temp = new HashMap<String, String>();
			temp.put("id", Integer.toString(recipes.keyAt(i)));
            temp.put("name", recipes.valueAt(i).name);
			//temp.put(Integer.toString(recipes.keyAt(i)),recipes.valueAt(i).name);
			returnValue.add(temp);
		}
		
		return returnValue;
	}
	
	public ArrayList<HashMap<String, String>> ingredients_to_hashmap()
	{
		ArrayList<HashMap<String, String>> returnValue = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> temp;
		for(int i = 0; i < ingredients.size(); i++)
		{
			temp = new HashMap<String, String>();
			temp.put("id", Integer.toString(ingredients.keyAt(i)));
            temp.put("name", ingredients.valueAt(i).plural);
			//temp.put(Integer.toString(recipes.keyAt(i)),recipes.valueAt(i).name);
			returnValue.add(temp);
		}
		
		return returnValue;
	}
}
