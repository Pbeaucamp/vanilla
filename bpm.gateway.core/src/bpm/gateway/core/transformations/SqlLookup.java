package bpm.gateway.core.transformations;

import java.awt.Point;

import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunSqlLookup;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SqlLookup extends Lookup implements DataStream{

	
	private DataBaseInputStream dbStream = new DataBaseInputStream();

	
	/**
	 * 
	 */
	public SqlLookup() {
		super();
		inputs.add(dbStream);
	
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.transformations.Lookup#addInput(bpm.gateway.core.Transformation)
	 */
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 1 && !inputs.contains(stream)){
			throw new Exception("Only one input allowed for this Transformation");
		}
		setMasterInput(stream.getName());
		return super.addInput(stream);
	}
	
	public void initDbStream(DocumentGateway doc){
		dbStream.setDocumentGateway(doc);
	}

	public String getDefinition() {
		return dbStream.getDefinition();
	}

	public Server getServer() {
		return dbStream.getServer();
	}

	public void setDefinition(String definition) {
		dbStream.setDefinition(definition);
		refreshDescriptor();
		//initDescriptor();
		
	}

	public void setServer(Server s) {
		dbStream.setServer(s);
		
	}
	
	public void setServer(String s) {
		dbStream.setServer(s);
		
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		if (!isInited()){
			return;
		}
		try{
			
			try{
				for(StreamElement f : dbStream.getDescriptor(this).getStreamElements()){
					descriptor.addColumn(f.clone(getName(), ""));
				}
			}catch(Exception ex){
				
			}
			
			if (getInputs().size() > 1){
				for(StreamElement f : getInputs().get(1).getDescriptor(this).getStreamElements()){
					descriptor.addColumn(f.clone(getName(), ""));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	/* (non-Javadoc)
	 * @see bpm.gateway.core.transformations.Lookup#getElement()
	 */
	@Override
	public Element getElement() {
		Element e =  super.getElement();
		e.setName("sqlLookup");
		
		if (dbStream.getServer() != null){
			e.addElement("serverRef").setText(dbStream.getServer() .getName());
		}
		if (dbStream.getDefinition() != null){
			e.addElement("definition").setText(dbStream.getDefinition());
		}
		return e;
	}
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSqlLookup(this, bufferSize);
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DataBase Server :" + getServer().getName() + "\n");
		buf.append("SQL Lookup  :\n" + dbStream.getDefinition() + "\n");
		buf.append(super.getAutoDocumentationDetails());
		
		return buf.toString();
	}
	@Override
	public void initDescriptor() {
		setInited();
		if (getInputs().size() > 1){			
			descriptor = new DefaultStreamDescriptor();
			
			if (bufferMapping != null){
				for(Point p : bufferMapping){
					try{
						createMapping(p.x, p.y);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					
				bufferMapping = null;
			}
			if(bufferMappingName != null){
				for(String input : bufferMappingName.keySet()){
					try {
						createMapping(input, bufferMappingName.get(input));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				bufferMappingName = null;
			}
		}
		refreshDescriptor();
	}
}
