package bpm.gateway.core.transformations.vanilla;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.vanilla.RunVanillaMapRoleGroup;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaRoleGroupAssocaition extends AbstractTransformation {

	private DefaultStreamDescriptor descriptor;
	
	private String groupIdInputName;
	
	/*
	 * Only used for old models
	 */
	private Integer bufferGroupIdInputIndex;
	
	private List<Integer> rolesIds = new ArrayList<Integer>();
	
	public VanillaRoleGroupAssocaition (){
		try{
			descriptor = new DefaultStreamDescriptor();
			
			StreamElement el = null;
			
			/*
			 * add UserPassword column
			 */
			el = new StreamElement();
			el.tableName= "RoleId";
			el.name = "RoleGroup";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_SURNAME column
			 */
			el = new StreamElement();
			el.tableName= "RoleGroup";
			el.name = "GroupId";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);

		}catch(Exception e){
			
		}
	}
	
	public void addRole(String roleId){
		try{
			addRole(Integer.parseInt(roleId));
		}catch(Exception ex){
			
		}
	}
	public void addRole(Integer roleId){
		if (roleId == null || roleId < 0){
			return;
		}
		
		for(Integer i : rolesIds){
			if (i.equals(roleId)){
				return;
			}
		}
		
		rolesIds.add(roleId);
	}
	
	public void removeRole(Integer roleId){
		for(Integer i : rolesIds){
			if (i.equals(roleId)){
				rolesIds.remove(i);
				return;
			}
		};
	}
	
	public void setGroupIdIndex(Integer index){
		if (index != null && index >= 0){
			
			StreamElement element = null;
			try {
				element = inputs.get(0).getDescriptor(this).getStreamElements().get(index);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(element != null){
				groupIdInputName = element.getFullName();
			}
		}
		else {
			groupIdInputName = null;
		}
	}
	
	public void setGroupIdName(String groupIdName){
		groupIdInputName = groupIdName;
	}
	
	public void setBufferGroupIdIndex(String index){
		try{
			bufferGroupIdInputIndex = Integer.parseInt(index);
		}catch(Exception ex){ }
	}
	
	public Integer getGroupIdIndex(){
		List<StreamElement> elements = null;
		try {
			elements = inputs.get(0).getDescriptor(this).getStreamElements();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(elements != null){
			for(int i=0; i<elements.size(); i++){
				if(elements.get(i).getFullName().equals(groupIdInputName)){
					return i;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaRoleGroupAssociation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
	
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		/*
		 * for the mapping
		 */
		if (groupIdInputName != null){
			e.addElement("groupIdName").setText(groupIdInputName);
		}
		
		for(Integer i : rolesIds){
			e.addElement("roleId").setText(i + "");
		}

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try {
			return new RunVanillaMapRoleGroup(repositoryCtx, this, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public Transformation copy() {
		return null;
	}
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().contains(stream)){
			return false;
		}
		if (getInputs().size() > 0){
			throw new Exception("Can only have one input");
		}
		return super.addInput(stream);
	}
	
	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		for(StreamElement e : descriptor.getStreamElements()){
			e.originTransfo = this.getName();
			e.transfoName = this.getName();
		}

		if(bufferGroupIdInputIndex != null){
			setGroupIdIndex(bufferGroupIdInputIndex);
			bufferGroupIdInputIndex = null;
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}
	
	public List<Integer> getRolesId() {
		return new ArrayList<Integer>(rolesIds);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();

		buf.append("GroupId Field : " + groupIdInputName + "\n");
		
		boolean first = true;
		buf.append("Vanilla Roles Id : \n" );
		for(Integer i : rolesIds){
			if (first){
				first = false;
			}
			else{
				buf.append(", ");
			}
			buf.append(i);
		}
		
		buf.append("\n");
		return buf.toString();
	}
}
