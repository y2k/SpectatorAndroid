package net.itwister.spectator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {

	public static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.SHORT);

	public static final String WEB_USER_AGENT = "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.15";

	public static final int DISK_CACHE_SIZE = 512 * 1024 * 1024;
	public static final float MEMORY_CACHE_SIZE = 0.25f;

	public static final int ICON_SIZE = 48;

	public static final String SENDER_ID = "445037560545";

	/** Константы генерируемые ANT'ом при сборке в Jenkins */
	public static final class Ant {

		@SuppressWarnings("unused")
		private static final boolean IS_ANT = false;

		public static final BuildTarget TARGET = BuildConfig.DEBUG ? BuildTarget.Eclipse : BuildTarget.Market;

		public enum BuildTarget {

			Market, Eclipse
		}
	}

	public static final class Url {

		// public static final String URL_HOST = "http://192.168.0.69:8119";
		public static final String URL_HOST = "https://debug.spectator.api-i-twister.net";
	}
}