package net.itwister.tools.inner;

import android.util.Log;

public class Ln {

	public static void d(String format, Object... params) {
		String msg = String.format(format, params);
		Log.d(getTag(), msg);
	}

	public static void e(String format, Object... params) {
		String msg = String.format(format, params);
		Log.e(getTag(), msg);
	}

	public static void i(String format, Object... params) {
		String msg = String.format(format, params);
		Log.i(getTag(), msg);
	}

	public static void printStackTrace(Throwable e) {
		e.printStackTrace();
	}

	public static void v(String format, Object... params) {
		String msg = String.format(format, params);
		Log.v(getTag(), msg);
	}

	public static void w(String format, Object... params) {
		String msg = String.format(format, params);
		Log.w(getTag(), msg);
	}

	private static String getTag() {
		StackTraceElement el = Thread.currentThread().getStackTrace()[4];
		return el.getFileName() + ":" + el.getLineNumber();
	}
}