package bpm.workflow.runtime.resources.servers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.resources.IResource;

/**
 * Interface for the servers
 * @author CHARBONNIER, MARTIN
 *
 */
public class Server implements IResource {
	private String id;
	private String name;
	private String url;
	private String login;
	private String password;
	

	protected Server(){}
	

	public String getId() {
		return id;
	}
	
	/**
	 * donot use, only for parsing XML
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		setId(name.replace(" ", "_"));
	}
	/**
	 * 
	 * @return the url of the server
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Set the url of the server
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 
	 * @return the login to connect to the server
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * Set the login to connect to the server
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	/**
	 * 
	 * @return the password to connect to the server
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Set the password to connect to the server
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	

	public List<Element> toXPDL() {
		List<Element> list = new ArrayList<Element>();
		
		
		//dataFields
		Element url =  DocumentHelper.createElement("DataField").addAttribute("Id", id + "_url").addAttribute("Name",  id + "_url");
		url.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
		url.addElement("InitialValue").setText(getUrl());
		
		if (getLogin() != null) {
			Element login =  DocumentHelper.createElement("DataField").addAttribute("Id", id + "_login").addAttribute("Name",  id + "_login");
			login.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			login.addElement("InitialValue").setText(getLogin());
			list.add(login);
		}
		
		if (getPassword() != null) {
			Element password =  DocumentHelper.createElement("DataField").addAttribute("Id", id + "_password").addAttribute("Name",  id + "_password");
			password.addElement("DataType").addElement("BasicType").addAttribute("Type", "STRING");
			password.addElement("InitialValue").setText(getPassword());
			list.add(password);
		}
		
		
		list.add(url);
		
		
		
		return list;
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("server");
		e.addElement("id").setText(id);
		e.addElement("name").setText(name);
		e.addElement("url").setText(url);
		
		if (login != null)
			e.addElement("login").setText(login);
		
		if (password != null)
			e.addElement("password").setText(password);
		
		return e;
	}

	
}
