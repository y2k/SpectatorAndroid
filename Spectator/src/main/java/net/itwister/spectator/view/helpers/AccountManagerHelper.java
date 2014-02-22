package net.itwister.spectator.view.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;

public class AccountManagerHelper {

	private static final String GOOGLE_TYPE = "com.google";

	public static Account findGoogleAccount(Context context, String name) {
		AccountManager am = AccountManager.get(context);
		for (final Account a : am.getAccountsByType(GOOGLE_TYPE)) {
			if (TextUtils.equals(a.name, name)) return a;
		}
		return null;
	}
}