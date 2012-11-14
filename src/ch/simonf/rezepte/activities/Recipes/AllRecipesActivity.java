package ch.simonf.rezepte.activities.Recipes;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.recipe.Ingredient;
import ch.simonf.rezepte.recipe.Recipe;
import ch.simonf.rezepte.utils.AsyncQueue;
import ch.simonf.rezepte.utils.Converter;
import ch.simonf.rezepte.utils.Globals;
import ch.simonf.rezepte.utils.MySQL;
import ch.simonf.rezepte.utils.RecipesAdapter;

/* Status:
 * static listing of all recipes
 * 
 * ToDo:
 * - Context Menu for long press on recipes ("Edit") ?
 * - "add recipe to favorites" function?
 */

public class AllRecipesActivity extends ListActivity {
	
	// reference to mysql object
	MySQL mysql = Globals.mysql;
	RecipesAdapter recipesAdapter;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_recipes_list);
		
		ListView lv = getListView();
		
		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute(){
				// setup "please wait" dialog
				mysql.setupDialog(AllRecipesActivity.this, "Lade Rezepte...");
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				// get all recipes
				mysql.getRecipes();
				return null;
			}
			
			protected void onPostExecute(Void results)
			{
				//
				updateListView();
				// dismiss the loading dialog
				mysql.dismissDialog();
			}
		}.execute();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String pid = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						ViewRecipeActivity.class);
				// sending pid to next activity
				in.putExtra("id", pid);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});
		
	}
	
	
	public void updateListView() 
	{
		// put mysql data into ListView
		ListAdapter adapter = new SimpleAdapter(
				AllRecipesActivity.this, mysql.recipes_to_hashmap(),
				R.layout.all_recipes_row, new String[] { "id",
						"name"},
				new int[] { R.id.pid, R.id.name });
		// updating listview
		
		// convert SparseArray recipes to ArrayList in order to pass it along our Adapter
		ArrayList<Recipe> recipesList = Converter.SparseArray_to_ArrayList(mysql.recipes);
		
		RecipesAdapter recipesAdapter = new RecipesAdapter(this, R.layout.all_recipes_row, recipesList);
		
		setListAdapter(recipesAdapter);
	}
	

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}
}