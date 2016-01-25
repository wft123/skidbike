package edu.rapa.iot.android.skidbike.model;


/*
 * News파싱을 위해 필요한 News객체입니다.
 * 뉴스의 제목, 주소, 상세내용, 발행일을 모두 String 변수로 가지고 있습니다.
 */

public class News {

	private String news_Title;
	private String news_Link;
	private String news_Description;
	private String news_PubDate;

	public News() {

	}

	public News(String news_Title, String news_Link, String news_Description,
			String news_PubDate) {
		super();
		this.news_Title = news_Title;
		this.news_Link = news_Link;
		this.news_Description = news_Description;
		this.news_PubDate = news_PubDate;
	}

	public String getNews_Title() {
		return news_Title;
	}

	public void setNews_Title(String news_Title) {
		this.news_Title = news_Title;
	}

	public String getNews_Link() {
		return news_Link;
	}

	public void setNews_Link(String news_Link) {
		this.news_Link = news_Link;
	}

	public String getNews_Description() {
		return news_Description;
	}

	public void setNews_Description(String news_Description) {
		this.news_Description = news_Description;
	}

	public String getNews_PubDate() {
		return news_PubDate;
	}

	public void setNews_PubDate(String news_PubDate) {
		this.news_PubDate = news_PubDate;
	}

	@Override
	public String toString() {
		return "News [news_Title=" + news_Title + ", news_Link=" + news_Link
				+ ", news_Description=" + news_Description + ", news_PubDate="
				+ news_PubDate + "]";
	}

}
