package com.messengo.tablette.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.messengo.tablette.bean.User;
import com.messengo.tablette.services.UpdateService;

public class BeginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		Intent intent = new Intent(this, UpdateService.class); 
		if (this.startService(intent) == null)
			Log.i("UpdateServiceMessengo", "The service isn't started, pease try again");		
	
		
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
