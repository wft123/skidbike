package edu.rapa.iot.android.skidbike.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.util.LoggerMgr;
import edu.rapa.iot.android.skidbike.util.SBPreference;

public class ImageService extends Service{
	/*
	 * Custom App Widget 클릭 시 이벤트 구현 서비스
	 * 
	 * 
	 * 
	 */	
	private Context context;
	private int mId;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		mId = intent.getIntExtra("wId", 10);
		
		LoggerMgr lMgr = new LoggerMgr(context);
		SBPreference sPre = new SBPreference(context);
		// 기록 여부를 판별하여 위젯 이미지 설정
		if (sPre.getValue("collecting", false)) {
			sPre.put("collecting", false);
			lMgr.releaseAlarm();
			SBWidget.appWidgetUpdate(context, mId, R.drawable.bin_rest);
			Toast.makeText(context, "수집 종료", Toast.LENGTH_SHORT).show();
		} else {
			sPre.put("collecting", true);
			sPre.put("record_id", (sPre.getValue("record_id", 0) + 1));
			lMgr.setAlarm();
			SBWidget.appWidgetUpdate(context, mId, R.drawable.bin_bike);
			Toast.makeText(context, "수집 시작", Toast.LENGTH_SHORT).show();
		}		
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
