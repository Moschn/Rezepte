package ch.simonf.rezepte.activities.Recipes;

import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.recipe.Ingredient;
import ch.simonf.rezepte.recipe.Measurement;
import ch.simonf.rezepte.recipe.Recipe;
import ch.simonf.rezepte.utils.Globals;
import ch.simonf.rezepte.utils.JSONParser;
import ch.simonf.rezepte.utils.MySQL;

/* Status:
 * Recipe attributes are listed in a static manner
 * 
 * ToDo:
 * - tidy up code
 * - Context Menu for long press on ingredient & instruction ("Edit") ?
 * - recipe ratings
 */

public class ViewRecipeActivity extends Activity {

	// reference to mysql object
	MySQL mysql = Globals.mysql;
	
	TextView recipe_name;
	TextView recipe_servings;
	TextView recipe_prepare_time;
	TextView recipe_cooking_time;
	TextView recipe_ingredients;
	TextView recipe_measurement;
	TextView recipe_amount;

	String pid;
	int id;
	
	Recipe recipe;
	
	JSONObject product;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// getting product details from intent
		Intent i = getIntent();
		
		// get id for this specific recipe
		id = Integer.parseInt(i.getStringExtra("id"));
		recipe = mysql.recipes.get(id);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute(){
				// setup "please wait" dialog
				mysql.setupDialog(ViewRecipeActivity.this, "Lade " + mysql.recipes.get(id).name + "...");
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				// get recipe details
				mysql.getRecipeDetails(id);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void results)
			{
				// update recipe details view
				updateRecipeDetailsView();
				
				// dismiss the loading dialog
				mysql.dismissDialog();
			}
		}.execute();
	}
	
	// visualize recipe
	private void updateRecipeDetailsView()
	{
		setContentView(R.layout.view_recipe);
		recipe_name = (TextView) findViewById(R.id.view_recipe_name_label);
		recipe_servings = (TextView) findViewById(R.id.view_recipe_servings_label);
		recipe_prepare_time = (TextView) findViewById(R.id.view_recipe_prepare_time_label);
		recipe_cooking_time = (TextView) findViewById(R.id.view_recipe_cooking_time_label);
		
		recipe_name.setText(recipe.name);
		recipe_servings.setText(Integer.toString(recipe.servings));
		recipe_prepare_time.setText(Integer.toString(recipe.prepare_time));
		recipe_cooking_time.setText(Integer.toString(recipe.cooking_time));				
		
		TableLayout tl_ingr = (TableLayout) findViewById(R.id.view_recipe_ingredients_table);
		
		int horizontalPadding = 10;
		
		// generate one row for each ingredient
		for(int i = 0; i < recipe.arrangements.size(); i++) {
			
			float amount = recipe.arrangements.get(i).amount.amount;
			Measurement measurement = recipe.arrangements.get(i).measurement;
			Ingredient ingredient = recipe.arrangements.get(i).ingredient;
			String comment = recipe.arrangements.get(i).comment;
			
		    TableRow tr = new TableRow(ViewRecipeActivity.this);
		    tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		   
		    
		    // set amount
		    TextView col1 = new TextView(ViewRecipeActivity.this);
		    col1.setText(recipe.arrangements.get(i).amount.get_number());
		    col1.setPadding(0, 0, horizontalPadding, 0);
		    tr.addView(col1);

		    
		    // set measurment 
		    TextView col2 = new TextView(ViewRecipeActivity.this);
		    
		    if(measurement != null)
		    {
			    
			    // if string is not empty (abbr.isEmpty() requires API 9)
			    if(measurement.abbr.length() > 0)
			    	col2.setText(measurement.abbr);
			    
			    else
			    {
			    	if(amount == 1)
			    		col2.setText(measurement.name);
			    	
			    	else
			    		col2.setText(measurement.plural);					    
			    }
			    
		    }
		    col2.setPadding(0, 0, horizontalPadding, 0);
		    tr.addView(col2);
		    
		    // set Ingredient
		    TextView col3 = new TextView(ViewRecipeActivity.this);
		    if(amount == 1)
		    	col3.setText(ingredient.name);
		    
		    else
		    	col3.setText(ingredient.plural);
		    
		    col3.setPadding(0, 0, horizontalPadding, 0);
		    
		    tr.addView(col3);
		    
		    // set additional comment
		    TextView col4 = new TextView(ViewRecipeActivity.this);
		    col4.setText(comment);
		    
		    tl_ingr.addView(tr);// add rows to the table.
		}
		
		// get Instructions
		TableLayout tl_instr = (TableLayout) findViewById(R.id.view_recipe_instructions_table);
		
		// generate one row for each instruction step
		for(int i = 0; i < recipe.instructions.size(); i++) {
		    TableRow tr = new TableRow(ViewRecipeActivity.this);
		    tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		   
		    TextView col1 = new TextView(ViewRecipeActivity.this);
		    col1.setText(recipe.instructions.get(i));
		    tr.addView(col1);
		    
		    tl_instr.addView(tr);// add rows to the table.
		}
		
	}
}
	

	 /*// Background Async Task to  Save product Details
	class SaveProductDetails extends AsyncTask<String, String, String> {

		// Before starting background thread Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ViewRecipeActivity.this);
			pDialog.setMessage("Saving product ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		 // Saving product
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String name = recipe_name.getText().toString();
			//String price = txtPrice.getText().toString();
			//String description = txtDesc.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair(TAG_NAME, name));
			//params.add(new BasicNameValuePair(TAG_PRICE, price));
			//params.add(new BasicNameValuePair(TAG_DESCRIPTION, description));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}


	// Background Async Task to Delete Product
	class DeleteProduct extends AsyncTask<String, String, String> {

		// Before starting background thread Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ViewRecipeActivity.this);
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		// Deleting product
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pid", pid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}

	}*/

