package bpm.workflow.runtime.resources.servers;

import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * FreeMetrics Server
 * @author CAMUS, MARTIN
 *
 */
public class FreemetricServer extends DataBaseServer{
	
	private String fmLogin = "";
	private String fmPassword = "";
	
	
	public FreemetricServer(){}
	
	/**
	 * Create a freemetrics server
	 * @param prop : url, username, password, jdbcDriver, name, dataBaseName, port, fmLogin, fmPassword
	 */
	public FreemetricServer(Properties prop){
		setUrl(prop.getProperty("url"));
		setLogin(prop.getProperty("username"));
		setPassword(prop.getProperty("password"));
		setJdbcDriver(prop.getProperty("jdbcDriver"));
		setName(prop.getProperty("name"));
		setDataBaseName(prop.getProperty("dataBaseName"));
		setPort(prop.getProperty("port"));
		setFmLogin(prop.getProperty("fmLogin"));
		setFmPassword(prop.getProperty("fmPassword"));
		
	}
	/**
	 * 
	 * @return the freemetrics login
	 */
	public final String getFmLogin() {
		return fmLogin;
	}
	/**
	 * Set the freemetrics login
	 * @param fmLogin
	 */
	public final void setFmLogin(String fmLogin) {
		this.fmLogin = fmLogin;
	}
	/**
	 * 
	 * @return the freemetrics password
	 */
	public final String getFmPassword() {
		return fmPassword;
	}

	/**
	 * Set the freemetrics password
	 * @param fmPassword
	 */
	public final void setFmPassword(String fmPassword) {
		this.fmPassword = fmPassword;
	}

	public List<Element> toXPDL() {
		List<Element> list = super.toXPDL();
		
		Element fmLogin =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_fmLogin").addAttribute("Name",  getId() + "_fmLogin");
		fmLogin.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		fmLogin.addElement("InitialValue").setText(getFmLogin());

		
		Element fmPassword =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_fmPassword").addAttribute("Name",  getId() + "_fmPassword");
		fmPassword.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		fmPassword.addElement("InitialValue").setText(getFmPassword());
		
		

		list.add(fmLogin);
		list.add(fmPassword);
		
		
		
		

		return list;
	}
	
	@Override
	public Element getXmlNode(){
		Element e = super.getXmlNode();
		
		e.setName("freemetricsServer");
		if(!fmLogin.equalsIgnoreCase("")){
			e.addElement("freemtricsLogin").setText(fmLogin);
		}
		if(!fmPassword.equalsIgnoreCase("")){
			e.addElement("freemtricsPassword").setText(fmPassword);
		}
		
		return e;
	}
}
