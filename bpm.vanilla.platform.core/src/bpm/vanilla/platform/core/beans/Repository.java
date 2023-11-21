package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class Repository implements Serializable{
	
	private static final long serialVersionUID = 291585923565827551L;
	
	private int id;
	private String name;
	protected String url;
	private String societe;
	private String key;

	public Repository() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSociete() {
		return societe;
	}

	public void setSociete(String societe) {
		this.societe = societe;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();

		buf.append("    <repository>\n");
		buf.append("    	<id>" + id + "</id>");
		buf.append("    	<name>" + name + "</name>");
		buf.append("    	<url>" + url + "</url>");
		buf.append("    	<societe>" + societe + "</societe>");
		buf.append("    	<key>" + key + "</key>");
		buf.append("    </repository>\n");

		return buf.toString();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
