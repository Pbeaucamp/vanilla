package bpm.vanilla.map.core.design;

import java.io.Serializable;

public class MapDataSource implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nomDataSource, url, driver, login, mdp;
	private int id;
	public MapDataSource() {
		
	}
	
	public MapDataSource(String nomDataSource, String url, String driver, String login, String mdp) {
		this.nomDataSource = nomDataSource;
		this.url = url;
		this.driver = driver;
		this.login = login;
		this.mdp = mdp;
	}

	public String getNomDataSource() {
		return nomDataSource;
	}

	public void setNomDataSource(String nomDataSource) {
		this.nomDataSource = nomDataSource;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
