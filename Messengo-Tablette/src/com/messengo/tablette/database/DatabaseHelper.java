package com.messengo.tablette.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	  public static final String TABLE_MESSAGES = "messages";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_MESSAGE = "message";
	  public static final String COLUMN_ID_MESSAGE = "idMessage";
	  public static final String COLUMN_DATE = "date";
	  public static final String COLUMN_WITH_USER_NAME = "with_username";
	  public static final String COLUMN_WITH_USER_PHONE = "with_userphone";
	  public static final String COLUMN_WITH_USER_ID = "with_userid";
	  public static final String COLUMN_EXP_IS_ME = "epx_is_me";
	  
	  private static final String DATABASE_NAME = "messengo.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_MESSAGES + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_MESSAGE + " text not null, "
	      + COLUMN_ID_MESSAGE + " text not null, "
	      + COLUMN_DATE + " text not null, "
	      + COLUMN_WITH_USER_NAME + " text not null, "
	      + COLUMN_WITH_USER_PHONE + " text not null, "
	      + COLUMN_WITH_USER_ID + " text not null, "
	      + COLUMN_EXP_IS_ME + " text not null "
	      +");";

	  public DatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
	    onCreate(db);
	  }

}
