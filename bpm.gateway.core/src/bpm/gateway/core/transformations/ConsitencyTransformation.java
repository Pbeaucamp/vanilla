package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.mapping.RunConsistency;
import bpm.vanilla.platform.core.IRepositoryContext;

public class ConsitencyTransformation extends AbstractTransformation implements Trashable {
	
	private Transformation masterInput;
	private String masterName;
	
	private Transformation trashOutput;
	private String trashName;
	
	private List<ConsistencyMapping> consistencyMappings = new ArrayList<ConsistencyMapping>();
	
	private DefaultStreamDescriptor descriptor;

	private boolean needsRefresh = false;
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		//check if it's after digester
		if(masterName != null && masterInput == null) {
			if(stream.getName().equals(masterName)) {
				setMasterInput(stream);
				needsRefresh = true;
				return super.addInput(stream);
			}
		}
		else {
			for(ConsistencyMapping map : consistencyMappings) {
				if(map.getInputName() != null && map.getInput() == null) {
					if(stream.getName().equals(map.getInputName())) {
						map.setInput(stream);
						map.setParent(this);
						needsRefresh = true;
						return super.addInput(stream);
					}
				}
			}
		}
		 
		if(masterInput == null) {
			setMasterInput(stream);
		}
		else {
			if(!masterInput.getName().equals(stream.getName())) {
				boolean exists = false;
				for(ConsistencyMapping map : consistencyMappings) {
					if(map.getInput().getName().equals(stream.getName())) {
						exists = true;
						break;
					}
				}
				if(!exists) {
					ConsistencyMapping map = new ConsistencyMapping();
					map.setParent(this);
					map.setInput(stream);
					consistencyMappings.add(map);
				}
			}

		}
		needsRefresh = true;
		return super.addInput(stream);
	}
	
	@Override
	public void removeInput(Transformation transfo) {
		int i = 0;
		for(ConsistencyMapping map : consistencyMappings) {
			if(map.getInput().equals(transfo)) {
				break;
			}
			i++;
		}
		if(i < 0) {
			masterInput = null;
			masterName = null;
		}
		else {
			consistencyMappings.remove(i);
		}
		needsRefresh = false;
		super.removeInput(transfo);
	}
	
	@Override
	public Transformation copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAutoDocumentationDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if(needsRefresh) {
			refreshDescriptor();
			needsRefresh = false;
		}
		return descriptor;
	}

	@Override
	public Element getElement() {
		//Transfo description
		Element e = DocumentHelper.createElement("consistencyTransformation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
	
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		e.addElement("masterName").setText(masterName);
		e.addElement("trashName").setText(trashName);
		
		for(ConsistencyMapping map : consistencyMappings) {
			Element mapElem = e.addElement("mapping");
			mapElem.addElement("inputName").setText(map.getInputName());
			for(String key : map.getMappings().keySet()) {
				Element mappingE = mapElem.addElement("mappingValues");
				mappingE.addElement("mappingkey").setText(key);
				mappingE.addElement("mappingvalue").setText(map.getMappings().get(key));
			}
		}
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunConsistency(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if(needsRefresh) {
			descriptor = new DefaultStreamDescriptor();
			
			if(allInited()) {
				try {
					for(StreamElement element : masterInput.getDescriptor(null).getStreamElements()) {
						descriptor.addColumn(element);
					}
					for(ConsistencyMapping mapping : consistencyMappings) {
						if(mapping.isKeepInOutput() && ((AbstractTransformation)mapping.getInput()).isInited()) {
							for(StreamElement element : mapping.getInput().getDescriptor(null).getStreamElements()) {
								descriptor.addColumn(element);
							}
						}
					}
				} catch (ServerException e) {
					e.printStackTrace();
				}
				needsRefresh = false;
			}
		}
	}

	private boolean allInited() {
		if(!((AbstractTransformation)masterInput).isInited()) {
			return false;
		}
		for(ConsistencyMapping mapping : consistencyMappings) {
			if(!(mapping.isKeepInOutput() && ((AbstractTransformation)mapping.getInput()).isInited())) {
				return false;
			}
		}
		return true;
	}

	public Transformation getMasterInput() {
		return masterInput;
	}

	public void setMasterInput(Transformation masterInput) {
		this.masterInput = masterInput;
		masterName = masterInput.getName();
		needsRefresh = true;
	}

	public List<ConsistencyMapping> getConsistencyMappings() {
		return consistencyMappings;
	}

	public void setConsistencyMappings(List<ConsistencyMapping> consistencyMappings) {
		this.consistencyMappings = consistencyMappings;
	}

	public List<StreamElement> getDescriptorWithoutMappings() {
		List<StreamElement> elements = new ArrayList<StreamElement>();
		try {
			for(StreamElement elem : getDescriptor(null).getStreamElements()) {
				if(!elem.transfoName.equals(masterInput.getName())) {
					break;
				}
				elements.add(elem);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return elements;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	
	public void addMapping(ConsistencyMapping mapping) {
		mapping.setParent(this);
		consistencyMappings.add(mapping);
	}

	public String getTrashName() {
		return trashName;
	}

	public void setTrashName(String trashName) {
		this.trashName = trashName;
	}

	@Override
	public void addOutput(Transformation stream) {
		if(trashOutput == null && trashName != null) {
			if(stream.getName().equals(trashName)) {
				trashOutput = stream;
			}
		}
		super.addOutput(stream);
	}

	@Override
	public Transformation getTrashTransformation() {
		return trashOutput;
	}

	@Override
	public void setTrashTransformation(Transformation transfo) {
		this.trashOutput = transfo;
		trashName = trashOutput.getName();
	} 
}
