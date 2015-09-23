package com.simbrella.InfoCell;

import java.net.InetAddress;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.simbrella.InfoCell.crouton.Crouton;
import com.simbrella.InfoCell.crouton.Style;

import static com.simbrella.InfoCell.StaticDB.*;

@SuppressWarnings("unused")
public class RegisterActivity extends Activity {

	private TextView tvEnterNumber;
	private Button btnRegister;
	private EditText txtPhone;
	private Spinner spCode;
	private ConnectionDetector cd;
	private Typeface tf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		if (MainListActivity.activityMain != null) {
			MainListActivity.activityMain.finish();
		}
		tvEnterNumber = (TextView) findViewById(R.id.tvEnterNumberRegister);
		btnRegister = (Button) findViewById(R.id.btnSubmitPhone);
		txtPhone = (EditText) findViewById(R.id.etxtPhone);
		spCode = (Spinner) findViewById(R.id.spCode);
	    tf = Typeface.createFromAsset(getAssets(),
				 "ARIAL.TTF");
	    btnRegister.setTypeface(tf);
	    txtPhone.setTypeface(tf);
	    tvEnterNumber.setTypeface(tf);
		cd = new ConnectionDetector(getApplicationContext());
		
		 
		Boolean isInternetPresent = cd.isConnectingToInternet(); 

		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (cd.isConnectingToInternet()) {
					// TODO Auto-generated method stub
					String value = txtPhone.getText().toString().trim();
					if (value != null && !value.equals("")) {
						// secondStep
						if (value.length() == 7) {
							if (spCode.getSelectedItem().toString() != null
									&& !spCode.getSelectedItem().toString()
											.isEmpty()) {

								// real second step
								StaticDB.phoneNumber = new StringBuilder()
										.append("994")
										.append(spCode.getSelectedItem()
												.toString().substring(1, 3))
										.append(value).toString();
								
								String strKey = randomString(16);
								StaticDB.key = strKey;
								StaticDB.SaveSettings();
								getPinCode(strKey);
							}

							// firstStep(value);
						} else {
							showAlert(getString(R.string.str_numberLen));
						}
					} else {
						showAlert(getString(R.string.str_numberEmpty));
						// firstStep("");
					}
					return;

				} else {
					showAlert(getResources().getString(
							R.string.txt_internet_connection));
				}
			}

		});
	}

	private void getPinCode(String key) {
	

		String urlParams = String.format("msisdn=%s&key=%s&os=%s",
				StaticDB.phoneNumber, key,"android");

		RequestTask req = new RequestTask(getApplicationContext(), null, false);

		req.execute("http://tools.dsc.az/infoappnew/requestpin.asp", urlParams);

		secondStep();
	}

	private void secondStep() {

		Intent intent = new Intent(RegisterActivity.this,
				ConfirmationActivity.class);
		startActivity(intent);
		finish();

	}

	private void showAlert(String message) {

		Crouton.makeText(this, message, Style.ALERT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
	

	String randomString( int len ) 
	{
		final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rnd = new Random();
		
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}

}
