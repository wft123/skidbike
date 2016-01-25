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
	 * 현재 위치 로그를 기록하는 Service
	 * 위젯 클릭 시와 속도계에서 기록시작 버튼 클릭시 실행된다.
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
		// Preference, gps, DAO 초기화 
		gps = new GpsInfo(this);
		sp = new SBPreference(this);
		lDAO = new LoggerDAO(this);
		lDAO.open();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d("LoggingService","running......");
		
		// gps service 에서 좌표 수집
		lat = gps.getLatitude();
		lng = gps.getLongitude();

		//Current Time 수집
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 M월 d일");
		SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH시 mm분 ss초");
		strCurDate = CurDateFormat.format(date);
		strCurTime = CurTimeFormat.format(date);

		Log.d("address","현재시각 :"+strCurDate +" "+ strCurTime);

		// 날씨정보 수집
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
		// NotificationManager 객체 생
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// status bar 에 등록될 메시지(Tiker, 아이콘, 그리고 noti가 실행될 시간)
		Notification notification =
				new Notification(R.drawable.bin_bike,
						ticker, System.currentTimeMillis());

		// List에 표시될 항목
		notification.setLatestEventInfo(this, title, text, null);

		// notification 의 고유아이디
					nm.notify(2222, notification);
	}
	
	/* 날씨정보 수집하는 AsyncTask 
	 * 수집 후에 DAO를 통해 내부 DB에 기록한다.
	 */
	private class WeatherLoadingTask extends AsyncTask<String, String, String[]> {
		@Override
		protected String[] doInBackground(String... strs) {
			return new WeatherParser().getWeather(lat, lng);
		} // doInBackground : 백그라운드 작업을 진행한다.

		@Override
		protected void onPostExecute(String result[]) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 3; i++) {
				sb.append(result[i] + " ");
			}
			lDAO.add(new Skid(strCurDate,strCurTime,sb.toString(),result[3],result[4],""+lat,""+lng,""+sp.getValue("record_id", 0)));
			notified("Skid Bike","위치 정보 수집",sb.toString());
			stopSelf();
		}
	} 
	
}
