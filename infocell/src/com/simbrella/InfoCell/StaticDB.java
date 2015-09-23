package com.simbrella.InfoCell;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: iiskenderov Date: 9/24/13 Time: 3:23 PM To
 * change this template use File | Settings | File Templates.
 */
public class StaticDB {
	private static StaticDB instance;

	private Context _context = null;
	private static Activity _activity = null;

	private StaticDB(InputStream xml, Context context, Activity activity) {
		_context = context;
		_activity = activity;

		loadMenu(xml);
		loadSettings();
	}

	private void loadSettings() {
		try {
			TelephonyManager tMgr = (TelephonyManager) _context
					.getSystemService(Context.TELEPHONY_SERVICE);
			phoneNumber = tMgr.getLine1Number();
		} catch (Exception ex) {

		}

		// TO-DO Remove when deploying
		// phoneNumber="994503780188";
		// CanUnregister=true;

		if (phoneNumber == null || phoneNumber.equals("")) {
			// phone gives no access to read phone number
			SharedPreferences pref = _activity
					.getPreferences(Context.MODE_PRIVATE);

			phoneNumber = pref.getString("phoneNumber", "");
			key = pref.getString("key", "");
			IsRegistered = pref.getBoolean("IsRegistered", false);
			pinNumber = pref.getString("pinNumber", "");

			CanUnregister = true;
		} else {
			IsRegistered = true;
		}
	}

	public static void SaveSettings() {
		SharedPreferences pref = _activity.getPreferences(Context.MODE_PRIVATE);

		SharedPreferences.Editor editor = pref.edit();

		editor.putString("phoneNumber", phoneNumber);
		editor.putString("key", key);
		editor.putBoolean("IsRegistered", IsRegistered);
		editor.putString("pinNumber", pinNumber);
		editor.commit();
	}

	public static long selection = -1;
	public static Menu mainMenu = null;
	public static Menu selectedMenu = null;
	public static List<Menu> AllMenus = new ArrayList<Menu>();

	public static String phoneNumber = "";
	public static String key = "";
	public static boolean IsRegistered = false;
	public static String pinNumber = "";
	public static boolean CanUnregister = false;

	public static synchronized StaticDB getInstance(InputStream xml,
			Context context, Activity activity) {
		if (instance == null) {
			instance = new StaticDB(xml, context, activity);
		}
		return instance;
	}

	private static void loadMenu(InputStream xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringComments(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(xml));
			doc.getDocumentElement().normalize();
			mainMenu = new Menu(doc.getDocumentElement(), null, AllMenus);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

}
