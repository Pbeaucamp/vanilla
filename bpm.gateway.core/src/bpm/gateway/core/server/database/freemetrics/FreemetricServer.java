package bpm.gateway.core.server.database.freemetrics;

import org.dom4j.Element;

import bpm.gateway.core.Server;
import bpm.gateway.core.server.database.DataBaseServer;

/**
 * Class to store Freemetrics Server Informations
 * @author LCA
 *
 */
public class FreemetricServer extends DataBaseServer {
	
	private String fmLogin = "";
	private String fmPassword = "";
	
	
	
	public final String getFmLogin() {
		return fmLogin;
	}

	public final void setFmLogin(String fmLogin) {
		this.fmLogin = fmLogin;
	}

	public final String getFmPassword() {
		return fmPassword;
	}

	public final void setFmPassword(String fmPassword) {
		this.fmPassword = fmPassword;
	}

	@Override
	public String getType() {
		return Server.FREEMETRICS_TYPE;
	}
	
	@Override
	public Element getElement(){
		Element e = super.getElement();
		
		e.setName("freemetricsServer");
		e.addElement("freemtricsLogin").setText(fmLogin);
		e.addElement("freemtricsPassword").setText(fmPassword);
		
		return e;
	}
}
