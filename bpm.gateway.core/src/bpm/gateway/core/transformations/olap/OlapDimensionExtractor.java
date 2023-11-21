package bpm.gateway.core.transformations.olap;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunOlapDimensionExtractor2;
import bpm.vanilla.platform.core.IRepositoryContext;

public class OlapDimensionExtractor extends AbstractTransformation implements IOlapDimensionable {

	
	private String cubeName;
	private Integer directoryItemId;
	
	private String dimensionName;
	private String hierarchyName;
	
	private DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
	private List<String> levels = new ArrayList<String>();
	private String directoryItemName;
	
	public void addLevel(String levelName){
		if (!levels.contains(levelName)){
			levels.add(levelName);
			refreshDescriptor();
		}
	}
	
	public void removeLevel(String levelName){
		levels.remove(levelName);
		refreshDescriptor();
	}
	
	public void setDimensionName(String dimensionName){
		this.dimensionName = dimensionName;
	}
	
	public void setHierarchieName(String hierarchyName){
		this.hierarchyName = hierarchyName;
	}
	
	public String getDimensionName(){
		return this.dimensionName;
	}
	
	public String getHierarchieName(){
		return this.hierarchyName;
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return desc;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("olapDimensionInput");

		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (getDirectoryItemId() != null){
			e.addElement("directoryItemId").setText(getDirectoryItemId() + "");
			e.addElement("directoryItemName").setText(getDirectoryItemName() + "");
		}
		
		if (getCubeName() != null){
			e.addElement("cubeName").setText(getCubeName());
		}
		if (getDimensionName() != null){
			e.addElement("dimensionName").setText(getDimensionName());
		}
		if (getHierarchieName() != null){
			e.addElement("hierarchieName").setText(getHierarchieName());
		}
		
		for(String s : levels){
			e.addElement("levelName").setText(s);
		}
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunOlapDimensionExtractor2(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		try{
			desc = (DefaultStreamDescriptor)getDocument().getOlapHelper().getDescriptor(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	

	public String getCubeName() {
		return cubeName;
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
		
	}

	public void setDirectoryItemId(Integer id) {
		this.directoryItemId = id;
		
	}
	
	public void setDirectoryItemId(String id) {
		this.directoryItemId = Integer.parseInt(id);
		
	}

	public Transformation copy() {
		return null;
	}

	public List<String> getLevelNames() {
		return new ArrayList<String>(levels);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DirectoryItem Id : " + directoryItemId + "\n");
		buf.append("CubeName : " + cubeName + "\n");
		buf.append("DimensionName : " + dimensionName + "\n");
		buf.append("HierarchyName : " + hierarchyName + "\n");
		
		for(int i = 0; i < levels.size(); i++){
			buf.append("Extracted Level : " + levels.get(i) + "\n");
		}
		return buf.toString();
	}

	@Override
	public String getDirectoryItemName() {
		return directoryItemName;
	}

	@Override
	public void setDirectoryItemName(String name) {
		directoryItemName = name;
	}

}
