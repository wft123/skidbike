package edu.rapa.iot.android.skidbike.model;

import android.database.Cursor;

public interface IDAOMgr {
	/*
	 * DAO�� �Լ��� �����ϴ� �������̽�
	 */
	Cursor getList();
	int size();
	Cursor search(long index);
	long add(Skid skid);
	boolean update(long index, Skid skid);
	boolean remove(String index);
	void clear();	
	Cursor getFacility();
	Cursor search(String category);
	long add(Facility facility);

}
