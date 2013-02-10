package com.messengo.tablette.activity;


import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.messengo.tablette.adapter.AccountsAdapter;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.webservice.AuthService;
import com.messengo.tablette.webservice.WebService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	public static final String PREF_USER = "userMail";
	public static final int		PREF_ALREADY_LOGIN = 1;
	public static final int		PREF_NOT_LOGIN = 0;
	
	private ProgressDialog dialog;
	
	private	User			myUser = null;

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
//        final SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
//        if (settings.getInt(PREF_STAT, -1) == 1){
//    		String email = settings.getString(PREF_USER, "");
//        	
//        }

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
				
				
				//				GCMRegistrar.checkDevice(this);
				//				GCMRegistrar.checkManifest(this);
				//				if (GCMRegistrar.isRegistered(this)) {
				//					Log.d("info", GCMRegistrar.getRegistrationId(this));
				//				}
				//				RegistrationId = GCMRegistrar.getRegistrationId(this);
				//				if (RegistrationId.equals("")) {
				//					GCMRegistrar.register(this, "245096161427");
				//					Log.d("info", GCMRegistrar.getRegistrationId(this));
				//					RegistrationId = GCMRegistrar.getRegistrationId(this);
				//				} else {
				//					Log.d("info", "already registered as" + RegistrationId);
				//				}
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ImageView tick = (ImageView)view.findViewById(R.id.imageViewTick);
		tick.setVisibility(View.VISIBLE);
		gmailAddress = allAccounts[position];
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
				editor.putString(PREF_USER, myUser.geteMail());
				editor.commit();				
				//Start new Activity
				Intent i = new Intent(LoginActivity.this, ListMsgActivity.class);
				i.putExtra(ListMsgActivity.INTENT_USER, myUser);
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

	private void parseConnection(String response) throws JSONException {

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
			}
		}else{
			handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);	
		}
	}
	
}
