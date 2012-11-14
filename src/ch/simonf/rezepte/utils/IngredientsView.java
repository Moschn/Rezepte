package ch.simonf.rezepte.utils;

import java.util.ArrayList;

import ch.simonf.rezepte.R;
import ch.simonf.rezepte.recipe.Ingredient;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class IngredientsView extends ListActivity{
    
    private ProgressDialog mProgressDialog = null; 
    private ArrayList<Ingredient> mOrders = null;
    private IngredientsAdapter mAdapter;
    private Runnable viewOrders;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_ingredients_list);
    }
}