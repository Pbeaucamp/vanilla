package bpm.workflow.runtime.resources.servers;

import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * File Server (Hard Disk, FTP or Mail Server)
 * @author Charles MARTIN
 *
 */
public class FileServer extends Server {
	public static String KEY_PATH = "keypath";
	public static String SERVER_FTP = "FTP Server";
	public static String SERVER_FILES = "Hard Drive";
	public static String SERVER_MAILS = "Mail Server";
	public static String SERVER_SFTP = "SFTP Server";
	
	public static String[] SERVERS_TYPES = {SERVER_FTP, SERVER_FILES, SERVER_SFTP};//SERVER_MAILS, };
	
	private String types;
	private String repertoiredef;
	private String port;
	private String keyPath;

	/**
	 * do not use, only for XML parsing
	 */
	public FileServer(){}
	
	/**
	 * 
	 * @return the port of the file server
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Set the port of the file server
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * 
	 * @return the optional folder for the definition of the location
	 */
	public String getRepertoireDef() {
		return repertoiredef;
	}

	/**
	 * Set the optional folder for the definition of the location
	 * @param repertoiredef
	 */
	public void setRepertoireDef(String repertoiredef) {
		this.repertoiredef = repertoiredef;
	}


	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	/**
	 * Create a file server
	 * @param prop : url,username,password,name,typeserver,repertoiredef,port
	 */
	protected FileServer(Properties prop){
		setUrl(prop.getProperty("url"));
		setLogin(prop.getProperty("username"));
		setPassword(prop.getProperty("password"));
		setName(prop.getProperty("name"));
		setTypeServ(prop.getProperty("typeserver"));
		setKeyPath(prop.getProperty(KEY_PATH));

		for(Object propi : prop.keySet()){
			if(((String)propi).equalsIgnoreCase("repertoiredef")){
				setRepertoireDef(prop.getProperty("repertoiredef"));
			}
			if(((String)propi).equalsIgnoreCase("port")){
				setPort(prop.getProperty("port"));
			}
		}

	}

	public void setTypeServ(String property) {
		this.types = property;
	}
	public String getTypeServ(){
		return types;
	}
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("fileserver");
		
		if(!types.equalsIgnoreCase("")){
			e.addElement("typeserver").setText(types);
		}
		if(repertoiredef != null){
			e.addElement("repertoiredef").setText(repertoiredef);
		}
		if(port != null){
			e.addElement("port").setText(port);
		}
		if (keyPath != null) {
			e.addElement(KEY_PATH).setText(getKeyPath());
		}



		return e;
	}



	@Override
	public List<Element> toXPDL() {
		List<Element> list = super.toXPDL();

		Element typel =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_typeserver").addAttribute("Name",  getId() + "_typeserver");
		typel.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		typel.addElement("InitialValue").setText(getTypeServ()+"");

		list.add(typel);

		if(repertoiredef != null){
			Element repert =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_repertoiredef").addAttribute("Name",  getId() + "_repertoiredef");
			repert.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			repert.addElement("InitialValue").setText(repertoiredef+"");

			list.add(repert);
		}
		
		if (getPort() != null) {
			Element ePort =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_port").addAttribute("Name",  getId() + "_port");
			ePort.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			ePort.addElement("InitialValue").setText(getPort());

			list.add(ePort);
		}
		
		if (getKeyPath() != null) {
			Element keypath =  DocumentHelper.createElement("DataField").addAttribute("Id", getId() + "_keypath").addAttribute("Name",  getId() + "_keypath");
			keypath.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			keypath.addElement("InitialValue").setText(getKeyPath());

			list.add(keypath);
		}

		return list;
	}


}
