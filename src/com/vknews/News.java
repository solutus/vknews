package com.vknews;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class News extends ListActivity {

	public class ListLoader extends AsyncTask<Void, Void, Void> {
        ArrayList<NewsItem> mNews;
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			ArrayList<NewsItem> news;
			try {
				news = ApiHandler.getData(System.currentTimeMillis()/1000);
				setListAdapter(new VkNewsAdapter(News.this, R.layout.news, news));
				Toast.makeText(News.this,
						"Display data...",
						Toast.LENGTH_LONG).show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Toast.makeText(News.this,
					"Load data...",
					Toast.LENGTH_LONG).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			preLoadSrcBitmap();
			return null;
		}

	}

	//static final String IMAGE_URL = "http://4.bp.blogspot.com/_C5a2qH8Y_jk/StYXDpZ9-WI/AAAAAAAAAJQ/sCgPx6jfWPU/S1600-R/android.png";

	public class VkNewsAdapter extends ArrayAdapter<NewsItem> {
		Bitmap bm;  
		ArrayList<NewsItem> mNews;
		public VkNewsAdapter(Context context, int textViewResourceId,
				ArrayList<NewsItem> news) {
			super(context, textViewResourceId, news);
			mNews = news;

			//bm = srcBitmap;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) { 
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.news, parent, false);
			}
			
			TextView nameText = (TextView) row.findViewById(R.id.name);
			nameText.setText("name surname");
			
			TextView newsText = (TextView) row.findViewById(R.id.news);
			newsText.setText(mNews.get(position).text);
			
			TextView timeAgo = (TextView) row.findViewById(R.id.time_ago);
			timeAgo.setText(mNews.get(position).date);
			
			ImageView icon = (ImageView) row.findViewById(R.id.photo);
			icon.setImageBitmap(bm);

			return row;
		}
	}

	Bitmap srcBitmap;

	private void preLoadSrcBitmap() {
		BitmapFactory.Options bmOptions;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		srcBitmap = LoadImage(IMAGE_URL, bmOptions);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		/*
		 * setListAdapter(new ArrayAdapter<String>(this, R.layout.row,
		 * R.id.weekofday, DayOfWeek));
		 */
		Log.e("my", "news activity loaded");
		new ListLoader().execute();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String selection = l.getItemAtPosition(position).toString();
		Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
	}

	private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (IOException e1) {
		}

		return bitmap;
	}

	private InputStream OpenHttpConnection(String strURL) throws IOException {
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