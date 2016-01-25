package edu.rapa.iot.android.skidbike.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.controler.LoggerDAO;
import edu.rapa.iot.android.skidbike.model.Skid;
import edu.rapa.iot.android.skidbike.util.GpsInfo;
import edu.rapa.iot.android.skidbike.util.SBPreference;
import edu.rapa.iot.android.skidbike.util.WeatherParser;

public class LoggingService extends Service {
	/*
	 * ���� ��ġ �α׸� ����ϴ� Service
	 * ���� Ŭ�� �ÿ� �ӵ��迡�� ��Ͻ��� ��ư Ŭ���� ����ȴ�.
	 */

	// GPS Location
	private GpsInfo gps;
	private double lat, lng = 0;
	// DataBase
	private LoggerDAO lDAO;
	// Current Time
	private String strCurDate, strCurTime = null;
	
	private SBPreference sp;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		Log.d("LoggingService","service start...");
		// Preference, gps, DAO �ʱ�ȭ 
		gps = new GpsInfo(this);
		sp = new SBPreference(this);
		lDAO = new LoggerDAO(this);
		lDAO.open();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d("LoggingService","running......");
		
		// gps service ���� ��ǥ ����
		lat = gps.getLatitude();
		lng = gps.getLongitude();

		//Current Time ����
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy�� M�� d��");
		SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH�� mm�� ss��");
		strCurDate = CurDateFormat.format(date);
		strCurTime = CurTimeFormat.format(date);

		Log.d("address","����ð� :"+strCurDate +" "+ strCurTime);

		// �������� ����
		new WeatherLoadingTask().execute(null,null);
		
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("LoggingService","onDestroy()");
		gps.stopUsingGPS();
		gps.stopSelf();
		gps = null;
		lDAO.close();
	}

	private void notified(String ticker, String title, String text){
		// NotificationManager ��ü ��
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// status bar �� ��ϵ� �޽���(Tiker, ������, �׸��� noti�� ����� �ð�)
		Notification notification =
				new Notification(R.drawable.bin_bike,
						ticker, System.currentTimeMillis());

		// List�� ǥ�õ� �׸�
		notification.setLatestEventInfo(this, title, text, null);

		// notification �� �������̵�
					nm.notify(2222, notification);
	}
	
	/* �������� �����ϴ� AsyncTask 
	 * ���� �Ŀ� DAO�� ���� ���� DB�� ����Ѵ�.
	 */
	private class WeatherLoadingTask extends AsyncTask<String, String, String[]> {
		@Override
		protected String[] doInBackground(String... strs) {
			return new WeatherParser().getWeather(lat, lng);
		} // doInBackground : ��׶��� �۾��� �����Ѵ�.

		@Override
		protected void onPostExecute(String result[]) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 3; i++) {
				sb.append(result[i] + " ");
			}
			lDAO.add(new Skid(strCurDate,strCurTime,sb.toString(),result[3],result[4],""+lat,""+lng,""+sp.getValue("record_id", 0)));
			notified("Skid Bike","��ġ ���� ����",sb.toString());
			stopSelf();
		}
	} 
	
}
