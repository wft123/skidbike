package edu.rapa.iot.android.skidbike.controler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.rapa.iot.android.skidbike.model.Facility;
import edu.rapa.iot.android.skidbike.model.IDAOMgr;
import edu.rapa.iot.android.skidbike.model.Skid;

public class LoggerDAO implements IDAOMgr {
	/**
	 *  Skid 객체를 DB에 저장하고 관리하는 DAO
	 */

	public static final String KEY_ROWID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_TIME = "time";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_WEATHER = "weather";
	public static final String KEY_TEMP = "temp";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_RECORD_ID = "record_id";

	private static final String TAG = "LoggerDAO";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table skids (_id integer primary key autoincrement, "
			+ "date text not null, time text not null, address text not null, weather text not null, temp text not null, latitude text not null, longitude text not null, record_id integer not null);";

	private static final String DATABASE_NAME = "dataa";
	private static final String DATABASE_TABLE = "skids";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	public LoggerDAO(Context ctx) {
		this.mCtx = ctx;
	}

	public LoggerDAO open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		// Database Version 이 바뀌면 call 된다.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	@Override
	public long add(Skid skid) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATE, skid.getDate());
		initialValues.put(KEY_TIME, skid.getTime());
		initialValues.put(KEY_ADDRESS, skid.getAddress());
		initialValues.put(KEY_WEATHER, skid.getWeather());
		initialValues.put(KEY_TEMP, skid.getTemp());
		initialValues.put(KEY_LATITUDE, skid.getLatitude());
		initialValues.put(KEY_LONGITUDE, skid.getLongitude());
		initialValues.put(KEY_RECORD_ID, skid.getRecord_id());

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	@Override
	public Cursor getList() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_TIME, KEY_ADDRESS, KEY_WEATHER, KEY_TEMP, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_RECORD_ID }, null, null, null, null, null);
	}

	public Cursor getRideLog() {
		// TODO Auto-generated method stub

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_TIME, KEY_ADDRESS, KEY_WEATHER, KEY_TEMP, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_RECORD_ID }, null, null, KEY_RECORD_ID,
				null, null);
	}

	@Override
	public Cursor search(long index) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_TIME, KEY_ADDRESS, KEY_WEATHER, KEY_TEMP, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_RECORD_ID }, KEY_ROWID + "=" + index, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getRecords(String index) {
		Cursor mCursor =
		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_TIME, KEY_ADDRESS, KEY_WEATHER, KEY_TEMP, KEY_LATITUDE,
				KEY_LONGITUDE, KEY_RECORD_ID }, KEY_RECORD_ID + "=" + index,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	@Override
	public boolean update(long index, Skid skid) {
		ContentValues args = new ContentValues();
		args.put(KEY_DATE, skid.getDate());
		args.put(KEY_TIME, skid.getTime());
		args.put(KEY_ADDRESS, skid.getAddress());
		args.put(KEY_WEATHER, skid.getWeather());
		args.put(KEY_TEMP, skid.getTemp());
		args.put(KEY_LATITUDE, skid.getLatitude());
		args.put(KEY_LONGITUDE, skid.getLongitude());
		args.put(KEY_RECORD_ID, skid.getRecord_id());

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + index, null) > 0;
	}

	@Override
	public boolean remove(String index) {
		return mDb.delete(DATABASE_TABLE, KEY_RECORD_ID + "=" + index, null) > 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor getFacility() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor search(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long add(Facility facility) {
		// TODO Auto-generated method stub
		return 0;
	}

}
