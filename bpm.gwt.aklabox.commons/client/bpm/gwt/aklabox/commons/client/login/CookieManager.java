package bpm.gwt.aklabox.commons.client.login;

import java.util.Date;

import bpm.gwt.aklabox.commons.shared.CommonConstants;

import com.google.gwt.user.client.Cookies;

public class CookieManager {

	public boolean hasCookie() {
		if (Cookies.getCookie(CommonConstants.COOKIE_EMAIL) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	public void setCookie(boolean rememberCookie, String cookieEmail) {
		final long DURATION = 1000 * 60 * 60 * 24 * 10;
		Date expires = new Date(System.currentTimeMillis() + DURATION);
		Cookies.setCookie(CommonConstants.COOKIE_EMAIL, cookieEmail, expires, null, "/", false);
	}
}
