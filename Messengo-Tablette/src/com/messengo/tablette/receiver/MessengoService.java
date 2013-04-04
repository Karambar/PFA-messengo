package com.messengo.tablette.receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.messengo.tablette.activity.LoginActivity;
import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.webservice.WebService;

public class MessengoService extends Service{

	final static int DELAY = 600;
	private AtomicBoolean active= new AtomicBoolean(true);
	private ArrayList<Conversation> data =  new ArrayList<Conversation>();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("MessengoService", "Service started");
		new Thread(threadBody).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return(null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		active.set(false);
	}

	
	private void parseMessagesList(String response) throws JSONException {

		int					i = -1, j = -1;
		Conversation 		tmpConversation;
		ArrayList<Message>	tmpAllMessages = new ArrayList<Message>();
		Message				tmpMessage;

		Log.i("ServerInformations", response);
		
		data.clear();
		if (response != null){
			JSONObject objectCreated = new JSONObject(response);
			JSONObject responseObject = objectCreated.optJSONObject("response");			
			if (responseObject != null 
					&& Integer.valueOf(responseObject.optString("code")) == 200){
				JSONArray infoObject = objectCreated.getJSONArray("infos");
				while (infoObject != null && ++i < infoObject.length()){
					tmpConversation = new Conversation();
					JSONObject aConversation = infoObject.getJSONObject(i);
					tmpConversation.setUserId(Integer.valueOf(aConversation.optString("with_userId")));
					tmpConversation.setUserName(aConversation.optString("with_userName"));
					tmpConversation.setUserTel(aConversation.optString("with_userTel"));
					JSONArray allMessages = aConversation.optJSONArray("conversation");
					j = -1;
					tmpAllMessages = new ArrayList<Message>();
					while (++j < allMessages.length()){
						tmpMessage = new Message();
						JSONObject aMessage = allMessages.getJSONObject(j);
						tmpMessage.setDate(aMessage.optString("date_msg"));
						tmpMessage.setMsg(aMessage.optString("msg"));
						if (aMessage.optString("exp_is_me").equals("0"))
							tmpMessage.setMine(false);
						else
							tmpMessage.setMine(true);
						tmpAllMessages.add(tmpMessage);
					}
					tmpConversation.setConversation(tmpAllMessages);
					data.add(tmpConversation);
				}
			}else{
			}	
		}
		else{
		}
	}

	private Runnable threadBody = new Runnable() {
		public void run() {
			final SharedPreferences settings = getSharedPreferences(LoginActivity.PREF_NAME, 0);
			User myUser = new User();
			myUser.seteMail(settings.getString(LoginActivity.PREF_USER_EMAIL, null));
			myUser.setIdGoogle(settings.getString(LoginActivity.PREF_USER_IDGOOGLE, null));
			myUser.setPassphrase(settings.getString(LoginActivity.PREF_USER_PASSPHRASE, null));
					
			Log.i("MessengoService", "Start Thread");
			while(active.get())
			{
				ArrayList<String> args = new ArrayList<String>();
				args.add(myUser.getIdGoogle());
				args.add(myUser.getPassphrase());

				try {
					String response = WebService.getInstance().downloadUrl("http://messengo.webia-asso.fr/webservice", "getMessages", args);
					parseMessagesList(response);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	
				Log.i("MessengoService", "Update");
				SystemClock.sleep(DELAY);
				
			}
		}
	};
	
}
