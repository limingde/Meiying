package com.dd.meiying.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dd.meiying.MeiyingAplication;

public class MeiyingResource {
	private static final String TAG = "meiyingResource";

	public static final String DEVELOPER_OPTION_CMD = "*111#";

	public static final String VERSION_NAME = "meiying_version";

	// 不同的app有不同的code，针对使用push推送不同的应用, 魅影为null，其他的需要编号
	public static final String appNameCode = null;

	public static final String appNameCodeSegment = "#";

	private static String appVersionName = null;
	private static String appVersionCode = null;
	private static String packageName = null;

	public static final String CLIENT_ID_NAME = "meiying_client_id";

	public static final String FOLDER = "/meiying/cache/image";// 这个目录里的图片名会md5，这样相册看不到
	public static final String PHOTO_FOLDER = "/meiying/photo";// 这个目录里的图片名不会md5，这样相册看得到
	public static final String PUSH_MSG_FOLDER = "/meiying/push";// 存放push消息，放程序目录下
	public static final String HTTP_CACHE_DIR = "/meiying/cache/http";// http缓存目录

	// 根据结果,用于Handler判断
	public static final int SUC = 1;
	public static final int ERROR = 0;
	public static final int WAITTING = 2;
	public static final int CONNECTTIMEOUT = 3;
	public static final int LOGIN = 4;
	public static final int LOGOUT = 5;
	public static final int DISCONNECTED = 6;

	// sharedPreference name
	public static final String SAVE_DATA_TOPIC = "Topic";
	public static final String SAVE_DATA_PRODUCT = "Product";
	public static final String SAVE_DATA_CONST = "const";
	public static final String SAVE_DATA_CONST_HTTPHOST = "ConstHttphost";
	public static final String SAVE_USER = "userinfo";
	public static final String SAVE_CHECKVERSION_TIME = "checkversion.time";

	public static final String KEY_LATEST_VBOOK_TIME = "latest vbook time";
	public static final String KEY_USER_INFO_NEW_NUM = "user info new num";
	public static final String KEY_USER_INFO_NEW_NUM_feed = "user info new num feed";
	public static final String KEY_USER_INFO_NEW_NUM_coin = "user info new num coin";

	public static final String KEY_CHECKVERSION_TIME = "key_checkversion.time";
	public static final String KEY_CONST_HTTPHOST = "ConstHttphost";
	public static final String KEY_PUSH_STATE = "msgpushstate";
	public static final String KEY_HTTP_HEADER_MUD = "header.mud";
	public static final String KEY_PUSH_PARAMS_SERVER_HOST = "push_params.server_host";
	public static final String KEY_PUSH_PARAMS_SERVER_PORT = "push_params.server_port";
	public static final String KEY_PUSH_PARAMS_LOG_ENABLE = "push_params.log_enable";

	public static final String KEY_PUSH_PARAMS_XIAOMI_REGID = "push_params.regId";
	public static final String ACTION_GET_XIAOMI_REGID = "get xiaomi regId";

	public static final String KEY_FIRST_IN_GRADE = "first in grade";

	public static final String KEY_GUIDE_VERSION = "guide version";
	public static final String KEY_SHOW_GUIDE = "show_guide_040000";
	public static final String KEY_APP_START_COUNT = "app start count";

	public static final String KEY_THE_DAY_THAT_TIME = "the_day_that_time";
	public static final String KEY_THE_DAY_THAT_HAS_CLICK = "the_day_that_has_click";

	public static final String KEY_DAFEN_COUNT = "dafen count";
	public static final String KEY_DAFEN_TIMESTAMP = "dafen timestamp";

	public static final String KEY_Huati_tips_closed = "huati tips closed";

	public static final String KEY_CHAT_CURRENT_PARTER = "current chat partner";

	public static final String KEY_DEL_HTTP_CACHE_TIMESTAMP = "delete http cache timestamp";

	public static final String KEY_DEL_IMAGE_CACHE_TIMESTAMP = "delete image cache timestamp";

	public static final String KEY_SHOW_MORE_MASS = "show more mass";

	public static final String KEY_PUSH_IS_KICKED = "is kicked";

	public static final String KEY_DYNAMIC_TIME = "dynamic time";
	public static final String KEY_DIVCE_HAS_LOGIN = "divce has login";
	public static final String KEY_DIVCE_HAS_CHECKIN = "divce has checkin";

	public static final boolean MSG_PUSH_STATE_OPEN = true;
	public static final boolean MSG_PUSH_STATE_CLOSE = false;

	public static final  String KEY_IMGS_HAS_AUTO_CLEAR = "imgs has auto clear";

	public static final  String KEY_USER_PERIOD_INFO_ID = "user_period_info_id";
	public static final  String KEY_USER_PERIOD_INFO_CIRCLE = "user period info circle";
	public static final  String KEY_USER_PERIOD_INFO_LASTDAYS = "user period info lastdays";
	public static final  String KEY_USER_PERIOD_INFO_STARTDAY = "user period info startday";
	public static final  String KEY_USER_PERIOD_INFO_REMIND_PEROID = "user period info remind period";
	public static final  String KEY_USER_PERIOD_INFO_REMIND_OVULATION = "user period info remind ovulation";
	public static final  String KEY_USER_PERIOD_INFO_REMIND_PEROID_TIME = "user period info remind period time";
	public static final  String KEY_USER_PERIOD_INFO_REMIND_OVULATION_TIME = "user period info remind ovulation time";

	public static final  String KEY_PERIOD_DOWNLOAD_TIME = "period download time";

	private static String mUniqueId;



	private static String getUniqueId(Context context,
			ContentResolver contentResolver) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(contentResolver,
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		// 如何是新的app增加appNameCode的编译
		if (!TextUtils.isEmpty(appNameCode)) {
			uniqueId = uniqueId + appNameCodeSegment + appNameCode;
		}
		return uniqueId;
	}
	public static String getImei() {
		String imei = "";
		try{
			Context context = MeiyingAplication.CONTEXT.getBaseContext();
			final TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ""+imei;
	}
	private static class Installation {
		private static String sID = null;
		private static final String INSTALLATION = "INSTALLATION";

		public synchronized static String id(Context context) {
			if (sID == null) {
				File installation = new File(context.getFilesDir(), INSTALLATION);
				try {
					if (!installation.exists())
						writeInstallationFile(installation);
					sID = readInstallationFile(installation);
					// 如何是新的app增加appNameCode的编译
					if (!TextUtils.isEmpty(appNameCode)) {
						sID = sID + appNameCodeSegment + appNameCode;
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			return sID;
		}

		private static String readInstallationFile(File installation)
				throws IOException {
			RandomAccessFile f = new RandomAccessFile(installation, "r");
			byte[] bytes = new byte[(int) f.length()];
			f.readFully(bytes);
			f.close();
			return new String(bytes);
		}

		private static void writeInstallationFile(File installation)
				throws IOException {
			FileOutputStream out = new FileOutputStream(installation);
			String id = UUID.randomUUID().toString();
			out.write(id.getBytes());
			out.close();
		}
	}

	public static void changeDevelopHttpHost(Activity act, String newHttpHost) {
		//		if (meiyingConfig.DEBUG) {
		//			meiyingConfig.JSON_URL = newHttpHost;
		//			meiyingConst.getConst().HttpHost = newHttpHost;
		//			SharedPreferences sp = act.getSharedPreferences(
		//					SAVE_DATA_CONST_HTTPHOST, Context.MODE_PRIVATE);
		//			Editor editor = sp.edit();
		//			editor.putString(KEY_CONST_HTTPHOST, newHttpHost);
		//			editor.commit();
		//		}
	}

	public static void resetDevelopHttpHost(Activity act) {
		//		changeDevelopHttpHost(act, meiyingConfig.JSON_URL_ORIGINAL);
	}

	public static void loadHttpHost(Activity act) {
		//		if (meiyingConfig.DEBUG) {
		//			SharedPreferences sp = act.getSharedPreferences(
		//					SAVE_DATA_CONST_HTTPHOST, Context.MODE_PRIVATE);
		//			meiyingConfig.JSON_URL = sp.getString(KEY_CONST_HTTPHOST,
		//				meiyingConfig.JSON_URL);
		//		}
	}

	public static String getApplicationVersionName() {
		if (appVersionName != null) {
			return appVersionName;
		}
		try {
			Context context = MeiyingAplication.getContext();
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			appVersionName = pinfo.versionName;
			return appVersionName;

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static String getPackageName() {
		if (packageName != null) {
			return packageName;
		}
		try {
			Context context = MeiyingAplication.getContext();
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			packageName = pinfo.packageName;
			return packageName;

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getApplicationVersionCode() {
		if (appVersionCode != null) {
			return appVersionCode;
		}
		try {
			Context context = MeiyingAplication.getContext();
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			appVersionCode = String.valueOf(pinfo.versionCode);
			if (!TextUtils.isEmpty(appNameCode)) {
				appVersionCode = appNameCode + appVersionCode;
			}
			return appVersionCode;

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getApplicationVersionCodeForXiaomi() {
		try {
			String app_version_code;
			app_version_code = "4" + MeiyingResource.getApplicationVersionCode().substring(1);
			if (!TextUtils.isEmpty(appNameCode)) {
				app_version_code = appNameCode + app_version_code;
			}
			return app_version_code;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	public static long getDirSizeInByte(File file) {
		try{
			//判断文件是否存在     
			if (file != null && file.exists()) {     
				//如果是目录则递归计算其内容的总大小    
				if (file.isDirectory()) {     
					File[] children = file.listFiles();     
					long size = 0;     
					for (File f : children){
						size += getDirSizeInByte(f);
					}
					return size;     
				} 
				//如果是文件则直接返回其大小   
				else {
					return file.length();        
				}     
			}
		}catch (Exception e) {
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");     
		}
		return 0;
	}

	//	public static ArrayList<VideoSite> videoSites = null;
	//	public static ArrayList<MallSite> mallSites = null;
}
