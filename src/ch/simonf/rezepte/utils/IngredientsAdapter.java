package ch.simonf.rezepte.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.recipe.Ingredient;

public class IngredientsAdapter extends ArrayAdapter<Ingredient> 
{

	private ArrayList<Ingredient> ingredients;
	
	public IngredientsAdapter(Context context, int textViewResourceId, ArrayList<Ingredient> ingredients) {
		super(context, textViewResourceId, ingredients);
		this.ingredients = ingredients;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (v == null) {
			v = inflater.inflate(R.layout.all_ingredients_row, null);
		}


		// i refers to the current Ingredient instance
		Ingredient i = ingredients.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView tvName = (TextView) v.findViewById(R.id.name);
			TextView tvId = (TextView) v.findViewById(R.id.pid);
			LinearLayout llContainer = (LinearLayout) v.findViewById(R.id.container);
			CheckBox cbSelected = (CheckBox) v.findViewById(R.id.checkBox);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (tvName != null){
				tvName.setText(i.plural);
			}
			if (tvId != null){
				tvId.setText(Integer.toString(i.get_id()));
			}
			
			
			int id = i.get_id();
			// change the view attributes of this row, if user instance contains ingredient
			if(Globals.user.ingredients.get(id) != null)
			{
				cbSelected.setChecked(true);
			}
			else
			{
				cbSelected.setChecked(false);
			}

		}

		// the view must be returned to our activity
		return v;
	}
}