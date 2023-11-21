package org.fasd.inport;

import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.digester3.Digester;
import org.eclipse.core.runtime.Platform;
import org.fasd.olap.SecurityProvider;
import org.fasd.olap.ServerConnection;
import org.fasd.security.Security;
import org.xml.sax.SAXException;


public class DigesterSecurity {
	private Security security;
	
	public DigesterSecurity() throws Exception{
		String path = Platform.getInstallLocation().getURL().getPath() + "security.xml";
		FileReader f = new FileReader(path);
		Digester dig = new Digester();
		dig.setValidating(false);
		
		String root = "security";
		
		dig.addObjectCreate(root, Security.class);
		
//		server-connections
		dig.addObjectCreate(root + "/server-connection/server-connection-item", ServerConnection.class);
			dig.addCallMethod(root + "/server-connection/server-connection-item/id", "setId", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/name", "setName", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/description", "setDescription", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/type", "setType", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/host", "setHost", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/port", "setPort", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/user", "setUser", 0);
			dig.addCallMethod(root + "/server-connection/server-connection-item/password", "setPassword", 0);
		dig.addSetNext(root + "/server-connection/server-connection-item", "addServerConnection");
		
//		security-provider
		dig.addObjectCreate(root + "/security-provider/security-provider-item", SecurityProvider.class);
			dig.addCallMethod(root + "/security-provider/security-provider-item/id", "setId", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/name", "setName", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/description", "setDescription", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/server-id", "setServerId", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/type", "setType", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/url", "setUrl", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/user", "setUser", 0);
			dig.addCallMethod(root + "/security-provider/security-provider-item/password", "setPassword", 0);
		dig.addSetNext(root + "/security-provider/security-provider-item", "addSecurityProvider");

		
		try {
			security = (Security) dig.parse(f);
			
			for(SecurityProvider p : security.getSecurityProviders()){
				p.setServer(security.findServer(p.getServerId()));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public Security getSecurity(){
		return security;
	}
	
}
