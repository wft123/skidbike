package edu.rapa.iot.android.skidbike.view;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import edu.rapa.iot.android.skidbike.R;
import edu.rapa.iot.android.skidbike.controler.LoggerDAO;

public class RideLogActivity extends ListActivity implements OnItemClickListener {
	
	/*
	 * 저장된 기록을 record_id로 Group을 지어서 ListView에 표출하는 Activity	
	 */
	
	LoggerDAO lDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lDAO = new LoggerDAO(this);
		lDAO.open();
		fillData();
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lDAO.close();
	}
	
	// DB에서 데이터를 가져와서 List에 뿌려준다.
	private void fillData() {
		Cursor skidCursor = lDAO.getRideLog();
		startManagingCursor(skidCursor);

		String[] from = new String[] { lDAO.KEY_DATE, lDAO.KEY_ADDRESS };
		int[] to = new int[] { R.id.dateTv, R.id.addressTv };

		SimpleCursorAdapter skids = new SimpleCursorAdapter(this,
				R.layout.listitem_ridelog, skidCursor, from, to);
		getListView().setAdapter(skids);
	}

	// Item click 시 record_id를 RideDetailActivity로 전송한다.
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(RideLogActivity.this, RideDetailActivity.class);
		Cursor skidCursor = lDAO.search(id);
		String recordId = skidCursor.getString(skidCursor
				.getColumnIndexOrThrow(LoggerDAO.KEY_RECORD_ID));
		Log.d("recordid", recordId);
		i.putExtra(LoggerDAO.KEY_RECORD_ID, recordId);
		startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.log_del);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 2:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Cursor skidCursor = lDAO.search(info.id);
			String recordId = skidCursor.getString(skidCursor
					.getColumnIndexOrThrow(LoggerDAO.KEY_RECORD_ID));
			lDAO.remove(recordId);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

}
