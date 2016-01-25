package edu.rapa.iot.android.skidbike.util;

import java.util.HashMap;

import edu.rapa.iot.android.skidbike.R;


public class Constants {
	// Application에서 자주 사용되는 상수를 정의해 놓은 Class
	public static final String SERVER_IP = "192.168.3.28";
	public static final int SERVER_PORT = 7070;
	public static final HashMap<String,Integer> weatherHash = getHash(); 
	public static final String API_KEY = "62299b0df732297358cca39baf6f8cd4";
	
	// 날씨에 따른 이미지를 제공해주는 HashMap
	private static HashMap<String,Integer> getHash(){
		HashMap<String,Integer> tempHash = new HashMap<String,Integer>();
		
		tempHash.put("맑음", R.drawable.sun);
		tempHash.put("구름조금", R.drawable.cloudy);
		tempHash.put("구름많음", R.drawable.manycloud);
		tempHash.put("흐림", R.drawable.blackcloud);
		tempHash.put("비 끝남", R.drawable.blackcloud);
		tempHash.put("약한비", R.drawable.rain);
		tempHash.put("이슬비", R.drawable.rain);
		tempHash.put("소나기", R.drawable.rain);
		tempHash.put("비", R.drawable.rain);
		tempHash.put("약한비", R.drawable.rain);
		tempHash.put("비 끝남", R.drawable.rain);
		tempHash.put("눈", R.drawable.snow);
		
		return tempHash;
	}
	
}
