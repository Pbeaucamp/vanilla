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
import bpm.gateway.runtime2.transformation.vanilla.RunVanillaCreateGroup;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaCreateGroup extends AbstractTransformation implements IVanillaDirectMappable{

	public static final int IX_GROUP_NAME = 0;
	public static final int IX_GROUP_COMMENT = 1;
	public static final int IX_GROUP_IMAGE = 2;
	public static final int IX_GROUP_CUSTOM_1 = 3;
	public static final int IX_GROUP_PARENT_ID = 4;
	
//	protected Integer[] mapping = new Integer[5];
	
	private HashMap<Integer, String> mappingNames = new HashMap<Integer, String>();
	
	/*
	 * Only used for old models
	 */
	private List<Point> bufferMappingIndex;
	
	private DefaultStreamDescriptor descriptor;
	
	public VanillaCreateGroup (){
		try{
			descriptor = new DefaultStreamDescriptor();
			
			StreamElement el = null;
			
			/*
			 * add GroupName column
			 */
			el = new StreamElement();
			el.name = "GroupName";
			el.tableName= "Groups";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add UserPassword column
			 */
			el = new StreamElement();
			el.tableName= "Groups";
			el.name = "comment";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_SURNAME column
			 */
			el = new StreamElement();
			el.tableName= "Groups";
			el.name = "image";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_FONCTION column
			 */
			el = new StreamElement();
			el.name = "custom_1";
			el.tableName= "Groups";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
			
			/*
			 * add US_TELEPHONE column
			 */
			el = new StreamElement();
			el.name = "parentId";
			el.tableName= "Groups";
			el.originTransfo = this.getName();
			el.className = String.class.getName();
			descriptor.addColumn(el);
		
			
		}catch(Exception e){
			
		}

	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaCreateGroup");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
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

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try{
			return new RunVanillaCreateGroup(repositoryCtx, this, bufferSize);
		}catch(Exception ex){
			throw new RuntimeException(ex.getMessage(), ex);
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
			case IX_GROUP_COMMENT:
				buf.append("GroupComment Field : ");
				break;
			case IX_GROUP_CUSTOM_1:
				buf.append("GroupCustom1 Field : ");
				break;
			case IX_GROUP_IMAGE:
				buf.append("GroupImage Field : ");
				break;
			case IX_GROUP_NAME:
				buf.append("GroupName Field : ");
				break;
			case IX_GROUP_PARENT_ID:
				buf.append("GroupParentId Field : ");
				break;
			}
			
			buf.append(mappingNames.get(i) + "\n");
		}
		
		return buf.toString();
	}
}
