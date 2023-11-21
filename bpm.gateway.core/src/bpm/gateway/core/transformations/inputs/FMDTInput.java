package bpm.gateway.core.transformations.inputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunFMDTInput;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FMDTInput extends AbstractTransformation /*implements DataStream */{

	private Integer repositoryItemId;
	private String xmlQuery;
	private String businessModelName;
	private String businessPackageName;
	private String connectionName;
	
	private DefaultStreamDescriptor descriptor;
	
	/**
	 * only to dont slow because of parsing the xmlQueryString more than necesseray
	 */
	private IQuery querySql;
	private RepositoryItem repositoryItem;
	
	private HashMap<String, List<String>> promptValues = new HashMap<String, List<String>>();
	
	public FMDTInput(){
		addPropertyChangeListener(this);
	}
	
	public String getBusinessModelName() {
		return businessModelName;
	}

	public List<String> getPromptValues(String promptName){
		for(String s : promptValues.keySet()){
			if (s.equals(promptName)){
				return promptValues.get(s);
			}
		}
		return new ArrayList<String>();
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
	public void initDescriptor() {
		try {
			descriptor = (DefaultStreamDescriptor)getDocument().getFMDTHelper().getDescriptor(this);
			setInited();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("fmdtInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (repositoryItemId != null){
			e.addElement("repositoryItemId").setText(repositoryItemId + "");
		}
		
		if (connectionName != null){
			e.addElement("connectionName").setText(connectionName);
		}
		
		if (businessModelName != null){
			e.addElement("businessModelName").setText(businessModelName);
		}
		
		if (businessPackageName != null){
			e.addElement("businessPackageName").setText(businessPackageName);
		}
		if (querySql != null){
			for(Prompt p : querySql.getPrompts()){
				Element s = e.addElement("prompt");
				
				
				s.addElement("promptName").setText(p.getName());
				
				for(String k : promptValues.keySet()){
					if (k.equals(p.getName())){
						try{
							s.addElement("promptValue").setText(promptValues.get(k).get(0));
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
				
				
				
			}
		}
		
		
		
		if (xmlQuery != null){
			e.addElement("definition").addCDATA(xmlQuery);
		}
		
		
		
		
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}
	
	
	/**
	 * used only by digester
	 * @param promptName
	 * @param promptValue
	 * @throws NullPointerException
	 */
	public void setPromptValue(String promptName, String promptValue) throws NullPointerException{
		
		for(String k : promptValues.keySet()){
			if (k.equals(promptName)){
				promptValues.get(promptName).add(promptValue);
				return;
			}
		}
		
		List<String> vals = new ArrayList<String>();
		vals.add(promptValue);
		promptValues.put(promptName, vals);
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new FMDTInputRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunFMDTInput(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}

	}

	public String getDefinition() {
		return xmlQuery;
	}



	public void setDefinition(String definition) {
		this.xmlQuery = definition;
		
		//TODO : create the 
		try {
			descriptor = (DefaultStreamDescriptor)getDocument().getFMDTHelper().getDescriptor(this);
			fireProperty(PROPERTY_INPUT_CHANGED);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
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
	
	protected final void setQueryFmdt(IQuery q){
		this.querySql = q;
	}
	
	public IQuery getQueryFmdt(){
		return querySql;
	}
	
	public RepositoryItem getRepositoryItem(){
		return repositoryItem;
	}
	public void setRepositoryItem(RepositoryItem item){
		this.repositoryItem = item;
	}

	public Transformation copy() {
		FMDTInput copy = new FMDTInput();
		
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setBusinessModelName(getBusinessModelName());
		copy.setBusinessPackageName(getBusinessPackageName());
		copy.setRepositoryItem(getRepositoryItem());
		copy.setQueryFmdt(getQueryFmdt());
		
		return copy;
	}

	public HashMap<String, List<String>> getPromptValues() {
		return promptValues;
	}

	public void setPromptValues(HashMap<String, List<String>> promptValues2) {
		this.promptValues = promptValues2;
		
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
		
	}
	
	public String getConnectioName(){
		return this.connectionName;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DirectoryItemId : " + repositoryItemId + "\n");
		buf.append("BusinessModel : " + businessModelName + "\n");
		buf.append("BusinessPackage : " + businessPackageName + "\n");
		buf.append("ConnectionName : " + connectionName + "\n");
		buf.append("Metadata Query : \n" + (querySql != null ? querySql.getXml() : "null") + "\n");
		
		
		return buf.toString();
	}
}

