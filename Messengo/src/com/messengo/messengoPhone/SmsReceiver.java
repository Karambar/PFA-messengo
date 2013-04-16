package com.messengo.messengoPhone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{

	static final String ACTION =
			"android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String from = "";
		String msg = "";
		SharedPreferences pref = context.getSharedPreferences("Messengo", 0);
		if (intent.getAction().equals(ACTION) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			StringBuilder buf = new StringBuilder();
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdusObj = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdusObj.length];
				for (int i = 0; i<pdusObj.length; i++) {
					messages[i] = SmsMessage.createFromPdu ((byte[])
							pdusObj[i]);
				}
				for (int i = 0; i < messages.length; i++) {
					SmsMessage message = messages[i];
					buf.append("Messengo : ");
					buf.append(message.getDisplayOriginatingAddress());
					buf.append(" - ");
					buf.append(message.getDisplayMessageBody());
					from = message.getDisplayOriginatingAddress();
					msg = message.getDisplayMessageBody();
				}
			}
			if (pref.getBoolean("active", true)) {
			//	Toast.makeText(context, buf.toString(), Toast.LENGTH_LONG).show();
				ConnectionManager cnt = new ConnectionManager(context);
				cnt.sendSms(msg, from, "1");
			}
		}
	}
}