package bpm.vanilla.repository.beans;


public class InfoConnections {
	
	private String login;
	private String password;
	private String repositoryDBUrl;
	private String driver;
	
	public InfoConnections(String login, String password, String repositoryDBUrl, String driver) {
		this.login = login;
		this.password = password;
		this.repositoryDBUrl = repositoryDBUrl;
		this.driver = driver;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getRepositoryDBUrl() {
		return repositoryDBUrl;
	}
	
	public String getDriver() {
		return driver;
	}
	
	@Override
	public boolean equals(Object obj) {
		return login.equals(((InfoConnections)obj).getLogin()) && driver.equals(((InfoConnections)obj).getDriver()) && password.equals(((InfoConnections)obj).getPassword()) && repositoryDBUrl.equals(((InfoConnections)obj).getRepositoryDBUrl());
	}
}
