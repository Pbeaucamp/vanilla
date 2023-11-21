package bpm.gateway.core.transformations.vanilla;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.MappingException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.vanilla.RunVanillaCreateUser;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaCreateUser extends AbstractTransformation implements  IVanillaDirectMappable{
	
	
	public static final int IX_USER_NAME = 0;
	public static final int IX_USER_PASSWORD = 1;
	public static final int IX_USER_SURNAME = 2;
	public static final int IX_USER_FUNCTION = 3;
	public static final int IX_USER_PHONE = 4;
	public static final int IX_USER_CELLULAR = 5;
	public static final int IX_USER_SKYPENAME = 6;
	public static final int IX_USER_SKYPENUMBER = 7;
	public static final int IX_USER_BUSINESSMAIL =8;
	public static final int IX_USER_PRIVATEMAIL = 9;
	public static final int IX_USER_IMAGE = 10;
	public static final int IX_USER_ISSUPER = 11;
	
//	protected Integer[] mapping = new Integer[12];
	
	private HashMap<Integer, String> mappingNames = new HashMap<Integer, String>();
	
	/*
	 * Only used for old models
	 */
	private List<Point> bufferMappingIndex;

	
	private DefaultStreamDescriptor descriptor; 
	private boolean updateExisting = false;
	
	/**
	 * @return the updateExisting
	 */
	public boolean isUpdateExisting() {
		return updateExisting;
	}
	/**
	 * @param updateExisting the updateExisting to set
	 */
	public void setUpdateExisting(boolean updateExisting) {
		this.updateExisting = updateExisting;
	}
	
	public void setUpdateExisting(String updateExisting) {
		try{
			this.updateExisting = Boolean.parseBoolean(updateExisting);
		}catch(Exception ex){
			
		}
	}
	
	public VanillaCreateUser(){
		try{
			descriptor = new DefaultStreamDescriptor();
			
			StreamElement el = null;
			
			/*
			 * add UserName column
			 */
			el = new StreamElement();
			el.name = "UserName";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add UserPassword column
			 */
			el = new StreamElement();
			el.tableName= "users";
			el.name = "UserPassword";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_SURNAME column
			 */
			el = new StreamElement();
			el.tableName= "users";
			el.name = "UserSurname";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_FONCTION column
			 */
			el = new StreamElement();
			el.name = "UserFunction";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_TELEPHONE column
			 */
			el = new StreamElement();
			el.name = "UserTelephone";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_Cellular column
			 */
			el = new StreamElement();
			el.name = "UserCellular";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			

			/*
			 * add US_SKYPE_NAME column
			 */
			el = new StreamElement();
			el.name = "UserSypeName";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_SKYPE_NUMBER column
			 */
			el = new StreamElement();
			el.name = "UserSypeNumber";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add BusinessMail column
			 */
			el = new StreamElement();
			el.name = "UserBusinessMail";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add Private column
			 */
			el = new StreamElement();
			el.name = "UserPrivateMail";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add Private column
			 */
			el = new StreamElement();
			el.name = "UserImage";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			
			/*
			 * add SuperUser column
			 */
			el = new StreamElement();
			el.name = "UserSuperUser";
			el.tableName= "users";
			el.originTransfo = this.getName();
			el.className = Boolean.class.getName();
			descriptor.addColumn(el);
			
		}catch(Exception e){
			
		}
		
	}
	/**
	 * 
	 * @return the descriptor of the table Vanilla Group
	 */
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
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
		
		if(bufferMappingIndex != null){
			for(Point pt : bufferMappingIndex){
				try {
					createMapping(pt.x, pt.y);
				} catch (MappingException e1) {
					e1.printStackTrace();
				}
			}
			bufferMappingIndex = null;
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try{
			return new RunVanillaCreateUser(repositoryCtx, this, bufferSize);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaCreateUser");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("updateExisting").setText("" + updateExisting);


		/*
		 * for the mapping
		 */
		for(Integer type : mappingNames.keySet()){
			if (mappingNames.get(type) != null){
				Element transfoMap = e.addElement("inputMappingName");
				transfoMap.addElement("inputName").setText( type + "");
				transfoMap.addElement("outputName").setText(mappingNames.get(type));
			}
		}
		
		return e;
	}
	public Transformation copy() {
		return null;
	}
	
	/**
	 * if the current mapping does not exist already create it 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void createMapping(int transfoColNum, int colNum) throws MappingException{
		if (colNum == -1){
			mappingNames.remove(transfoColNum);
		}
		else{
			StreamElement element = null;
			try {
				element = inputs.get(0).getDescriptor(this).getStreamElements().get(colNum);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(element != null){
				mappingNames.put(transfoColNum, element.getFullName());
			}
			else {
				throw new MappingException("This column do not exist.");
			}
		}
	}
	
	/**
	 * Only used by digester for old model
	 * 
	 * @param transfoColNum
	 * @param colNum
	 * @throws MappingException
	 */
	public void createBufferMapping(String transfoColNum, String colNum) throws MappingException{
		if (colNum == null || colNum.toLowerCase().equals("null") || colNum.equals("-1")){
			return;
		}
		else{
			Point pt = new Point();
			pt.x = Integer.parseInt(transfoColNum);
			pt.y = Integer.parseInt(colNum);
			
			if(bufferMappingIndex == null){
				bufferMappingIndex = new ArrayList<Point>();
			}
			
			bufferMappingIndex.add(pt);
		}
	}
	
	/**
	 * Only used by digester
	 * 
	 * @param transfoColNum
	 * @param colNum
	 * @throws MappingException
	 */
	public void createMappingName(String transfoColNum, String colNum) throws MappingException {
		mappingNames.put(Integer.parseInt(transfoColNum), colNum);
	}


	/**
	 * remove the specified point from the mapping
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void deleteMapping(int transfoColNum){
		mappingNames.remove(transfoColNum);
	}
	
	
	public Integer getMappingValueForInputNum(int colNum){
		StreamElement element = null;
		try {
			element = inputs.get(0).getDescriptor(this).getStreamElements().get(colNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element != null){
			for(Integer key : mappingNames.keySet()){
				if(mappingNames.get(key).equals(element.getFullName())){
					return key;
				}
			}
		}
		return null;
	}
	
	public Integer getMappingValueForThisNum(int colNum){
		try {
			for(Integer key : mappingNames.keySet()){
				if(key == colNum){
					List<StreamElement> elements = getInputs().get(0).getDescriptor(this).getStreamElements();
					for(int i=0; i<elements.size(); i++){
						if(elements.get(i).getFullName().equals(mappingNames.get(key))){
							return i;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();

		for(Integer i : mappingNames.keySet()){
			switch(i){
			case IX_USER_BUSINESSMAIL:
				buf.append("UserBusinessMail Field : ");
				break;
			case IX_USER_CELLULAR:
				buf.append("UseCellularNumber Field : ");
				break;
			case IX_USER_FUNCTION:
				buf.append("UserFuntion Field : ");
				break;
			case IX_USER_IMAGE:
				buf.append("USerImage Field : ");
				break;
			case IX_USER_ISSUPER:
				buf.append("SuperUser Field : ");
				break;
			case IX_USER_NAME:
				buf.append("UserName Field : ");
				break;
				
				
			case IX_USER_PASSWORD:
				buf.append("UserPassword Field : ");
				break;
			case IX_USER_PHONE:
				buf.append("UserPhoneNumber Field : ");
				break;
			case IX_USER_PRIVATEMAIL:
				buf.append("UserPrivateMail Field : ");
				break;
			case IX_USER_SKYPENAME:
				buf.append("UserSkypeName Field : ");
				break;
			case IX_USER_SKYPENUMBER:
				buf.append("UserSkypeNumber Field : ");
				break;
			case IX_USER_SURNAME:
				buf.append("UserSurname Field : ");
				break;
		
			}
			
			buf.append(mappingNames.get(i) + "\n");
		}
		
		buf.append("Update existing users : " + updateExisting + "\n");
		
		return buf.toString();
	}
	
}
