package com.messengo.messengoPhone;

import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;



public final class CommonUtilities {

	static final String SERVER_URL = "http://5.135.144.248/gcm/register.php";
	static final String SENDER_ID = "985643011238";
	static final String TAG = "Messengo";


	public static String getEmail(Context context) {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(context).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				return account.name;
			}
		}
		return null;
	}


	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		Log.d(TAG, "Phone number = " + tMgr.getSubscriberId());
		return tMgr.getSubscriberId();
	}
}
