package bpm.workflow.runtime.resources.servers;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;



/**
 * List of the servers contained in the workspace
 * @author CHARBONNIER, MARTIN
 *
 */
public class ListServer {

	private static ListServer instance ;
	private List<Server> servers;

	/**
	 * 
	 * @return the list of the server contained in the workspace
	 */
	public static ListServer getInstance(){
		if (instance == null){
			instance = new ListServer(new ArrayList<Server>());
		}
		return instance;
	}
	
	/**
	 * do not use, only for XML parsing
	 */
	public ListServer(){
		if (servers == null){
		servers = new ArrayList<Server>();
		}
		if (instance == null){
			instance = new ListServer(new ArrayList<Server>());
		}
	}
	/**
	 * Create a list of server
	 * @param s : the list<Server>
	 */
	private  ListServer(ArrayList<Server> s){
		servers = s;
	}
	
	/**
	 * 
	 * @return the list<Server> contained in the workspace
	 */
	public List<Server> getServers(){
		return new ArrayList<Server>(servers);
	}
	
	/**
	 * Add a server in the workspace and the list of the server
	 * @param s : server to add
	 */
	public void addServer(Server s){
		Server serv = getServer(s.getName());
		if (serv == null){
			servers.add(s);
			instance.addServer(s);
		}
		else{
			servers.remove(serv);
			servers.add(s);
		}
		
	}
	
	/**
	 * Remove the specified server from the list
	 * @param s : server to remove
	 */
	public void removeServer(Server s){
		if (s != null) {
			servers.remove(s);
			instance.removeServer(s.getName());
		}
	}
	
	/**
	 * Remove the specified server from the list thanks to its name
	 * @param serverName
	 */
	public void removeServer(String serverName){
		servers.remove(getServer(serverName));
		instance.removeServer(getServer(serverName));
	}
	
	
	/**
	 * 
	 * @param name
	 * @return the server which is in the list thanks to its name
	 */
	public Server getServer(String name){
		Server selectedServer = null;
		if (servers != null && !servers.isEmpty()) {
			List<Integer> indexToRemove = new ArrayList<Integer>();
			for(int i=0; i<servers.size(); i++){
				Server s = servers.get(i);
				if (s != null && s.getName().equals(name)){
					selectedServer = s;
					break;
				}
				else if (s == null) {
					indexToRemove.add(i);
				}
			}
			
			if (!indexToRemove.isEmpty()) {
				for (Integer index : indexToRemove) {
					servers.remove(index);
				}
			}
		}
		return selectedServer;
	}
	
	/**
	 * return all Servers of the specified class
	 * @param serverClass
	 * @return
	 */
	public List<Server> getServers(Class<?> serverClass) {
		List<Server> list = new ArrayList<Server>();
		
		for(Server s : servers){
			Class<?> o = s.getClass();
			if (serverClass == s.getClass()){
				list.add(s);
			}
		}
		return list;
		
	}
	
	
	
	public List<Element> getDataFieldsXPDL() {
		List<Element> list = new ArrayList<Element>();
		
		int i = 0;	
		for(Server s : servers){
			Element url =  DocumentHelper.createElement("DataField").addAttribute("Id", s.getId() + "_url").addAttribute("Name",  s.getId() + "_url");
			url.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			url.addElement("InitialValue").setText(s.getUrl());
			
			Element login =  DocumentHelper.createElement("DataField").addAttribute("Id", s.getId() + "_login").addAttribute("Name",  s.getId() + "_login");
			login.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			login.addElement("InitialValue").setText(s.getLogin());
			
			Element password =  DocumentHelper.createElement("DataField").addAttribute("Id", s.getId() + "_password").addAttribute("Name",  s.getId() + "_password");
			password.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			password.addElement("InitialValue").setText(s.getPassword());
			
			if(s instanceof DataBaseServer){
				Element jdbcDriver =  DocumentHelper.createElement("DataField").addAttribute("Id", ((DataBaseServer)s).getName() + "_jdbcDriver").addAttribute("Name",  ((DataBaseServer)s).getName() + "_jdbcDriver");
				jdbcDriver.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
				jdbcDriver.addElement("InitialValue").setText(((DataBaseServer)s).getJdbcDriver());

				Element dataBaseName =  DocumentHelper.createElement("DataField").addAttribute("Id", ((DataBaseServer)s).getName() + "_dataBaseName").addAttribute("Name",  ((DataBaseServer)s).getName() + "_dataBaseName");
				dataBaseName.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
				dataBaseName.addElement("InitialValue").setText(((DataBaseServer)s).getDataBaseName());

				list.add(dataBaseName);
				list.add(jdbcDriver);
				
				if (((DataBaseServer)s).getSchemaName() != null && !((DataBaseServer)s).getSchemaName().trim().equals("")){
					Element schemaName =  DocumentHelper.createElement("DataField").addAttribute("Id", ((DataBaseServer)s).getName() + "_schemaName").addAttribute("Name",  ((DataBaseServer)s).getName() + "_schemaName");
					schemaName.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
					schemaName.addElement("InitialValue").setText(((DataBaseServer)s).getSchemaName());

					list.add(schemaName);
				}
			}
			
			
			list.add(url);
			list.add(login);
			list.add(password);
		}
 
		
			
		return list;
	}
	
	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("listServer");
		for(Server s : servers){
			e.add(s.getXmlNode());
		}
		return e;
	}
	
	public void saveXml(OutputStream stream) throws Exception{
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setTrimText(false);
		
		
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("resources");
		doc.setRootElement(root);
		root.add(getXmlNode());

		try{
			XMLWriter writer =  new XMLWriter(stream, format);
			writer.write(doc);
			writer.close();
		}catch(Exception ex){
			throw new Exception("Error when saving ListServer xml : "+ ex.getMessage(), ex );
		}
		
		
	}
	
}
