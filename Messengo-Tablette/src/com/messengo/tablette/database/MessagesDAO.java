package com.messengo.tablette.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;

public class MessagesDAO {
	  private SQLiteDatabase database;
	  private DatabaseHelper dbHelper;
	  private String[] allColumns = { DatabaseHelper.COLUMN_ID,
			  DatabaseHelper.COLUMN_MESSAGE,
			  DatabaseHelper.COLUMN_DATE,
			  DatabaseHelper.COLUMN_WITH_USER_NAME,
			  DatabaseHelper.COLUMN_WITH_USER_PHONE,
			  DatabaseHelper.COLUMN_WITH_USER_ID,
			  DatabaseHelper.COLUMN_EXP_IS_ME  };

	  public MessagesDAO(Context context) {
	    dbHelper = new DatabaseHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Boolean createMessage(Message msg, String userName, String userPhone, String userID) {
		  Cursor idMsgDb = database.query(DatabaseHelper.TABLE_MESSAGES, 
				  new String[] {DatabaseHelper.COLUMN_ID_MESSAGE}, DatabaseHelper.COLUMN_ID_MESSAGE + " LIKE \"" + String.valueOf(msg.getId()) + "\"" , null, null, null, null);

		  idMsgDb.moveToFirst();
		  if (msg == null)
			  return false;
		  if (!idMsgDb.isAfterLast())
			  if (idMsgDb.getString(0) == null || Integer.valueOf(idMsgDb.getString(0)) == msg.getId())
				  return false;
		ContentValues values = new ContentValues();
	    values.put(DatabaseHelper.COLUMN_MESSAGE, msg.getMsg());
	    values.put(DatabaseHelper.COLUMN_ID_MESSAGE, String.valueOf(msg.getId()));
	    values.put(DatabaseHelper.COLUMN_DATE, msg.getDate());
	    if (msg.isMine())
	    	values.put(DatabaseHelper.COLUMN_EXP_IS_ME, "1");
	    else
	    	values.put(DatabaseHelper.COLUMN_EXP_IS_ME, "0");
	    values.put(DatabaseHelper.COLUMN_WITH_USER_ID, userID);
	    values.put(DatabaseHelper.COLUMN_WITH_USER_NAME, userName);
	    values.put(DatabaseHelper.COLUMN_WITH_USER_PHONE, userPhone);
	    long insertId = database.insert(DatabaseHelper.TABLE_MESSAGES, null,
	        values);
	    if (insertId == -1)
	    	return false;
	    return true;
	  }
  
	  public void deleteComment(Message msg) {
	    long id = msg.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(DatabaseHelper.TABLE_MESSAGES, DatabaseHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	  public ArrayList<Message> getAllMessagesFromUserId(Integer id){
		  
		  return null;
	  }
	  
	  public ArrayList<Conversation> getAllMessages() {
		  ArrayList<Conversation> allMessages = new ArrayList<Conversation>();

		  Cursor withUserIds = database.query(DatabaseHelper.TABLE_MESSAGES, 
				  new String[] {DatabaseHelper.COLUMN_WITH_USER_ID}, null, null, DatabaseHelper.COLUMN_WITH_USER_ID, null, null);

		  withUserIds.moveToFirst();
		  while (!withUserIds.isAfterLast()){
			  Conversation convers = new Conversation();
			  Cursor msg = database.query(DatabaseHelper.TABLE_MESSAGES, allColumns, DatabaseHelper.COLUMN_WITH_USER_ID + " LIKE \"" + withUserIds.getString(0) + "\"" , null, null, null, null);
			  convers.setUserId(Integer.valueOf(withUserIds.getString(0)));
			  msg.moveToFirst();
			  ArrayList<Message> allMsg = new ArrayList<Message>();
			  while (!msg.isAfterLast()){
				  Message tmp = new Message();
				  tmp.setDate(msg.getString(2));
				  tmp.setId(msg.getInt(0));
				  if (msg.getString(6).equals("1"))
					  tmp.setMine(true);
				  else
					  tmp.setMine(false);
				  tmp.setMsg(msg.getString(1));
				  Log.i("Database", "Add Message To response");
				  allMsg.add(tmp);
				  msg.moveToNext();
			  }
			  msg.moveToLast();
			  convers.setUserName(msg.getString(3));
			  convers.setUserTel(msg.getString(4));
			  convers.setConversation(allMsg);
			  allMessages.add(convers);
			  msg.close();
			  withUserIds.moveToNext();
		  }
	    withUserIds.close();
	    int i = -1;
	    while (++i < allMessages.size()){
		    Log.i("Database", allMessages.get(i).toString());	    	
	    }
	    return allMessages;
	  }

	private Message cursorToMessage(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}
}
