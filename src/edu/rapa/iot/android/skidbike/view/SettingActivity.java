package edu.rapa.iot.android.skidbike.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.util.SBPreference;

public class SettingActivity extends Activity {
	/*
	 * 환경 설정을 위한 Activity
	 */

	Button timeSettingBtn;
	Switch scrSwitch;
	SBPreference sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sp = new SBPreference(this);

		scrSwitch = (Switch) findViewById(R.id.screenSwitch);		
		scrSwitch.setChecked(sp.getValue("Keep_Screen", false));
		
		
		timeSettingBtn = (Button) findViewById(R.id.timeSettingBtn);
		timeSettingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(1);
			}
		});					
		timeSettingBtn.setText(sp.getValue("interval", 1)+"분");
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sp.put("Keep_Screen", scrSwitch.isChecked());
	}
	
	// 수집 간격 설정을 위한 Dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		
		final LinearLayout linar = (LinearLayout) View.inflate(
				SettingActivity.this, R.layout.dialog_spinner,
				null);
		
		final Spinner spinner = (Spinner) linar.findViewById(R.id.selectTimeSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.loc_time_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
			
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.locationCollectTime))
		.setView(linar)
		.setPositiveButton("확인", new DialogInterface.OnClickListener(){   
			public void onClick(DialogInterface dialog, int whichButton){
				int interval = Integer.parseInt(spinner.getSelectedItem().toString().substring(0, spinner.getSelectedItem().toString().length()-1));
				sp.put("interval", interval);
				timeSettingBtn.setText(spinner.getSelectedItem().toString());
				Toast.makeText(SettingActivity.this, interval+" 설정 되었습니다", Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton("취소", new DialogInterface.OnClickListener(){      
			public void onClick(DialogInterface dialog, int whichButton){
				Toast.makeText(SettingActivity.this, "취소 되었습니다", Toast.LENGTH_SHORT).show();
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		return dialog;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
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
