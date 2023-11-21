package bpm.gwt.commons.server.security;

public class Authentication {

	public enum TypeAuthentication {
		CREDENTIALS,
		TOKEN
	}
	
	private TypeAuthentication type;
	
	private String login;
	private String password;
	private String token;
	
	public Authentication(String login, String password) {
		this.type = TypeAuthentication.CREDENTIALS;
		this.login = login;
		this.password = password;
	}
	
	public Authentication(String token) {
		this.type = TypeAuthentication.TOKEN;
		this.token = token;
	}
	
	public TypeAuthentication getType() {
		return type;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getToken() {
		return token;
	}
}
