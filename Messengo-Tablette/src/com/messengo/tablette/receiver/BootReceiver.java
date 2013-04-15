package com.messengo.tablette.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	Intent serviceIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (serviceIntent == null)
			serviceIntent = new Intent();
		serviceIntent.setClass(context, MessengoService.class);
		context.startService(serviceIntent);
	}

}
