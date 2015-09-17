package com.heart.friend;

public class Friend {

	private String id;
	private String name;
	private String pic;
	private String cpic;
	private String phone;
	private String email;

	public Friend(String receivedId, String receivedName, String receivedPhone, String receivedPic, String receivedCPic, String receivedEmail){
		this.id = receivedId;
		this.name = receivedName;
		this.phone = receivedPhone;
		this.pic = receivedPic;
		this.cpic = receivedCPic;
		this.email = receivedEmail;
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
	
	public String getCPicPath() {
		return cpic;
	}
	
	public String getEmail() {
		return email;
	}
}
