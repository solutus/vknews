package com.vknews;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class NewsItem {
	String text;
	String date;
	Profile profile;

	public class Profile {
		String photoRec;
		String firstName;
		String lastName;

		public Profile(String firstName, String lastName, String photoRec) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.photoRec = photoRec;
		}

		@Override
		public String toString() {
			return firstName + ":" + lastName + ":" + photoRec;
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
			String source_id = o.getString("source_id");
			Log.i("my", source_id);
			Profile profile = profiles.get(source_id);
			Log.i("my", profile.toString());
			long item_time = Long.parseLong(o.getString("date"));
			Date d = new Date(System.currentTimeMillis() - item_time);
			NewsItem n = new NewsItem(text, d.toString(), profile);
			items.add(n);
		}
		return items;
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
			Log.w("my", p.toString());
		}

		return result;
	}
}
