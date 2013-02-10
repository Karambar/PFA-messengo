package com.messengo.tablette.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

public class WebService {

	/*
	 * Tag pour la classe dans DDMS
	 */
	private static final String TAG = "WebService";

	private static WebService instance;

	public static synchronized WebService getInstance(){
		if (instance == null)
		{
			instance = new WebService();
		}
		return instance;
	}

	private WebService(){
	}
	
	public String downloadUrl(String server, String methode, ArrayList<String>data) throws IOException {
		return (downloadUrl(getUrl(server, methode, data)));
	}
	
	
	public String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is);
	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	// Reads an InputStream and converts it to a String.
	private String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		stream.close();
		return sb.toString();
	}

	private String getUrl(String server, String methodeName, ArrayList<String> args) {
		String url = "";
		
		url = url.concat(server + "/"+ methodeName + "/");
		Iterator<String> it = args.iterator();
		while (it.hasNext()){
			String tmp = it.next();
			url = url.concat(tmp + "/");
		}
		Log.i(TAG, "URL : " + url);
		return url;
	}
	
	
}
