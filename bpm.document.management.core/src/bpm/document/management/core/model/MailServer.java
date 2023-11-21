package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MailServer implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum AuthType {
		NONE, SIMPLE
	}

	public static List<String> authTypes;

	static {
		authTypes = new ArrayList<>();
		authTypes.add(AuthType.NONE.toString());
		authTypes.add(AuthType.SIMPLE.toString());
	}

	private int id;
	private String serverName = "";
	private String url = "";
	private int port;
	private String login = "";
	private String password = "";
	private AuthType authType = AuthType.NONE;
	private boolean tlsEnabled;
	private String folder;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AuthType getAuthType() {
		return authType;
	}

	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}

	public boolean isTlsEnabled() {
		return tlsEnabled;
	}

	public void setTlsEnabled(boolean tlsEnabled) {
		this.tlsEnabled = tlsEnabled;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailServer other = (MailServer) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	@Override
	public String toString() {
		return serverName;
	}
}
