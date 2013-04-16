package com.messengo.messengoPhone;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.widget.Toast;


public class MessengoSettings extends PreferenceActivity implements ICallback {

	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	public static String regId;
	private ConnectionManager cntMgr;
	private Account                 gmailAddress;
	private  AccountManager googleAccountManager;
	private Account[]               allAccounts;
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);


		setupSimplePreferencesScreen();
		AccountManager googleAccountManager = AccountManager.get(this.getApplicationContext());
		Account[] allAccounts = googleAccountManager.getAccountsByType("com.google");
		Account gmailAddress = allAccounts[0];
		AuthService.getInstance(this).refreshAuthToken(this, gmailAddress);
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (MessengoService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}
		addPreferencesFromResource(R.xml.pref_general);

		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_data_sync);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_data_sync);


//		ArrayList<String> entries = new ArrayList<String>();

//		for (int i = 0;i < allAccounts.length ; i++) {
//			entries.add(allAccounts[i].toString());
//		}
		/*		ListPreference accountList = (ListPreference) findPreference("accountList");
		String[] entriesArray = entries.toArray(new String[entries.size()]);
		if (entriesArray == null)
			Log.d("MESSENGO", "EntriesArray null");
		if (accountList == null)
			Log.d("MESSENGO", "AccountList null");
		accountList.setEntries(entriesArray);
		accountList.setEntryValues(entriesArray);
		 */ 
		Preference active = (Preference)findPreference("active_checkbox");
		active.setOnPreferenceChangeListener(activeMessengo); 
		Preference save = (Preference)findPreference("saveSms");
		save.setOnPreferenceClickListener(saveSms);
		Preference delete = (Preference)findPreference("clean");
		delete.setOnPreferenceClickListener(clean);
	}

	public OnPreferenceClickListener saveSms = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Toast.makeText(MessengoSettings.this, "Sauvegarde des sms en cours ...", Toast.LENGTH_SHORT).show();
			SmsManage sms = new SmsManage(MessengoSettings.this);
			sms.extract();
			return false;
		}
	};

	public OnPreferenceClickListener clean = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Toast.makeText(MessengoSettings.this, "Suppression des données en cours ...", Toast.LENGTH_SHORT).show();
			return false;
		}
	};

	public OnPreferenceChangeListener activeMessengo = new Preference.OnPreferenceChangeListener() {            
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(newValue instanceof Boolean){
				editor.putBoolean("active", (Boolean) newValue);
				editor.commit();
			}
			return true;
		}
	};

	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	@SuppressLint("InlinedApi")
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				preference.setSummary(
						index >= 0
						? listPreference.getEntries()[index]
								: null);
			} else {
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	@Override
	public void tokenCallback(String token) {	
		settings = getSharedPreferences(AuthService.PREF_NAME, 0);
		editor = settings.edit();
		editor.putString("token", token);
		editor.commit();
		if (token.equals(""))
			Log.d("MESSENGO", "AccessToken empty");
		if (isMyServiceRunning() == false) {
			Intent service = new Intent(MessengoSettings.this, MessengoService.class);
			service.putExtra("token", token);
			startService(new Intent(MessengoSettings.this, MessengoService.class));
		}
	}

	@Override
	public void GCMCallback() {
		cntMgr.registerGCM();
	}

}
