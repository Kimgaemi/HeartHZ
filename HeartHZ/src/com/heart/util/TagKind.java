package com.heart.util;

public class TagKind {
	private int musicCur;
	private int[] emotion = new int[4];
	private int[] time = new int[5];
	private int[] weather = new int[5];
	private String tagDate;
	private int isdateInput;
	private int TagCount;

	public TagKind() {
		for (int i = 0; i < 4; i++) {
			emotion[i] = 0;
		}
		for (int i = 0; i < 5; i++) {
			time[i] = 0;
			weather[i] = 0;
		}
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int year = cal.get(cal.YEAR);
		int month = cal.get(cal.MONTH) + 1;
		String monthString = String.valueOf(month);
		int date = cal.get(cal.DATE);
		if (month < 10)
			monthString = "0" + monthString;
		tagDate = monthString + String.valueOf(date) + String.valueOf(year);
		isdateInput = -1;
		musicCur = -1;
	}

	public int getTagCount() {
		int result = 0;
		if (getMusicCur() >= 0) {
			result++;
		}
		if (getEmotion() >= 0) {
			result++;
		}
		if (getTime() >= 0) {
			result++;
		}
		if (getWeather() >= 0) {
			result++;
		}
		if (getCDate() >= 0) {
			result++;
		}
		return result;
	}

	public int getEmotion() {
		int result = -1;
		for (int i = 0; i < 4; i++) {
			if (emotion[i] == 1) {
				result = i;
				break;
			}
		}
		return result;
	}

	public int getTime() {
		int result = -1;
		for (int i = 0; i < 5; i++) {
			if (time[i] == 1) {
				result = i;
				break;
			}
		}
		return result;
	}

	public int getMusicCur() {
		return musicCur;
	}

	public int getWeather() {
		int result = -1;
		for (int i = 0; i < 5; i++) {
			if (weather[i] == 1) {
				result = i;
				break;
			}
		}
		return result;
	}

	public String getDate() {
		return tagDate;
	}

	public void setEmotion(int pos, int value) {
		for (int i = 0; i < 4; i++)
			emotion[i] = 0;
		emotion[pos] = value;
	}

	public void setTime(int pos, int value) {
		for (int i = 0; i < 4; i++)
			time[i] = 0;
		time[pos] = value;
	}

	public void setWeather(int pos, int value) {
		for (int i = 0; i < 4; i++)
			weather[i] = 0;
		weather[pos] = value;
	}

	public int getCDate() {
		return isdateInput;
	}

	public void setDate(String inputDate, int value) {
		if (value == 1) {
			isdateInput = 1;
			this.tagDate = inputDate;
		} else {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			int year = cal.get(cal.YEAR);
			int month = cal.get(cal.MONTH) + 1;
			String monthString = String.valueOf(month);
			int date = cal.get(cal.DATE);
			if (month < 10)
				monthString = "0" + monthString;
			tagDate = monthString + String.valueOf(date) + String.valueOf(year);
			isdateInput = -1;
		}

	}

	public void setMusicCur(int num) {
		musicCur = num;
	}
}
