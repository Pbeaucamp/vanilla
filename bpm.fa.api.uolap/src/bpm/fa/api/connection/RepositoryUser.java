package bpm.fa.api.connection;

public class RepositoryUser implements IUser {
	private String user;
	private String pass;

	/**
	 * 
	 * @param user
	 * @param pass
	 */
	public RepositoryUser(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}
	
	public String getPassword() {
		return pass;
	}


	public String getUserName() {
		return user;
	}

	public void setPassword(String pass) {
		this.pass = pass;
	}

	public void setUserName(String user) {
		this.user = user;
	}

}
