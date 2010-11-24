package com.vknews;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ApiHandler {
	public static final String SUCCESS_LOGIN_ADDRESS = "http://vkontakte.ru/api/login_success.html#session=";
	public static final String APP_ID = "2021752";
	public static final String API_ADDRESS = "http://api.vkontakte.ru/api.php";
	public static final String GET_NEWS = "newsfeed.get";
	public static final String AUTHORIZATION_URL = "http://vkontakte.ru/login.php?&layout=touch&type=browser&settings=8192&app="
			+ ApiHandler.APP_ID;

	private static final int COUNT = 20;

	private static ApiHandler sSelf;
	//private String expire;
	private String mid;
	private String secret;
	private String sid;

	private ApiHandler(String url) throws JSONException {
		JSONObject json = new JSONObject(url);
		//expire = json.getString("expire");
		mid = json.getString("mid");
		secret = json.getString("secret");
		sid = json.getString("sid");
	}

	public static void init(String url) throws JSONException {
		sSelf = new ApiHandler(url);
	}

	public static ArrayList<NewsItem> getData(long endTime) throws ClientProtocolException,
			IOException, JSONException {
		String request = createRequest(endTime);
		Log.e("my", "request:");
		Log.e("my", request);
		HttpGet g = new HttpGet(request);
		HttpEntity entity = new DefaultHttpClient().execute(g).getEntity();
		String response = EntityUtils.toString(entity);
		Log.e("my", "Response:");
		Log.e("my", response);
		return new NewsItem().parse(response);
	}

	private static String createRequest(long endTime) {
		StringBuffer address = new StringBuffer(API_ADDRESS);
		address.append("?api_id=" + APP_ID);
		address.append("&count=" + COUNT);
		address.append("&filters=post");
		address.append("&format=JSON");
		address.append("&method=" + GET_NEWS);
		address.append("&sid=" + sSelf.sid);
		address.append("&sig=" + getSig(endTime));

		address.append("&end_time=" + endTime);
		address.append("&v=3.0");
		return address.toString(); 
	}

	private static String getSig(long endTime) {
		StringBuffer s = new StringBuffer(sSelf.mid);
		s.append("api_id=" + APP_ID);
		s.append("count=20");
		s.append("end_time=" + endTime);
		s.append("filters=post");
		s.append("format=JSON");
		s.append("method=" + GET_NEWS);
		s.append("v=3.0");
		s.append(sSelf.secret);
		Log.e("my", "md5: " + s.toString());
		return hashMd5(s.toString());
	}

	
	/**
	 * Get Md5 hash.
	 * 
	 * @param input
	 *            Input string.
	 * @return hash string.
	 */
	public static String hashMd5(String input) {
		if (input == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);
			while (md5.length() < 32) {
				//md5 = "0" + md5;
			}
			return md5;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	
	

}
