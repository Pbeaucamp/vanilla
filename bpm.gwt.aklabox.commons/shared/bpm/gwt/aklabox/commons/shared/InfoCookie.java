package bpm.gwt.aklabox.commons.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoCookie implements IsSerializable {

	private int userId;
	
	private Date expires;
	
	public InfoCookie(int userId) {
		this.userId = userId;
		this.expires = new Date(System.currentTimeMillis() + CommonConstants.DURATION);
	}
	
	public int getUserId() {
		return userId;
	}
	
	public boolean isValid() {
		return new Date().before(expires);
	}
}
