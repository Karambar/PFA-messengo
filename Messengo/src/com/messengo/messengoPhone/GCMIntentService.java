package com.messengo.messengoPhone;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService  {

	@Override
	protected void onError(Context arg0, String arg1) {
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d("messengo", "Received a message: " + arg1.getStringExtra("message"));
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
	}

}
