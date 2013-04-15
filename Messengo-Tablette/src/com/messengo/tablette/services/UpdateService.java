package com.messengo.tablette.services;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.messengo.tablette.activity.LoginActivity;
import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.database.MessagesDAO;
import com.messengo.tablette.webservice.WebService;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service implements IUpdateService{
	private Timer timer ; 
	private User myUser;
	private MessagesDAO dao;
	private List<IUpdateServiceListener> listeners = null; 
    private static IUpdateService service; 
    private static final String TAG = "UpdateServiceMessengo";
    
	@Override 
	public void onCreate() { 
	    super.onCreate(); 
        service = this; 
	    timer = new Timer();
		dao = new MessagesDAO(this);
		dao.open();
	    Log.d(TAG, "onCreate"); 
	} 
	 
	public static IUpdateService getService() { 
		return service;
	} 
	
	private void parseMessagesList(String response) throws JSONException {

		int					i = -1, j = -1;
		Conversation 		tmpConversation;
		Message				tmpMessage;

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
					while (++j < allMessages.length()){
						tmpMessage = new Message();
						JSONObject aMessage = allMessages.getJSONObject(j);
						tmpMessage.setDate(aMessage.optString("date_msg"));
						tmpMessage.setMsg(aMessage.optString("msg"));
						tmpMessage.setId(Integer.valueOf(aMessage.optString("id_message")));
						if (aMessage.optString("exp_is_me").equals("0"))
							tmpMessage.setMine(false);
						else
							tmpMessage.setMine(true);
						
						if (dao.createMessage(tmpMessage, tmpConversation.getUserName(), tmpConversation.getUserTel(), String.valueOf(tmpConversation.getUserId())) == true)
							notifyListenersDatachanged();						
					}
				}
			}else{
			}	
		}
		else{
		}
	}
	
	private void notifyListenersDatachanged() {
		int i = -1;
		while (++i < listeners.size()){
			listeners.get(i).dataChanged(null);
		}
	}

	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) { 
	    Log.d(TAG, "onStartCommand"); 
	    timer.scheduleAtFixedRate(new TimerTask() { 
	        public void run() { 
				final SharedPreferences settings = getSharedPreferences(LoginActivity.PREF_NAME, 0);
	    		if (settings.getInt(LoginActivity.PREF_STAT, LoginActivity.PREF_NOT_LOGIN) == LoginActivity.PREF_ALREADY_LOGIN){
	    			if (myUser == null){
	    				myUser = new User();
	    				myUser.seteMail(settings.getString(LoginActivity.PREF_USER_EMAIL, null));
	    				myUser.setIdGoogle(settings.getString(LoginActivity.PREF_USER_IDGOOGLE, null));
	    				myUser.setPassphrase(settings.getString(LoginActivity.PREF_USER_PASSPHRASE, null));
	    			}
		        	Log.i(TAG, "Update");	       
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
	    		}
	        }
	    }, 0, 10000); 
	 
	    return START_NOT_STICKY; 
	} 
	 
	@Override 
	public void onDestroy() { 
		Log.d(this.getClass().getName(), "onDestroy"); 
		this.listeners.clear(); 
	    this.timer.cancel(); 
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void addListener(IUpdateServiceListener listener) {
		if(listeners == null){ 
	        listeners = new ArrayList<IUpdateServiceListener>(); 
	    } 
	    listeners.add(listener); 		
	}

	public void removeListener(IUpdateServiceListener listener) {
		if(listeners != null){ 
	        listeners.remove(listener); 
	    } 		
	}
}
