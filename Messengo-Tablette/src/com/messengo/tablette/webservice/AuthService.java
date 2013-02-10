package com.messengo.tablette.webservice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class AuthService {
	private static final String TAG = AuthService.class.getName();

	public static final String PREF_NAME = "Sample App";
	public static final String PREF_TOKEN = "accessToken";

	private final static String OAUTH_SCOPE = "oauth2:";
	private final static String G_PLUS_SCOPE = "https://www.googleapis.com/auth/plus.me";
	private final static String USERINFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	private final static String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	private final static String SCOPE = OAUTH_SCOPE + USERINFO_SCOPE;

	private static AuthService instance;
	
	public static synchronized AuthService getInstance(){
		if (instance == null)
		{
			instance = new AuthService();
		}
		return instance;
	}
	
	private AuthService() {
	}
	
	public void refreshAuthToken(final Activity activity, final Account account) {
		final SharedPreferences settings = activity.getSharedPreferences(PREF_NAME, 0);
		String accessToken = settings.getString(PREF_TOKEN, "");
		
		final AccountManagerCallback<Bundle> cb = new AccountManagerCallback<Bundle>() {
			public void run(AccountManagerFuture<Bundle> future){
				try {
					final Bundle result = future.getResult();
					final String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
					final String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
					final Intent authIntent = result.getParcelable(AccountManager.KEY_INTENT);
					if (accountName != null && authToken != null) {
						final SharedPreferences.Editor editor = settings.edit();
						editor.putString(PREF_TOKEN, authToken);
						editor.commit();
					} else if (authIntent != null) {
						activity.startActivity(authIntent);
					} else {
						Log.e(TAG, "AccountManager was unable to obtain an authToken.");
					}
				} catch (OperationCanceledException e) {
					// TODO: The user has denied you access to the API, you
					// should handle that
				} catch (Exception e) {
					//handleException(e);
				}
			}
		};
		AccountManager.get(activity).invalidateAuthToken("com.google", accessToken);
		AccountManager.get(activity).getAuthToken(account, SCOPE, null, activity, cb, null);
	}
}