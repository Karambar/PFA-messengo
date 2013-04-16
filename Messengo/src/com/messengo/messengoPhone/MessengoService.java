package com.messengo.messengoPhone;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MessengoService extends Service implements ICallback{
	
	public Service service;
	private ConnectionManager cntMgr;
	private SharedPreferences settings;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("MESSENGO", "starting onBind");
		return null;
	}
	@Override
	public void onCreate() {
		Log.d("MESSENGO", "starting onBind");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.service = this;
		SendSmsObserver smsObeserver = (new SendSmsObserver(new Handler()));
		ContentResolver contentResolver = this.getContentResolver();
		contentResolver.registerContentObserver(Uri.parse("content://sms"),true, smsObeserver);
		Log.d("MESSENGO", "starting service");
		settings = getSharedPreferences(AuthService.PREF_NAME, 0);

		String token = settings.getString("token","");

		
		cntMgr = new ConnectionManager(service, this);
		cntMgr.connectToWS(token);

		return super.onStartCommand(intent, flags, startId);
	}
	
	public class SendSmsObserver extends ContentObserver {

		public SendSmsObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Uri uri = Uri.parse("content://sms");
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
			String phone = cursor.getString(cursor.getColumnIndex("address"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));// 2 = sent, etc.
			String date = cursor.getString(cursor.getColumnIndex("date"));
			String body = cursor.getString(cursor.getColumnIndex("body"));
			if (type == 2) {
				ConnectionManager cnt = new ConnectionManager(service);
				cnt.sendSms(body, phone.replaceAll(" ", ""), "2");
				Log.d("MESSENGO", "phone : " + phone + " type :" + type + " date : " + date + " body :" + body);
			}
		}
	}

	@Override
	public void tokenCallback(String token) {
		if (token.equals(""))
			Log.d("MESSENGO", "AccessToken empty");
	}
	@Override
	public void GCMCallback() {
		cntMgr.registerGCM();
		
	}

}
