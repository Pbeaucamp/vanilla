package bpm.gateway.core.transformations.olap;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunOlapFactExtractor;
import bpm.vanilla.platform.core.IRepositoryContext;

public class OlapFactExtractorTranformation extends AbstractTransformation implements IOlap, Trashable, IOlapDimensionable{


	private Integer diretcoryItemId;
	private String cubeName;
	private String dimensionName;
	private String hierarchieName;
	
	private List<DimensionFilter> dimensionFilters = new ArrayList<DimensionFilter>();

	private DefaultStreamDescriptor descriptor;
	private Transformation trash;
	private String trashName;
	private String directoryItemName;
	

	
	
	public String getHierarchieName() {
		return hierarchieName;
	}



	public void setHierarchieName(String hierarchieName) {
		this.hierarchieName = hierarchieName;
	}



	
	public Integer getDirectoryItemId() {
		return diretcoryItemId;
	}

	public void setDirectoryItemId(Integer diretcoryItemId) {
		this.diretcoryItemId = diretcoryItemId;
		cubeName = null;
		dimensionName = null;
	}
	public void setDirectoryItemId(String itemId){
		try{
			this.diretcoryItemId  = Integer.parseInt(itemId);
		}catch(Exception e){
			
		}
		
	}

	@Override
	public void removeOutput(Transformation transfo) {
		super.removeOutput(transfo);
		for(DimensionFilter f : dimensionFilters){
			if (transfo.getName().equals(f.getOutputName())){
				f.setOutputTransformation(null);
			}
		}
		if (transfo == trash){
			trash = null;
		}
	}

	public String getCubeName() {
		return cubeName;
	}


	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
		try {
			descriptor = (DefaultStreamDescriptor)getDocument().getOlapHelper().getDescriptor(this);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public List<DimensionFilter> getDimensionFilters() {
		return new ArrayList<DimensionFilter>(dimensionFilters);
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		if (trashName != null && trashName.equals(stream.getName())){
			setTrashTransformation(stream);
			trashName = null;
		}
	}
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("olapInput");

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
		
		for(DimensionFilter f : getDimensionFilters()){
			e.add(f.getElement());
		}
		
		if (trash != null){
			e.addElement("trashRef").setText(trash.getName());
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//
//		return new OlapInputRuntime(this, runtimeEngine);
//	}
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		
		return new RunOlapFactExtractor(this, bufferSize);
	}
	
	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		try{
			descriptor = (DefaultStreamDescriptor)getDocument().getOlapHelper().getDescriptor(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		
		return null;
	}



	public void addDimensionFilter(DimensionFilter df) {
		this.dimensionFilters.add(df);
	}
	
	public void removeDimensionFilter(DimensionFilter df) {
		this.dimensionFilters.remove(df);
	}

	public void setDescriptor(DefaultStreamDescriptor desc) {
		descriptor = desc;
		
	}

	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null){
			trash = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trash;
	}

	public void setTrashTransformation(Transformation transfo) {
		if (transfo == null){
			trash = null;
		}
		if (getOutputs().contains(transfo)){
			trash = transfo;
		}
		
		
	}
	
	public void setTrashTransformation(String transfoName) {
		trashName = transfoName;
		
	}
	
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DirectoryItem Id : " + diretcoryItemId + "\n");
		buf.append("CubeName : " + cubeName + "\n");
		buf.append("DimensionName : " + dimensionName + "\n");
		buf.append("HierarchyName : " + hierarchieName + "\n");
		
		if (trash != null){
			buf.append("TrashOutput : " + trash.getName() + "\n");
		}
		
		for(DimensionFilter f : dimensionFilters){
			buf.append("Filter " + f.getName() + " for output " + f.getOutputName() + " : \n");
			for(FilterClause c : f.getFilters()){
				buf.append("\t- with " + c.getLevelName() + " = " + c.getValue() + "\n");
			}
			buf.append("\n");
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
