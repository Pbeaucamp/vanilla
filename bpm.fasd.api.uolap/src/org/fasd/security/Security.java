package org.fasd.security;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.OLAPElement;
import org.fasd.olap.SecurityProvider;
import org.fasd.olap.ServerConnection;


public class Security extends OLAPElement {
	private List<SecurityProvider> providers = new ArrayList<SecurityProvider>();
	private List<ServerConnection> servers = new ArrayList<ServerConnection>();
	
	public Security(){
		super("");
	}
	
	public ServerConnection findServerConnection(String id){
		for (ServerConnection s : servers){
			if (s.getId().equals(id)){
				return s;
			}
		}
		return null;
	}
	
	public List<SecurityProvider> getSecurityProviders(){
		return providers;
	}
	
	public List<ServerConnection> getServerConnections(){
		return servers;
	}
	
	public void addServerConnection(ServerConnection c){
		servers.add(c);
	}
	
	public void addSecurityProvider(SecurityProvider p){
		providers.add(p);
	}
	
	public void removeServerConnection(ServerConnection s){
		servers.remove(s);
	}
	
	public void removeSecurityProvider(SecurityProvider s){
		providers.remove(s);
	}

	public ServerConnection findServer(String serverId) {
		for (ServerConnection s :servers){
			if (s.getId().equals(serverId)){
				return s;
			}
		}
		return null;
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("<security>\n");
		buf.append("    <server-connection>\n");
		for(ServerConnection s : servers){
			buf.append(s.getFAXML());
		}
		buf.append("    </server-connection>\n");
		
		buf.append("    <security-provider>\n");
		for(SecurityProvider s : providers){
			buf.append(s.getFAXML());
		}
		buf.append("    </security-provider>\n");
		buf.append("</security>\n");
		return buf.toString();
	}
}
