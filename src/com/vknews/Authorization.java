package com.vknews;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Authorization extends Activity {
	private WebView mWebView;
	private ProgressDialog mProgress;
    private final static String AUTHORIZATION_MESSAGE = "Авторизация";

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new VkNewsClient());
		
		WebSettings ws = mWebView.getSettings();
		ws.setJavaScriptEnabled(true);
		
		mProgress = ProgressDialog.show(this, AUTHORIZATION_MESSAGE,
				Utils.LOADING_MESSAGE, true);
		mWebView.loadUrl(ApiHandler.AUTHORIZATION_URL);
	}

	private class VkNewsClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (mProgress.isShowing()) {
				mProgress.dismiss();
			}

			if (url.startsWith(ApiHandler.SUCCESS_LOGIN_ADDRESS)) {
				url = Uri.decode(url);
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
		}
	}
}