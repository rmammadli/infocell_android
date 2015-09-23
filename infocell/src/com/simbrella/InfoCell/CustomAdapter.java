package com.simbrella.InfoCell;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<CharSequence> {

	Context context;
	int layoutResourceId;
	List<CharSequence> data = null;
	Typeface tf;
	LayoutInflater lif = null;

	public CustomAdapter(Context context, int layoutResourceId,
			List<CharSequence> adapterList, String FONT) {
		super(context, layoutResourceId, adapterList);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = adapterList;
		 tf = Typeface.createFromAsset(context.getAssets(),"ARIAL.TTF" );
		

		lif = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		if (convertView == null)
			vi = lif.inflate(layoutResourceId, null);

		Object obj = data.get(position);

		TextView tv = (TextView) vi;
		tv.setTypeface(tf);
		tv.setText(obj.toString()); // use whatever method you want for the
									// label
									// tv.setTypeface(tf);

		return tv;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = lif.inflate(R.layout.slide_menu_list_item, null);

		Object obj = data.get(position);

		TextView tv = (TextView) vi;
		tv.setTypeface(tf);
		tv.setText(obj.toString()); // use whatever method you want for the
									// label
									// tv.setTypeface(tf);
		tv.setPadding(5, 20, 5, 20);

		return tv;
	}
}
