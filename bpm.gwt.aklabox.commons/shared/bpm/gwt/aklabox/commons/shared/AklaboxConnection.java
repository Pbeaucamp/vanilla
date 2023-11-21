package bpm.gwt.aklabox.commons.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AklaboxConnection implements IsSerializable {

	private String url;
	private String login;
	private String password;
	
	public AklaboxConnection() {
		
	}
	
	public AklaboxConnection(String url, String login, String password) {
		this.url = url;
		this.login = login;
		this.password = password;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
}
