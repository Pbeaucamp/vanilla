package bpm.gateway.core.transformations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.internal.GraphNodeBuilder;
import bpm.gateway.core.internal.ModelGraphNode;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.gid.GidNode;
import bpm.gateway.core.transformations.gid.GidTreeBuilder;
import bpm.gateway.core.transformations.gid.Query;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.gid.GidRuntime;
import bpm.vanilla.platform.core.IRepositoryContext;

public class GlobalDefinitionInput extends AbstractTransformation implements PropertyChangeListener{

	private List<Transformation> content = new ArrayList<Transformation>();
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private int width;
	private int height;
	private String customQuery = "";
	
	public List<Transformation> getContent(){
		return new ArrayList<Transformation>(content);
	}
	
	
	
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		throw new Exception("Global Input cannot have Input Transformations");
	}




	@Override
	public String getContainer() {
		
		return null;
	}




	public boolean addContent(Transformation t){
		if (content.contains(t)){
			return false;
		}
		
		content.add(t);
		t.setContainer(this.getName());
		((AbstractTransformation)t).addPropertyChangeListener(this);
		refreshDescriptor();
		return true;
	}
	
	
	public boolean removeContent(Transformation t){
		if (!content.contains(t)){
			return false;
		}
		
		content.remove(t);
		((AbstractTransformation)t).removePropertyChangeListener(this);
		t.setContainer(null);
		return true;
	}
	
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if(descriptor.getStreamElements() == null || descriptor.getStreamElements().isEmpty()) {
			if(!isInited()) {
				setInited();
			}
			refreshDescriptor();
		}
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("globalInputDefinition");
		
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("width").setText("" + width);
		e.addElement("height").setText("" + height);
		
		e.addElement("customQuery").setText("" + customQuery);
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;
	}
	
	
	

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setHeight(String height) {
		this.height = Integer.parseInt(height);
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {

		return new GidRuntime(this, bufferSize);
	}

	
	@Override
	public void setInited() {
		
		/*
		 * once this step is inited, the refreshDescriptor will be called but the transformation held
		 * by this step are not yet inited.
		 * so we need to init them by rebuilding a modelGraphn on those and launche an init
		 * 
		 */
		GraphNodeBuilder builder = new GraphNodeBuilder();
		ModelGraphNode root = builder.createTree(getContent());
		try {
			root.initTransfo();
		} catch(Exception e) {
		}
		
		super.setInited();
	}
	
	@Override
	public void refreshDescriptor() {
		
		if (!isInited()){
			return;
		}
		try{
			/*
			 * to refresh a descriptor we need the to rebuild the Sql query and analyze its metadata 
			 */
			GidTreeBuilder treeBuilder = new GidTreeBuilder();
			GidNode node = treeBuilder.buildTree(this);
			if(node == null) {
				return;
			}
			
			Query query = node.evaluteQuery();
			List<Server> servers = treeBuilder.getDataStreamsServers(node);
			
			if (servers.size() > 1){
				throw new Exception("Cannot have a Gid step using multiples DataBase server's");
			}
			
			DataBaseServer server = null;
			if (servers.size() > 0){
				server = (DataBaseServer)servers.get(0);
			}
			descriptor = DataBaseHelper.createDescriptorFromQuery(getName(), server, query, getDocument());
			
			for(Transformation t : outputs){
				t.refreshDescriptor();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			Activator.getLogger().error("Error when refreshing Gid - " + ex.getMessage(), ex);
		}
		
		
		

	}

	public Transformation copy() {
		
		return null;
	}




	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
		if (event.getPropertyName().equals(PROPERTY_CONTENTS)){
			refreshDescriptor();
		}
		
	}
	public String getAutoDocumentationDetails() {
		
		return "";
	}




	public String getCustomQuery() {
		return customQuery;
	}




	public void setCustomQuery(String customQuery) {
		this.customQuery = customQuery;
	}
	
	@Override
	public void setName(String name) {
		for(Transformation t : content) {
			t.setContainer(name);
		}
		super.setName(name);
	}
}
