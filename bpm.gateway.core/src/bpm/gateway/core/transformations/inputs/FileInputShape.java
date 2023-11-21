package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.file.FileShape;
import bpm.gateway.core.server.file.FileShapeHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunShapeInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileInputShape extends FileShape implements Trashable {
	/**
	 * step used as trash to collect rows that have not been inserted for some reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;
	
	private boolean saveInNorparena = false;
	private boolean fromNewMap = true;
	private String newMapName = "";
	private Integer existingMapId = -1;
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("fileInputShape");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("saveInNorparena").setText(String.valueOf(saveInNorparena));
		e.addElement("fromNewMap").setText(String.valueOf(fromNewMap));
		e.addElement("newMapName").setText(newMapName);
		e.addElement("existingMapId").setText(String.valueOf(existingMapId));
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		if (getTrashTransformation() != null){
			e.addElement("trashOuput").setText(errorHandlerTransformation.getName());
		}

		e.addElement("useMdm").setText(useMdm() + "");
		
		if (getSelectedContract() != null) {
			e.addElement("supplierId").setText(getSelectedContract().getParent().getId() + "");
		}
		if (getSelectedContract() != null) {
			e.addElement("contractId").setText(getSelectedContract().getId() + "");
		}
		
		return e;

	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try {
			return new RunShapeInput(repositoryCtx, this, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		


		if (getSelectedContract() != null && getSelectedContract().getDocId() != null) {
			if (getSelectedContract().getFileVersions() == null) {
				try {
					getSelectedContract().setFileVersions(this.getDocument().getMdmHelper().getGedDocument(getSelectedContract()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String ext = getSelectedContract().getFileVersions().getCurrentVersion(getSelectedContract().getVersionId()).getFormat();
			if (ext.equalsIgnoreCase("shp")) {
				try {
					FileShapeHelper.createStreamDescriptor(this, 100);
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Transformation t : outputs) {
					t.refreshDescriptor();
				}
			}
		}
		else if (getDefinition() != null && !getDefinition().isEmpty() && !useMdm()) {
			try {
				FileShapeHelper.createStreamDescriptor(this, 100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Transformation t : outputs) {
				t.refreshDescriptor();
			}
		}
	}

	@Override
	public void initDescriptor() {
		super.initDescriptor();
		try{
			FileShapeHelper.createStreamDescriptor(this, 100);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public Transformation copy() {
		FileInputShape copy = new FileInputShape();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());

		return copy;
	}
	
	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
	}


	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;
		
	}
	public void setTrashTransformation(String name){
		this.trashName = name;
	}
	
	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		if (outputs.contains(stream)){
			if (trashName != null && trashName.equals(stream.getName())){
				setTrashTransformation(stream);
				trashName = null;
			}
		}
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition()+ "\n");
		
		if (getTrashTransformation() != null){
			buf.append("ErrorHandler: " + getTrashTransformation().getName()+ "\n");
		}
		return buf.toString();
	}

	public void setSaveInNorparena(boolean saveInNorparena) {
		this.saveInNorparena = saveInNorparena;
	}

	public void setSaveInNorparena(String saveInNorparena) {
		if(saveInNorparena.equalsIgnoreCase("true")){
			this.saveInNorparena = true;
		}
		else {
			this.saveInNorparena = false;
		}
	}
	
	public boolean saveInNorparena() {
		return saveInNorparena;
	}

	public void setFromNewMap(boolean fromNewMap) {
		this.fromNewMap = fromNewMap;
	}

	public void setFromNewMap(String fromNewMap) {
		if(fromNewMap.equalsIgnoreCase("true")){
			this.fromNewMap = true;
		}
		else {
			this.fromNewMap = false;
		}
	}
	
	public boolean isFromNewMap() {
		return fromNewMap;
	}

	public void setNewMapName(String newMapName) {
		this.newMapName = newMapName;
	}
	
	public String getNewMapName() {
		return newMapName;
	}

	public void setExistingMapId(Integer existingMapId) {
		this.existingMapId = existingMapId;
	}

	public void setExistingMapId(String existingMapId) {
		try {
			this.existingMapId = Integer.parseInt(existingMapId);
		} catch (Exception e) {
			this.existingMapId = -1;
		}
	}
	
	public Integer getExistingMapId(){
		return existingMapId;
	}
}
