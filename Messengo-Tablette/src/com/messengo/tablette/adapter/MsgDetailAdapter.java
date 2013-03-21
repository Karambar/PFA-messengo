package com.messengo.tablette.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.messengo.tablette.activity.R;
import com.messengo.tablette.bean.Message;

public class MsgDetailAdapter extends BaseAdapter{

	private ArrayList<Message> data = null;
	private LayoutInflater inflater;
	@SuppressWarnings("unused")
	private Context context;
	
	public MsgDetailAdapter(Context context, ArrayList<Message> data){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.data = data;
	}
	
	public void updateData(ArrayList<Message> data){
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
		convertView = inflater.inflate(R.layout.cell_msgdetail, null);
		if (position >= data.size())
			return convertView;
		
		TextView msg = (TextView)convertView.findViewById(R.id.textViewMsg);
		msg.setText(data.get(position).getMsg());
		if (data.get(position).isMine()){
			((ImageView)convertView.findViewById(R.id.imageViewMe)).setVisibility(View.VISIBLE);
			((ImageView)convertView.findViewById(R.id.imageViewReciver)).setVisibility(View.GONE);
		}
		return convertView;
	}

}