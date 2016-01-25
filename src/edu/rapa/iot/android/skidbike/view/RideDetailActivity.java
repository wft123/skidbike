package edu.rapa.iot.android.skidbike.view;

import java.util.ArrayList;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.controler.LoggerDAO;
import edu.rapa.iot.android.skidbike.model.Skid;
import edu.rapa.iot.android.skidbike.util.Constants;

public class RideDetailActivity extends Activity {
	/*
	 * �������� �ڼ��� ��ȸ�� �� �ִ� Activity 
	 */

	LoggerDAO lDAO = new LoggerDAO(this);
	ArrayList<Skid> skids = new ArrayList<Skid>();
	TextView dateTv, startInfoTv, endInfoTv, recordDistTv, tempTv;
	ImageView weatherImg;

	private MapView mMapView = null;

	// onCreate ������ �ش� record_id�� ���� DB������ ������ �� ArrayList�� �����Ѵ�.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ride_detail);

		dateTv = (TextView) findViewById(R.id.detail_date);
		startInfoTv = (TextView) findViewById(R.id.detail_startInfo);
		endInfoTv = (TextView) findViewById(R.id.detail_endInfo);
		recordDistTv = (TextView) findViewById(R.id.detail_recordDist);
		tempTv = (TextView) findViewById(R.id.detail_temp);
		weatherImg = (ImageView) findViewById(R.id.detail_weatherImg);

		lDAO.open();

		Intent i = getIntent();
		String recordId = i.getExtras().getString(LoggerDAO.KEY_RECORD_ID);

		Cursor loggers = lDAO.getRecords(recordId);
		lDAO.close();
		for (int a = 0; a < loggers.getCount(); a++) {
			Skid skid = new Skid(loggers.getString(loggers
					.getColumnIndexOrThrow(LoggerDAO.KEY_DATE)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_TIME)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_ADDRESS)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_WEATHER)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_TEMP)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_LATITUDE)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_LONGITUDE)),
					loggers.getString(loggers
							.getColumnIndexOrThrow(LoggerDAO.KEY_RECORD_ID)));
			skids.add(skid);
			loggers.moveToNext();
		}
		loggers.close();

		fillMap();
		fillText();
	}

	// �ʿ� ����� ��ġ���� Marker�� �߰��ϰ� Line�� �׸���.
	private void fillMap() {
		// Map init
		mMapView = new MapView(this);
		mMapView.setDaumMapApiKey(Constants.API_KEY);
		((LinearLayout) findViewById(R.id.detailFragmentLayout))
				.addView(mMapView);

		MapPolyline polyline = new MapPolyline();
		polyline.setTag(1000);
		polyline.setLineColor(Color.argb(128, 255, 51, 0));

		MapPOIItem[] poiItems = new MapPOIItem[skids.size()];

		for (int i = 0; i < skids.size(); i++) {
			Double lat = Double.parseDouble(skids.get(i).getLatitude());
			Double lng = Double.parseDouble(skids.get(i).getLongitude());

			MapPOIItem poiItem = new MapPOIItem();

			poiItem.setItemName(skids.get(i).getTime());
			poiItem.setTag(i);
			poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lng));
			poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
			poiItems[i] = poiItem;

			polyline.addPoint(MapPoint.mapPointWithGeoCoord(lat, lng));
		}

		poiItems[0].setItemName("��� : " + poiItems[0].getItemName());
		poiItems[0].setMarkerType(MapPOIItem.MarkerType.YellowPin);
		poiItems[skids.size() - 1].setItemName("���� : "
				+ poiItems[skids.size() - 1].getItemName());
		poiItems[skids.size() - 1]
				.setMarkerType(MapPOIItem.MarkerType.YellowPin);

		mMapView.addPOIItems(poiItems);
		mMapView.addPolyline(polyline);

		MapPointBounds mapPointBounds = new MapPointBounds(
				polyline.getMapPoints());
		int padding = 100; // px
		mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(
				mapPointBounds, padding));
	}
	
	// ������ ������ View �� �ѷ��ش�.
	private void fillText() {
		dateTv.setText(skids.get(0).getDate());
		startInfoTv.setText("��� : "+skids.get(0).getAddress());
		endInfoTv.setText("���� : "+skids.get(skids.size() - 1).getAddress());
		recordDistTv.setText("���� �� ����Ÿ� : "+String.format("%.2f", calTotalDistance(skids) / 1000d)+" Km");
		weatherImg.setImageResource(Constants.weatherHash.get(skids.get(0).getWeather()));
		tempTv.setText(skids.get(0).getWeather()+"  "+skids.get(0).getTemp().split(":")[1]);
	}
	
	// �� �Ÿ���� : ���� ��ǥ���� ������ǥ�� ���� �Ÿ��� ���Ͽ� �����Ͽ� ���
	private double calTotalDistance(ArrayList<Skid> skids){
		double totalDist = 0.0;
		
		for(int i = 0 ; i < skids.size()-1; i++){
			double lat1 = Double.parseDouble(skids.get(i).getLatitude());
			double lon1 = Double.parseDouble(skids.get(i).getLongitude());
			double lat2 = Double.parseDouble(skids.get(i+1).getLatitude());
			double lon2 = Double.parseDouble(skids.get(i+1).getLongitude());
			totalDist += calDistance(lat1, lon1, lat2, lon2);
		}
		return totalDist;
	}

	// ���� ��ǥ�� ���� ��ǥ�� �Ÿ��� ���ϴ� �Լ�
	private double calDistance(double lat1, double lon1, double lat2,
			double lon2) {

		double theta, dist;
		theta = lon1 - lon2;
		dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);

		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344; // ���� mile ���� km ��ȯ.
		dist = dist * 1000.0;

		return dist;
	}

	// �־��� ��(degree) ���� �������� ��ȯ
	private double deg2rad(double deg) {
		return (double) (deg * Math.PI / (double) 180d);
	}

	// �־��� ����(radian) ���� ��(degree) ������ ��ȯ
	private double rad2deg(double rad) {
		return (double) (rad * (double) 180d / Math.PI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ride_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
