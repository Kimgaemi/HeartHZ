package com.heart.util;

public class Config {

	// JSON Node names
	public static final String TAG_SUCCESS = "success";

	public static final String TAG_USER = "user";
	public static final String TAG_PW = "password";
	public static final String TAG_NAME = "name";
	public static final String TAG_PHONE = "phone";
	public static final String TAG_MODEL = "model";
	public static final String TAG_USER_ID = "user_id";
	public static final String TAG_USER_EMAIL = "email";
	public static final String TAG_PIC_PATH = "pic_path";
	public static final String TAG_MUSIC_PATH = "music_path";
	public static final String TAG_CPIC_PATH = "pic_cpath";

	public static final String TAG_FRIENDS = "friends";
	public static final String TAG_FRIEND_ID = "friend_id";
	public static final String TAG_FRIEND_NAME = "friend_name";
	public static final String TAG_FIREND_PIC = "friend_pic";
	public static final String TAG_FIREND_PHONE = "friend_phone";

	public static final String TAG_FILES = "files";
	public static final String TAG_FILE_NO = "file_no";
	public static final String TAG_FILE_PATH = "file_path";
	public static final String TAG_FILE_TITLE = "file_title";

	public static final String TAG_EMOTION = "emotion";
	public static final String TAG_WEATHER = "weather";
	public static final String TAG_TIME = "time";
	public static final String TAG_DATE = "date";

	public static final String TAG_CONTEXT_ARR = "contenxt";



	// url to load user info (phone > all)
	// **URL_SIGN_IN = URL_USER_DETAILS (PHP)
	public static final String URL_SIGN_IN = "http://210.125.96.96/heart_php/get_user_details.php";	
	public static final String URL_USER_DETAILS = "http://210.125.96.96/heart_php/get_user_details.php";	

	// url to create new user
	public static final String URL_SIGN_UP = "http://210.125.96.96/heart_php/create_user.php";
	public static final String URL_CHANGE_FLAG = "http://210.125.96.96/heart_php/change_flag.php";	

	// url to load all friends (user id > all related firends info)
	public static final String URL_GET_FIRENDS_LIST = "http://210.125.96.96/heart_php/get_all_friends.php";
	public static final String URL_GET_USER_LIST = "http://210.125.96.96/heart_php/get_all_users.php";
	public static final String URL_GET_MESSAGE_LIST = "http://210.125.96.96/heart_php/get_all_messages.php";

	// url to set the relation between users
	public static final String URL_SET_RELATION = "http://210.125.96.96/heart_php/create_relation.php";

	// File upload url (replace the ip with your server address)
	public static final String URL_FILE_UPLOAD = "http://210.125.96.96/heart_php/file_upload.php";
	public static final String URL_USER_UPDATE = "http://210.125.96.96/heart_php/user_upload.php";
	public static final String URL_GET_USER_PROFILE = "http://210.125.96.96/heart_php/get_profile.php";
	
	// Directory name to store captured images and videos
	public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";


}
