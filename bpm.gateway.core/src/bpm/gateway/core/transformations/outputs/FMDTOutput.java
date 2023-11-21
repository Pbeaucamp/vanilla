package bpm.gateway.core.transformations.outputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.metadata.query.QuerySql;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FMDTOutput extends AbstractTransformation {

	private Integer repositoryItemId;
	private String xmlQuery;
	private String businessModelName;
	private String businessPackageName;
	
	
	private DefaultStreamDescriptor descriptor;
	
	/**
	 * only to dont slow because of parsing the xmlQueryString more than necesseray
	 */
	private QuerySql querySql;
	private RepositoryItem repositoryItem;
	
	public FMDTOutput(){
		addPropertyChangeListener(this);
	}
	
	public String getBusinessModelName() {
		return businessModelName;
	}

	public void setBusinessModelName(String businessModelName) {
		this.businessModelName = businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}

	public void setBusinessPackageName(String businessPackageName) {
		this.businessPackageName = businessPackageName;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fmdtOutput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (repositoryItemId != null){
			e.addElement("repositoryItemId").setText(repositoryItemId + "");
		}
		
		if (businessModelName != null){
			e.addElement("businessModelName").setText(businessModelName);
		}
		
		if (businessPackageName != null){
			e.addElement("businessPackageName").setText(businessPackageName);
		}
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (xmlQuery != null){
			e.addElement("definition").addCDATA(xmlQuery);
		}
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return null;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	public String getDefinition() {
		return xmlQuery;
	}


	public void setDefinition(String definition) {
		this.xmlQuery = definition;
		
		//TODO : create the 
	}

	

	public Integer getRepositoryItemId() {
		return repositoryItemId;
	}

	public void setRepositoryItemId(Integer repositoryItemId) {
		this.repositoryItemId = repositoryItemId;
	}

	public void setRepositoryItemId(String repositoryItemId) {
		try{
			setRepositoryItemId(Integer.parseInt((repositoryItemId)));
		}catch(NumberFormatException e){
			
		}
	}
	
	protected final void setQueryFmdt(QuerySql q){
		this.querySql = q;
	}
	
	public QuerySql getQueryFmdt(){
		return querySql;
	}
	
	public RepositoryItem getRepositoryItem(){
		return repositoryItem;
	}
	public void setRepositoryItem(RepositoryItem item){
		this.repositoryItem = item;
	}

	public Transformation copy() {
		FMDTOutput copy = new FMDTOutput();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);

		return copy;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}
}

