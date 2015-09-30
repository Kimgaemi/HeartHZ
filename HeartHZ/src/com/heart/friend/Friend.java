package com.heart.friend;

public class Friend {

	private String id;
	private String name;
	private String pic;
	private String phone;
	private int days;

	public Friend(String receivedId, String receivedName, String receivedPhone,
			String receivedPic, int days) {
		this.id = receivedId;
		this.name = receivedName;
		this.phone = receivedPhone;
		this.pic = receivedPic;
		this.days = days;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getPicPath() {
		return pic;
	}

	public int getDays() {
		return days;
	}
}
