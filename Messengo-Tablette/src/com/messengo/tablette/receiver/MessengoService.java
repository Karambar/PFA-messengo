package com.messengo.tablette.receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.messengo.tablette.activity.LoginActivity;
import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.webservice.WebService;

public class MessengoService extends Service{

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("MessengoService", "Service started");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return(null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	};
	
}
