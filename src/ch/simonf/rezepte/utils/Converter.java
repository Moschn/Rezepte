package ch.simonf.rezepte.utils;

import java.util.ArrayList;

import android.util.SparseArray;

// converts all kinds of stuff
public final class Converter<T> {
	
	public final static ArrayList SparseArray_to_ArrayList(SparseArray sparseArray)
	{
		ArrayList arrayList = new ArrayList();
		for(int i = 0; i < sparseArray.size(); i++)
		{
			arrayList.add(sparseArray.valueAt(i));
		}
		return arrayList;
	}
	
}
