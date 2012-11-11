package ch.simonf.rezepte.utils;

import java.util.LinkedList;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import ch.simonf.rezepte.R;

public class AsyncQueue {
	LinkedList<AsyncTask<Void, Void, Void>> queue = new LinkedList<AsyncTask<Void, Void, Void>>();
	
	// executes the first task of the list
	@TargetApi(9)
	public void next()
	{
		if(!queue.isEmpty())
			queue.pop().execute();
	}
	
	// adds a task to the queue
	public void add(AsyncTask<Void, Void, Void> task)
	{
		queue.add(task);
	}
}
