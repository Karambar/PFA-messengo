package com.messengo.tablette.adapter;

import java.util.ArrayList;

import com.messengo.tablette.activity.R;
import com.messengo.tablette.bean.Message;

import android.accounts.Account;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AccountsAdapter extends BaseAdapter{

	private Account[] data = null;
	private LayoutInflater inflater;
	@SuppressWarnings("unused")
	private Context context;
	
	public AccountsAdapter(Context context, Account[] data){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.data = data;
	}
	
	public int getCount() {
		if (data != null)
			return data.length;
		return -1;
	}

	public Object getItem(int position) {
		return data[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.cell_email, null);
		TextView email = (TextView)convertView.findViewById(R.id.textViewEmail);
		email.setText(data[position].name);
		return convertView;
	}

}
