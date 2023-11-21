package bpm.document.management.core.model.aklademat;


public class ParapheurSettings implements IAkladematSettings {

	private static final long serialVersionUID = 1L;

	private String endPoint;

	private String user;
	private String password;

	private String keyStorePath;
	private String keyStorePass;

	private String trustStorePath;
	private String trustStorePass;

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKeyStorePath() {
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePass() {
		return keyStorePass;
	}

	public void setKeyStorePass(String keyStorePass) {
		this.keyStorePass = keyStorePass;
	}

	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	public String getTrustStorePass() {
		return trustStorePass;
	}

	public void setTrustStorePass(String trustStorePass) {
		this.trustStorePass = trustStorePass;
	}

}
