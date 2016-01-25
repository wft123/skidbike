package edu.rapa.iot.android.skidbike.model;


/*
 * 시설물 파싱을 위해 필요한 facility객체입니다.
 * 시설물의 번호, 파일명, 카테고리, 주소, 위도, 경도 값을 가지고 있습니다
 * 파일명을 이용하여 추후 시설물 사진을 이용할 수 있습니다.
 */

public class Facility {

	private int objectId;
	private String fileName;
	private String category;
	private String address;
	private double lon;
	private double lat;

	public Facility() {

	}

	public Facility(int objectId, String fileName, String category,
			String address, double lon, double lat) {
		super();
		this.objectId = objectId;
		this.fileName = fileName;
		this.category = category;
		this.address = address;
		this.lon = lon;
		this.lat = lat;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public String toString() {
		return "Facility [objectId=" + objectId + ", fileName=" + fileName
				+ ", category=" + category + ", address=" + address + ", lon="
				+ lon + ", lat=" + lat + "]";
	}
	
	

}
