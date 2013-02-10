package com.messengo.tablette.activity;


import java.util.ArrayList;

import com.messengo.tablette.bean.Message;
import com.messengo.tablette.fragment.ListMsgFragment;
import com.messengo.tablette.fragment.MsgDetailFragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ListMsgActivity extends FragmentActivity {

	public final static String		INTENT_DATA = "data";
	public final static String		INTENT_USER = "user";
		
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	 	setContentView(R.layout.listmsg_activity);
	}

	public void onObjectChoosen(ArrayList<Message> Obj){
		
		MsgDetailFragment viewer = (MsgDetailFragment)getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
		
	    if (viewer == null || !viewer.isInLayout()) 
	    {
	    	Intent detailIntent = new Intent(getApplicationContext(), MsgDetailActivity.class);
	    	detailIntent.putExtra(ListMsgActivity.INTENT_DATA, Obj);
	    	this.startActivity(detailIntent);
	    } 
	    else 
	    {
	        viewer.updateMessageDetails(Obj);
	    }
	}
}
