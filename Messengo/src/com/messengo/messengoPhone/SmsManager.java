package com.messengo.messengoPhone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;


public class SmsManager {

	private Context ct;
	private List<String> bodyList;
	private List<String> numberList;
	private File file;


	public SmsManager(Context ct) {
		this.ct = ct;
		bodyList = new ArrayList<String>();
		numberList = new ArrayList<String>();
	}

	public void retrieveSMS(){
		Uri uriSMSURI = Uri.parse("content://sms/inbox");
		Cursor cur = ct.getContentResolver().query(uriSMSURI, null, null, null, null);

		while (cur.moveToNext()) {
			String address = cur.getString(cur.getColumnIndex("address"));
			String body = null;
			try {
				body = URLEncoder.encode(cur.getString(cur.getColumnIndexOrThrow("body")).toString(), "UTF-8");
				bodyList.add(body);
				numberList.add(address);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getBodyList() {
		return bodyList;
	}

	public List<String> getNumberList() {
		return numberList;
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

	public String writeUsingXMLSerializer() throws Exception {
		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);		StringWriter writer = new StringWriter();
		xmlSerializer.setOutput(writer);
		xmlSerializer.startDocument("UTF-8", true);
		xmlSerializer.startTag("", "messages");
		for (int i = 0; i < numberList.size(); i++) {
			xmlSerializer.startTag("", "message");
			xmlSerializer.attribute("", "sender", numberList.get(i));
			xmlSerializer.text(bodyList.get(i));
			xmlSerializer.endTag("", "message");
		}
		xmlSerializer.endTag("", "messages");
		xmlSerializer.endDocument();
		return writer.toString();
	}


	public void writeToFile() {
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
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			fileos.write(writeUsingXMLSerializer().getBytes());
			fileos.flush();
			fileos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
