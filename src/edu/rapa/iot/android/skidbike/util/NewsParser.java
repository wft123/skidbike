package edu.rapa.iot.android.skidbike.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.rapa.iot.android.skidbike.model.News;


/*
 * SAX 파싱으로 뉴스 기사를 긁어오는 파서입니다.
 * Facility객체로 ArrayList로 값을 저장하며, 이는 
 * 
 */


public class NewsParser {

	private ArrayList<News> news_Array = new ArrayList<News>();

	public ArrayList<News> parser() throws ParserConfigurationException,
			SAXException, IOException {
		try {
			SAXParserFactory sf = SAXParserFactory.newInstance();
			SAXParser parser = sf.newSAXParser();
			URL newsURL = new URL("http://api.newswire.co.kr/rss/industry/1609");

			DefaultHandler newsHan = new newsHandler();

			parser.parse(newsURL.openStream(), newsHan);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return news_Array;

	}

	class newsHandler extends DefaultHandler {

		News news;

		boolean isItem=false;
		String state=null;
		
		
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
			if (qName.equals("item")) {
				isItem = true;	
				news = new News();
			}
			if(isItem){
				state = qName;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			if(String.valueOf(ch, start, length).trim().equals("")) return;
			
			if (isItem){
				if(state.equals("title") ){
					news.setNews_Title(String.valueOf(ch, start, length));
				}
				if(state.equals("link") ){
					String link = String.valueOf(ch, start, length);
					if(news.getNews_Link() !=null)
						link = news.getNews_Link()+String.valueOf(ch, start, length);
					news.setNews_Link(link);
				}
				if(state.equals("description") ){
					news.setNews_Description(String.valueOf(ch, start, length));
				}
				if(state.equals("pubDate") ){
					news.setNews_PubDate(String.valueOf(ch, start, length));
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			if(qName.equals("item")){
				isItem = false;
				news_Array.add(news);
				news = null;
			}
		}
	}
}
