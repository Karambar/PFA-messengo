package com.messengo.tablette.fragment;

import java.net.URI;
import java.util.ArrayList;

import com.messengo.tablette.activity.ListMsgActivity;
import com.messengo.tablette.activity.R;
import com.messengo.tablette.adapter.MsgDetailAdapter;
import com.messengo.tablette.bean.Message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MsgDetailFragment extends Fragment implements OnClickListener{

	private ListView				msgList;
	private Button					send;
	private EditText				msgArea;
	private MsgDetailAdapter		msgAdapter;
	private ArrayList<Message> data =  new ArrayList<Message>();
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        View mainView = inflater.inflate(R.layout.msgdetail_fragment, container, false);		
		
		Intent i = getActivity().getIntent();
        if (i != null){
        	data = (ArrayList<Message>)i.getSerializableExtra(ListMsgActivity.INTENT_DATA);
        }
		
        send = (Button)mainView.findViewById(R.id.buttonSend);
        msgArea = (EditText)mainView.findViewById(R.id.editTextArea);
        msgAdapter = new MsgDetailAdapter(getActivity(), data);
        msgList = (ListView)mainView.findViewById(R.id.listViewMsgDetail);
        msgList.setAdapter(msgAdapter);
        send.setOnClickListener(this);
		return mainView;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.buttonSend){
			@SuppressWarnings("unused")
			String msg = msgArea.getText().toString();
		}
		
	}
	
	public void updateMessageDetails(ArrayList<Message> Obj){
    	data = Obj;
    	msgAdapter.updateData(Obj);
    	msgList.setAdapter(msgAdapter);
	}
	
}
