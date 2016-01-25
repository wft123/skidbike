package edu.rapa.iot.android.skidbike.util;

import java.util.HashMap;

import edu.rapa.iot.android.skidbike.R;


public class Constants {
	// Application���� ���� ���Ǵ� ����� ������ ���� Class
	public static final String SERVER_IP = "192.168.3.28";
	public static final int SERVER_PORT = 7070;
	public static final HashMap<String,Integer> weatherHash = getHash(); 
	public static final String API_KEY = "62299b0df732297358cca39baf6f8cd4";
	
	// ������ ���� �̹����� �������ִ� HashMap
	private static HashMap<String,Integer> getHash(){
		HashMap<String,Integer> tempHash = new HashMap<String,Integer>();
		
		tempHash.put("����", R.drawable.sun);
		tempHash.put("��������", R.drawable.cloudy);
		tempHash.put("��������", R.drawable.manycloud);
		tempHash.put("�帲", R.drawable.blackcloud);
		tempHash.put("�� ����", R.drawable.blackcloud);
		tempHash.put("���Ѻ�", R.drawable.rain);
		tempHash.put("�̽���", R.drawable.rain);
		tempHash.put("�ҳ���", R.drawable.rain);
		tempHash.put("��", R.drawable.rain);
		tempHash.put("���Ѻ�", R.drawable.rain);
		tempHash.put("�� ����", R.drawable.rain);
		tempHash.put("��", R.drawable.snow);
		
		return tempHash;
	}
	
}
