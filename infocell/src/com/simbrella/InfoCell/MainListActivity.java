package com.simbrella.InfoCell;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.simbrella.InfoCell.crouton.Crouton;
import com.simbrella.InfoCell.crouton.Style;

import static com.simbrella.InfoCell.StaticDB.*;

@SuppressWarnings("unused")
public class MainListActivity extends SlidingActivity implements
		OnItemClickListener {
	/**
	 * Called when the activity is first created.
	 */
	final String LOG_TAG = "myLogs";
	private ArrayAdapter<CharSequence> adapter;
	private Menu mainMenu = null;
	private StaticDB sDB;
	private ListView lvMain;
	private LinearLayout llButtons;

	private LinearLayout llbtnWeather;
	private LinearLayout llbtnHoroscope;
	private LinearLayout llbtnNews;
	private LinearLayout llbtnMessage;
	private LinearLayout llbtnAzercell;
	private LinearLayout llbtnEntertaiment;
	private LinearLayout llbtnInfo;
	private LinearLayout llbtnLanguage;

	private TextView tvbtnWeather;
	private TextView tvbtnHoroscope;
	private TextView tvbtnNews;
	private TextView tvbtnMessage;
	private TextView tvbtnAzercell;
	private TextView tvbtnEntertaiment;
	private TextView tvbtnInfo;
	private TextView tvbtnLanguage;
	private ActionBar actionBar;

	private SpannableString s = null;

	public static Activity activityMain;
	boolean slidingMenuStatusIsOpen = false;

	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		actionBar = getActionBar();
		// Typeface tf = Typeface.createFromAsset(getAssets(),
		// "ARIAL.TTF");

		final Context context = this;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.layout_slide_menu, null, true);

		activityMain = this;
		// Sliding
		// Menu--------------------------------------------------------------------------
		setBehindContentView(v);
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.drawer_shadow);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slide_menu_offset);
		getSlidingMenu().setFadeDegree(0.35f);
		setSlidingActionBarEnabled(false);

		llButtons = (LinearLayout) findViewById(R.id.llButtons);
		llbtnWeather = (LinearLayout) findViewById(R.id.llWeatherButton);
		llbtnHoroscope = (LinearLayout) findViewById(R.id.llHoroscopeButton);
		llbtnNews = (LinearLayout) findViewById(R.id.llNewsButton);
		llbtnMessage = (LinearLayout) findViewById(R.id.llMessageButton);
		llbtnAzercell = (LinearLayout) findViewById(R.id.llAzercellButton);
		llbtnEntertaiment = (LinearLayout) findViewById(R.id.llEntertaimentButton);
		llbtnInfo = (LinearLayout) findViewById(R.id.llInfoButton);
		llbtnLanguage = (LinearLayout) findViewById(R.id.llLanguageButton);
		lvMain = (ListView) findViewById(R.id.lvInfoCellMain);

		tvbtnWeather = (TextView) findViewById(R.id.tvWeatherButton);
		tvbtnHoroscope = (TextView) findViewById(R.id.tvHoroscopeButton);
		tvbtnNews = (TextView) findViewById(R.id.tvNewsButton);
		tvbtnMessage = (TextView) findViewById(R.id.tvMessageButton);
		tvbtnAzercell = (TextView) findViewById(R.id.tvAzercellButton);
		tvbtnEntertaiment = (TextView) findViewById(R.id.tvEntertaimentButton);
		tvbtnInfo = (TextView) findViewById(R.id.tvInfoButton);
		tvbtnLanguage = (TextView) findViewById(R.id.tvLanguageButton);
		//
		// tvbtnWeather.setTypeface(tf);
		// tvbtnHoroscope.setTypeface(tf);
		// tvbtnNews.setTypeface(tf);
		// tvbtnMessage.setTypeface(tf);
		// tvbtnAzercell.setTypeface(tf);
		// tvbtnEntertaiment.setTypeface(tf);
		// tvbtnInfo.setTypeface(tf);
		// tvbtnLanguage.setTypeface(tf);

		// get intent extra
		String extra = getIntent().getStringExtra("buttonClick");
		if (getIntent().hasExtra("buttonClick")) {
			llButtons.setVisibility(View.GONE);
			lvMain.setVisibility(View.VISIBLE);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);

		} else {
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(false);

		}

		try {

			String fileName = "menu.xml";

			sDB = StaticDB.getInstance(getAssets().open(fileName),
					getApplicationContext(), this);

			if (StaticDB.IsRegistered) {

				// selection = getIntent().getLongExtra("selection", -1);
				s = null;
				// selection = 1;
				if (selection == -1) {
					mainMenu = sDB.mainMenu;

					s = new SpannableString("InfoCell");
					// s.setSpan(new CustomTypefaceSpan("bold", tf), 0,
					// s.length(),
					// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					// Update the action bar title with the TypefaceSpan
					// instance

				} else {
					mainMenu = sDB.AllMenus.get((int) selection);
					s = new SpannableString(mainMenu.Title);
					// s.setSpan(new CustomTypefaceSpan("bold", tf), 0,
					// s.length(),
					// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				actionBar.setTitle(s);

				getSlidingMenu().setOnOpenListener(new OnOpenListener() {
					@Override
					public void onOpen() {
						// TODO Auto-generated method stub
						actionBar.setTitle("Bizimlə əlaqə");

					}
				});

				getSlidingMenu().setOnCloseListener(new OnCloseListener() {
					@Override
					public void onClose() {
						// TODO Auto-generated method stub
						actionBar.setTitle(s.toString());

					}
				});

				mainMenu.getList();

				adapter = new MyArrayAdapter(this, mainMenu.adapterList);

				lvMain.setAdapter(adapter);
				adapter.notifyDataSetChanged();

				lvMain.setOnItemClickListener(this);

				llbtnWeather.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu1.xml");
						// getSlidingMenu().toggle();
						// loadList("menu1.xml");

						onItemClick(lvMain, adapter.getView(0, null, null), 0,
								adapter.getItemId(0));
						// finish();

					}
				});

				llbtnHoroscope.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu2.xml");
						// getSlidingMenu().toggle();
						onItemClick(lvMain, adapter.getView(1, null, null), 1,
								adapter.getItemId(1));
						// loadList("menu2.xml");
						// finish();

					}
				});
				llbtnNews.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu3.xml");
						// getSlidingMenu().toggle();
						onItemClick(lvMain, adapter.getView(2, null, null), 2,
								adapter.getItemId(2));

					}
				});
				llbtnMessage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu4.xml");
						// getSlidingMenu().toggle();
						onItemClick(lvMain, adapter.getView(3, null, null), 3,
								adapter.getItemId(3));

					}
				});
				llbtnAzercell.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu5.xml");
						// getSlidingMenu().toggle();
						onItemClick(lvMain, adapter.getView(4, null, null), 4,
								adapter.getItemId(4));

					}
				});
				llbtnEntertaiment
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								// loadList("menu6.xml");
								// getSlidingMenu().toggle();
								onItemClick(lvMain,
										adapter.getView(5, null, null), 5,
										adapter.getItemId(5));

							}
						});
				llbtnInfo.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu7.xml");
						// getSlidingMenu().toggle();
						onItemClick(lvMain, adapter.getView(6, null, null), 6,
								adapter.getItemId(6));

					}
				});
				llbtnLanguage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// loadList("menu8.xml");
						// getSlidingMenu().toggle();
						onItemClick(lvMain, adapter.getView(7, null, null), 7,
								adapter.getItemId(7));

					}
				});

				if (!StaticDB.IsRegistered) {
					if (StaticDB.phoneNumber.toString().length() != 12) {
						// firstStep
						firstStep();
					} else {
						secondStep();
					}
				}
			} else {

				if (StaticDB.phoneNumber.toString().length() == 12) {
					secondStep();
				} else {

					firstStep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Sliding menu listner
		getSlidingMenu().setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				// TODO Auto-generated method stub
				actionBar.setDisplayShowHomeEnabled(true);
				actionBar.setDisplayHomeAsUpEnabled(true);
				slidingMenuStatusIsOpen = true;

			}
		});
		getSlidingMenu().setOnCloseListener(new OnCloseListener() {

			@Override
			public void onClose() {
				// TODO Auto-generated method stub

				if (getIntent().hasExtra("buttonClick")) {
					actionBar.setDisplayShowHomeEnabled(true);
					actionBar.setDisplayHomeAsUpEnabled(true);

				} else {
					actionBar.setDisplayShowHomeEnabled(false);
					actionBar.setDisplayHomeAsUpEnabled(false);

				}
				slidingMenuStatusIsOpen = false;
			}
		});

	}

	private void firstStep() {
		Intent intentRegister = new Intent(MainListActivity.this,
				RegisterActivity.class);
		startActivity(intentRegister);
		// finish();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		if (StaticDB.CanUnregister) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main, menu);
			return true;

		}

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (slidingMenuStatusIsOpen) {
				getSlidingMenu().toggle();
			} else {
				onBackPressed();
			}
			break;
		case R.id.unregister:
			unregisterPhone();
			break;

		case R.id.invitation:
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(
					Intent.EXTRA_TEXT,
					"Infocell-i telefonuna yukle, informasiya cibine axsin. iOS uchun: https://itunes.apple.com/az/app/infocell/id828302557?mt=8 Android uchun: https://play.google.com/store/apps/details?id=com.simbrella.InfoCell");
			startActivity(Intent.createChooser(share, "Share Text"));
			break;

		case R.id.about:
			getSlidingMenu().toggle();
			break;

		}
		return true;
	}

	private void unregisterPhone() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.str_unregistration);

		builder.setPositiveButton(R.string.str_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						StaticDB.phoneNumber = "";
						StaticDB.pinNumber = "";
						StaticDB.SaveSettings();

						firstStep();

					}
				});

		builder.setNegativeButton(R.string.str_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		builder.show();

	}

	private void secondStep() {

		Intent intent = new Intent(MainListActivity.this,
				ConfirmationActivity.class);
		startActivity(intent);

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int menuType = mainMenu.Submenus.get((int) id).Type;

		if (menuType == 1) {
			selection = AllMenus.indexOf(mainMenu.Submenus.get((int) id));

			Intent intent = new Intent(getApplicationContext(),
					MainListActivity.class).putExtra("buttonClick", "btn");
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			// int activityNum = 0;
			// activityNum = Integer.parseInt(loadPrefs("activityLife",
			// "activityNum")) + 1;
			// savePrefs("activityLife", "activityNum",
			// Integer.toString(activityNum));

			// intent.putExtra("selection", selection);
		} else if (menuType == 2) {
			try {
				sendRequest(mainMenu.Submenus.get((int) id));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (menuType == 3 || menuType == 6 || menuType == 4) {
			executeInputForm(mainMenu.Submenus.get((int) id));
		} else if (menuType == 5) {
			sendSms(mainMenu.Submenus.get((int) id));
		} else if (menuType == 7) {
			makeCall(mainMenu.Submenus.get((int) id));
		}

	}

	private void executeInputForm(Menu menu) {
		StaticDB.selectedMenu = menu;

		Intent intent = new Intent(this, textInputForm.class);
		startActivity(intent);
	}

	private void sendRequest(Menu menu) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {

		String key;
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(StaticDB.key);
		stringBuilder.append(StaticDB.phoneNumber);
		stringBuilder.append(menu.Keyword);

		key = MD5(stringBuilder.toString());

		String urlParams = String.format("msisdn=%s&short=%s&text=%s&key=%s",
				StaticDB.phoneNumber, menu.CMD, menu.Keyword, key);

		RequestTask req = new RequestTask(getApplicationContext(), null, false);

		req.execute("http://tools.dsc.az/infoappnew/content.asp", urlParams);
	}

	public void sendSms(Menu menu) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(menu.CMD, null, menu.Keyword, null, null);

		showAlert(getString(R.string.str_smsSent));
	}

	public void makeCall(Menu menu) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + menu.CMD));
		startActivity(callIntent);
	}

	public void showAlert(String str) {

		Crouton.makeText(this, str, Style.INFO).show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// selection = -1;
		overridePendingTransition(R.anim.right_out, R.anim.left_in);
		// savePrefs("status", "operationact", "0");
	}

	@Override
	public void onResume() {
		super.onResume();
		selection = -1;
	}

	private void savePrefs(String prefName, String key, String value) {
		SharedPreferences sp = getSharedPreferences(prefName, 0);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private String loadPrefs(String prefName, String key) {
		SharedPreferences sp = getSharedPreferences(prefName, MODE_PRIVATE);// this.getPreferences(Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		return sp.getString(key, "");

	}

	public static String MD5(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5 = new byte[64];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5 = md.digest();
		return convertedToHex(md5);
	}

	private static String convertedToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < data.length; i++) {
			int halfOfByte = (data[i] >>> 4) & 0x0F;
			int twoHalfBytes = 0;

			do {
				if ((0 <= halfOfByte) && (halfOfByte <= 9)) {
					buf.append((char) ('0' + halfOfByte));
				}

				else {
					buf.append((char) ('a' + (halfOfByte - 10)));
				}

				halfOfByte = data[i] & 0x0F;

			} while (twoHalfBytes++ < 1);
		}
		return buf.toString();
	}

}
