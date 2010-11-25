package com.vknews;

import org.json.JSONException;

import android.app.Activity;
import android.app.Application;
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
		Log.e("my", "onCreate");
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new VkNewsClient());
		WebSettings ws = mWebView.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
	}

	@Override
	protected void onResume() {
		Log.e("my", "onResume");
		mWebView.clearView();
	    mWebView.loadUrl(ApiHandler.AUTHORIZATION_URL);
		super.onResume();
	}
	
	private class VkNewsClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
		    Log.e("my", "page finished: " + url);
//			mWebView.clearHistory();
		    if (url.startsWith(ApiHandler.SUCCESS_LOGIN_ADDRESS)) {
				url = Uri.decode(url);
//				Log.d("my", "success!: " + url);
				try {
					ApiHandler.init(url
							.substring(ApiHandler.SUCCESS_LOGIN_ADDRESS
									.length()));
					
					startActivity(new Intent(Authorization.this, News.class));
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		    super.onPageFinished(view, url);
		}
		 
		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			Log.e("my", "update history: " + url + ": reload: " + isReload);
			//super.doUpdateVisitedHistory(view, url, true);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.w("my", "shO:" + url);
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
}