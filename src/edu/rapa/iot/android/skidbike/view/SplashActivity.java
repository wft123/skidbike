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
	 * �ʱ� �� ����� ǥ��Ǵ� ���÷��� ȭ���� Activity �Դϴ�.
	 * ���� �� ����� ������������� ������ XML������ �Ľ��Ͽ�
	 * SQLite�� �̿��Ͽ� "\data"�� �ڷḦ �����ϰ� �˴ϴ�.
	 * ������������� ������ ��å�� ���� 1000������ �ڷḸ 
	 * ������ �� �����Ƿ�, 1000���� �Ѿ�� ���� �ڵ����� ������ �ҷ����Ե˴ϴ�.
	 *  2��° ���� ���ʹ� 3�ʰ��� ������ �ð��� ������ �ֽ��ϴ�.
	 */
	
	
	
	private static final int START_NUM = 1;
	private static final int END_NUM = 2000;

	
	/*
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * ���÷��ð� �����ɋ� Preference�� ���� �� ���� ���θ� �Ǵ��մϴ�.
	 * True�Ͻ� ������ �ҷ����� ����
	 * false�Ͻ� ������ �ҷ����⸦ �������� �ʰ� �ٷ� �������� �Ѿ�ϴ�.
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

			Toast.makeText(SplashActivity.this, "�ü��� ������ �޾ƿɴϴ�",
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
	 * DB�� �ü��� ������ �ֱ����� AsyncTask�� ���� ���Դϴ�.
	 * excute�� �Ķ���ͷ� Integer���� ������, �� Integer����
	 * ���α׷������� max���� �ش��ϰ� �˴ϴ�.
	 * ���α׷����ٸ� �׷��ֱ����� �ҽ��� �Բ� �ۼ��Ǿ� �ֽ��ϴ�.
	 * 1. onPreExecute�� �̿��Ͽ� doInBackground �۾� ����
	 * ���α׷����� ������ ���� �غ� �ϰ� �˴ϴ�.
	 * 2. doInBackground������ �Ľ��� ���ؼ� ArrayList�� facility��ü��
	 *    �����ϰ�, DB�� add�ϴ� �۾��� �����ϰ� �˴ϴ�. DB�۾��� �����ɸ��� �����
	 *    ��ü���� ������ AsyncTask�� ������ �Ǵ� ���� �Դϴ�.
	 * 3. onProgressUpdate�� ���α׷������� ������ �׷��ִ� �۾��� �ϰԵ˴ϴ�.
	 * 4. onPostExcute������ handler�� �̿��Ͽ� LoginActivity�� ȣ���մϴ�. 
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
			mDlg.setMessage("�۾� ����");
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

			// �ü��� ���� �Ľ�(from:����� ���� ������)
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
			Log.d("Facility Parsing", "�Ľ� �Ϸ�.....");

			// DB�� �Է� �۾� (f_parser to SQLite)

			for (int i = 0; i < facilityArry.size(); i++) {
				fDAO.add(facilityArry.get(i));
				publishProgress("progress", Integer.toString(i), Integer.toString(i) + "���� �ü��� ����Ϸ�");
				Log.d("Parset To DB", i + "��° �迭 DB����");
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
			Toast.makeText(mContext, Integer.toString(result) + "�� �ü��� ���� ���� �Ϸ�", Toast.LENGTH_SHORT).show();
			Log.d("Facility Parsing", "DB ����Ϸ�");

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
					// �ڷΰ��� ������� �ȳ������� �����ֱ� >> finish!!
					finish();
				}
			}, 1000);
		}
	}
}
