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

/**
 * Represents one news
 */
public class NewsItem {
	
	/**
	 * JSON keys
	 */
	private static final String RESPONSE = "response";
	private static final String ITEMS = "items";
	private static final String TEXT = "text";
	private static final String SOURCE_ID = "source_id";
	private static final String DATE = "date";
	private static final String PROFILES = "profiles";
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";
	private static final String PHOTO_REC = "photo_rec";
	private static final String UID = "uid";
	
	private static final String GET_REQUEST = "GET";
    /**
     * News text
     */
	String text;
	/**
	 * Posted date
	 */
	long date;
	/**
	 * User profile who submit news
	 */
	Profile profile;

	/**
	 * User profile who submit news
	 */
	public class Profile {
		/**
		 * User photo
		 */
		Bitmap photo;
		/**
		 * Name
		 */
		String firstName;
		/**
		 * Surname
		 */
		String lastName;

		/**
		 * Constructor
		 */
		public Profile(String firstName, String lastName, String photoRec) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.photo = loadBitmap(photoRec);
		}

		/**
		 * Bitmap loader
		 * @param imageUrl - image url 
		 * @return
		 */
		private Bitmap loadBitmap(String imageUrl) {
			BitmapFactory.Options bmOptions;
			bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;
			return loadImage(imageUrl, bmOptions);
		}

		/**
		 * Open connection and loads image
		 * @param url
		 * @param options
		 * @return
		 */
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

	}

	/**
	 * Default constructor
	 */
	public NewsItem() {
	}

	/**
	 * Constructor
	 * @param text
	 * @param date
	 * @param p
	 */
	public NewsItem(String text, long date, Profile p) {
		this.text = text;
		this.date = date;
		this.profile = p;
	}

	/**
	 * Parses JSON and gets {@link NewsItem} array
	 * @param r
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<NewsItem> parse(String r) throws JSONException {
		JSONObject params = new JSONObject(r).getJSONObject(RESPONSE);
		HashMap<String, Profile> profiles = getProfiles(params);

		ArrayList<NewsItem> items = new ArrayList<NewsItem>();
		JSONArray arr = params.getJSONArray(ITEMS);
		int length = arr.length();
	
		for (int i = 0; i < length; i++) {
			JSONObject o = arr.getJSONObject(i);
			text = o.getString(TEXT);
			String source_id = o.getString(SOURCE_ID);
			Profile profile = profiles.get(source_id);
			long item_time = Long.parseLong(o.getString(DATE));
			NewsItem n = new NewsItem(text, item_time, profile);
			items.add(n);
		}
		return items;
	}

	/**
	 * Parses profile
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	private HashMap<String, Profile> getProfiles(JSONObject obj)
			throws JSONException {
		HashMap<String, Profile> result = new HashMap<String, NewsItem.Profile>();
		JSONArray arr = obj.getJSONArray(PROFILES);
		int length = arr.length();
		for (int i = 0; i < length; i++) {
			JSONObject o = arr.getJSONObject(i);
			String firstName = o.getString(FIRST_NAME);
			String lastName = o.getString(LAST_NAME);
			String photoRec = o.getString(PHOTO_REC);
			Profile p = new Profile(firstName, lastName, photoRec);
			result.put(o.getString(UID), p);
		}

		return result;
	}

	/**
	 * Return input stream for requested URL
	 * @param strURL
	 * @return
	 * @throws IOException
	 */
	private InputStream openHttpConnection(String strURL) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod(GET_REQUEST);
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}

		return inputStream;
	}
}
