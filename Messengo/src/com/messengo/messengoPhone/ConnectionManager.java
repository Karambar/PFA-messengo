package com.messengo.messengoPhone;

import static com.messengo.messengoPhone.CommonUtilities.SENDER_ID;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class ConnectionManager {

	private Context ct;
	AsyncTask<Void, Void, Void> mRegisterTask;
	protected static String email;

	public ConnectionManager(Context ct) {
		this.ct = ct;
		ConnectionManager.email = CommonUtilities.getEmail(ct);     
	}

	public void registerGCM() {
		GCMRegistrar.checkDevice(ct);
		GCMRegistrar.checkManifest(ct);
		final String regId = GCMRegistrar.getRegistrationId(ct);
		if (regId.equals("")) {
			GCMRegistrar.register(ct, SENDER_ID);
		} else {
			if (GCMRegistrar.isRegisteredOnServer(ct)) {
				GCMRegistrar.unregister(ct);
			} else {
				mRegisterTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						ServerUtilities.register(ct, email, regId);
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}
				};
				mRegisterTask.execute();
			}
		}
	}

	public class mRegisterTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			ServerUtilities.register(ct, params[0], params[1]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mRegisterTask = null;
		}

	}

	public void connectToWS(String token) {
		if (token.length() > 0)
			new WSconnect().execute(token);
	}

	public class WSconnect extends AsyncTask<String, Void, Void> {
		boolean broken = false;
		final SharedPreferences settings = ct.getSharedPreferences(AuthService.PREF_NAME, 0);
		final SharedPreferences.Editor editor = settings.edit();

		@Override
		protected Void doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			try {
				get.setURI(new URI("http://messengo.webia-asso.fr/webservice/connection/" + params[0] + "/"));
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				String content = ConvertToString.convertStreamToString(entity.getContent());
				JSONObject obj = new JSONObject(content);
				JSONObject obj2 = obj.getJSONObject("response");
				if (!(obj2.getString("code").equals("200"))) {
					broken = true;
					return null;
				}
				JSONObject obj3 = obj.getJSONObject("infos");
				if ((obj3.getString("idGoogle").equals("")) || obj3.getString("idGoogle") == null) {
					broken = true;
					return null;
				}
				editor.putString("idGoogle", obj3.getString("idGoogle"));
				editor.putString("passphrase", obj3.getString("passphrase"));
				editor.commit();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (broken == true)
				Toast.makeText(ct, "Connection problem, try again later.", Toast.LENGTH_LONG).show();
		}
	}

	public void sendSms(String sms, String number) {
		final SharedPreferences settings = ct.getSharedPreferences(AuthService.PREF_NAME, 0);
		try {
			sms = URLEncoder.encode(sms, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.d("MESSENGO", "SendSms");
		new WSSendSms().execute(settings.getString("idGoogle", ""), settings.getString("passphrase", ""), number, sms);
		Log.d("MESSENGO", "SentSms");
	}

	public class WSSendSms extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			try {
				Log.d("MESSENGO", "sending to webservice");
				get.setURI(new URI("http://messengo.webia-asso.fr/webservice/addMessage/" + params[0] + "/" + params[1] + "/1/" + params[2] + "/" + params[3] + "/"));
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				String content = ConvertToString.convertStreamToString(entity.getContent());
				Log.d("MESSENGO", content);
			} catch (URISyntaxException e) {
				Log.d("MESSENGO", e.getMessage());
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				Log.d("MESSENGO", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("MESSENGO", e.getMessage());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				Log.d("MESSENGO", e.getMessage());
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}

}