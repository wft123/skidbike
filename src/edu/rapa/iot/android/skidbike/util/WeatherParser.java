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
	 * �ּ� ������ ���� ������ �����ϴ� Parser
	 */

	public String[] getWeather(double lat, double lon) {

		String line;
		try {
			line = getStringFromUrl("http://map.naver.com/common2/getRegionByPosition.nhn?xPos="
					+ lon + "&yPos=" + lat);
			// ���ڿ��� " �� ��ȣ �����
			line = line.replace("\"", "");
			// , �� �������� ���ڿ� split
			String[] array = line.split(",");
			// ���ϴ� ������ �̾Ƴ���!
			String[] wArray = { array[2].split(":")[1], array[4].split(":")[1],
					array[6].split(":")[1], array[10].split(":")[1],
					"���� : "+array[11].split(":")[1] };
			return wArray;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// getStringFromUrl : �־��� URL �������� ���ڿ��� ��´�.
	public String getStringFromUrl(String url)
			throws UnsupportedEncodingException {

		// �Է½�Ʈ���� "UTF-8" �� ����ؼ� ���� ��, ���� ������ �����͸� ���� �� �ִ� BufferedReader ��
		// �����Ѵ�.
		BufferedReader br = new BufferedReader(new InputStreamReader(
				getInputStreamFromUrl(url), "UTF-8"));

		// ���� �����͸� ������ StringBuffer �� �����Ѵ�.
		StringBuffer sb = new StringBuffer();

		try {
			// ���� ������ ���� �����͸� �ӽ� ������ ���ڿ� ���� line
			String line = null;

			// ���� ������ �����͸� �о StringBuffer �� �����Ѵ�.
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	} // getStringFromUrl

	// getInputStreamFromUrl : �־��� URL �� ���� �Է� ��Ʈ��(InputStream)�� ��´�.
	public static InputStream getInputStreamFromUrl(String url) {
		InputStream contentStream = null;
		try {
			// HttpClient �� ����ؼ� �־��� URL�� ���� �Է� ��Ʈ���� ��´�.
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			contentStream = response.getEntity().getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentStream;
	} // getInputStreamFromUrl

}
