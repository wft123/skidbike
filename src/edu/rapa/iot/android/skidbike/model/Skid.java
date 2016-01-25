package edu.rapa.iot.android.skidbike.model;

public class Skid {
	// 저장되는 Logger 정보를 담는 Skid 객체
	
	private String date;
	private String time;
	private String address;
	private String weather;
	private String temp;
	private String latitude;
	private String longitude;
	private String record_id;
	
	public Skid(String date, String time, String address, String weather,
			String temp, String latitude, String longitude, String record_id) {
		super();
		this.date = date;
		this.time = time;
		this.address = address;
		this.weather = weather;
		this.temp = temp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.record_id = record_id;
	}

	@Override
	public String toString() {
		return "Skid [date=" + date + ", time=" + time + ", address=" + address
				+ ", weather=" + weather + ", temp=" + temp + ", latitude="
				+ latitude + ", longitude=" + longitude + ", record_id="
				+ record_id + "]";
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRecord_id() {
		return record_id;
	}

	public void setRecord_id(String record_id) {
		this.record_id = record_id;
	}
	
}
