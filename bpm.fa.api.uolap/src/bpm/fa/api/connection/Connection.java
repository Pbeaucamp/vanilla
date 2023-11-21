package bpm.fa.api.connection;


public class Connection implements IConnection {
	private RepositoryUser user;
	private String url;

	public Connection(RepositoryUser us, String url) {
		this.user = us;
		this.url = url;
	}

	public String getURL() {
		return url;
	}

	public IUser getUser() {
		return user;
	}

	
}
