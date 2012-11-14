package ch.simonf.rezepte.activities;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import ch.simonf.rezepte.R;
import ch.simonf.rezepte.activities.Ingredients.AllIngredientsActivity;
import ch.simonf.rezepte.activities.Ingredients.NewIngredientActivity;
import ch.simonf.rezepte.activities.Recipes.AllRecipesActivity;
import ch.simonf.rezepte.utils.AsyncQueue;
import ch.simonf.rezepte.utils.Globals;
import ch.simonf.rezepte.utils.MySQL;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;


public class MainScreenActivity extends Activity{
	
	Button btnViewProducts;
	Button btnNewProduct;
	Button btnShowRecipes;
	
	private MySQL mysql;
	
	//----Facebook----//
    Facebook facebook = new Facebook("217995184999450");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    private SharedPreferences mPrefs;
    //----------------//
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// remove top title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main_screen);
		
		Globals.mysql = new MySQL();
		//Globals.user = new User();
		this.mysql = Globals.mysql;
		//mysql.user = Globals.user;
		
		// not used at the moment
		Globals.asyncQueue = new AsyncQueue();
		
		// Buttons
		btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
		btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
		btnShowRecipes = (Button) findViewById(R.id.btnShowRecipes);
		
		// view ingredients click event
		btnViewProducts.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), AllIngredientsActivity.class);
				startActivity(i);
				
			}
		});
		
		// create product click event
		btnNewProduct.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(), NewIngredientActivity.class);
				startActivity(i);
				
			}
		});
		
		// show recipes click event
		btnShowRecipes.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				// Launching show recipes activity
				Intent i = new Intent(getApplicationContext(), AllRecipesActivity.class);
				startActivity(i);
			}
			
		});
		
		
		final AsyncTask<Void, Void, Void> fetchFacebookInfo = new AsyncTask<Void, Void, Void>() {

	        String jsonUser;
	        JSONObject obj;
	        
			protected void onPreExecute(){
				mysql.setupDialog(MainScreenActivity.this, "Login...");
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					jsonUser = facebook.request("me");
					obj = Util.parseJson(jsonUser);
					
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FacebookError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			protected void onPostExecute(Void results)
			{
				String facebookId = obj.optString("id");
		        String name = obj.optString("name");
		        
		        //create new User instance with retrieved facebook id
		        Globals.user = new User(facebookId);
		        mysql.user = Globals.user;
		        
		        // check if user already exists
		        Globals.user.inExistence();
		        
		        TextView welcomeMessage = (TextView) findViewById(R.id.main_screen_welcome_message);
		        welcomeMessage.setText("Willkommen " + name);
		        
		        mysql.dismissDialog();
			}
		};
		
		
		
		//-------------Facebook---------------//
		
        /*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
		
        
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] {}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                    fetchFacebookInfo.execute();
                }
    
                @Override
                public void onFacebookError(FacebookError error) {}
    
                @Override
                public void onError(DialogError e) {}
    
                @Override
                public void onCancel() {}
            });
        }
        else
        	fetchFacebookInfo.execute();
        
        
		
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
