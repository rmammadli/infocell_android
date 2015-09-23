package com.simbrella.InfoCell;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

import com.simbrella.InfoCell.crouton.Crouton;
import com.simbrella.InfoCell.crouton.Style;

/**
 * Created with IntelliJ IDEA. User: kadamov Date: 07.10.13 Time: 14:36 To
 * change this template use File | Settings | File Templates.
 */
public class textInputForm extends Activity implements View.OnClickListener {

	// LinearLayout mainView;

	private List<EditText> edits = new ArrayList<EditText>();
	private Spinner list1 = null;
	private Spinner list2 = null;
	private LinearLayout llInputText;
	private LinearLayout llMainPanel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_input_form);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		ScrollView s = (ScrollView) findViewById(R.id.scrollView);
		llInputText = (LinearLayout) findViewById(R.id.llInputText);
		llMainPanel = (LinearLayout) findViewById(R.id.llMainPanel);

		// mainView = (LinearLayout) s.findViewById(R.id.mainPanel);

		Button v = (Button) findViewById(R.id.cancelButton);
		v.setOnClickListener(this);

		v = (Button) findViewById(R.id.sendButton);
		v.setOnClickListener(this);
		initForm();
	}

	private void initForm() {

		// if (mainView == null) {
		// return;
		// }

		 Typeface tf = Typeface.createFromAsset(this.getAssets(),"ARIAL.TTF");
		 

		llMainPanel.setVisibility(View.GONE);
		llInputText.setVisibility(View.VISIBLE);
		for (int i = 0; i < StaticDB.selectedMenu.InputCount; i++) {

			TextView tv1 = (TextView) findViewById(R.id.tvEnteName);

			if (StaticDB.selectedMenu.Labels.size() - 1 >= i) {
				tv1.setText(StaticDB.selectedMenu.Labels.get(i));
			} else {
				tv1.setText(StaticDB.selectedMenu.Labels.get(0));
			}
			 tv1.setTypeface(tf);

			// mainView.addView(tv1);

			EditText e = (EditText) findViewById(R.id.etxtName);

			if (StaticDB.selectedMenu.InputType == 1) {
				e.setInputType(InputType.TYPE_CLASS_PHONE);
				e.setTypeface(tf);
			}
			// mainView.addView(e);

			// edits.add(e);
		}
		if (StaticDB.selectedMenu.Type == 4) {
			llMainPanel.setVisibility(View.VISIBLE);
			llInputText.setVisibility(View.GONE);
			TextView tv1 = new TextView(this);
			tv1.setText(StaticDB.selectedMenu.List1Label);
			 tv1.setTypeface(tf);
			tv1.setTextColor(getResources().getColor(R.color.white));
			tv1.setBackgroundColor(getResources().getColor(
					R.color.azercell_color));
			// mainView.addView(tv1);

			list1 = (Spinner) findViewById(R.id.spList1);

			List<CharSequence> listValues = new ArrayList<CharSequence>();

			for (int i = 0; i < StaticDB.selectedMenu.List1.size(); i++) {
				listValues.add(StaticDB.selectedMenu.List1.get(i).Title);
			}

			// ArrayAdapter<CharSequence> adapter = new
			// ArrayAdapter<CharSequence>(this,
			// android.R.layout.simple_spinner_item,listValues);

			// ArrayAdapter<CharSequence> adapter = new CustomAdapter(this,
			// android.R.layout.simple_list_item_1, listValues,
			// getString(R.string.tahoma_ttf));
			ArrayAdapter<CharSequence> adapter = new MyArrayAdapterSpinner(
					this, listValues);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			list1.setAdapter(adapter);

			tv1 = new TextView(this);
			tv1.setText(StaticDB.selectedMenu.List2Label);
			 tv1.setTypeface(tf);
			tv1.setTextColor(getResources().getColor(R.color.white));
			tv1.setBackgroundColor(getResources().getColor(
					R.color.azercell_color));
			// mainView.addView(tv1);

			list2 = (Spinner) findViewById(R.id.spList2);
			listValues = new ArrayList<CharSequence>();

			for (int i = 0; i < StaticDB.selectedMenu.List2.size(); i++) {
				listValues.add(StaticDB.selectedMenu.List2.get(i).Title);
			}

			// ArrayAdapter<CharSequence> adapter2 = new
			// ArrayAdapter<CharSequence>(this,
			// android.R.layout.simple_spinner_item,listValues);
			// ArrayAdapter<CharSequence> adapter2 = new CustomAdapter(this,
			// android.R.layout.simple_list_item_1, listValues,
			// getString(R.string.tahoma_ttf));
			ArrayAdapter<CharSequence> adapter2 = new MyArrayAdapterSpinner(
					this, listValues);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			list2.setAdapter(adapter2);

		}

	}

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.cancelButton) {
			doCancel();
		} else {
			doSend();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		}
		return true;
	}

	private void doSend() {

		String formattedParam = StaticDB.selectedMenu.Keyword;
		List<String> values = new ArrayList<String>();

		for (int i = 0; i < edits.size(); i++) {
			String text = edits.get(i).getText().toString();

			if (text == null || text.equals("")) {
				showError("Zəhmət olmasa tələb olunan xanaları doldurun");
				return;
			}
			values.add(text);
		}

		if (StaticDB.selectedMenu.Type == 4) {

			for (int i = 0; i < values.size(); i++) {
				formattedParam = formattedParam.replace(
						String.format("{%d}", i), values.get(i));
			}

			formattedParam = formattedParam.replace(String.format("{%d}",
					values.size()), StaticDB.selectedMenu.List1.get(list1
					.getSelectedItemPosition()).Value);
			formattedParam = formattedParam.replace(String.format("{%d}",
					values.size() + 1), StaticDB.selectedMenu.List2.get(list2
					.getSelectedItemPosition()).Value);

		} else {
			if (formattedParam != null && !formattedParam.equals("")) {
				for (int i = 0; i < values.size(); i++) {
					formattedParam = formattedParam.replace(
							String.format("{%d}", i), values.get(i));
				}
			} else {
				formattedParam = values.get(0);
			}
		}

		if (StaticDB.selectedMenu.Type == 6) {
			// sms
			try {
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(StaticDB.selectedMenu.CMD, null,
						formattedParam, null, null);
				showAlert("İsmarıc göndərildi");
				finish();
			} catch (Exception ex) {
				showError(ex.getMessage() + " : " + formattedParam);
			}
		} else {

			String urlParams = String.format("msisdn=%s&short=%s&text=%s",
					StaticDB.phoneNumber, StaticDB.selectedMenu.CMD,
					formattedParam);

			RequestTask req = new RequestTask(getApplicationContext(), this,
					true);

			req.execute("http://tools.dsc.az/infoapp/content.asp", urlParams);
		}

	}

	public void showAlert(String str) {

		Crouton.makeText(this, str, Style.INFO).show();

	}

	private void showError(String s) {
		Crouton.makeText(this, s, Style.ALERT).show();

	}

	private void doCancel() {
		finish();
	}
}
