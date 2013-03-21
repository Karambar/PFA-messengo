package com.messengo.tablette.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messengo.tablette.activity.R;
import com.messengo.tablette.bean.Conversation;

public class MsgListAdapter extends BaseAdapter{

	private ArrayList<Conversation> data = null;
	private LayoutInflater inflater;
	@SuppressWarnings("unused")
	private Context context;
	
	public MsgListAdapter(Context context, ArrayList<Conversation> data){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.data = data;
	}
	
	public int getCount() {
		if (data != null)
			return data.size();
		return -1;
	}
	

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.cell_msglist, null);
		if (position >= data.size())
			return convertView;
		TextView reciever = (TextView)convertView.findViewById(R.id.textViewReciver);
		reciever.setText(data.get(position).getUserName());

		TextView date = (TextView)convertView.findViewById(R.id.textViewTime);
		date.setText(data.get(position).getConversation().get(0).getDate());

		TextView text = (TextView)convertView.findViewById(R.id.textViewMsgDetail);
		text.setText(data.get(position).getConversation().get(0).getMsg());
		TextView nbrOfMsg = (TextView)convertView.findViewById(R.id.textViewNbrMsg);
		nbrOfMsg.setText(String.valueOf(data.get(position).getConversation().size()));
		return convertView;
	}

}
