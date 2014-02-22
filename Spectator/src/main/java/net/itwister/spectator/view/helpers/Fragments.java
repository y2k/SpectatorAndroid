package net.itwister.spectator.view.helpers;

import java.io.Serializable;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class Fragments {

	public static final String PARAMETER = "arg1";
	public static final String PARAMETER2 = "arg2";
	public static final String PARAMETER3 = "arg3";
	public static final String PARAMETER4 = "arg4";

	private static final String[] ORDER = { PARAMETER, PARAMETER2, PARAMETER3, PARAMETER4 };

	private Fragments() {}

	public static void addNotExists(FragmentManager fm, int viewId, Fragment f) {
		if (fm.findFragmentById(viewId) == null) fm.beginTransaction().add(viewId, f).commit();
	}

	public static boolean exists(Fragment f, int childLayoutId) {
		return f.getChildFragmentManager().findFragmentById(childLayoutId) != null;
	}

	public static boolean getBoolean(Fragment f, int order) {
		Bundle args = f.getArguments();
		return args == null ? false : args.getBoolean(ORDER[order]);
	}

	public static int getInt(Fragment f, int orderId) {
		return f.getArguments() == null ? 0 : f.getArguments().getInt(ORDER[orderId]);
	}

	public static String getString(Fragment f, int order) {
		Bundle args = f.getArguments();
		return args == null ? null : args.getString(ORDER[order]);
	}

	public static boolean hasParams(Fragment f) {
		return f.getArguments() != null && !f.getArguments().isEmpty();
	}

	public static <T extends Fragment> T instance(Class<T> cls, Bundle args) {
		try {
			T inst = cls.newInstance();
			inst.setArguments(args);
			return inst;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends Fragment> T instance(Class<T> cls, Object... params) {
		Bundle args = new Bundle();

		for (int i = 0; i < Math.min(ORDER.length, params.length); i++) {
			Object p = params[i];
			if (p instanceof String) args.putString(ORDER[i], (String) p);
			else if (p instanceof Boolean) args.putBoolean(ORDER[i], (Boolean) p);
			else if (p instanceof Integer) args.putInt(ORDER[i], (Integer) p);
			else if (p instanceof Serializable) args.putSerializable(ORDER[i], (Serializable) p);
			else if (p != null) throw new IllegalStateException("" + p.getClass());
		}

		return instance(cls, args);
	}

	public static void replace(Fragment host, int layoutId, Fragment f) {
		host.getChildFragmentManager().beginTransaction().replace(layoutId, f).commit();
	}

	public static <T extends DialogFragment> void show(Context context, Class<T> cls, Object... params) {
		instance(cls, params).show(((FragmentActivity) context).getSupportFragmentManager(), null);
	}
}