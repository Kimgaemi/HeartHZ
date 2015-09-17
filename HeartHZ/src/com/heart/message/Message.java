package com.heart.message;

import android.os.Environment;
import android.util.Log;

public class Message {

	private int[] fileTag;
	private String fileNo;
	private String fileTitle;
	private String fileSender;
	private String senderPic;
	private String filePath;
	private String tagDate; 

	public Message(String fileNo, String fileTitle, String fileSender, String senderPic, int[] fileTag, String tagDate) {
		this.fileNo = fileNo;
		this.fileTitle = fileTitle;
		this.fileSender = fileSender;
		this.senderPic = senderPic;
		this.fileTag = fileTag;
		this.tagDate = tagDate;
	}

	public String getTitle() {
		return fileTitle;
	}

	public String getFrom() {
		return fileSender;
	}

	public String getPicPath() {
		Log.d("Tag", senderPic);
		return senderPic;
	}
	
	public String getFileNo() {
		return fileNo;
	}
	
	public String getFilePath() {
		filePath = Environment.getExternalStorageDirectory().getPath();
		filePath = filePath + "/HeartHZ/" + fileNo + ".wav";
		return filePath;
	}

	public String getEmotion() {
		String emotion = "";

		switch(fileTag[0]) {
		case 1 :
			emotion = "Joy";
			break;
		case 2 :
			emotion = "Gloomy";
			break;
		case 3:
			emotion = "Relaxed";
			break;
		case 4:
			emotion = "Tired";
			break;
		default :
			emotion = null;
			break;
		}

		return emotion;	
	}

	public String getWeather() {
		String weather = "";
		switch(fileTag[1]) {
		case 1 :
			weather = "Sunny";
			break;
		case 2 :
			weather = "Rainy";
			break;
		case 3:
			weather = "Snowy";
			break;
		case 4:
			weather = "Lighting";
			break;
		case 5:
			weather = "Cloudy";
			break;
		default :
			weather = null;
			break;
		}

		return weather;	
	}

	public String getTime() { 
		String time = "";
		switch(fileTag[2]) {
		case 1 :
			time = "Morning";
			break;
		case 2 :
			time = "DayTime";
			break;
		case 3:
			time = "Sunset";
			break;
		case 4:
			time = "Night";
			break;
		case 5:
			time = "Break of Day";
			break;
		default :
			time = null;
			break;
		}

		return time;	
	}

	public String getDate() {
		return tagDate;
	}
}
