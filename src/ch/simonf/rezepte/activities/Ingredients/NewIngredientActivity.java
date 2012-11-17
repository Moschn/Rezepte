package ch.simonf.rezepte.activities.Ingredients;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.recipe.Ingredient;
import ch.simonf.rezepte.utils.Dialog;
import ch.simonf.rezepte.utils.Globals;
import ch.simonf.rezepte.utils.MySQL;

/* Status:
 * user can add new ingredients with the basic parameters
 * Name, Plural and Spice.
 * quality control: user gets a "ingredient already exists" dialog
 * 
 * ToDo:
 * - quality control of new ingredients (rating system?)
 */

public class NewIngredientActivity extends Activity {
	MySQL mysql = Globals.mysql;
	Dialog dialog = Globals.dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_ingredient);
		
		dialog.set_context(NewIngredientActivity.this);

		Button btCreate = (Button) findViewById(R.id.button_create);

		btCreate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				new CreateIngredient().execute();
			}
		});
	}

	class CreateIngredient extends AsyncTask<Void, Void, Boolean> {

		EditText etName = (EditText) findViewById(R.id.input_name);
		EditText etPlural = (EditText) findViewById(R.id.input_plural);
		CheckBox cbSpice = (CheckBox) findViewById(R.id.checkbox_spice);
		
		@Override
		protected void onPreExecute() {
			dialog.showProgressDialog("Zutat wird hinzugefügt");
		}

		protected Boolean doInBackground(Void... args) {
			
			Ingredient ingredient = new Ingredient();
			ingredient.name = etName.getText().toString();
			ingredient.plural = etPlural.getText().toString();
			ingredient.spice = cbSpice.isChecked();
			
			return mysql.createIngredient(ingredient);
			
		}

		protected void onPostExecute(Boolean result) 
		{
			dialog.dismissProgressDialog();
			if(!result)
				dialog.show1ButtonDialog("Fehler", "Diese Zutat existiert bereits!", "OK");	
		}

	}
}
