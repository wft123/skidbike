package edu.rapa.iot.android.skidbike.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.util.Constants;
import edu.rapa.iot.android.skidbike.util.LoggerMgr;
import edu.rapa.iot.android.skidbike.util.SBPreference;
import edu.rapa.iot.android.skidbike.util.Speedometer;
import edu.rapa.iot.android.skidbike.util.WeatherParser;

public class SpeedActivity extends Activity {
	
	/*
	 * SpeedActivity�� �ӵ���, ��������ȭ���� ������ �ִ� Activity�Դϴ�
	 * GPS�� NetWork�� �̿��Ͽ� ��ġ�� ���µ�
	 * onLocationChanged�� �̿��Ͽ�, ��ġ�� ����ɶ����� �ڵ�����
	 * �ӵ�, ��������Ÿ��� ������Ʈ �ϰ� �˴ϴ�.
	 * 
	 * 1. �ӵ��� ���ñ��
	 *  - ������ ����� onLocationChanged�� �̿��Ͽ� ��ġ�� ������� �����Կ�
	 *   ���� ���������� longitude, latitude�� ������Ʈ �ϰ� �˴ϴ�.
	 *  - locationlistener�� �ʱⰪ�� 0�ʸ��� ����, 0m���� �������� �����Ǿ��ֽ��ϴ�.
	 *  - Accurancy filtering�� ���� �ʾұ� ������ �ʱ� ��ġ�� ���ö� �ӵ��� Ƣ������� ��찡 �ֽ��ϴ�.
	 *  - �ð��� ǥ���ϴ� �κ��� Handler�� �̿��Ͽ� 1�ʸ��� �ð��� ������Ʈ �ϰԵ˴ϴ�.
	 *  
	 *  
	 * 2. �� �������
	 *  - MapFragement.java�� Fragment�� �����Ͽ����ϴ�.
	 *  - ������ ���� �κ��� �ش� ���Ͽ� ����Ͽ����ϴ�.
	 *  
	 * 3. �÷��� ���
	 *  - ī�޶� ȣ���Ͽ� �����ü�� �ϵ���� �÷����� ��Ʈ�� �մϴ�.
	 *  
	 * 4. ��� ���� ���
	 *  - LoggingService�� �����Ͽ� ����� �����ϰ� �˴ϴ�.
	 */
	
	
	
	
	
	
	// Fragment State
	private static final int GUAGE_MODE = 0;
	private static final int MAP_MODE = 1;
	private int mode = 0;
	private MapFragment mFrag = new MapFragment();
	private SpeedometerFrag sFrag = new SpeedometerFrag();

	LocationManager lm = null;
	LocationListener ll = null;
	String provider = null;

	int rttTime = 0;

	int avgSpeedCnt;
	double currentSpeed, maxSpeed, avgSpeed, recordedDist, recordedTime,
			currentTime, lat1, lon1, lat2, lon2 = 0;

	TextView currentSpeedTv;
	TextView maxSpeedTv;
	TextView avgSpeedTv;
	TextView odoMeterTv;
	TextView recordedTimeTv;
	TextView currentTimeTv;
	TextView weatherTv;
	Button recordBtn;

	ImageView weatherImg;
	LoggerMgr lMgr;
	recordedTimeThread rTT;
	Speedometer speedometer;
	SBPreference sp;
	String address;

	public static Camera cam = null;
	boolean isOff = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speed);

		weatherTv = (TextView) findViewById(R.id.weatherTv);
		weatherImg = (ImageView) findViewById(R.id.weatherImg);

		lMgr = new LoggerMgr(this);

		sp = new SBPreference(this);
		
		// ȭ�� ������ �ʰ� �ϱ�!  (����� ������ ����...)
		if(sp.getValue("Keep_Screen", false)) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		/** ��ġ���� ��ü�� �����Ѵ�. */
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new SpeedoActionListener();

		avgSpeedCnt = 1;

		// ��Ϲ�ư ���� ��
		recordBtn = (Button) findViewById(R.id.recordBut);
		if(sp.getValue("collecting", false)){
			recordBtn.setText("�������");
		}else{
			recordBtn.setText("��Ͻ���");
		}
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// recordedTimeThread rTT;
				if (rTT == null)
					rTT = new recordedTimeThread();

				if (!sp.getValue("collecting", false)) {
					showDialog(0);
				} else if (sp.getValue("collecting", false)) {
					Log.d("check", "��� ����");
					showDialog(1);
				} else {
					Log.d("check", "����");
				}
			}
		});

		Criteria c = new Criteria();
		provider = lm.getBestProvider(c, true);

		if (provider == null || !lm.isProviderEnabled(provider)) {
			List<String> list = lm.getAllProviders();

			for (int i = 0; i < list.size(); i++) {
				String temp = list.get(i);
				if (lm.isProviderEnabled(temp)) {
					provider = temp;
					break;
				}
			}
		}
		Location location = lm.getLastKnownLocation(provider);
		
		if(location==null){
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		if (location == null) {
			Toast.makeText(this, "��밡���� ��ġ ���� �����ڰ� �����ϴ�.", Toast.LENGTH_SHORT)
					.show();
		} else {
			ll.onLocationChanged(location);
			new WeatherLoadingTask().execute(null, null);
		}

		new currentTimeThread().start();

		SpeedometerFrag speedometerFrag = new SpeedometerFrag();
		FragmentManager fManager = getFragmentManager();
		FragmentTransaction fTransaction = fManager.beginTransaction();
		fTransaction.replace(R.id.speedFragmentLayout, speedometerFrag);
		fTransaction.commit();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("���� ���");
		String msg;
		switch (id) {
		case 0:
			builder.setMessage("����� ���� �Ͻðڽ��ϱ�?")
					.setCancelable(false)
					.setPositiveButton("Ȯ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									sp.put("collecting", true);
									recordBtn.setText("�������");
									sp.put("record_id",
											sp.getValue("record_id", 0) + 1);
									lMgr.setAlarm();
									rTT.start();
								}
							})
					.setNegativeButton("���",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							});
			break;
		case 1:
			builder.setMessage("����� ���� �Ͻðڽ��ϱ�?")
					.setCancelable(false)
					.setPositiveButton("Ȯ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									sp.put("collecting", false);
									recordBtn.setText("��Ͻ���");
									lMgr.releaseAlarm();
									rTT.stateChange();
									rTT = null;
									rttTime = 0;
								}
							})
					.setNegativeButton("���",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							});
			break;
		}

		AlertDialog dialog = builder.create();
		return dialog;
	}

	public class SpeedometerFrag extends Fragment {

		Activity myActivity;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			myActivity = activity;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View myView = inflater.inflate(R.layout.fragment_speed_guage, null);
			speedometer = (Speedometer) myView.findViewById(R.id.Speedometer);
			return myView;
		}

	}

	private String[] getArgs() {
		// Current Time Print
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy�� M�� d��");
		SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH�� mm�� ss��");
		String strCurDate = CurDateFormat.format(date);
		String strCurTime = CurTimeFormat.format(date);
		String[] args = { strCurDate, strCurTime, address };
		return args;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Location ��û
		lm.requestLocationUpdates(provider, 1, 0, ll);
	}

	@Override
	public void onPause() {
		super.onPause();
		lm.removeUpdates(ll);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (((Button) findViewById(R.id.recordBut)).getText().toString()
				.equals("�������")) {
			lMgr.releaseAlarm();
		}
	}

	private void flashControl() {

		if (isOff) {
			try {
				if (getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_CAMERA_FLASH)) {
					cam = Camera.open();
					Parameters p = cam.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_TORCH);
					cam.setParameters(p);
					cam.startPreview();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_CAMERA_FLASH)) {
					cam.stopPreview();
					cam.release();
					cam = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		isOff = !isOff;

	}

	// == ���� �ð� ��� ������ ==

	class currentTimeThread extends Thread {
		public void run() {
			while (true) {
				Message myMsg = mHandler.obtainMessage(0, "");
				mHandler.sendMessage(myMsg);
				SystemClock.sleep(1000);
			}
		}
	}

	// == ��� �ð� ��� ������ ==

	class recordedTimeThread extends Thread {
		boolean recordedCheck = true;

		public void run() {
			while (recordedCheck) {
				Message myMsg = mHandler.obtainMessage(1, "");
				mHandler.sendMessage(myMsg);
				SystemClock.sleep(1000);
			}
			Log.d("Thread Check", "��� ���� �Ǿ����ϴ�~~~~~~~~~~~~");
		}

		public void stateChange() {
			recordedCheck = !recordedCheck;
		}
	}

	// Handler

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
				currentTimeTv.setText(dateFormat.format(calendar.getTime()));
				break;
			case 1:
				String rttT_Hour = String.format("%02d", rttTime / 3600);
				String rttT_Min = String.format("%02d", rttTime / 60);
				String rttT_Sec = String.format("%02d", rttTime % 60);
				rttTime++;
				recordedTimeTv.setText(rttT_Hour + ":" + rttT_Min + ":"
						+ rttT_Sec);
				break;
			}
		}
	};

	private class SpeedoActionListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			maxSpeedTv = (TextView) findViewById(R.id.maxSpeedMeter);
			avgSpeedTv = (TextView) findViewById(R.id.avgSpeedMeter);
			odoMeterTv = (TextView) findViewById(R.id.odoMeter);
			recordedTimeTv = (TextView) findViewById(R.id.recodedTimeMeter);
			currentTimeTv = (TextView) findViewById(R.id.currentTimeMeter);

			if (location != null) {

				// ����ӵ�
				currentSpeed = (location.getSpeed()) * 3.6;

				if (speedometer != null)
					speedometer.onSpeedChanged(Float.parseFloat(""
							+ currentSpeed));

				Log.d("����ӵ�", String.valueOf(currentSpeed));

				// �ְ�ӵ�
				if ((location.getSpeed()) * 3.6 > maxSpeed) {
					maxSpeed = (location.getSpeed()) * 3.6;
				}

				double msb = Math.round(maxSpeed * 100d) / 100d;
				maxSpeedTv.setText(String.valueOf(msb) + " Km/h");

				// ��ռӵ�
				avgSpeed = avgSpeed + currentSpeed;
				double as = avgSpeed / avgSpeedCnt;
				double asb = Math.round(as * 100d) / 100d;
				avgSpeedTv.setText(String.valueOf(asb) + " Km/h");
				avgSpeedCnt++;

				// ����Ÿ�

				if (lat1 != 0 && lon1 != 0) {

					if (lat1 != lat2 && lon1 != lon2) {
						lat2 = location.getLatitude();
						lon2 = location.getLongitude();

						double odoTemp = calDistance(lat1, lon1, lat2, lon2);
						recordedDist = recordedDist + odoTemp;
						double recordedTemp = Math.round(recordedDist * 100d) / 100d;
						odoMeterTv.setText(String.valueOf(String
								.valueOf(recordedTemp)) + "m");

						lat1 = lat2;
						lon1 = lon2;
						Log.d("�����Ÿ�", "�Ÿ� ���� ����");
					} else {
						Log.d("�����Ÿ�", "���� ����");
					}
				} else {
					lat1 = location.getLatitude();
					lon1 = location.getLongitude();
					Log.d("�����Ÿ�", "�ʱⰪ ���޾ƿ�");
				}

			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		public double calDistance(double lat1, double lon1, double lat2,
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getTitle().equals("select")) {
			FragmentManager fManager = getFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();
			switch (mode) {
			case GUAGE_MODE:
				fTransaction.replace(R.id.speedFragmentLayout, mFrag);
				mode = 1;
				break;
			case MAP_MODE:
				fTransaction.replace(R.id.speedFragmentLayout, sFrag);
				mode = 0;
				break;
			}
			fTransaction.commit();
		} else {
			flashControl();
		}
		return super.onOptionsItemSelected(item);
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
					address = sb.toString();
					sb.append("\n");
				}
			}
			weatherImg.setImageResource(Constants.weatherHash.get(result[3]));
			weatherTv.setText(sb.toString());
		} // onPostExecute : ��׶��� �۾��� ���� �� UI �۾��� �����Ѵ�.
	} // JsonLoadingTask
}
