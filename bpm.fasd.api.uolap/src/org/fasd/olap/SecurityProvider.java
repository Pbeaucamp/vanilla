package org.fasd.olap;

public class SecurityProvider extends OLAPElement {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
	private String type ="";
	private String serverId ="";
	private ServerConnection server;
	private String url ="";
	private String user ="";
	private String password ="";
	private String description = "";
	
	public SecurityProvider(){
		super("");
		counter++;
		setId("b"+counter);
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

	public SecurityProvider(String name){
		super(name);
		counter++;
		setId("b"+counter);
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
//		update(this, description);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
//		update(this, password);
	}

	public ServerConnection getServer() {
		return server;
	}

	public void setServer(ServerConnection server) {
		this.server = server;
		serverId = server.getId();
//		update(this, server);
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
//		update(this, type);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
//		update(this, url);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
//		update(this, user);
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("        <security-provider-item>\n");
		buf.append("            <name>" + this.getName() + "</name>\n");
		buf.append("            <id>" + this.getId() + "</id>\n");
		buf.append("            <type>" + type + "</type>\n");
		
		buf.append("            <server-id>");
		if (server != null)
			buf.append(server.getId());
		buf.append("</server-id>\n");
		
		buf.append("            <url>" + url + "</url>\n");
		buf.append("            <user>" + user + "</user>\n");
		buf.append("            <password>" + password + "</password>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("        </security-provider-item>\n");
		
			
		return buf.toString();	
	}

}
