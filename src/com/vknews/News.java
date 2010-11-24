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
			setListAdapter(new VkNewsAdapter(News.this, R.layout.news, mNews));
			Toast.makeText(News.this, "Display data...", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Toast.makeText(News.this, "Load data...", Toast.LENGTH_LONG).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				mNews = ApiHandler.getData(System.currentTimeMillis() / 1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	// static final String IMAGE_URL =
	// "http://4.bp.blogspot.com/_C5a2qH8Y_jk/StYXDpZ9-WI/AAAAAAAAAJQ/sCgPx6jfWPU/S1600-R/android.png";

	public class VkNewsAdapter extends ArrayAdapter<NewsItem> {
		ArrayList<NewsItem> mNews;

		public VkNewsAdapter(Context context, int textViewResourceId,
				ArrayList<NewsItem> news) {
			super(context, textViewResourceId, news);
			mNews = news;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.news, parent, false);
			}

			NewsItem news = mNews.get(position);
			NewsItem.Profile profile = news.profile;
			TextView nameText = (TextView) row.findViewById(R.id.name);
			nameText.setText(profile.firstName + " " + profile.lastName);

			TextView newsText = (TextView) row.findViewById(R.id.news);
			newsText.setText(news.text);

			TextView timeAgo = (TextView) row.findViewById(R.id.time_ago);
			timeAgo.setText(news.date);

			ImageView icon = (ImageView) row.findViewById(R.id.photo);
			icon.setImageBitmap(profile.photo);

			return row;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new ListLoader().execute();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String selection = l.getItemAtPosition(position).toString();
		Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
	}

}