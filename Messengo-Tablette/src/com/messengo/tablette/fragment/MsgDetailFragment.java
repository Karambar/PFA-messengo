package com.messengo.tablette.fragment;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.messengo.tablette.activity.ListMsgActivity;
import com.messengo.tablette.activity.R;
import com.messengo.tablette.adapter.MsgDetailAdapter;
import com.messengo.tablette.bean.Contact;
import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;
import com.messengo.tablette.bean.User;
import com.messengo.tablette.util.CommonUtilities;
import com.messengo.tablette.util.Configuration;
import com.messengo.tablette.webservice.WebService;

public class MsgDetailFragment extends Fragment implements OnClickListener, Runnable{

	private ListView				msgList;
	private Button					send;
	private EditText				msgArea;
	private MsgDetailAdapter		msgAdapter;
	private ArrayList<Message> 		data =  new ArrayList<Message>();
	private Conversation			conversation;
	private EditText				contactArea;
	
	private LinearLayout			msgLayoutEmpty;
	private LinearLayout			msgLayout;
	private LinearLayout			layoutAddContact;
	
	private ProgressDialog 			dialog;
	private static final int 		HANDLER_START_PROGRESS = 1;
	private static final int 		HANDLER_STOP_PROGRESS_SUCCESS = 2;
	private static final int 		HANDLER_STOP_PROGRESS_ERROR_NETWORK = 3;
	private static final int		HANDLER_STOP_PROGRESS_ERROR_TRANSMISSION = 4;

	
	private User					myUser = null;
	
	private AutoCompleteTextView 	ContactView;
	private ArrayAdapter			contactAdapter;
	private List<Contact>			contactList;
	
	
	
	private ArrayList<Map<String, String>> mPeopleList;
	private SimpleAdapter mAdapter;
	
/*
 * Flag Type of view
 */
	private int						typeOfView;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        View mainView = inflater.inflate(R.layout.msgdetail_fragment, container, false);		
		
		Intent i = getActivity().getIntent();
        if (i != null){
        	conversation = (Conversation)i.getSerializableExtra(ListMsgActivity.INTENT_DATA);
        	if (conversation != null)
        		data = conversation.getConversation();
        	typeOfView = i.getIntExtra(Configuration.FLAG_MSG_TYPE_OF_VIEW, Configuration.STATVIEW_EMPTY_CONVERSATION);
            myUser = (User)i.getSerializableExtra(Configuration.INTENT_USER);
        }
        
        msgLayoutEmpty = (LinearLayout)mainView.findViewById(R.id.LayoutDetailMessageEmpty);
        msgLayout = (LinearLayout)mainView.findViewById(R.id.LayoutDetailMessage);
        layoutAddContact = (LinearLayout)mainView.findViewById(R.id.LinearLayoutContacts);
        
        ContactView = (AutoCompleteTextView)mainView.findViewById(R.id.autoCompleteContact);

        
        if ((data == null || data.isEmpty()) && typeOfView == Configuration.STATVIEW_EMPTY_CONVERSATION){
			msgLayoutEmpty.setVisibility(View.VISIBLE);
			msgLayout.setVisibility(View.GONE);
		}else if (typeOfView == Configuration.STATVIEW_NEWMESSAGE){
			layoutAddContact.setVisibility(View.VISIBLE);
			data = new ArrayList<Message>();
		}
        
        send = (Button)mainView.findViewById(R.id.buttonSend);
        msgArea = (EditText)mainView.findViewById(R.id.editTextArea);
        contactArea = (EditText)mainView.findViewById(R.id.editTextContactList);

        msgAdapter = new MsgDetailAdapter(getActivity(), data);
        msgList = (ListView)mainView.findViewById(R.id.listViewMsgDetail);
        msgList.setAdapter(msgAdapter);
        send.setOnClickListener(this);
       
        

        
  
        
        contactList = CommonUtilities.getContactArray(getActivity());
        contactAdapter = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_dropdown_item_1line, contactList);
        ContactView.setAdapter(contactAdapter);

		return mainView;
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.buttonSend){
			new Thread(this).start();
		}
	}
	
	public void updateMessageDetails(Conversation Obj){
    	conversation  = Obj;
		data = Obj.getConversation();
    	msgAdapter.updateData(data);
    	msgList.setAdapter(msgAdapter);
    	if (data == null || data.isEmpty()){
    		msgLayoutEmpty.setVisibility(View.VISIBLE);
			msgLayout.setVisibility(View.GONE);
		}else{
			msgLayoutEmpty.setVisibility(View.GONE);
			msgLayout.setVisibility(View.VISIBLE);		
		}
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
	
	private void updateStatView(){
		if (typeOfView == Configuration.STATVIEW_CONVERSATION){
			msgLayoutEmpty.setVisibility(View.GONE);
			msgLayout.setVisibility(View.VISIBLE);
			layoutAddContact.setVisibility(View.GONE);
		}else if (typeOfView == Configuration.STATVIEW_EMPTY_CONVERSATION){
			msgLayoutEmpty.setVisibility(View.VISIBLE);
			msgLayout.setVisibility(View.GONE);
			layoutAddContact.setVisibility(View.GONE);	
		}else{
			msgLayoutEmpty.setVisibility(View.GONE);
			msgLayout.setVisibility(View.VISIBLE);
			layoutAddContact.setVisibility(View.VISIBLE);
		}
		msgAdapter = new MsgDetailAdapter(getActivity(), data);
        msgList.setAdapter(msgAdapter);
	}
	
	private Handler handlerUpdate = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg){
			if (msg.what == HANDLER_START_PROGRESS){
				progressDialogue("Envoi du message en cours ...");
			}else if (msg.what == HANDLER_STOP_PROGRESS_SUCCESS){
				Toast.makeText(getActivity(), "Votre message a été envoyé avec succés", Toast.LENGTH_SHORT).show();	
				msgArea.setText("");
				contactArea.setText("");
				typeOfView = Configuration.STATVIEW_CONVERSATION;
				updateStatView();
				dialog.cancel();		
			}else if (msg.what == HANDLER_STOP_PROGRESS_ERROR_NETWORK){
				Toast.makeText(getActivity(), "Nous ne pouvons communiquer avec le serveur", Toast.LENGTH_SHORT).show();
				dialog.cancel();		
			}else if (msg.what == HANDLER_STOP_PROGRESS_ERROR_TRANSMISSION){
				Toast.makeText(getActivity(), "Nous n'avons pu envoyer votre message. Veuillez rehessayer", Toast.LENGTH_SHORT).show();
				dialog.cancel();		
			}
			super.handleMessage(msg);	
		}
	};
	
	public void run() {
		String msg = msgArea.getText().toString();
		if (msg != null && !msg.isEmpty() && myUser != null){
			handlerUpdate.sendEmptyMessage(HANDLER_START_PROGRESS);	
			ArrayList<String> args = new ArrayList<String>();
			args.add(myUser.getIdGoogle());
			args.add(myUser.getPassphrase());			
			args.add("0");
			if (typeOfView == Configuration.STATVIEW_NEWMESSAGE){
				String number = "";
				if (( number = CommonUtilities.getPhonenUmberFromContactList(ContactView.getText().toString(), contactList)) != null)
					args.add(number);
				else
					args.add(ContactView.getText().toString());
			}else{
				args.add(conversation.getUserTel());
			}
			args.add(URLEncoder.encode(msg));
			try {
				String response = WebService.getInstance().downloadUrl("http://messengo.webia-asso.fr/webservice", "addMessage", args);				
				try {
					parseResponse(response);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void parseResponse(String response) throws JSONException {
		Log.i("ServerInformations", response);		
		if (response != null){
			JSONObject objectCreated = new JSONObject(response);
			JSONObject responseObject = objectCreated.optJSONObject("response");			
			if (responseObject != null 
					&& Integer.valueOf(responseObject.optString("code")) == 200){
				
				handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_SUCCESS);	
			}else{
				handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_TRANSMISSION);	
			}	
		}else{
			handlerUpdate.sendEmptyMessage(HANDLER_STOP_PROGRESS_ERROR_NETWORK);	
		}
	}

	public void startNewConversation() {
		typeOfView = Configuration.STATVIEW_NEWMESSAGE;
		data = new ArrayList<Message>();
	    msgAdapter = new MsgDetailAdapter(getActivity(), data);
        msgList.setAdapter(msgAdapter);  
		layoutAddContact.setVisibility(View.VISIBLE);
	}
}
