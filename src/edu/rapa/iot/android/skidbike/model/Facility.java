package edu.rapa.iot.android.skidbike.model;


/*
 * �ü��� �Ľ��� ���� �ʿ��� facility��ü�Դϴ�.
 * �ü����� ��ȣ, ���ϸ�, ī�װ�, �ּ�, ����, �浵 ���� ������ �ֽ��ϴ�
 * ���ϸ��� �̿��Ͽ� ���� �ü��� ������ �̿��� �� �ֽ��ϴ�.
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
