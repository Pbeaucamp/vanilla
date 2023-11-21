package bpm.gateway.core.manager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;

/**
 * This class is a container of all Resources(for now only Server definitions)
 * 
 * The content of this class will be added in the xml definition of the model, but can be saved alone as a repository of server.
 * 
 * Only one instance is allowed.
 * 
 * @author LCA
 * 
 */
public class ResourceManager {

	public static final String VAR_BIGATEWAY_HOME = "BIGATEWAY_HOME";
	public static final String VAR_GATEWAY_TEMP = "GATEWAY_TEMP";

	private static ResourceManager instance = null;

	private List<Server> servers = new ArrayList<Server>();

	private List<Variable> variables = new ArrayList<Variable>();

	private List<Parameter> parameters = new ArrayList<Parameter>();

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public void fireContentChange() {
		listeners.firePropertyChange("content", "old", "new");
	}

	/**
	 * 
	 * @return all Parameter for the gateway transformation
	 */
	public List<Parameter> getParameters() {
		return parameters;

	}

	/**
	 * add a Parameter to the parameter list
	 * 
	 * @param p
	 */
	public void addParameter(Parameter p) {
		parameters.add(p);
	}

	/**
	 * remobve a parameter from the parameter List
	 * 
	 * @param p
	 */
	public void deleteParameter(Parameter p) {
		parameters.remove(p);
	}

	/**
	 * 
	 * @param pName
	 * @return the parameter whith the given name or null it does not exist
	 */
	public Parameter getParameter(String pName) {
		for(Parameter p : parameters) {
			if(p.getName().equals(pName)) {
				return p;
			}
		}
		return null;
	}

	private ResourceManager(boolean internal) {
		try {
			addServer(FileSystemServer.getInstance());
		} catch(Exception e) {
			e.printStackTrace();
		}

		// create Predefined environment variables
		// add the PREFERENCES_URL as variable
		Variable PREFERENCES_SERVER = new Variable();
		PREFERENCES_SERVER.setName("PREFERENCES_SERVER");
		PREFERENCES_SERVER.setType(Variable.STRING);
		PREFERENCES_SERVER.setScope(Variable.ENVIRONMENT_VARIABLE);
		PREFERENCES_SERVER.setValue("http://localhost:8080/Preferences");
		addVariable(PREFERENCES_SERVER);

		// add the GATEWAY_HOME as variables
		Variable HOME = new Variable();
		HOME.setName("BIGATEWAY_HOME");
		HOME.setType(Variable.STRING);
		HOME.setScope(Variable.ENVIRONMENT_VARIABLE);
		HOME.setValue("");

		addVariable(HOME);

		// add the variable TEMP folder
		Variable GATEWAY_TEMP = new Variable();
		GATEWAY_TEMP.setName("GATEWAY_TEMP");
		GATEWAY_TEMP.setType(Variable.STRING);
		GATEWAY_TEMP.setScope(Variable.ENVIRONMENT_VARIABLE);
		GATEWAY_TEMP.setValue("");

		addVariable(GATEWAY_TEMP);

	}

	public ResourceManager() {
		try {
			addServer(FileSystemServer.getInstance());
		} catch(Exception e) {
			e.printStackTrace();
		}

		// create Predefined environment variables
		// add the PREFERENCES_URL as variable
		Variable PREFERENCES_SERVER = new Variable();
		PREFERENCES_SERVER.setName("PREFERENCES_SERVER");
		PREFERENCES_SERVER.setType(Variable.STRING);
		PREFERENCES_SERVER.setScope(Variable.ENVIRONMENT_VARIABLE);
		PREFERENCES_SERVER.setValue("http://localhost:8080/Preferences");
		addVariable(PREFERENCES_SERVER);

		addVariable(ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME));

		addVariable(ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP));

	}

	/**
	 * Add the server if it is not already in the manager or null.
	 * 
	 * @param server
	 * @return true if the server is added
	 */
	public synchronized boolean addServer(Server server) throws Exception {
		if(server == null || servers.contains(server)) {
			throw new Exception("A null server or already contained server cannot be added.");
		}

		for(Server s : servers) {
			if(s.getName().equals(server.getName()) && s.getClass() == server.getClass()) {
				throw new Exception("A server of that type already exists.");
			}
		}

		if(server.getCurrentConnection(null) == null) {
			try {
				server.setCurrentConnection(server.getConnections().get(0));

			} catch(ServerException e) {
				e.printStackTrace();
			}
		}
		return servers.add(server);
	}

	/**
	 * remove the given server from the Manager
	 * 
	 * @param server
	 * @return true is server is removed
	 */
	public boolean removeServer(Server server) {

		return servers.remove(server);
	}

	/**
	 * @return the unique instance of the Manager
	 */
	public static ResourceManager getInstance() {
		if(instance == null) {
			instance = new ResourceManager(true);
		}

		return instance;
	}

	/**
	 * 
	 * @return all registered Server
	 */
	public final List<Server> getServers() {
		return new ArrayList<Server>(servers);
	}

	/**
	 * return all Servers of the specified class
	 * 
	 * @param serverClass
	 * @return
	 */
	public List<Server> getServers(Class<?> serverClass) {
		List<Server> list = new ArrayList<Server>();

		for(Server s : servers) {
			Class<?> o = s.getClass();
			if(serverClass.isAssignableFrom(s.getClass())) {
				list.add(s);
			}
		}
		return list;

	}

	/**
	 * 
	 * @param serverName
	 * @return the server with the given name
	 */
	public synchronized Server getServer(String serverName) {
		for(Server s : servers) {
			if(s.getName().equals(serverName)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * remove all server and the defulat FileSystemServer
	 */
	public void clean() {
		servers.clear();
		servers.add(FileSystemServer.getInstance());

	}

	/**
	 * load resources file from an XML file
	 * 
	 * @param fileName
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void loadFromFile(String fileName) throws IOException, SAXException {
		ResourcesDigester dig = new ResourcesDigester(new File(fileName));

		for(Object o : dig.getServers()) {
			if(o instanceof Server) {
				try {
					ResourceManager.getInstance().addServer((Server) o);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			if(o instanceof Variable) {
				ResourceManager.getInstance().addVariable((Variable) o);
			}
		}

	}

	/**
	 * save to resources in the specified file
	 * 
	 * @throws IOException
	 */
	public static Document getXmlDocument() throws IOException {

		Document doc = DocumentHelper.createDocument();

		Element root = DocumentHelper.createElement("sharedResources");

		for(Server s : instance.getServers()) {
			if(s != FileSystemServer.getInstance()) {
				root.add(s.getElement());
			}
		}

		for(Variable v : instance.getVariables(Variable.ENVIRONMENT_VARIABLE)) {
			root.add(v.getElement());

		}

		doc.add(root);

		return doc;

	}

	/**
	 * add a Variable to the resource Manager to be easily accessed
	 * 
	 * @param v
	 */
	public void addVariable(Variable v) {
		for(Variable _v : variables) {
			if(v == _v || v.getName().equals(_v.getName())) {
				return;
			}
		}

		variables.add(v);
	}

	/**
	 * 
	 * @param variableType
	 * @return
	 */
	public List<Variable> getVariables(int variableType) {
		List<Variable> subSet = new ArrayList<Variable>();

		for(Variable v : variables) {
			if(v.getScope() == variableType) {
				subSet.add(v);
			}
		}

		return subSet;
	}

	/**
	 * 
	 * @return all the variables registered
	 */
	public List<Variable> getVariables() {
		return new ArrayList<Variable>(variables);
	}

	/**
	 * @see bpm.gateway.core.server.userdefined.Variable
	 * @param classeType
	 * @param vname
	 * @return the specified variable with the given name and type
	 */
	public Variable getVariable(int classeType, String vname) {
		for(Variable v : getVariables(classeType)) {
			if(v.getName().equals(vname)) {
				return v;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param varName
	 * @return the variable with the given outputName for parsing purpose
	 */
	public Variable getVariableFromOutputName(String varName) {
		for(Variable v : getVariables()) {
			if(v.getOuputName().equals(varName)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * remove the given variable
	 * 
	 * @param o
	 */
	public void removeVariable(Variable v) {
		variables.remove(v);

	}

	public Variable getVariable(String name) {
		for(Variable v : variables) {
			if(v.getName().equals(name)) {
				return v;
			}
		}
		return null;
	}

	public static void saveXmlDocument(OutputStream os) throws Exception {

		XMLWriter writer = new XMLWriter(os, OutputFormat.createPrettyPrint());
		writer.write(getXmlDocument());
		writer.close();
	}
	
	/**
	 * Use only in the UI for auto-complete.
	 * This create a List containing all the variables + dummy variables for the parameters
	 * @return
	 */
	public List<Variable> getVariablesAndParameters() {
		List<Variable> vars = new ArrayList<Variable>();
		
		vars.addAll(getVariables());
		for(Parameter p : getParameters()) {
			Variable v = new Variable();
			v.setName(p.getName());
			v.setType(p.getType());
			v.setValue(p.getDefaultValue());
			v.setScope(1);
			vars.add(v);
		}
		
		return vars;
	}

}
