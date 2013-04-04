package com.messengo.tablette.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.messengo.tablette.bean.User;

public class BeginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		final SharedPreferences settings = this.getSharedPreferences(LoginActivity.PREF_NAME, 0);
		if (settings.getInt(LoginActivity.PREF_STAT, LoginActivity.PREF_NOT_LOGIN) == LoginActivity.PREF_ALREADY_LOGIN)
		{
			Intent i = new Intent(this, ListMsgActivity.class);
			User myUser = new User();
			myUser.seteMail(settings.getString(LoginActivity.PREF_USER_EMAIL, null));
			myUser.setIdGoogle(settings.getString(LoginActivity.PREF_USER_IDGOOGLE, null));
			myUser.setPassphrase(settings.getString(LoginActivity.PREF_USER_PASSPHRASE, null));
			i.putExtra(ListMsgActivity.INTENT_USER, myUser);
			this.startActivity(i);
		}else{
			Intent i = new Intent(this, LoginActivity.class);
			this.startActivity(i);			
		}
		finish();
	}
}
