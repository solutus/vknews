package com.vknews;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Authorization extends Activity {
	private WebView mWebView;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new VkNewsClient());
		WebSettings ws = mWebView.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		mWebView.loadUrl(ApiHandler.AUTHORIZATION_URL);
	}

	private class VkNewsClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.e("my", url);
			if (url.equals("about:blank")) {
				Log.e("my", "blank");
				return true;
			}
			view.loadUrl(url); 
			return true;
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			url = Uri.decode(url);
			Log.d("my", "url: " + url);
			if (url.startsWith(ApiHandler.SUCCESS_LOGIN_ADDRESS)) {
				Log.d("my", "url: " + url);
				try {
					ApiHandler.init(url.substring(ApiHandler.SUCCESS_LOGIN_ADDRESS.length()));
					startActivity(new Intent(Authorization.this, News.class));
//					finish();
			        return;
				} catch (JSONException e) {
					e.printStackTrace();
				} 
			}
			super.onLoadResource(view, url);
		}
	}
}