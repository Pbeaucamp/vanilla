package bpm.workflow.runtime.resources;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.resources.servers.DataBaseServer;

/**
 * Query for a profiling step
 * @author ??
 *
 */
public class ProfilingQueryObject {

	private DataBaseServer profilingDataBase;
	private int id;
	private String name;
	
	private String dataBaseRef;
	
	public ProfilingQueryObject(int id, DataBaseServer profilingDataBase){
		this.id = id;
		this.profilingDataBase = profilingDataBase; 
		
	}
	
	/**
	 * do not use, only for XML parsing
	 * @param dataBaseRef
	 */
	public ProfilingQueryObject(){
		
	}
	
	/**
	 * do not use, only for XML parsing
	 * @param dataBaseRef
	 */
	public void setDataBaseRef(String dataBaseRef){
		this.dataBaseRef = dataBaseRef;
	}
	
	/**
	 * do not use, only for XML parsing
	 * @param dataBaseRef
	 */
	public String getDataBaseRef(){
		return this.dataBaseRef;
	}
	
	
	/**
	 * do not use, only for XML parsing
	 * @param dataBaseRef
	 */
	public void setDataBaseServer(DataBaseServer server){
		profilingDataBase = server;
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataBaseServer getProfilingDataBase() {
		return profilingDataBase;
	}

	public int getId() {
		return id;
	}
	
	/**
	 * do not use only for XML parsing
	 * @param id
	 */
	public void setId(String id){
		try{
			this.id = Integer.parseInt(id);
		}catch(NumberFormatException e){
			
		}
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("profilingQuery");
		e.addElement("id").setText(id + "");
		if(name != null){
		e.addElement("name").setText(name);
		}
		e.addElement("dataBaseRef").setText(profilingDataBase.getId());
		
		return e;

	}
	
	
}
