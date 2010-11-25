package com.vknews;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class NewsItem {
	String text;
	String date;
	Profile profile;

	public class Profile {
		Bitmap photo;
		String firstName;
		String lastName;

		public Profile(String firstName, String lastName, String photoRec) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.photo = loadBitmap(photoRec);
		}

		private Bitmap loadBitmap(String imageUrl) {
			BitmapFactory.Options bmOptions;
			bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;
			return loadImage(imageUrl, bmOptions);
		}
		
		private Bitmap loadImage(String url, BitmapFactory.Options options) {
			Bitmap bitmap = null;
			InputStream in = null;
			try {
				in = openHttpConnection(url);
				bitmap = BitmapFactory.decodeStream(in, null, options);
				in.close();
			} catch (IOException e1) {
			}

			return bitmap;
		}

		
		@Override
		public String toString() {
			return firstName + ":" + lastName;
		}
	}

	public NewsItem() {
	}

	public NewsItem(String text, String date, Profile p) {
		this.text = text;
		this.date = date;
		this.profile = p;
	}

	@Override
	public String toString() {
		return text + ":" + date + ":" + profile.toString();
	}

	public ArrayList<NewsItem> parse(String r) throws JSONException {
		JSONObject params = new JSONObject(r).getJSONObject("response");
		HashMap<String, Profile> profiles = getProfiles(params);

		ArrayList<NewsItem> items = new ArrayList<NewsItem>();
		JSONArray arr = params.getJSONArray("items");
		int length = arr.length();
        Log.e("my", "length: " + length);
		for (int i = 0; i < length; i++) {
			JSONObject o = arr.getJSONObject(i);
			text = o.getString("text");
			Log.i("my", text);
			String source_id = o.getString("source_id");
//			Log.i("my", source_id);
			Profile profile = profiles.get(source_id);
//			Log.i("my", profile.toString()); 
			long item_time = Long.parseLong(o.getString("date"));
			Log.e("my", "item time: " + item_time);
			Log.e("my", "cur time: " + System.currentTimeMillis());
			String date = formatTimeAgo(System.currentTimeMillis()/1000 - item_time);
			NewsItem n = new NewsItem(text, date, profile);
			items.add(n);
		}
		return items;
	}

	private String formatTimeAgo(long time){
		final long DAY = 86400;
		final long HOUR = 3600;
		final long MINUTE = 60;
		Log.e("my", "format time: " + time);
		long days = time / DAY;
		time = time % (days * DAY);
		long hours = time / HOUR;
		time = time %(hours * HOUR);
		long minutes = time / MINUTE;
		
	    return minutes + " минут /" + hours + " часов /" + days + " дней назад";
	}
	
	private HashMap<String, Profile> getProfiles(JSONObject obj)
			throws JSONException {
		HashMap<String, Profile> result = new HashMap<String, NewsItem.Profile>();
		JSONArray arr = obj.getJSONArray("profiles"); 
		int length = arr.length();
		for (int i = 0; i < length; i++) {
			JSONObject o = arr.getJSONObject(i);
			String firstName = o.getString("first_name");
			String lastName = o.getString("last_name");
			String photoRec = o.getString("photo_rec");
			Profile p = new Profile(firstName, lastName, photoRec);
			result.put(o.getString("uid"), p);
//			Log.w("my", p.toString());
		}

		return result;
	}
	
	private InputStream openHttpConnection(String strURL) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}

		return inputStream;
	}
}
