package ch.simonf.rezepte.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.recipe.Recipe;

public class RecipesAdapter extends ArrayAdapter<Recipe> 
{

	private ArrayList<Recipe> recipes;
	
	public RecipesAdapter(Context context, int textViewResourceId, ArrayList<Recipe> recipes) {
		super(context, textViewResourceId, recipes);
		this.recipes = recipes;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (v == null) {
			v = inflater.inflate(R.layout.all_recipes_row, null);
		}


		// i refers to the current Recipe instance
		Recipe i = recipes.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView tvName = (TextView) v.findViewById(R.id.name);
			TextView tvId = (TextView) v.findViewById(R.id.pid);
			TextView tvMatch = (TextView) v.findViewById(R.id.match);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (tvName != null){
				tvName.setText(i.name);
			}
			
			if (tvId != null){
				tvId.setText(Integer.toString(i.get_id()));
			}
			
			if (tvMatch != null){
				int match = (int)Globals.user.getRecipeMatch(i);
				tvMatch.setText(Integer.toString(match) + "%");
			}
			
			

		}

		// the view must be returned to our activity
		return v;
	}
}