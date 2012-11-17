package ch.simonf.rezepte.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialog {
	
	// Progress Dialog
	private ProgressDialog progressDialog;
	private Context context;
	
	
	public void show1ButtonDialog(String title, String message, String button) {
	    new AlertDialog.Builder(context)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setTitle(title)
	    .setMessage(message)
	    .setPositiveButton(button, null)
	    .show();
	}
	
	public void show2ButtonDialog(String message, String button1, String button2)
	{
	    new AlertDialog.Builder(context)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setTitle("Titel")
	    .setMessage("möchten Sie wirklich beenden?")
	    .setPositiveButton("ja", new DialogInterface.OnClickListener() {
	
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	  
	        }
	
	    })
	    .setNegativeButton("nein", null)
	    .show();
	}
	
	
	public void showProgressDialog(String message)
	{
		showProgressDialog(this.context, message);
	}
	
	public void showProgressDialog(Context context, String message)
	{
		// setup dialog
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

	}
	
	public void dismissProgressDialog() 
	{
		// dismisses the current dialog
		progressDialog.dismiss();
	}
	
	public void set_context(Context context)
	{
		this.context = context;
	}

}
