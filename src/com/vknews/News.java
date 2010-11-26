package com.vknews;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class News extends ListActivity{// implements OnScrollListener {
	private static final long MONTH = 2592000;
	private static final long DAY = 86400;
	private static final long HOUR = 3600;
	private static final long MINUTE = 60;
	private int mPrevTotalItemCount = 0;
	private VkNewsAdapter mAdapter;
	private ListLoader mLoader;
	private ProgressDialog mProgress;
	private ArrayList<NewsItem> mNews;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);
		mLoader = new ListLoader();
		mLoader.execute();
		//getListView().setOnScrollListener(this);
		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new LogOutListener());
		Log.e("my", "oncreate news");
	}

	class LogOutListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(News.this, Authorization.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ApiHandler.logout();
			startActivity(i);
			finish();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String selection = l.getItemAtPosition(position).toString();
		Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
	}

	//@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.w("my", "f: " + firstVisibleItem + ": vis: " + visibleItemCount
				+ " tot:" + totalItemCount + "prev tot: " + mPrevTotalItemCount);
		if (((firstVisibleItem + visibleItemCount) >= totalItemCount)
				&& totalItemCount != 0
		// && totalItemCount != mPrevTotalItemCount
		) {
			Log.v("my", "onListEnd, extending list");
			mPrevTotalItemCount = totalItemCount;
			Log.e("my", "task cancelled: " + mLoader.isCancelled());
			mLoader.cancel(true);
			mLoader = new ListLoader();
			mLoader.execute();
		}
	}

	//@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	private static String formatTimeAgo(long time) {
		long days = time / DAY;
		if (days != 0) {
			time = time % (days * DAY);
		}

		long hours = time / HOUR;
		if (hours != 0) {
			time = time % (hours * HOUR);
		}

		long minutes = time / MINUTE;
		return minutes + " минут /" + hours + " часов /" + days + " дней назад";
	}

	public class ListLoader extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			try {
				News.this.getListView().setAdapter(mAdapter);
				if (mProgress.isShowing()) {
					mProgress.dismiss();
				}
				Log.e("my", "onPost task cancelled: " + mLoader.isCancelled());
				mLoader.cancel(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			mProgress = ProgressDialog.show(News.this, "Получение данных",
					"идет загрузка...", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (mNews == null) {
					processNewsInitial();
				} else {
					processNews();
				}

				mPrevTotalItemCount += mNews.size();
				mAdapter.notifyDataSetChanged();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		private void processNewsInitial() throws ClientProtocolException,
				IOException, JSONException {
			Log.i("my", "processNewsInitial");
			long lastTime = System.currentTimeMillis() / 1000;
			mAdapter = new VkNewsAdapter(News.this, R.layout.news, mNews);
			long startTime = lastTime - MONTH;
			mNews = ApiHandler.getData(lastTime, startTime);
			mAdapter = new VkNewsAdapter(News.this, R.layout.news, mNews);
		}

		private void processNews() throws ClientProtocolException, IOException,
				JSONException {
			// TODO Auto-generated method stub
			Log.i("my", "processNews");
			long lastTime = mNews.get(mPrevTotalItemCount - 1).date;
			long startTime = lastTime - MONTH;

			mNews = ApiHandler.getData(lastTime, startTime);
			Log.i("my", "items before: " + mAdapter.getCount());
			for (NewsItem n : mNews) {
				mAdapter.add(n);
			}
			Log.i("my", "items after: " + mAdapter.getCount());
		}
	}

	private class VkNewsAdapter extends ArrayAdapter<NewsItem> {

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
			String date = formatTimeAgo(System.currentTimeMillis() / 1000
					- news.date);
			timeAgo.setText(date);

			ImageView icon = (ImageView) row.findViewById(R.id.photo);
			icon.setImageBitmap(profile.photo);

			if (position == getCount() - 1) {
				new ListLoader().execute();
			}
			
			return row;
		}
	}
}