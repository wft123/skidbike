package edu.rapa.iot.android.skidbike.view;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.controler.FacilityDAO;
import edu.rapa.iot.android.skidbike.controler.LoggerDAO;
import edu.rapa.iot.android.skidbike.model.Facility;
import edu.rapa.iot.android.skidbike.model.Skid;
import edu.rapa.iot.android.skidbike.util.FacilityParser;
import edu.rapa.iot.android.skidbike.util.SBPreference;

public class SplashActivity extends Activity {

	/*
	 * 초기 앱 실행시 표출되는 스플래쉬 화면의 Activity 입니다.
	 * 최초 앱 실행시 서울공공데이터 센터의 XML파일을 파싱하여
	 * SQLite를 이용하여 "\data"에 자료를 저장하게 됩니다.
	 * 서울공공데이터 센터의 정책에 따라 1000개씩의 자료만 
	 * 가져올 수 있으므로, 1000개가 넘어감에 따라 자동으로 나눠서 불러오게됩니다.
	 *  2번째 실행 부터는 3초간의 딜레이 시간을 가지고 있습니다.
	 */
	
	
	
	private static final int START_NUM = 1;
	private static final int END_NUM = 2000;

	
	/*
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 스플래시가 생성될떄 Preference로 최초 앱 실행 여부를 판단합니다.
	 * True일시 데이터 불러오기 실행
	 * false일시 데이터 불러오기를 싱행하지 않고 바로 메인으로 넘어갑니다.
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window win = getWindow();
		win.requestFeature(Window.FEATURE_NO_TITLE);
		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		SBPreference sp = new SBPreference(this);

		if (sp.getValue("isEmptyFacility", true)) {
			sp.put("isEmptyFacility", false);

			Toast.makeText(SplashActivity.this, "시설물 정보를 받아옵니다",
					Toast.LENGTH_LONG).show();
			new DBLoadingTask(SplashActivity.this).execute(END_NUM);
		} else {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					Intent intent;
					if (new SBPreference(SplashActivity.this).getValue(
							"userID", "").equals("")) {
						intent = new Intent(SplashActivity.this,LoginActivity.class);
					} else {
						intent = new Intent(SplashActivity.this,MainActivity.class);
					}
					startActivity(intent);
					finish();
				}
			}, 1000);
		}
	}
	
	
	
	/*
	 * DB에 시설물 정보를 넣기위한 AsyncTask의 구현 부입니다.
	 * excute시 파라미터로 Integer값을 받으며, 이 Integer값은
	 * 프로그레스바의 max값에 해당하게 됩니다.
	 * 프로그레스바를 그려주기위한 소스가 함께 작성되어 있습니다.
	 * 1. onPreExecute를 이용하여 doInBackground 작업 전에
	 * 프로그레스바 생성을 위한 준비를 하게 됩니다.
	 * 2. doInBackground에서는 파싱을 통해서 ArrayList를 facility객체로
	 *    생성하고, DB에 add하는 작업을 진행하게 됩니다. DB작업이 오래걸리는 관계로
	 *    전체적인 구조가 AsyncTask를 가지게 되는 이유 입니다.
	 * 3. onProgressUpdate는 프로그레스바의 진행을 그려주는 작업을 하게됩니다.
	 * 4. onPostExcute에서는 handler를 이용하여 LoginActivity를 호출합니다. 
	 */
	
	private class DBLoadingTask extends AsyncTask<Integer, String, Integer> {

		private ProgressDialog mDlg;
		private Context mContext;

		public DBLoadingTask(Context context) {
			mContext = context;
		}

		private FacilityDAO fDAO;
		ArrayList<Facility> facilityArry = new ArrayList<Facility>();
		FacilityParser f_parser = new FacilityParser();

		@Override
		protected void onPreExecute() {
			mDlg = new ProgressDialog(mContext);
			mDlg.setCancelable(false);
			mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDlg.setMessage("작업 시작");
			mDlg.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			final int taskCnt = params[0];
			publishProgress("max", Integer.toString(taskCnt));

			fDAO = new FacilityDAO(SplashActivity.this);
			fDAO.open();
			Log.d("SQLite", "SQLite Open......");

			// 시설물 정보 파싱(from:서울시 공공 데이터)
			try {
				if (END_NUM - START_NUM > 1000) {
					facilityArry = f_parser.parser(START_NUM, (END_NUM - 1000));
					facilityArry = f_parser.parser((END_NUM - 999), END_NUM);
				} else {
					facilityArry = f_parser.parser(START_NUM, END_NUM);
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Facility Parsing", "파싱 완료.....");

			// DB에 입력 작업 (f_parser to SQLite)

			for (int i = 0; i < facilityArry.size(); i++) {
				fDAO.add(facilityArry.get(i));
				publishProgress("progress", Integer.toString(i), Integer.toString(i) + "개의 시설물 저장완료");
				Log.d("Parset To DB", i + "번째 배열 DB저장");
				System.out.println(facilityArry.get(i).getObjectId());

			}

			fDAO.close();
			return facilityArry.size();
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			if (progress[0].equals("progress")) {
				mDlg.setProgress(Integer.parseInt(progress[1]));
				mDlg.setMessage(progress[2]);
			} else if (progress[0].equals("max")) {
				mDlg.setMax(Integer.parseInt(progress[1]));
			}
		}

		@Override
		protected void onPostExecute(Integer result) {

			mDlg.dismiss();
			Toast.makeText(mContext, Integer.toString(result) + "개 시설물 정보 저장 완료", Toast.LENGTH_SHORT).show();
			Log.d("Facility Parsing", "DB 저장완료");

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					Intent intent;
					if (new SBPreference(SplashActivity.this).getValue("userID", "").equals("")) {
						intent = new Intent(SplashActivity.this, LoginActivity.class);
					} else {
						intent = new Intent(SplashActivity.this, MainActivity.class);
					}
					startActivity(intent);
					// 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
					finish();
				}
			}, 1000);
		}
	}
}
