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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat.Style;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA. User: kadamov Date: 07.10.13 Time: 13:05 To
 * change this template use File | Settings | File Templates.
 */

public class RequestTask extends AsyncTask<String, String, String> {

	private android.content.Context _context = null;
	private Activity _activity = null;
	private boolean _doClose = false;

	public RequestTask(android.content.Context context, Activity activity,
			boolean doClose) {
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

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			String[] splittedParams = uri[1].split("&");

			for (int i = 0; i < splittedParams.length; i++) {
				String[] vals = splittedParams[i].split("=");
				nameValuePairs.add(new BasicNameValuePair(vals[0], vals[1]));
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

	public void showAlert(String str) {

		String resCode = str.substring(0, 2).trim();
		String showText = _context.getString(R.string.str_errorSending);

		boolean success = false;

		if (resCode.equals("0")) {
			showText = _context.getString(R.string.str_requestSent);
			success = true;
		} else if (resCode.equals("1")) {
			showText = _context.getString(R.string.str_syntaxError);
		} else if (resCode.equals("2")) {
			showText = _context.getString(R.string.str_limit_expired);
		} else if (resCode.equals("21")) {
			showText = _context.getString(R.string.str_wrongpin);
		} else if (resCode.equals("22")) {
			showText = _context.getString(R.string.str_wrongkey);
		} else if (resCode.equals("3")) {
			showText = _context.getString(R.string.str_noBalance);
		} else if (resCode.equals("4")) {
			showText = _context.getString(R.string.str_charginDown);
		}


		if (success && _doClose) {
			showError(showText);

		} else {

			Toast.makeText(_context, showText, Toast.LENGTH_LONG).show();
		}
	}

	private void showError(String s) {
		AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
		builder.setMessage(s)
				.setCancelable(false)
				.setPositiveButton(_context.getString(R.string.str_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								_activity.finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public void onPostExecute(String result) {
		super.onPostExecute(result);
		// Do anything with response..
		showAlert(result);
	}
}
