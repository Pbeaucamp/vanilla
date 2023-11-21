package org.fasd.olap;

public class ServerConnection extends OLAPElement {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
	private String type = "";	//authentification, datasource, engine, sourcecode
	private String host = "";
	private String port = "";
	private String user = "";
	private String password = "";
	private String description = "";
	
	public ServerConnection(){
		counter ++;
		setId("m" + counter);
	}
	
	public void setId(String id) {
		this.id = id;
		try{
			int i = Integer.parseInt(id.substring(1));
			if (i > counter){
				counter = i + 1;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
	}
	
	public ServerConnection(String name){
		super(name);
		setId("m" + counter);
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <server-connection-item>\n");
		buf.append("            <name>" + this.getName() + "</name>\n");
		buf.append("            <id>" + this.getId() + "</id>\n");
		buf.append("            <type>" + type + "</type>\n");
		buf.append("            <host>" + host + "</host>\n");
		buf.append("            <port>" + port + "</port>\n");
		buf.append("            <user>" + user + "</user>\n");
		buf.append("            <password>" + password + "</password>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("        </server-connection-item>\n");
	
		return buf.toString();	
	}

}
