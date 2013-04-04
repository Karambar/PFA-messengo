package com.messengo.tablette.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.messengo.tablette.bean.Conversation;
import com.messengo.tablette.bean.Message;

public class MessagesDAO {
	  private SQLiteDatabase database;
	  private DatabaseHelper dbHelper;
	  private String[] allColumns = { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_MESSAGE };

	  public MessagesDAO(Context context) {
	    dbHelper = new DatabaseHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

//	  public Message createMessage(String msg) {
//	    ContentValues values = new ContentValues();
//	    values.put(DatabaseHelper.COLUMN_MESSAGE, msg);
//	    long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
//	        values);
//	    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
//	        allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
//	        null, null, null);
//	    cursor.moveToFirst();
//	    Comment newComment = cursorToComment(cursor);
//	    cursor.close();
//	    return newComment;
//	  }

	  public void deleteComment(Message msg) {
	    long id = msg.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(DatabaseHelper.TABLE_MESSAGES, DatabaseHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	  public ArrayList<Conversation> getAllMessages() {
		  ArrayList<Conversation> comments = new ArrayList<Conversation>();

		  Cursor cursor = database.query(DatabaseHelper.TABLE_MESSAGES,
				  allColumns, null, null, null, null, null);
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  Message comment = cursorToMessage(cursor);
			  cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return comments;
	  }

	private Message cursorToMessage(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}
}
