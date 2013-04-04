package com.messengo.tablette.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import com.messengo.tablette.bean.Contact;

public final class CommonUtilities {

	public static final String SERVER_URL = "http://5.135.144.248/gcm/register.php?tablet=pute";
	public static final String SENDER_ID = "985643011238";
	public static final String TAG = "Messengo";


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
	
	public static List<Contact> getContactArray(Context ctx){
		List<Contact> data = new ArrayList<Contact>();
			
		Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		while (phones.moveToNext())
		{
			
		  String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		  String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		  Contact tmp = new Contact(name, phoneNumber);
		  data.add(tmp);
		}
		phones.close();
		return data;
	}


	public static String getPhonenUmberFromContactList(String name, List<Contact> list) {
		int i = -1;
		
		while (++i < list.size()){
			if (list.get(i).getName().equals(name))
				return list.get(i).getNumber();
		}
		return null;
	}
}