package edu.rapa.iot.android.skidbike.util;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.rapa.iot.android.skidbike.model.Facility;
import edu.rapa.iot.android.skidbike.model.News;
import edu.rapa.iot.android.skidbike.util.NewsParser.newsHandler;

/*
 *  ����ÿ� �����ϴ� �پ��� ������ ���� �ü��� ���������� ���п��� �޾ƿ� �Ľ��� �ִ� Ŭ���� �Դϴ�.
 *  �� 1000���� ��ȸ �ѵ��� �ֽ��ϴ�.
 *  parser�� �Ķ������ int start, int end�� ���۹�ȣ�� �����ȣ�� �Է��ؾ��ϸ�,
 *  �̴� ����ÿ��� �����ϴ� ������ ȣ������ �ݵ�� ���۰� ���� �������־�� �ϱ⶧���� �Է¹ް� �˴ϴ�.
 *  Splashȭ�鿡�� static�� �̿��� �� �Ķ���Ͱ��� ������ ���ҽ��ϴ�.
 */




public class FacilityParser {

	private ArrayList<Facility> facility_Array = new ArrayList<Facility>();

	public ArrayList<Facility> parser(int start, int end) throws Exception,
			SAXException {

		SAXParserFactory sf = SAXParserFactory.newInstance();
		SAXParser parser = sf.newSAXParser();
		URL facilityURL = new URL("http://openapi.seoul.go.kr:8088/"
				+ "4642416d7663686139385747514f47/" + "xml/"
				+ "GeoInfoBikeConvenientFacilitiesWGS/" + start + "/" + end);

		DefaultHandler facilityHan = new facilityHandler();
		parser.parse(facilityURL.openStream(), facilityHan);

		return facility_Array;

	}

	public class facilityHandler extends DefaultHandler {

		Facility facility;

		boolean isRow = false;
		String state = null;

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			System.out.println("start Read");
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			System.out.println("End Read");
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("row")) {
				isRow = true;
				facility = new Facility();
			}
			if (isRow) {
				state = qName;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			if (String.valueOf(ch, start, length).trim().equals(""))
				return;

			if (isRow) {
				if (state.equals("OBJECTID")) {
					facility.setObjectId(Integer.parseInt(String.valueOf(ch,
							start, length)));
				}
				if (state.equals("FILENAME")) {
					facility.setFileName(String.valueOf(ch, start, length));
				}
				if (state.equals("CLASS")) {
					facility.setCategory(String.valueOf(ch, start, length));
				}
				if (state.equals("ADDRESS")) {
					facility.setAddress(String.valueOf(ch, start, length));
				}
				if (state.equals("LNG")) {

					facility.setLon(Double.parseDouble(String.valueOf(ch,
							start, length)));
				}
				if (state.equals("LAT")) {
					facility.setLat(Double.parseDouble(String.valueOf(ch,
							start, length)));
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			if (qName.equals("row")) {
				isRow = false;
				facility_Array.add(facility);
				facility = null;
			}
		}

	}
}
