package com.vknews;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class News extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);
		new ListLoader().execute();
		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new LogOutListener());
		Log.e("my", "oncreate news"); 
	}

	class LogOutListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(News.this, Authorization.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			//ApiHandler.clear();
			finish();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String selection = l.getItemAtPosition(position).toString();
		Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
	}

	public class ListLoader extends AsyncTask<Void, Void, Void> {
		ArrayList<NewsItem> mNews;

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			try {
				News.this.getListView().setAdapter(
						new VkNewsAdapter(News.this, R.layout.news, mNews));
				Toast.makeText(News.this, "Display data...", Toast.LENGTH_LONG)
						.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Toast.makeText(News.this, "Load data...", Toast.LENGTH_LONG).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				long endTime = System.currentTimeMillis() / 1000;
				long startTime = System.currentTimeMillis()/1000 - 3600*24*30*5;
                Log.e("my", "endtime: " + formatTime(endTime*1000) + ":" + endTime);
                Log.e("my", "starttime: " + formatTime(startTime*1000));
				mNews = ApiHandler.getData(endTime, startTime);
				Log.e("my", "length: " + mNews.toArray().length);
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

    public synchronized static String formatTime(long time) {
		SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        StringBuilder fullTime = new StringBuilder();
        fullTime.append(sFormatter.format(new Date(time))).insert((fullTime.length() - 2), ":");
        return fullTime.toString();
    }

	
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
}