package com.messengo.messengoPhone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;


public class SmsManage {

	private Context ct;
	private File file;
	private static final Uri CONVERSATIONS_URI = Uri.parse("content://mms-sms/conversations/");
	private static final Uri SMS_URI = Uri.parse("content://sms/");
	private static final String[] SMS_COLUMNS = new String[] { "address", "person", "date", "type",
		"subject", "body" };
	private static final String TAG = "MESSENGO";


	public SmsManage(Context ct) {
		this.ct = ct;
	}

	public void sendViaMail() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"dommer.q@gmail.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Votre export XML de vos sms");
		intent.putExtra(Intent.EXTRA_TEXT, "Bonjour,\nVoici votre sauvegarde sms.\n\nL'équipe Messengo vous souhaite une excellente journée et une bonne soutenance.");
		if (!file.exists() || !file.canRead()) {
			Toast.makeText(ct, "Attachment Error", Toast.LENGTH_SHORT).show();
			return;
		}
		Uri uri = Uri.parse("file://" + file);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		ct.startActivity(Intent.createChooser(intent, "Envoyer par email..."));
	}

	public void extract() {
		Toast.makeText(ct, "The extract of your SMS is now starting in background...", Toast.LENGTH_LONG).show();
		new getSms().execute();
	}

	public class getSms extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.FRANCE);
			String currentDateandTime = sdf.format(new Date());
			File sdcard = Environment.getExternalStorageDirectory();
			File dir = new File (sdcard.getAbsolutePath() + "/Messengo/save/");
			dir.mkdirs();
			file = new File(dir, currentDateandTime + ".xml");
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				FileOutputStream fileos = null;
				try {
					fileos = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				fileos.write(startBackup().getBytes());
				fileos.flush();
				fileos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			sendViaMail();
		}
	}

	public String startBackup() {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();

		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);

			serializer.startDocument(null, Boolean.valueOf(true));
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, "conversations");

			Cursor cursor = ct.getContentResolver().query(CONVERSATIONS_URI,
					new String[] { "thread_id", "date" }, null, null, null);

			Log.i("MESSENGO", "Number of conversations: " + cursor.getCount());
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String threadId = cursor.getString(0);
					String date = cursor.getString(1);
					if (threadId == null) {
						continue;
					}
					Log.i("MESSENGO", "Conversation: " + threadId);

					serializer.startTag(null, "conversation");
					serializer.attribute(null, "thread_id", threadId);
					if (date != null) {
						serializer.attribute(null, "date", date);
					}

					Cursor cursor2 = ct.getContentResolver().query(
							Uri.withAppendedPath(CONVERSATIONS_URI, threadId),
							new String[] { "_id", "ct_t" }, null, null, null);
					if (cursor2 != null && cursor2.moveToFirst()) {
						do {
							String id = cursor2.getString(0);
							String ct_t = cursor2.getString(1);
							if (ct_t == null) {
								addSMS(serializer, id);
							}
						} while (cursor2.moveToNext());
					}
					serializer.endTag(null, "conversation");
					cursor2.close();
				} while (cursor.moveToNext());
			}

			serializer.endTag(null, "conversations");
			serializer.endDocument();
			serializer.flush();
			writer.close();
			cursor.close();
			return writer.toString();
		} catch (IllegalArgumentException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IllegalStateException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return "";
	}
	private void addSMS(XmlSerializer serializer, String id) {
		try {
			Cursor cursor = ct.getContentResolver().query(SMS_URI, SMS_COLUMNS, "_id = " + id, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				serializer.startTag(null, "sms");
				serializer.attribute(null, "_id", id);
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String value = cursor.getString(i);
					if (value != null) {
						if (cursor.getColumnName(i).equals("body"))
							serializer.attribute(null, cursor.getColumnName(i), URLEncoder.encode(value, "UTF-8"));
						else
							serializer.attribute(null, cursor.getColumnName(i), value);
					}
				}
				serializer.endTag(null, "sms");
				cursor.close();
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IllegalStateException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
}