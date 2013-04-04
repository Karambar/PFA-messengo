package com.messengo.tablette.activity;


import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.messengo.tablette.adapter.AccountsAdapter;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.webservice.AuthService;
import com.messengo.tablette.webservice.WebService;

public class LoginActivity extends Activity implements OnClickListener, OnItemClickListener, Runnable {

	@SuppressWarnings("unused")
	private Account			gmailAddress;
	private Button			btnSend;
	private ListView		gmailAccounts;
	private AccountsAdapter	accountsAdapter;
	
	private  AccountManager	googleAccountManager;

	private String			RegistrationId = "";
	private Account[] 		allAccounts;
	
	private static final int HANDLER_START_PROGRESS = 1;
	private static final int HANDLER_STOP_PROGRESS_SUCCESS = 2;
	private static final int HANDLER_STOP_PROGRESS_ERROR_NETWORK = 3;
	
	public static final String PREF_NAME = "login";
	public static final String PREF_STAT = "loginStat";
	public static final String PREF_USER_EMAIL = "userMail";
	public static final String PREF_USER_IDGOOGLE = "userID";
	public static final String PREF_USER_PASSPHRASE = "userPassphrase";
	
	public static final int		PREF_ALREADY_LOGIN = 1;
	public static final int		PREF_NOT_LOGIN = 0;
	
	private ImageView		previewsVisibleTick;
	
	private ProgressDialog dialog;
	
	private	User			myUser = null;

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);        
        googleAccountManager = AccountManager.get(this);
        allAccounts = googleAccountManager.getAccountsByType("com.google");  
        gmailAccounts = (ListView)findViewById(R.id.listViewGoogleAccount);
        accountsAdapter = new AccountsAdapter(this, allAccounts);
        gmailAccounts.setAdapter(accountsAdapter);
        gmailAccounts.setOnItemClickListener(this);  
		btnSend = (Button)findViewById(R.id.buttonLogin);
		btnSend.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.buttonLogin){
			if (gmailAddress != null){
				new Thread(this).start();
			}else{
				Toast.makeText(this, "Veulliez selectionner une addresse", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ImageView tick = (ImageView)view.findViewById(R.id.imageViewTick);
		if (tick.isShown()){
			tick.setVisibility(View.GONE);
			gmailAddress = null;
		}else{
			if (previewsVisibleTick != null){
				previewsVisibleTick.setVisibility(View.GONE);
			}
			tick.setVisibility(View.VISIBLE);
			gmailAddress = allAccounts[position];
			previewsVisibleTick = tick;
		}
	}

	/**
	 * @return the registrationId
	 */
	public String getRegistrationId() {
		return RegistrationId;
	}

	/**
	 * @param registrationId the registrationId to set
	 */
	public void setRegistrationId(String registrationId) {
		RegistrationId = registrationId;
	}
	
	/*
	 * Fait apparaitre un dialogue de progression.
	 */
	public void progressDialogue(String myMessage){
		dialog = new ProgressDialog(this);
		dialog.setMessage(myMessage);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
	}
	
	private Handler handlerUpdate = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg){
			if (msg.what == HANDLER_START_PROGRESS){
				progressDialogue("Récupération des informations en cours ...");
			}else if (msg.what == HANDLER_STOP_PROGRESS_SUCCESS){
				dialog.cancel();
				// Save that you are login
				final SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
				final SharedPreferences.Editor editor = settings.edit();
				editor.putInt(PREF_STAT, PREF_ALREADY_LOGIN);
				editor.putString(PREF_USER_EMAIL, myUser.geteMail());
				editor.putString(PREF_USER_IDGOOGLE, myUser.getIdGoogle());
				editor.putString(PREF_USER_PASSPHRASE, myUser.getPassphrase());
				editor.commit();				
				//Start new Activity
				Intent i = new Intent(LoginActivity.this, ListMsgActivity.class);
				i.putExtra(ListMsgActivity.INTENT_USER, myUser);
				LoginActivity.this.finish();
				LoginActivity.this.startActivity(i);
			}else if (msg.what == HANDLER_STOP_PROGRESS_ERROR_NETWORK){
				Toast.makeText(LoginActivity.this, "Le serveur est innaccessible, veuillez essayer ulterieurement", Toast.LENGTH_SHORT).show();
				dialog.cancel();
			}
			super.handleMessage(msg);
		}
	};

	public void run() {
		handlerUpdate.sendEmptyMessage(HANDLER_START_PROGRESS);
		AuthService.getInstance().refreshAuthToken(this, gmailAddress);
		
		final SharedPreferences settings = this.getSharedPreferences(AuthService.PREF_NAME, 0);
		String accessToken = settings.getString(AuthService.PREF_TOKEN, "");	
		ArrayList<String> args = new ArrayList<String>();
		args.add(accessToken);
		args.add(gmailAddress.name);
		try {
			String response = WebService.getInstance().downloadUrl("http://messengo.webia-asso.fr/webservice", "connection", args);
			parseConnection(response);
		} catch (IOException e) {
			handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);	
			e.printStackTrace();
		} catch (JSONException e) {
			handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);	
			e.printStackTrace();
		}
	}
	private static final String TAG = "WebService";

	private void parseConnection(String response) throws JSONException {
		
		Log.i(TAG, response);
		if (response != null){
			JSONObject objectCreated = new JSONObject(response);
			JSONObject responseObject = objectCreated.optJSONObject("response");			
			if (responseObject != null 
					&& Integer.valueOf(responseObject.optString("code")) == 200){
				
				JSONObject infoObject = objectCreated.getJSONObject("infos");
				myUser = new User();
				myUser.setBirthday(infoObject.getString("birthday"));
				myUser.seteMail(infoObject.getString("email"));
				myUser.setIdGoogle(infoObject.getString("idGoogle"));
				myUser.setLocale(infoObject.getString("locale"));
				if (infoObject.getString("isMale").equals("1"))
					myUser.setMale(true);
				else
					myUser.setMale(false);
				myUser.setName(infoObject.getString("name"));
				myUser.setPicture(infoObject.getString("picture"));
				myUser.setShortName(infoObject.getString("shortName"));
				myUser.setPassphrase(infoObject.getString("passphrase"));
				handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_SUCCESS);				
			}else{
				handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);
			}
		}else{
			handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);	
		}
	}
	
}
