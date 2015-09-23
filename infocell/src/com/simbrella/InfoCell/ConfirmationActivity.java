package com.simbrella.InfoCell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.simbrella.InfoCell.crouton.Crouton;
import com.simbrella.InfoCell.crouton.Style;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmationActivity extends Activity {

	private TextView tvEnterCode;
	private EditText txtCode;
	private Button btnSubmit;
	private Button btnRestart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);

		if (MainListActivity.activityMain != null) {
			MainListActivity.activityMain.finish();
		}
		txtCode = (EditText) findViewById(R.id.etxtConfirmationCode);
		btnSubmit = (Button) findViewById(R.id.btnSubmitConfirmation);
		btnRestart = (Button) findViewById(R.id.btnRestartConfirmation);
		tvEnterCode = (TextView) findViewById(R.id.tvEnterConfirmCodeConfirmation);
		Typeface tf = Typeface.createFromAsset(getAssets(), "ARIAL.TTF");
		txtCode.setTypeface(tf);
		btnSubmit.setTypeface(tf);
		btnRestart.setTypeface(tf);
		tvEnterCode.setTypeface(tf);

		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String value = txtCode.getText().toString().trim();
				if (value != null && !value.equals("")) {

					confirmPinCode(value);

				} else {
					showAlert(getString(R.string.str_emptypin));
					// secondStep();
				}
				return;
			}
		});

		btnRestart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ConfirmationActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				StaticDB.IsRegistered = false;
				StaticDB.phoneNumber = "";
				StaticDB.key = "";
				StaticDB.SaveSettings();
				finish();

			}
		});
	}

	private void confirmPinCode(String pin) {

		String urlParams = String.format("msisdn=%s&pin=%s&os=%s",
				StaticDB.phoneNumber, pin, "android");

		RequestTaskConfirm req = new RequestTaskConfirm(ConfirmationActivity.this, null, false);

		req.execute("http://tools.dsc.az/infoappnew/confirmpin.asp", urlParams);

	}

	private void thirdStep() {
		Intent intent = new Intent(ConfirmationActivity.this,
				MainListActivity.class);
		startActivity(intent);
		finish();
	}

	private void showAlert(String message) {

		Crouton.makeText(this, message, Style.ALERT).show();
	}

	public class RequestTaskConfirm extends AsyncTask<String, String, String> {

		private android.content.Context _context = null;
		private Activity _activity = null;
		private boolean _doClose = false;

		public RequestTaskConfirm(android.content.Context context,
				Activity activity, boolean doClose) {
			_context = context;
			_activity = activity;
			_doClose = doClose;
		}

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				HttpPost post = new HttpPost(uri[0]);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);

				String[] splittedParams = uri[1].split("&");

				for (int i = 0; i < splittedParams.length; i++) {
					String[] vals = splittedParams[i].split("=");
					nameValuePairs
							.add(new BasicNameValuePair(vals[0], vals[1]));
				}

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				response = httpclient.execute(post);

				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (Exception e) {

				responseString = "ERROR : " + e.getMessage();
			}
			return responseString;
		}
		@Override
		public void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
			showAlertNext(result);
		}
	}
	
	public void showAlertNext(String str) {

		String resCode = str.substring(0, 2).trim();
		String showText = getResources().getString(R.string.str_errorSending);

		boolean success = false;

		if (resCode.equals("0")) {
			showText = getResources().getString(R.string.str_confirm_success);
			success = true;
		} else if (resCode.equals("1")) {
			showText = getResources().getString(R.string.str_syntaxError);
		} else if (resCode.equals("2")) {
			showText = getResources().getString(R.string.str_limit_expired);
		} else if (resCode.equals("21")) {
			showText = getResources().getString(R.string.str_wrongpin);
		} else if (resCode.equals("22")) {
			showText = getResources().getString(R.string.str_wrongkey);
		} else if (resCode.equals("3")) {
			showText = getResources().getString(R.string.str_noBalance);
		} else if (resCode.equals("4")) {
			showText = getResources().getString(R.string.str_charginDown);
		}

		if (success) {
			
			StaticDB.IsRegistered = true;
			StaticDB.pinNumber = txtCode.getText().toString().trim();
			StaticDB.SaveSettings();
			thirdStep();

		} else {

			showAlert(showText);
		}
	}

	private void showError(String s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(s)
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.str_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

	}


}
