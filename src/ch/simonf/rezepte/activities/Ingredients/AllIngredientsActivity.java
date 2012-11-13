package ch.simonf.rezepte.activities.Ingredients;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.activities.Recipes.ViewRecipeActivity;
import ch.simonf.rezepte.recipe.Ingredient;
import ch.simonf.rezepte.utils.Globals;
import ch.simonf.rezepte.utils.MySQL;


/* Status:
 * Ingredient listing work fine.
 * 
 * ToDo:
 * - Context Menu for long press on Ingredient ("Edit" & "Delete")
 * - make items selectable and user-attachable
 */

public class AllIngredientsActivity extends ListActivity  {
	
	// reference to mysql object
	MySQL mysql = Globals.mysql;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_recipes);
		
		ListView lv = getListView();
		
		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute(){
				// setup "please wait" dialog
				mysql.setupDialog(AllIngredientsActivity.this, "Lade Zutaten...");
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				// get all recipes
				mysql.getIngredients();
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
		
		lv.setOnCreateContextMenuListener
		(
		  new View.OnCreateContextMenuListener() 
		  {
		        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
		        {
		            AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		            View view = info.targetView;
		            
		        	String id = ((TextView) view.findViewById(R.id.pid)).getText().toString();
		        	String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
		        	AdapterContextMenuInfo mi =(AdapterContextMenuInfo) menuInfo;

		        	menu.add(0, 0, 0, "Bearbeiten");
		        	menu.add(0, 0, 0, "Löschen");
				}
		  }   
		);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tvId = (TextView) view.findViewById(R.id.pid);
				
				LinearLayout llContainer = (LinearLayout)view.findViewById(R.id.container);
				
				llContainer.setBackgroundColor(0xFFFF0000);
				
				String pid = tvId.getText().toString();
				
				
				
				Ingredient ingredient = mysql.ingredients.get(Integer.parseInt(pid));
				Globals.user.toggleIngredient(ingredient);
			}
		});
		
	}
	
	
	public void updateListView() 
	{
		// put mysql data into ListView
		ListAdapter adapter = new SimpleAdapter(
				AllIngredientsActivity.this, mysql.ingredients_to_hashmap(),
				R.layout.list_item, new String[] { "id",
						"name"},
				new int[] { R.id.pid, R.id.name });
		// updating listview
		setListAdapter(adapter);
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
