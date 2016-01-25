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

/*
 * �ü��� ������ �����ϱ� ���� ������ DAO�Դϴ�.
 * ��ü���� ����� DB�� �Ľ̵� �����͸� �����ϴµ� �ֽ��ϴ�.
 * 
 * �� DAO�� facility ���̺��� �����ϰ�, ��� not null�� ������ 6���� ROW�� �����մϴ�.
 * 
 * getList()�� ���� ��ü �����͸� ���� �� ������
 * search(String category)�� ���� ���� ���ϴ� ī�װ��� ���� ����� �� �ֽ��ϴ�.
 * 
 * add(Facility facility)�� ���� �� �߰��� �����մϴ�.
 */


public class FacilityDAO implements IDAOMgr {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";

	private static final String TAG = "FacilityDAO";
	private DatabaseHelper fDbHelper;
	private SQLiteDatabase fDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table facility (_id integer primary key, "
			+ "filename text not null, "
			+ "category text not null, "
			+ "address text not null, "
			+ "latitude integer not null, "
			+ "longitude integer not null);";

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "facility";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	public FacilityDAO(Context ctx) {
		this.mCtx = ctx;
	}

	public FacilityDAO open() throws SQLException {
		fDbHelper = new DatabaseHelper(mCtx);
		fDb = fDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		fDbHelper.close();
	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}

	}

	@Override
	public long add(Facility facility) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, facility.getObjectId());
		initialValues.put(KEY_FILENAME, facility.getFileName());
		initialValues.put(KEY_CATEGORY, facility.getCategory());
		initialValues.put(KEY_ADDRESS, facility.getAddress());
		initialValues.put(KEY_LATITUDE, facility.getLat());
		initialValues.put(KEY_LONGITUDE, facility.getLon());

		return fDb.insert(DATABASE_TABLE, null, initialValues);
	}

	@Override
	public Cursor getList() {
		return fDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_FILENAME, KEY_CATEGORY, KEY_ADDRESS, KEY_LATITUDE,
				KEY_LONGITUDE }, null, null, null, null, null);
	}

	@Override
	public Cursor getFacility() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Cursor search(String category) {
		Cursor fCursor = fDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_CATEGORY, KEY_LATITUDE, KEY_LONGITUDE }, KEY_CATEGORY
				+ " = ?", new String[] {category }, null, null, null);
		if (fCursor != null) {
			fCursor.moveToFirst();
		}
		return fCursor;
	}

	@Override
	public void clear() {

	}

	@Override
	public Cursor search(long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long add(Skid skid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean update(long index, Skid skid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(String index) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
