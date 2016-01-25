package edu.rapa.iot.android.skidbike.view;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.util.Constants;
import edu.rapa.iot.android.skidbike.util.SBPreference;

public class LoginActivity extends Activity {
	/*
	 * Splash 후 로그인 화면
	 */
	
	public final int LOGIN_SUCCESS = 0;
	public final int LOGIN_FAIL = 1;
	public final int SERVER_CLOSED = 2;
		
	Socket socket;
	ObjectOutputStream oos;
	ReceiverThread rcvThread;
	String ip = Constants.SERVER_IP;
	int port = Constants.SERVER_PORT;
	
	EditText idEt, pwdEt;
	
	CheckBox autoLoginCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window win = getWindow();
		win.requestFeature(Window.FEATURE_NO_TITLE);
		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_login);
				
		idEt = (EditText) findViewById(R.id.idEt);
		pwdEt = (EditText) findViewById(R.id.pwdEt);
		
		autoLoginCheckBox = (CheckBox) findViewById(R.id.autoLoginCheckBox);
		
		findViewById(R.id.loginBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(socket==null){ // 소켓이 생성되지 않으면 Activity를 종료한다.
					Toast.makeText(LoginActivity.this,"서버와 접속이 되지 않아 종료합니다",Toast.LENGTH_SHORT).show();
					finish();
					return;
				} // 입력 정보를 모두 입력하지 않으면 토스트를 띄운다.
				if(idEt.getText().toString().trim().equals("")||pwdEt.getText().toString().trim().equals("")){
					Toast.makeText(LoginActivity.this,getString(R.string.isEmpty),Toast.LENGTH_SHORT).show();
					return;
				}
				String msg = idEt.getText().toString().trim()+":"+pwdEt.getText().toString().trim();
				try { // 서버에 입력한 정보를 보낸다.
					oos.writeObject(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});
		// 회원가입 화면 전환 
		findViewById(R.id.registBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, RegistActivity.class));
			}
		});
		
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new ConnectThread(ip, port).start();
	}
	
	// 서버 접속 Thread
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

	// 서버에서 전달되는 데이터를 받기 위한 Thread
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
					
					if(obj.toString().contains("성공")){
						msg.what = LOGIN_SUCCESS;
					}else{
						msg.what = LOGIN_FAIL;
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
	
	// Thread를 UI에 반영하기 위한 핸들러
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case LOGIN_SUCCESS:
				Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				if(autoLoginCheckBox.isChecked()){
					new SBPreference(LoginActivity.this).put("userID", idEt.getText().toString().trim());
				}
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
				break;
			case LOGIN_FAIL:
				idEt.setText("");
				pwdEt.setText("");
				Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case SERVER_CLOSED:
				rcvThread = null;
				oos = null;
				socket = null;
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
