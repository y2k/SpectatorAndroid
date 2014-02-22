package net.itwister.spectator.model.account;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.itwister.spectator.BuildConfig;
import net.itwister.tools.inner.Ln;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.support.v4.util.AtomicFile;

public class PermanentCookieStorage implements CookieStore {

	private static PermanentCookieStorage sInstance;

	private List<Cookie> mCookies = new ArrayList<Cookie>();

	private AtomicFile mCookieFile;

	private PermanentCookieStorage(Context appContext) {
		try {
			mCookieFile = new AtomicFile(new File(appContext.getFilesDir(), "cookie.db"));
			mCookies = CookieSerializeHelper.readCookie(mCookieFile.readFully());
		} catch (Exception e) {
            Ln.printStackTrace(e);
		}
	}

	@Override
	public synchronized void addCookie(Cookie cookie) {
		if (BuildConfig.DEBUG) Ln.d("addCookie(cookie = %s)", cookie);
		for (int i = mCookies.size() - 1; i >= 0; i--) {
			if (mCookies.get(i).getName().equals(cookie.getName())) mCookies.remove(i);
		}
		mCookies.add(cookie);
		saveCookieToDisk();
	}

	@Override
	public synchronized void clear() {
		if (BuildConfig.DEBUG) Ln.d("clear()");
		mCookies.clear();
		saveCookieToDisk();
	}

	@Override
	public synchronized boolean clearExpired(Date date) {
		if (BuildConfig.DEBUG) Ln.d("clearExpired(date = %s)", date);
		Date now = new Date();
		for (int i = mCookies.size() - 1; i >= 0; i--) {
			Cookie c = mCookies.get(i);
			if (c.isExpired(now)) mCookies.remove(i);
		}
		saveCookieToDisk();
		return false;
	}

	@Override
	public synchronized List<Cookie> getCookies() {
		if (BuildConfig.DEBUG) Ln.d("getCookies(), [mCookies] = %s, mCookies = %s", mCookies.size(), mCookies);
		return new ArrayList<Cookie>(mCookies);
	}

	public String getCookiesString() {
		String cookiesString = "";
		for (int i = 0; i < mCookies.size(); i++) {
			Cookie cookie = mCookies.get(i);
			cookiesString += cookie.getName() + "=" + cookie.getValue();
			if (i != mCookies.size() - 1) {
				cookiesString += "; ";
			}
		}
		return cookiesString;
	}

	private void saveCookieToDisk() {
		try {
			FileOutputStream s = mCookieFile.startWrite();
			CookieSerializeHelper.writeCookie(s, mCookies);
			mCookieFile.finishWrite(s);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) e.printStackTrace();
		}
	}

	public static synchronized CookieStore getInstance() {
		return sInstance;
	}

	public static synchronized void initialize(Context context) {
		if (sInstance == null) {
			sInstance = new PermanentCookieStorage(context.getApplicationContext());
		}
	}

	static class CookieSerializeHelper {

		static List<Cookie> readCookie(byte[] data) throws Exception {
			List<Cookie> cookies = new ArrayList<Cookie>();
			ObjectInputStream s = new ObjectInputStream(new ByteArrayInputStream(data));
			try {
				List<CookieWrapper> wrappers = (List<CookieWrapper>) s.readObject();
				for (CookieWrapper w : wrappers) {
					BasicClientCookie c = new BasicClientCookie(w.name, w.value);
					c.setDomain(w.domain);
					c.setPath(w.path);
					c.setExpiryDate(w.expiryDate);
					cookies.add(c);
				}
			} finally {
				s.close();
			}
			return cookies;
		}

		static void writeCookie(OutputStream stream, List<Cookie> cookie) throws Exception {
			List<CookieWrapper> wrappers = new ArrayList<CookieWrapper>();
			for (Cookie c : cookie) {
				CookieWrapper w = new CookieWrapper();
				w.name = c.getName();
				w.value = c.getValue();
				w.domain = c.getDomain();
				w.path = c.getPath();
				w.expiryDate = c.getExpiryDate();
				wrappers.add(w);
			}

			try {
				ObjectOutputStream s = new ObjectOutputStream(stream);
				s.writeObject(wrappers);
			} finally {
				stream.close();
			}
		}

		static class CookieWrapper implements Serializable {

			private static final long serialVersionUID = 7420288349818694831L;

			Date expiryDate;
			String path;
			String domain;
			String name;
			String value;
		}
	}
}