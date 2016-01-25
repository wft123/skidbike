package edu.rapa.iot.android.skidbike.view;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.util.Constants;

public class RegistActivity extends Activity {
	/*
	 * ȸ������ Activity
	 */

	public final int REGIST_SUCCESS = 0;
	public final int REGIST_FAIL = 1;
	public final int SERVER_CLOSED = 2;
		
	Socket socket;
	ObjectOutputStream oos;
	ReceiverThread rcvThread;
	String ip = Constants.SERVER_IP;
	int port = Constants.SERVER_PORT;

	EditText emailE, phoneNumE, pwE, pwconE;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		emailE = (EditText) findViewById(R.id.newIDEt);
		phoneNumE = (EditText) findViewById(R.id.newPhoneNumEt);
		pwE = (EditText) findViewById(R.id.newPwEt);
		pwconE = (EditText) findViewById(R.id.newPwConfirmEt);
		
		new ConnectThread(ip,port).start();

		emailE = (EditText) findViewById(R.id.newIDEt);
		phoneNumE = (EditText) findViewById(R.id.newPhoneNumEt);
		pwE = (EditText) findViewById(R.id.newPwEt);
		pwconE = (EditText) findViewById(R.id.newPwConfirmEt);
		
		// ����̽��� �޴��� ��ȣ�� �������� �κ�
		TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String PhoneNumber = systemService.getLine1Number();
		PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
		PhoneNumber="0"+PhoneNumber;
		PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
		
		phoneNumE.setText(PhoneNumber);
		phoneNumE.setEnabled(false);		

		findViewById(R.id.okBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(socket==null){ // ������ ���ӵ��� ������ Activity ����
					Toast.makeText(RegistActivity.this,"������ ������ ���� �ʽ��ϴ�.",Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
				if(!isValid()) return;
				
				String msg = emailE.getText().toString().trim()+":"+pwE.getText().toString().trim()
						+":"+phoneNumE.getText().toString().trim();				
				try {
					oos.writeObject(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		});
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new ConnectThread(ip, port).start();
	}
	
	// �Է����� Validation Method
	private boolean isValid(){
		// �Է����� ���� �� 
		if(emailE.getText().toString().trim().equals("")||phoneNumE.getText().toString().trim().equals("")
			||pwE.getText().toString().trim().equals("")||pwconE.getText().toString().trim().equals("")){
			Toast.makeText(RegistActivity.this, getString(R.string.isEmpty), Toast.LENGTH_SHORT).show();
			return false;
		}
		// ��й�ȣ�� ����ġ ��
		if(!pwE.getText().toString().trim().equals(pwconE.getText().toString().trim())){
			Toast.makeText(RegistActivity.this, getString(R.string.password_not_correct), Toast.LENGTH_SHORT).show();
			return false;
		}				
		
		// �̸��� ������ �ƴ� ��
		if(!Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+",emailE.getText().toString().trim())){
			Toast.makeText(RegistActivity.this, getString(R.string.is_not_valid), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	// ���� ���� Thread
	class ConnectThread extends Thread{
		String ip;
		int port;
		public ConnectThread(String ip, int port){
			this.ip = ip;
			this.port = port;
		}

		public void run(){
			try {
				socket = new Socket(ip,port);
				Log.d("socket",socket.toString());
				oos = new ObjectOutputStream(socket.getOutputStream());
				rcvThread = new ReceiverThread();
				rcvThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
				mHandler.sendEmptyMessage(SERVER_CLOSED);
			}
		}
	}

	// �������� ���ϵ� ������ ����ϴ� Thread
	class ReceiverThread extends Thread {
		public ObjectInputStream in;    	

		public ReceiverThread() {
			try {
				this.in = new ObjectInputStream(socket.getInputStream());
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandler.sendEmptyMessage(SERVER_CLOSED);
			}
		}

		public void run() {
			while(in != null){
				try {
					Object obj = in.readObject();
					Message msg = new Message();
					
					if(obj.toString().contains("����")){
						msg.what = REGIST_SUCCESS;
					}else{
						msg.what = REGIST_FAIL;
					}					
					msg.obj = obj;
					mHandler.sendMessage(msg);
				} catch(Exception ex) {
					ex.printStackTrace();
					mHandler.sendEmptyMessage(SERVER_CLOSED);
				}
			}
		}
	}
	
	// UI�� �ݿ��ϱ� ���� �ڵ鷯
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case REGIST_SUCCESS:
				Toast.makeText(RegistActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				finish();
				break;
			case REGIST_FAIL:
				emailE.setText("");
				pwE.setText("");
				pwconE.setText("");
				Toast.makeText(RegistActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case SERVER_CLOSED:
				socket = null;
				oos = null;
				rcvThread = null;
				break;
			}
		}
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			if(rcvThread!=null) rcvThread.in.close();
			if(oos!=null) oos.close();
			if(socket!=null) socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
