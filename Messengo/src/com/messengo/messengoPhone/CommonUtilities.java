package com.messengo.messengoPhone;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;



public final class CommonUtilities {

	static final String SERVER_URL = "http://messengo.webia-asso.fr/gcm_server/register.php";
	static final String SENDER_ID = "245096161427";
	static final String TAG = "Messengo";


	public static String getEmail(Context context) {
		//		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		//		Account[] accounts = AccountManager.get(context).getAccounts();
		//		for (Account account : accounts) {
		//			if (emailPattern.matcher(account.name).matches()) {
		//				return account.name;
		//			}
		//		}
		//		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		//		Account[] accounts = AccountManager.get(context).getAccounts();
		//		for (Account account : accounts) {
		//			if (emailPattern.matcher(account.name).matches()) {
		//				return account.name;
		//			}
		//		}
		AccountManager	googleAccountManager;
		Account[] 		allAccounts;


		googleAccountManager = AccountManager.get(context);
		allAccounts = googleAccountManager.getAccountsByType("com.google");  


		return allAccounts[0].name;
	}
}