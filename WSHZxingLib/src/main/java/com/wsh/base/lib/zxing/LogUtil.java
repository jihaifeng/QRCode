package com.wsh.base.lib.zxing;

import android.util.Log;

/**
 * 
 * ClassName:LogUtil Description:Log输出控制类
 * 
 * @author 高坚 email:gaojian@raiyi.com
 * @version
 * @since Ver 1.1
 * @Date 2013 2013-6-24 上午9:08:30
 * @see
 */
public class LogUtil {
	private final static boolean DEBUG = false;

	public static void w(String TAG, String msg) {
		if (DEBUG)
			Log.w(TAG, msg);
	}

	public static void w(String TAG, String msg, Throwable e) {
		if (DEBUG)
			Log.w(TAG, msg, e);
	}

	public static void i(String TAG, String msg) {
		if (DEBUG)
			Log.i(TAG, msg);
	}

	public static void i(String TAG, String msg, Throwable e) {
		if (DEBUG)
			Log.i(TAG, msg, e);
	}

	public static void d(String TAG, String msg) {
		if (DEBUG)
			Log.d(TAG, msg);
	}

	public static void d(String TAG, String msg, Throwable e) {
		if (DEBUG)
			Log.d(TAG, msg, e);
	}

	public static void e(String TAG, String msg) {
		if (DEBUG)
			Log.e(TAG, msg);
	}

	public static void e(String TAG, String msg, Throwable e) {
		if (DEBUG)
			Log.e(TAG, msg, e);
	}

	public static void v(String TAG, String msg) {
		if (DEBUG)
			Log.v(TAG, msg);
	}

	public static void v(String TAG, String msg, Throwable e) {
		if (DEBUG)
			Log.v(TAG, msg, e);
	}

	public static void ex(String TAG, String msg, Exception e) {
		if (DEBUG)
			e.printStackTrace();
	}
}
