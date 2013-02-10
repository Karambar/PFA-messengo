package com.messengo.tablette.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.messengo.tablette.activity.ListMsgActivity;
import com.messengo.tablette.activity.MsgDetailActivity;
import com.messengo.tablette.activity.R;
import com.messengo.tablette.adapter.MsgListAdapter;
import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.webservice.WebService;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListMsgFragment extends Fragment implements OnItemClickListener, Runnable{

	private	ListView				msgList;
	private	MsgListAdapter			msgAdapter;

	private ArrayList<Conversation> data =  new ArrayList<Conversation>();
		
	private User 					myUser = null;

	private static final int HANDLER_START_PROGRESS = 1;
	private static final int HANDLER_STOP_PROGRESS_SUCCESS = 2;
	private static final int HANDLER_STOP_PROGRESS_ERROR_NETWORK = 3;

	private ProgressDialog dialog;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View mainView = inflater.inflate(R.layout.listmsg_fragment, container, false);		
	
		Intent i = getActivity().getIntent();
		if (i != null)
			myUser = (User)i.getExtras().getSerializable(ListMsgActivity.INTENT_USER);	
		msgList = (ListView)mainView.findViewById(R.id.listView1);
		new Thread(this).start();	
		
		return mainView;
	}
	
	private void updateListView(){
		if (data.isEmpty() || data == null)
			 Toast.makeText(getActivity(), "Vous n'avez aucun messages.", Toast.LENGTH_SHORT).show();
		msgAdapter = new MsgListAdapter(getActivity(), data);
		msgList.setAdapter(msgAdapter);
		msgList.setOnItemClickListener(this);
	}


	private void parseMessagesList(String response) throws JSONException {

		int					i = -1, j = -1;
		Conversation 		tmpConversation;
		ArrayList<Message>	tmpAllMessages = new ArrayList<Message>();
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
				handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);	
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    ((ListMsgActivity)getActivity()).onObjectChoosen(data.get(position).getConversation());
		
//		Intent intent = new Intent(getActivity().getApplicationContext(), MsgDetailActivity.class);
//		intent.putExtra(ListMsgActivity.INTENT_DATA, data.get(position).getConversation());
//		this.startActivity(intent);
	}

	/*
	 * Fait apparaitre un dialogue de progression.
	 */
	public void progressDialogue(String myMessage){
		dialog = new ProgressDialog(getActivity());
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
				updateListView();
				dialog.cancel();		
			}else if (msg.what == HANDLER_STOP_PROGRESS_ERROR_NETWORK){
				Toast.makeText(getActivity(), "Nous n'avons pu recuperer vos messages.", Toast.LENGTH_SHORT).show();
			}
			super.handleMessage(msg);	
		}
	};

	public void run() {
		handlerUpdate.sendEmptyMessage(HANDLER_START_PROGRESS);	
		
		ArrayList<String> args = new ArrayList<String>();
//		args.add(myUser.getIdGoogle());
//		args.add(myUser.getPassphrase());
		args.add("100423822029379370732");
		args.add("2dRCNORjxsBpcpXi6j57gaRZdWRXV9tF");

		try {
			String response = WebService.getInstance().downloadUrl("http://messengo.webia-asso.fr/webservice", "getMessages", args);
			parseMessagesList(response);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_SUCCESS);				
	}

	
}
