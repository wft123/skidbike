package edu.rapa.iot.android.skidbike.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LoggerMgr {
	/*
	 * �˶��� ����Ͽ� LoggerService�� ������� �ֱ������� ���ϴ� ������ �����Ѵ�.
	 */
	private final String TAG = "TestAlarmManagerActivity";
	private final String INTENT_ACTION = "edu.rapa.iot.android.loggingservice";
	private Context context;
	public LoggerMgr(Context context){
		this.context = context;
	}
	
	// �˶� ���
	public void setAlarm() {
		Log.i(TAG, "setAlarm()");
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent Intent = new Intent(INTENT_ACTION);
		PendingIntent pIntent = PendingIntent.getService(context, 22, Intent, 0);
		SBPreference sp = new SBPreference(context);

		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
				sp.getValue("interval", 1) * 60 * 1000, pIntent);
	}

	// �˶� ����
	public void releaseAlarm() {
		Log.i(TAG, "releaseAlarm()");
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent Intent = new Intent(INTENT_ACTION);
		PendingIntent pIntent = PendingIntent.getService(context, 22, Intent, 0);
		alarmManager.cancel(pIntent);
	}

}
