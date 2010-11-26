package com.vknews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.CookieManager;

public class ApiHandler {
	/**
	 * Redirects to this URL if login is success.
	 */
	public static final String SUCCESS_LOGIN_ADDRESS = "http://vkontakte.ru/api/login_success.html#session=";
	/**
	 * Appication Id 
	 */
	public static final String APP_ID = "2021752";
	/**
	 * Vkontakte API address
	 */
	public static final String API_ADDRESS = "http://api.vkontakte.ru/api.php";
	/**
	 * API Function
	 */
	public static final String GET_NEWS = "newsfeed.get";
	/** 
	 * Login url
	 */
	public static final String AUTHORIZATION_URL = "http://vkontakte.ru/login.php?layout=touch&type=browser&settings=8192&app="
			+ ApiHandler.APP_ID;

	/**
	 * Max size of records for one request
	 */
	private static final int COUNT = 20;

	/**
	 * Singleton instance
	 */
	private static ApiHandler sSelf;
	
	// private String expire;
	/**
	 * Vkontakte User id
	 */
	private String mid;
	/**
	 * Secret code 
	 */
	private String secret;
	/**
	 * Session id 
	 */
	private String sid;

	/**
	 * Private constructor
	 * @param url
	 * @throws JSONException
	 */
	private ApiHandler(String url) throws JSONException {
		JSONObject json = new JSONObject(url);
		// expire = json.getString("expire");
		mid = json.getString("mid");
		secret = json.getString("secret");
		sid = json.getString("sid");
	}

    /**
     * Set necessary data to singleton	
     * @param url
     * @throws JSONException
     */
	public static void init(String url) throws JSONException {
		sSelf = new ApiHandler(url);
	}

    /**
     * Return fetched data from vkontakte api url
     * @param endTime
     * @param startTime
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JSONException
     */
	public static ArrayList<NewsItem> getData(long endTime, long startTime)
			throws ClientProtocolException, IOException, JSONException {
		String request = createRequest(endTime, startTime);
		HttpGet g = new HttpGet(request);
		HttpEntity entity = new DefaultHttpClient().execute(g).getEntity();
		String response = EntityUtils.toString(entity);
		
		return new NewsItem().parse(response);
	}

	/**
	 * Creates request URL
	 * @param endTime
	 * @param startTime
	 * @return
	 */
	private static String createRequest(long endTime, long startTime) {
		StringBuffer address = new StringBuffer(API_ADDRESS);
		ArrayList<String>params = createParams( endTime, startTime);
		int length = params.size();
		for(int i=0; i<length; i++){
			String s = params.get(i);
			if(i==0){
				address.append("?" + s);	
			}
			address.append("&" + s);
		}
		
		address.append("&sig=" + getSig(params));
		address.append("&sid=" + sSelf.sid);
		return address.toString();
	}

	/**
	 * Composes signature
	 * @param endTime
	 * @param startTime
	 * @return
	 */
	private static String getSig(ArrayList<String>params){//long endTime, long startTime) {
		StringBuffer s = new StringBuffer(sSelf.mid);
		for(String p : params){
			s.append(p);
		}
		s.append(sSelf.secret);
		return Utils.hashMd5(s.toString());
	}
	
	private static ArrayList<String>createParams(long endTime, long startTime){
		ArrayList<String> params = new ArrayList<String>();
		params.add("api_id=" + APP_ID);
		params.add("count="+ COUNT);
		params.add("end_time=" + endTime);
		params.add("filters=post");
		params.add("format=JSON");
		params.add("method=" + GET_NEWS);
		params.add("start_time=" + startTime);
		params.add("v=3.0");
		Collections.sort(params);
		return params;
	}

	/**
	 * Clear session data
	 */
	public static void logout(){
		CookieManager.getInstance().removeAllCookie();
	}

}
