package com.messengo.messengoPhone;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class MessengoService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public class toto extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			try {
				get.setURI(new URI("http://www.google.fr/"));
				HttpResponse response = client.execute(get);
				Log.d("messengo", response.getStatusLine().toString());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private void start(Intent intent) {
		new toto().execute();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null)
			start(intent);
		return super.onStartCommand(intent, flags, startId);
	}

}
