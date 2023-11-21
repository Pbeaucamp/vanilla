package bpm.workflow.runtime.resources.servers;

import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Mail Server for sending mails
 * @author CHARBONNIER, MARTIN
 *
 */
public class ServerMail extends Server {
	private int port = 25;
	
	/**
	 * do not use, only for XML parsing
	 */
	public ServerMail(){
	}
	
	/**
	 * Create a mail server
	 * @param prop : url, username, password, name, port
	 */
	protected ServerMail(Properties prop){
		setUrl(prop.getProperty("url"));
		setLogin(prop.getProperty("username"));
		setPassword(prop.getProperty("password"));
		setName(prop.getProperty("name"));
		setPort(new Integer(prop.getProperty("port")).intValue() );
		
	}
		

	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("mailServer");
		e.addElement("port").setText(String.valueOf(port));
		
		return e;
	}
	/**
	 * 
	 * @return the port of the mail server
	 */
	public int getPort() {
		return port;
	}
	/**
	 * Set the port of the mail server
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * Set the port of the mail server
	 * @param port
	 */
	public void setPort(String port) {
		this.port = new Integer(port).intValue();
	}
	
	@Override
	public List<Element> toXPDL() {
		List<Element> list = super.toXPDL();
		
		Element port =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_port").addAttribute("Name",  getId() + "_port");
		port.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		port.addElement("InitialValue").setText(getPort()+"");
		
		list.add(port);
		
		return list;
	}
	
		
}
