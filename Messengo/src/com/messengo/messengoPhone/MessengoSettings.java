package com.messengo.messengoPhone;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;

public class MessengoSettings extends PreferenceActivity {

	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	private GoogleAccountCredential credential;
	@SuppressWarnings("unused")
	private static Drive service;

	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		.build();
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();

		//		GCMRegistrar.checkManifest(this);
		//		final String regId = GCMRegistrar.getRegistrationId(this);
		//		if (regId.equals("")) {
		//			Log.d("messengo", "Registering");
		//			GCMRegistrar.register(this, getString(R.string.CLIENT_ID));
		//		} else {
		//			Log.d("messengo", "Already registered: " + regId);
		//		}
		//		  try {
		//			Log.d("messengo", writeUsingXMLSerializer());
		//		} catch (Exception e) {
		//			Log.d("messengo", "exception : " + e);
		//			e.printStackTrace();
		//		}
		//		credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
		//		startActivityForResult(credential.newChooseAccountIntent(), 1);

	}
	public class toto extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			try {
				get.setURI(new URI("http://messengo.webia-asso.fr/webservice/connection/" + params[0]));
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				Log.d("messengo", ConvertToString.convertStreamToString(entity.getContent()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

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

		bindPreferenceSummaryToValue(findPreference("account"));

		Preference save = (Preference)findPreference("saveSms");
		save.setOnPreferenceClickListener(saveSms);
		Preference delete = (Preference)findPreference("clean");
		delete.setOnPreferenceClickListener(clean);
	}

	//TODO: Faire les vraies actions :)
	public OnPreferenceClickListener saveSms = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Toast.makeText(MessengoSettings.this, "Sauvegarde des sms en cours ...", Toast.LENGTH_SHORT).show();
			SmsManager sms = new SmsManager(MessengoSettings.this);
			sms.retrieveSMS();
			sms.writeToFile();
			sms.sendViaMail();
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


	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
			String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			if (accountName != null) {
				credential.setSelectedAccountName(accountName);
				service = getDriveService(credential);
			}
		}

	}

	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

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

	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager
				.getDefaultSharedPreferences(preference.getContext())
				.getString(preference.getKey(), ""));
	}



}
