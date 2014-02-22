package net.itwister.tools.io;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class BitmapHelper {

	public static int computeInMemorySize(File path, Config preferredConfig) {
		Options op = new Options();
		op.inInputShareable = true;
		if (preferredConfig != null) op.inPreferredConfig = preferredConfig;

		BitmapFactory.decodeFile(path.getAbsolutePath(), op);

		int bpp;
		switch (op.inPreferredConfig) {
			case ALPHA_8:
				bpp = 1;
				break;
			case ARGB_4444:
			case RGB_565:
				bpp = 2;
				break;
			default:
				bpp = 4;
				break;
		}

		return op.outWidth * op.outHeight * bpp;
	}

	public static Bitmap decodeWithConfig(File path, Config config) {
		Options op = new Options();
		if (config != null) op.inPreferredConfig = config;
		if (config == Config.ARGB_4444 || config == Config.RGB_565) op.inDither = true;
		return BitmapFactory.decodeFile(path.getAbsolutePath(), op);
	}
}