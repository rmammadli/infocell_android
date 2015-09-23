package com.simbrella.InfoCell;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<CharSequence> {
	private final Context context;
	List<CharSequence> data = null;

	public MyArrayAdapter(Context context, List<CharSequence> adapterList) {
		super(context, R.layout.slide_menu_list_item, adapterList);
		this.context = context;
		this.data = adapterList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.slide_menu_list_item, parent,
				false);
		TextView textView = (TextView) rowView.findViewById(R.id.title);
//		 Typeface tf = Typeface.createFromAsset(context.getAssets(),
//		 "ARIAL.TTF");
//		 textView.setTypeface(tf);
		Object obj = data.get(position);
		textView.setText(obj.toString());

		return rowView;
	}
}
