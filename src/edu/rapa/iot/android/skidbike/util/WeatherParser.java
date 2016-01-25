package edu.rapa.iot.android.skidbike.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class WeatherParser {
	/*
	 * 주소 정보와 날씨 정보를 수집하는 Parser
	 */

	public String[] getWeather(double lat, double lon) {

		String line;
		try {
			line = getStringFromUrl("http://map.naver.com/common2/getRegionByPosition.nhn?xPos="
					+ lon + "&yPos=" + lat);
			// 문자열의 " 이 기호 지우기
			line = line.replace("\"", "");
			// , 를 기준으로 문자열 split
			String[] array = line.split(",");
			// 원하는 정보만 뽑아내기!
			String[] wArray = { array[2].split(":")[1], array[4].split(":")[1],
					array[6].split(":")[1], array[10].split(":")[1],
					"현재 : "+array[11].split(":")[1] };
			return wArray;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// getStringFromUrl : 주어진 URL 페이지를 문자열로 얻는다.
	public String getStringFromUrl(String url)
			throws UnsupportedEncodingException {

		// 입력스트림을 "UTF-8" 를 사용해서 읽은 후, 라인 단위로 데이터를 읽을 수 있는 BufferedReader 를
		// 생성한다.
		BufferedReader br = new BufferedReader(new InputStreamReader(
				getInputStreamFromUrl(url), "UTF-8"));

		// 읽은 데이터를 저장한 StringBuffer 를 생성한다.
		StringBuffer sb = new StringBuffer();

		try {
			// 라인 단위로 읽은 데이터를 임시 저장한 문자열 변수 line
			String line = null;

			// 라인 단위로 데이터를 읽어서 StringBuffer 에 저장한다.
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	} // getStringFromUrl

	// getInputStreamFromUrl : 주어진 URL 에 대한 입력 스트림(InputStream)을 얻는다.
	public static InputStream getInputStreamFromUrl(String url) {
		InputStream contentStream = null;
		try {
			// HttpClient 를 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			contentStream = response.getEntity().getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentStream;
	} // getInputStreamFromUrl

}
