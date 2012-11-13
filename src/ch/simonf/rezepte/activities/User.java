package ch.simonf.rezepte.activities;

import android.os.AsyncTask;
import ch.simonf.rezepte.utils.Globals;
import ch.simonf.rezepte.utils.MySQL;

public class User {
	private String id;
	
	User(String id)
	{
		this.id = id;
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
				
				return Boolean.valueOf(Globals.mysql.getUserDetails());
			}
			protected void onPostExecute(Boolean results)
			{
				// if this user is currently not in existence
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
			
			protected void onPostExecute(Boolean results)
			{
			}
		
		}.execute();
	}
}
