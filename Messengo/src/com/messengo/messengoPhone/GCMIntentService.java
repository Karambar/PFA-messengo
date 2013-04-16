package com.messengo.messengoPhone;

import static com.messengo.messengoPhone.CommonUtilities.SENDER_ID;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		ServerUtilities.register(context, ConnectionManager.email, registrationId);

	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		SharedPreferences pref = context.getSharedPreferences("Messengo", 0);
		if (pref.getBoolean("active", true)) {
			String message = intent.getExtras().getString("send sms");
			//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			try {
				JSONObject obj = new JSONObject(message);
				Log.d("MESSENGO", message);
				String number = obj.getString("number");
				String msg = URLDecoder.decode(obj.getString("message"), "UTF-8");
				PendingIntent pi = PendingIntent.getActivity(this, 0,
						new Intent(this, GCMIntentService.class), 0);                
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(number, null, msg, pi, null); 
//				ConnectionManager cnt = new ConnectionManager(context);
//				cnt.sendSms(msg, number, "1");
				ContentValues values = new ContentValues();
				values.put("address", number);
				values.put("body", msg); 
				getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/sent"), values);

			} catch (JSONException e) {
				Log.d("MESSENGO", e.getMessage());
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				Log.d("MESSENGO", e.getMessage());
				e.printStackTrace();
			}
			Log.i("MESSENGO", "Received message : " + message);
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
	}

	@Override
	public void onError(Context context, String errorId) {
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
}
