package com.heart.util;

public class MenuItems {

	private String menuName;
	private String menuPic;
	private int menuDrawable;


	public MenuItems(String receivedName, String receivedPic, int receiveDrawable){
		this.menuName = receivedName;
		this.menuPic = receivedPic;
		this.menuDrawable = receiveDrawable;
	}

	public String getName() {
		return menuName;
	}

	public String getPicPath() {
		return menuPic;
	}

	public int getDrawable() {
		return menuDrawable;
	}
}
