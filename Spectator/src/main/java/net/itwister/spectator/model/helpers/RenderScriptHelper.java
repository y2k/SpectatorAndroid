package net.itwister.spectator.model.helpers;

import net.itwister.spectator.App;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class RenderScriptHelper {

	// Стандарный радиус 5 E [0, 25]
	public static Bitmap gausseBitmap(Bitmap img, float radius) {
		if (img == null) return null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) return gausse17(img, radius);
		return img;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static Bitmap gausse17(Bitmap original, float radius) {
		Bitmap img = original.copy(Config.ARGB_8888, true);
		original.recycle();

		RenderScript rs = RenderScript.create(App.getInstance());
		ScriptIntrinsicBlur s = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		s.setRadius(radius);
		Allocation in = Allocation.createFromBitmap(rs, img, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
		Allocation out = Allocation.createTyped(rs, in.getType());
		s.setInput(in);
		s.forEach(out);
		out.copyTo(img);
		rs.destroy();

		return img;
	}
}