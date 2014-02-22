package net.itwister.spectator.model.impl;

import javax.inject.Inject;

import net.itwister.spectator.model.AccountModel;
import net.itwister.spectator.model.web.SpectatorWebClient;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

public class AccountModelImpl implements AccountModel {

	private static final String COOKIE_KEY = "SessionId";

	@Inject
	private SpectatorWebClient web;

	@Inject
	private CookieStore cookies;

	@Override
	public boolean isSignin() {
		for (Cookie c : cookies.getCookies()) {
			if (COOKIE_KEY.equals(c.getName())) return true;
		}
		return false;
	}

	@Override
	public void signin(String token) throws Exception {
		//		rest.postForLocation(Constants.Url.URL_HOST + "/Account/Login", new LoginRequest(token));
		web.api().login(token);
		if (!isSignin()) throw new Exception("Could not login");
	}

	@Override
	public void signout() {
		cookies.clear();
	}
}