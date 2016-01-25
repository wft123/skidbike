package edu.rapa.iot.android.skidbike.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.model.News;
import edu.rapa.iot.android.skidbike.util.Constants;
import edu.rapa.iot.android.skidbike.util.GpsInfo;
import edu.rapa.iot.android.skidbike.util.NewsParser;
import edu.rapa.iot.android.skidbike.util.WeatherParser;

public class MainActivity extends Activity {

	/*
	 *  MainActivity�� �α��� �� ǥ��Ǹ�, �� �޴��� �̵������� ���� ȭ���Դϴ�.
	 *  ���� ǥ��, ���� ǥ��, ������ ����, �����ȸ ��� ����, ���� �޴� ������ �����մϴ�.
	 *  
	 *  1. ���� 
	 *  ���̹����� �����ϴ� ������ ���� �浵�� ������ �޾ƿɴϴ�. ����µ�, ��ġ, ���� ������ ǥ���մϴ�.
	 *  
	 *  2. ����
	 *  �������̾����� ������ ���� ī�װ� ������ SAX parser�� �Ľ��� �ɴϴ�.
	 *  CustomView�� �̿��Ͽ�, ����� �� ������ ���ÿ� ǥ���� �ָ�, Ŭ���� link�� ���������� ȣ���մϴ�.
	 *  
	 *  3. �޴���ư
	 *  �����ư�� ��Ϲ�ư���� hoverȿ���� �־� �ڿ��������� ǥ���߽��ϴ�.
	 * 
	 * 
	 * 
	 */
	
	
	// ��ġ���� ��ü
	LocationManager lm = null;
	LocationListener ll = null;
	// ��ġ���� ��ġ �̸�
	String provider = null;

	// GPSTracker class
	private GpsInfo gps;
	double lat1 = 0;
	double lon1 = 0;

	ImageView weatherImg;
	TextView weatherTv;

	// BackButton
	private final long FINSH_INTERVAL_TIME = 2000;
	private long backPressedTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		weatherTv = (TextView) findViewById(R.id.main_WeatherTv);
		weatherImg = (ImageView) findViewById(R.id.main_WeatherImg);

		// ���� ��Ƽ��Ƽ �̵� ��ư
		findViewById(R.id.main_Setting_But).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(MainActivity.this,
								SettingActivity.class));

					}
				});

		// ���� ��Ƽ��Ƽ �̵� ��ư
		findViewById(R.id.main_SpeedActivity_But).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(MainActivity.this,
								SpeedActivity.class));
					}
				});

		// �����ȸ ��Ƽ��Ƽ �̵� ��ư
		findViewById(R.id.main_RideLogActivity_But).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(MainActivity.this,
								RideLogActivity.class));
					}
				});
	}

	// ���ư Ȯ�� �Լ�
	@Override
	public void onBackPressed() {
		long tempTime = System.currentTimeMillis();
		long intervalTime = tempTime - backPressedTime;

		if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
			super.onBackPressed();
		} else {
			backPressedTime = tempTime;
			Toast.makeText(getApplicationContext(), "'�ڷ�'��ư���ѹ��������ø�����˴ϴ�.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		gps = new GpsInfo(this);
		// GPS ������� ��������
		if (gps.isGetLocation()) {
			lat1 = gps.getLatitude();
			lon1 = gps.getLongitude();
			new WeatherLoadingTask().execute(null, null);
			new NewsLoadingTask().execute(null, null);
		} else {
			gps.showSettingsAlert();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		gps.stopUsingGPS();
		gps.stopSelf();
		gps = null;
	}

	private class WeatherLoadingTask extends
			AsyncTask<String, String, String[]> {
		@Override
		protected String[] doInBackground(String... strs) {
			return new WeatherParser().getWeather(lat1, lon1);
		} // doInBackground : ��׶��� �۾��� �����Ѵ�.

		@Override
		protected void onPostExecute(String result[]) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				sb.append(result[i] + " ");
				if (i == 2) {
					sb.append("\n");
				}
			}
			weatherImg.setImageResource(Constants.weatherHash.get(result[3]));
			weatherTv.setText(sb.toString());
		} // onPostExecute : ��׶��� �۾��� ���� �� UI �۾��� �����Ѵ�.
	} // JsonLoadingTask

	private class NewsLoadingTask extends
			AsyncTask<String, String, ArrayList<News>> {

		ArrayList<News> newsArry = new ArrayList<News>();
		NewsParser n_Parser = new NewsParser();

		@Override
		protected ArrayList<News> doInBackground(String... params) {

			try {
				newsArry = n_Parser.parser();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.d("AsyncTask Check", newsArry.get(0).getNews_Title());
			return newsArry;
		}

		@Override
		protected void onPostExecute(ArrayList<News> result) {
			ListView newsList = (ListView) findViewById(R.id.main_News_List);
			ArrayAdapter adapter = new NewsAdapter(MainActivity.this,
					R.layout.newslist_cumtomview, 0, result);
			newsList.setAdapter(adapter);
			newsList.setOnItemClickListener(newsItemClickListner);

		}

		private AdapterView.OnItemClickListener newsItemClickListner = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(newsArry
						.get(position).getNews_Link())));

			}
		};

		class NewsAdapter extends ArrayAdapter {

			Activity act;
			int resource;

			public NewsAdapter(Context context, int resource,
					int textViewResourceId, ArrayList<News> obj) {
				super(context, resource, textViewResourceId, newsArry);
				act = (Activity) context;
				this.resource = resource;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inf = (MainActivity.this).getLayoutInflater();
				View view = inf.inflate(resource, null);
				TextView titleTv = (TextView) view
						.findViewById(R.id.main_News_TitleTV);
				TextView detailTv = (TextView) view
						.findViewById(R.id.main_News_DetailTv);
				titleTv.setText(newsArry.get(position).getNews_Title());
				detailTv.setText(newsArry.get(position).getNews_Description());
				return view;
			}

		}

	}
}
