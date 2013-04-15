package com.messengo.tablette.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.fragment.ListMsgFragment;
import com.messengo.tablette.fragment.MsgDetailFragment;
import com.messengo.tablette.gcm.ConnectionManager;
import com.messengo.tablette.util.Configuration;
import com.messengo.tablette.webservice.AuthService;

public class ListMsgActivity extends FragmentActivity {

	public final static String		INTENT_DATA = "data";
	public final static String		INTENT_USER = "user";
		
	private ConnectionManager cntMgr;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	 	setContentView(R.layout.listmsg_activity);
	 
	 	final SharedPreferences settings = this.getSharedPreferences(AuthService.PREF_NAME, 0);
		String accessToken = settings.getString(AuthService.PREF_TOKEN, "");	
	
		cntMgr = new ConnectionManager(this);
		cntMgr.registerGCM();
		cntMgr.connectToWS(accessToken);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.actionbar_listmsg, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			return true;
		case R.id.menu_help:
			return true;
		case R.id.menu_refresh:
			refreshListView();
			return true;
		case R.id.menu_settings:
			return true;
		case R.id.menu_newmsg:
			onNewConversation();
			return true;
		case R.id.menu_deco:
			final SharedPreferences settings = getSharedPreferences(LoginActivity.PREF_NAME, 0);
			final SharedPreferences.Editor editor = settings.edit();
			editor.putInt(LoginActivity.PREF_STAT, LoginActivity.PREF_NOT_LOGIN);
			editor.commit();
			Intent i = new Intent(this, LoginActivity.class);
			this.finish();
			this.startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void refreshListView(){
		ListMsgFragment viewer = (ListMsgFragment)getSupportFragmentManager().findFragmentById(R.id.menu_fragment);

		  if (viewer == null || !viewer.isInLayout()){

		  }else{
			  viewer.updateListView();
		  }
	}
	
	public void onNewConversation(){
		MsgDetailFragment viewer = (MsgDetailFragment)getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
		
	    if (viewer == null || !viewer.isInLayout()) {
	    	Intent detailIntent = new Intent(getApplicationContext(), MsgDetailActivity.class);
	    	detailIntent.putExtra(Configuration.FLAG_MSG_TYPE_OF_VIEW, Configuration.STATVIEW_NEWMESSAGE);
	    	detailIntent.putExtra(Configuration.INTENT_USER, ((ListMsgFragment)getSupportFragmentManager().findFragmentById(R.id.menu_fragment)).getMyUser());
	    	this.startActivity(detailIntent);	
	    }else{
	        viewer.startNewConversation();
	    }
	}
	
	
	public void onObjectChoosen(Conversation Obj){
		
		MsgDetailFragment viewer = (MsgDetailFragment)getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
		
	    if (viewer == null || !viewer.isInLayout()) 
	    {
	    	Intent detailIntent = new Intent(getApplicationContext(), MsgDetailActivity.class);
	    	detailIntent.putExtra(ListMsgActivity.INTENT_DATA, Obj);
	    	detailIntent.putExtra(Configuration.INTENT_USER, ((ListMsgFragment)getSupportFragmentManager().findFragmentById(R.id.menu_fragment)).getMyUser());
	    	detailIntent.putExtra(Configuration.FLAG_MSG_TYPE_OF_VIEW, Configuration.STATVIEW_CONVERSATION);
	    	this.startActivity(detailIntent);
	    } 
	    else 
	    {
	        viewer.updateMessageDetails(Obj);
	    }
	}
	
	
	
	
		
	
	
	
}
