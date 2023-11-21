package bpm.gateway.core.transformations.outputs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunDataBaseOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class DataBaseOutputStream extends AbstractTransformation implements ITargetableStream, IOutput, Trashable {

	public static final String PROPERTY_DEFINITION_CHANGED = "definition";

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private DataBaseServer server;
	private String definition;
	private String tableName;
	private boolean truncate;

	private Server fmdtDestinationServer;
	private boolean isRepositoryItem;
	private String destinationFolder;
	private String destinationName;
	private List<String> securisedGroupNames = new ArrayList<String>();

	/**
	 * step used as trash to collect rows that have not been inserted for some
	 * reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;

	/*
	 * contains the mapping between each input Transformation
	 * 
	 * (We let the mapping with Point for retrocompatibility)
	 */
	private HashMap<Transformation, HashMap<String, String>> mappingNames = new HashMap<Transformation, HashMap<String, String>>();

	/*
	 * only for digester
	 */
	private HashMap<String, List<Point>> bufferMapping;

	/*
	 * Only for digester
	 */
	private HashMap<String, HashMap<String, String>> bufferMappingName;

	// those field are use to avoid refreshing
	// the descriptor
	transient private String lastDefinition;
	transient private String lastTableName;
	transient private Server lastLoadedServer;

	public DataBaseOutputStream() {
		addPropertyChangeListener(this);
	}

	public String getDefinition() {
		return definition;

	}

	/**
	 * Used by digester
	 * 
	 * @param transfoName
	 * @param transfoIndex
	 * @param index
	 */
	public void createBufferMapping(String transfoName, String transfoIndex, String index) {

		if (bufferMapping == null) {
			bufferMapping = new HashMap<String, List<Point>>();
		}

		/*
		 * look for the key
		 */
		String k = null;
		for (String key : bufferMapping.keySet()) {
			if (key.equals(transfoName)) {
				k = key;
				break;
			}
		}
		if (k == null) {
			k = transfoName;

			bufferMapping.put(k, new ArrayList<Point>());
		}

		try {
			Integer x = Integer.parseInt(transfoIndex);
			Integer y = Integer.parseInt(index);

			bufferMapping.get(k).add(new Point(x, y));
		} catch (NumberFormatException e) {

		}
	}

	/**
	 * Used by digester
	 * 
	 * @param transfoName
	 * @param inputCol
	 * @param outputCol
	 */
	public void createBufferMappingName(String transfoName, String inputCol, String outputCol) {

		if (bufferMappingName == null) {
			bufferMappingName = new HashMap<String, HashMap<String, String>>();
		}

		/*
		 * look for the key
		 */
		String k = null;
		for (String key : bufferMappingName.keySet()) {
			if (key.equals(transfoName)) {
				k = key;
				break;
			}
		}
		if (k == null) {
			k = transfoName;

			bufferMappingName.put(k, new HashMap<String, String>());
		}

		bufferMappingName.get(k).put(inputCol, outputCol);
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	public Server getServer() {
		return server;
	}

	/**
	 * do not perform any database check, necessary to enable DB Migration
	 * Wizard
	 * 
	 * @param definition
	 * @param descriptor
	 */
	public void setDefinition(String definition, DefaultStreamDescriptor descriptor) {
		this.definition = definition;
		if (definition.contains(" from ")) {
			tableName = definition.substring(definition.toLowerCase().indexOf(" from ") + 6).trim();
		}
		this.descriptor = descriptor;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
		if (definition.toLowerCase().contains(" from ")) {
			tableName = definition.substring(definition.indexOf(" from ") + 6).trim();
		}

		fireProperty(PROPERTY_INPUT_CHANGED);

	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setServer(Server s) {
		this.server = (DataBaseServer) s;
	}

	public final void setServer(String serverName) {
		if (owner != null && owner.getResourceManager() != null && owner.getResourceManager().getServer(serverName) != null) {
			server = (DataBaseServer) owner.getResourceManager().getServer(serverName);
		}
		else {
			server = (DataBaseServer) ResourceManager.getInstance().getServer(serverName);
		}
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dataBaseOutputStream");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("truncate").setText("" + truncate);

		if (server != null) {
			e.addElement("serverRef").setText(server.getName());
		}

		if (definition != null) {
			e.addElement("definition").setText(definition);
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (getTrashTransformation() != null) {
			e.addElement("trashOuput").setText(errorHandlerTransformation.getName());
		}

		/*
		 * for the mapping
		 */
		for (Transformation t : inputs) {

			try {
				HashMap<String, String> maps = mappingNames.get(t);
				for (String input : maps.keySet()) {
					Element transfoMap = e.addElement("inputMappingName");
					transfoMap.addElement("transformationRef").setText(t.getName());

					transfoMap.addElement("inputName").setText(input);
					transfoMap.addElement("outputName").setText(maps.get(input));
				}
			} catch (Exception e1) {
			}
		}

		/*
		 * for generating FMDT
		 */
		if (getFmdtDestinationServer() != null) {
			e.addElement("fmdtDestinationServer-ref").setText(getFmdtDestinationServer().getName());
			e.addElement("destinationFolder").setText(destinationFolder);
			e.addElement("destinationName").setText(destinationName);

			for (String s : securisedGroupNames) {
				e.addElement("fmdtGroup").setText(s);
			}
		}

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		if (outputs.contains(stream)) {
			if (trashName != null && trashName.equals(stream.getName())) {
				setTrashTransformation(stream);
				trashName = null;
			}
		}

	}

	/**
	 * if the current mapping does not exist already create it
	 * 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void createMapping(Transformation input, int transfoColNum, int colNum) throws MappingException {
		if (!getInputs().contains(input)) {
			return;
		}

		StreamElement inputElement = null;
		try {
			inputElement = input.getDescriptor(this).getStreamElements().get(transfoColNum);
		} catch (ServerException e) {
			e.printStackTrace();
		}

		StreamElement outputElement = descriptor.getStreamElements().get(colNum);

		String inputName = inputElement.originTransfo + "::" + inputElement.name;
		//We remove line breaks
		inputName = inputName.replace("\n", "").replace("\r", "");
		String ouputName = outputElement.name;

		createMapping(input, inputName, ouputName);
	}

	private void createMapping(Transformation input, String inputCol, String outputCol) throws MappingException {
		if (!getInputs().contains(input)) {
			return;
		}

		if (mappingNames.get(input) == null) {
			mappingNames.put(input, new HashMap<String, String>());
		}

		/*
		 * verify that this mapping not already exist
		 */
		HashMap<String, String> maps = mappingNames.get(input);
		for (String in : maps.keySet()) {
			if (in.equals(inputCol) && maps.get(in).equals(outputCol)) {
				return;
			}

			if (in.equals(inputCol)) {
				throw new MappingException("A mapping already exists for that Input Stream Element");
			}

			if (maps.get(in).equals(outputCol)) {
				throw new MappingException("A mapping already exists for that DataBaseOuput Stream Element");
			}
		}

		mappingNames.get(input).put(inputCol, outputCol);
	}

	/**
	 * remove the specified point from the mapping
	 * 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void deleteMapping(Transformation input, int transfoColNum, int colNum) {
		StreamElement inputElement = null;
		try {
			inputElement = input.getDescriptor(this).getStreamElements().get(transfoColNum);
		} catch (ServerException e) {
			e.printStackTrace();
		}

		StreamElement outputElement = descriptor.getStreamElements().get(colNum);

		String inputName = inputElement.originTransfo + "::" + inputElement.name;
		//We remove line breaks
		inputName = inputName.replace("\n", "").replace("\r", "");
		String ouputName = outputElement.name;

		deleteMapping(input, inputName, ouputName);
	}

	/**
	 * Remove the specified colonne from the mapping
	 * 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	private void deleteMapping(Transformation input, String inputCol, String outputCol) {
		HashMap<String, String> maps = mappingNames.get(input);
		for (String key : maps.keySet()) {
			if (key.equals(inputCol) && maps.get(key).equals(outputCol)) {
				mappingNames.get(input).remove(key);
				return;
			}
		}
	}

	@Override
	public void clearMapping(Transformation input) {
		HashMap<String, String> maps = mappingNames.get(input);
		if (maps != null) {
			maps.clear();
		}
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		if (lastTableName != tableName || server != lastLoadedServer || lastDefinition != definition) {
			try {
				descriptor = (DefaultStreamDescriptor) DataBaseHelper.getDescriptor(this);
				lastTableName = tableName;
				lastDefinition = definition;
				lastLoadedServer = server;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// updates the mapping (remove them associated with columns that dont
		// exists anymore
		for (Transformation t : inputs) {

			if (bufferMapping != null) {
				// look for the key
				String key = null;
				for (String s : bufferMapping.keySet()) {
					if (s.equals(t.getName())) {
						key = s;
						break;
					}
				}

				if (key != null) {
					for (Point p : bufferMapping.get(key)) {
						try {
							createMapping(t, p.x, p.y);
						} catch (MappingException e) {
							e.printStackTrace();
						}
					}
					bufferMapping.remove(key);
				}
				if (bufferMapping.isEmpty()) {
					bufferMapping = null;
				}

			}
			else if (bufferMappingName != null) {
				// look for the key
				String key = null;
				for (String s : bufferMappingName.keySet()) {
					if (s.equals(t.getName())) {
						key = s;
						break;
					}
				}

				if (key != null) {
					HashMap<String, String> maps = bufferMappingName.get(key);
					for (String input : maps.keySet()) {
						try {
							createMapping(t, input, maps.get(input));
						} catch (MappingException e) {
							e.printStackTrace();
						}
					}
					bufferMappingName.remove(key);
				}
				if (bufferMappingName.isEmpty()) {
					bufferMappingName = null;
				}
			}

			List<String> toRemove = new ArrayList<String>();
			HashMap<String, String> maps = mappingNames.get(t);

			if (maps != null) {
				for (String input : maps.keySet()) {
					boolean found = false;
					try {
						for (StreamElement el : t.getDescriptor(this).getStreamElements()) {
							String elName = el.originTransfo + "::" + el.name;
							if(input.startsWith("::")) {
								elName = "::" + el.name;
							}
							//We remove line breaks
							elName = elName.replace("\n", "").replace("\r", "");
							
							if (input.equals(elName)) {
								found = true;
								break;
							}
						}
					} catch (ServerException e) {
						toRemove.add(input);
					}

					if (!found) {
						toRemove.add(input);
					}
					else {
						found = false;
						for (StreamElement el : descriptor.getStreamElements()) {
							if (maps.get(input).equals(el.name)) {
								found = true;
								break;
							}
						}

						if (!found) {
							toRemove.add(input);
						}
					}
				}

				for (String in : toRemove) {
					mappingNames.get(t).remove(in);
				}
			}

		}
		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
//		System.out.println(name);
//		System.out.println(descriptor.getColumnCount());
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunDataBaseOutput(this, bufferSize);
	}

	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		mappingNames.remove(transfo);
	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		boolean result = super.addInput(stream);
		if (result == false) {
			return result;
		}

		if (isInited()) {
			if (/* !inputs.contains(stream) && */mappingNames.get(stream) == null) {
				mappingNames.put(stream, new HashMap<String, String>());
			}

			/*
			 * look if the mappingBuffered is not empty
			 */
			if (bufferMapping == null && bufferMappingName == null) {
				return result;
			}

			if (bufferMapping != null) {
				// look for the key
				String key = null;
				for (String s : bufferMapping.keySet()) {
					if (s.equals(stream.getName())) {
						key = s;
						break;
					}
				}

				if (key == null) {
					return result;
				}

				for (Point p : bufferMapping.get(key)) {
					createMapping(stream, p.x, p.y);
				}

				bufferMapping.remove(key);

				if (bufferMapping.isEmpty()) {
					bufferMapping = null;
				}
			}
			else {
				// look for the key
				String key = null;
				for (String s : bufferMappingName.keySet()) {
					if (s.equals(stream.getName())) {
						key = s;
						break;
					}
				}

				if (key == null) {
					return result;
				}

				HashMap<String, String> maps = bufferMappingName.get(key);
				for (String input : maps.keySet()) {
					createMapping(stream, input, maps.get(input));
				}

				bufferMappingName.remove(key);

				if (bufferMappingName.isEmpty()) {
					bufferMappingName = null;
				}
			}
		}

		return result;
	}

	/**
	 * return the mappings for the given transformation Point.x : the
	 * transformation t column number Point.y : the current object column number
	 * 
	 * @param t
	 * @return
	 */
	public HashMap<String, String> getMappingsFor(Transformation t) {
		if (mappingNames.get(t) == null) {
			mappingNames.put(t, new HashMap<String, String>());
		}
		return new HashMap<String, String>(mappingNames.get(t));
	}

	@Override
	public Integer getMappingValueForInputNum(Transformation t, int colNum) {
		if (mappingNames.get(t) == null) {
			return null;
		}

		StreamElement element = null;
		try {
			element = t.getDescriptor(this).getStreamElements().get(colNum);
		} catch (ServerException e) {
			e.printStackTrace();
			return null;
		}

		if (element != null) {
			String elName = element.originTransfo + "::" + element.name;
			//We remove line breaks
			elName = elName.replace("\n", "").replace("\r", "");
			
			String output = mappingNames.get(t).get(elName);
			if(output == null) {
				mappingNames.get(t).get("::" + element.name);
			}
			if (output != null) {
				for (int i = 0; i < descriptor.getStreamElements().size(); i++) {
					if (output.equals(descriptor.getStreamElements().get(i).name)) {
						return i;
					}
				}
			}
		}

		return null;
	}

	@Override
	public Integer getMappingValueForThisNum(Transformation t, int colNum) {
		if (mappingNames.get(t) == null) {
			return null;
		}

		StreamElement element = descriptor.getStreamElements().get(colNum);

		if (element != null) {
			HashMap<String, String> maps = mappingNames.get(t);
			for (String key : maps.keySet()) {
				if (maps.get(key).equals(element.name)) {

					StreamDescriptor transfoDescriptor = null;
					try {
						transfoDescriptor = t.getDescriptor(this);
					} catch (Exception e) {

					}

					if (transfoDescriptor != null) {
						for (int i = 0; i < transfoDescriptor.getStreamElements().size(); i++) {
							String elName = transfoDescriptor.getStreamElements().get(i).originTransfo + "::" + transfoDescriptor.getStreamElements().get(i).name;
							if(key.startsWith("::")) {
								elName = "::" + transfoDescriptor.getStreamElements().get(i).name;
							}
							//We remove line breaks
							elName = elName.replace("\n", "").replace("\r", "");
							
							if (key.equals(elName)) {
								return i;
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * Test for the input column
	 * 
	 * @param t
	 * @param colName
	 * @return
	 */
	public boolean isMapped(Transformation t, String colName) {
		if (mappingNames.get(t) == null) {
			return false;
		}
		if (mappingNames.get(t).get(colName) != null && !mappingNames.get(t).get(colName).isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Test for the output column
	 * 
	 * @param colName
	 * @return
	 */
	public boolean isMapped(String colName) {
		for (Transformation t : inputs) {

			HashMap<String, String> maps = mappingNames.get(t);

			if (maps == null) {
				maps = new HashMap<String, String>();
			}
			for (String key : maps.keySet()) {
				if (maps.get(key).equals(colName)) {
					return true;
				}
			}
		}
		return false;
	}

	public Transformation copy() {
		DataBaseOutputStream copy = new DataBaseOutputStream();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(server);
		copy.setTruncate(truncate);

		return copy;
	}

	public boolean isTruncate() {
		return truncate;
	}

	public void setTruncate(boolean truncate) {
		this.truncate = truncate;
	}

	public void setTruncate(String truncate) {
		this.truncate = Boolean.parseBoolean(truncate);
	}

	// ///////////////////////////////////////////////////////
	// output as FMDT
	// ///////////////////////////////////////////////////////
	public Server getFmdtDestinationServer() {
		return fmdtDestinationServer;
	}

	public void setFmdtDestinationServer(Server fmdtDestinationServer) {
		this.fmdtDestinationServer = fmdtDestinationServer;
	}

	public void setFmdtDestinationServer(String fmdtDestinationServer) {
		this.fmdtDestinationServer = ResourceManager.getInstance().getServer(fmdtDestinationServer);
		this.destinationFolder = null;
	}

	public boolean isRepositoryItem() {
		return isRepositoryItem;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder, boolean isRepositoryItem) {
		this.destinationFolder = destinationFolder;
		this.isRepositoryItem = isRepositoryItem;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public void addSecurizedGroup(String groupName) {
		for (String s : securisedGroupNames) {
			if (s.equals(groupName)) {
				return;
			}
		}
		securisedGroupNames.add(groupName);
	}

	public void removeSecurizedGroup(String groupName) {
		for (String s : securisedGroupNames) {
			if (s.equals(groupName)) {
				securisedGroupNames.remove(s);
				return;
			}
		}
	}

	public List<String> getSecurizedGroup() {
		return new ArrayList<String>(securisedGroupNames);
	}

	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null) {
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;

	}

	public void setTrashTransformation(String name) {
		this.trashName = name;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();

		buf.append("Serveur de base de données : " + getServer().getName() + "\n");
		buf.append("Table cible : " + getDefinition() + "\n");
		buf.append("Vider la table : non\n");

		return buf.toString();
	}
}
