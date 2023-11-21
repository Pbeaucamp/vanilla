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
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.utils.Aggregate;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.aggregation.RunAggragtion;
import bpm.vanilla.platform.core.IRepositoryContext;



/**
 * This transformation perform some aggregations 
 * @author LCA
 *
 */
public class AggregateTransformation extends AbstractTransformation {

	public static final String[] AGG_ON_NULL_MODE = new String[]{"Skip Row", "Value as Zero"};
	public static final int MODE_SKIP = 0;
	public static final int MODE_ZERO = 1;
	
	private boolean checkIfOldModel = true;
	
	
	private int nullMode = MODE_ZERO;
	
	public int getNullMode() {
		return nullMode;
	}




	public void setNullMode(int nullMode) {
		this.nullMode = nullMode;
	}

	public void setNullMode(String nullMode) {
		try{
			this.nullMode = Integer.parseInt(nullMode);
		}catch(NumberFormatException e){
			
		}
	}



	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	private List<Aggregate> aggregates = new ArrayList<Aggregate>();
	private List<String> groupsByFieldNames = new ArrayList<String>();
	
	/*
	 * For digester use only (for old model)
	 */
	private List<Integer> bufferGroupsByFieldIndex;
	
	/**
	 * Aggregate is the description of the Aggration function to perform
	 * @param a
	 */
	public void addAggregate(Aggregate a){
		
		for(Aggregate aggs : aggregates){
			if (a.getStreamElementName() != null && aggs.getStreamElementName() != null && a.getStreamElementName().equals(aggs.getStreamElementName())){
				aggregates.remove(aggs);
				break;
			}
		}
		aggregates.add(a);
		
		refreshDescriptor();
		
		fireChangedProperty();
	}
	
	
	
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)){
			throw new Exception("The Filter Transformations can only have one Input");
		}
		boolean b =  super.addInput(stream);
		
		if (b){

			
			
		if (isInited()){
			groupsByFieldNames = new ArrayList<String>();
			aggregates = new ArrayList<Aggregate>();
			refreshDescriptor();
		}

		}
		return b;
	}

	
	/**
	 * remove the aggregation and rebuild the StreamDescriptor
	 * @param a
	 */
	public void removeAggregate(Aggregate a){
		boolean b = aggregates.remove(a);
		
		if (b){
			refreshDescriptor();
			fireChangedProperty();
		}
		
	}
	
	/**
	 * 
	 * @return the list of all the aggregation to do
	 */
	public List<Aggregate> getAggregates(){
		return new ArrayList<Aggregate>(aggregates);
	}


	/**
	 * Only used by digester
	 * @param index
	 */
	public void addGroupBy(String index){
		if(bufferGroupsByFieldIndex == null){
			bufferGroupsByFieldIndex = new ArrayList<Integer>();
		}
		
		try {
			bufferGroupsByFieldIndex.add(Integer.parseInt(index));
			fireChangedProperty();
		}catch(NumberFormatException e){ }
	}

	/**
	 * Only used by digester
	 * @param elementName
	 */
	public void addGroupByNames(String elementName){
		groupsByFieldNames.add(elementName);
	}
	
	
	/**
	 * add this STreamElement from the StreamDescriptor as Group field for this
	 * transformation
	 * @param e
	 */
	public void addGroupBy(StreamElement e){
		for(Aggregate a : aggregates){
			if (a.getStreamElementName() != null && a.getStreamElementName().equals(e.getFullName())){
				aggregates.remove(a);
				break;
			}
		}
		
		for(String a : groupsByFieldNames){
			if (a.equals(e.getFullName())){
				return;
			}
		}
		groupsByFieldNames.add(e.getFullName());
		refreshDescriptor();
		fireChangedProperty();
	}
	
	/**
	 * remove this field from the Group clause
	 * @param e
	 */
	public void removeGroupBy(StreamElement e){
		boolean b = false;
		
		for(String k : groupsByFieldNames){
			if (e.getFullName().equals(k)){
				b = groupsByFieldNames.remove(k);
				break;
			}
		}
		
		if (b ){
			refreshDescriptor();
			fireChangedProperty();
		}
	}
	
	
	
	/**
	 * return the Streamdescriptor's  indices for this Transformation 
	 * @return
	 */
	public List<Integer> getGroupBy(){
		List<Integer> groupsByPostion = new ArrayList<Integer>();
		for(String gr : groupsByFieldNames){
			try {
				for(int i=0; i<inputs.get(0).getDescriptor(this).getStreamElements().size(); i++){
					if(inputs.get(0).getDescriptor(this).getStreamElements().get(i).getFullName().equals(gr)){
						groupsByPostion.add(i);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return groupsByPostion;
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("aggregation");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("nullMode").setText(getNullMode() + "");
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		for(String i : groupsByFieldNames){
			e.addElement("groupByNames").setText(i);
		}
		
		for(Aggregate a : aggregates){
			Element e_a = e.addElement("aggregateNew");
			e_a.addElement("columnName").setText(a.getStreamElementName());
			e_a.addElement("function").setText(a.getFunction() + "");
		}
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;
	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunAggragtion(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		descriptor = new DefaultStreamDescriptor();
		
		
		if (!inputs.isEmpty()){
			
			try{
				if(bufferGroupsByFieldIndex != null){
					for(Integer groupByIndex : bufferGroupsByFieldIndex){
						StreamElement element = null;
						try {
							element = inputs.get(0).getDescriptor(this).getStreamElements().get(groupByIndex);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						if(element != null){
							addGroupBy(element);
						}
					}
					bufferGroupsByFieldIndex = null;
				}
				
				for(Integer i : getGroupBy()){
					descriptor.addColumn(inputs.get(0).getDescriptor(this).getStreamElements().get(i).clone(getName(), inputs.get(0).getName()));
				}
				
				
				if(checkIfOldModel){
					for(Aggregate agg : getAggregates()){
						if(agg.getStreamElementNum() != null){
							StreamElement element = null;
							try {
								element = inputs.get(0).getDescriptor(this).getStreamElements().get(agg.getStreamElementNum());
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							if(element != null){
								agg.setStreamElementName(element.getFullName());
								agg.setStreamElementNumberToNull();
							}
						}
					}
					checkIfOldModel = false;
				}
				
				
				for(Aggregate a : getAggregates()){
					descriptor.addColumn(getName(), "", a.getStreamElementName().split("::")[1],
							0,"java.lang.Double", inputs.get(0).getName(),false, "DOUBLE", "", false);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		
		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}
	
	
	public boolean isGroupBy(StreamElement e){
		
		try{
			for(String groupBy : groupsByFieldNames){
				if(groupBy.equals(e.getFullName())){
					return true;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return false;
	}
	
	
	
	public Integer isFunctionCode(StreamElement e){
		
		try{
			for(StreamElement field : descriptor.getStreamElements()){
				if (field.name.equals(e.name)){
					
					for(Aggregate a : aggregates){
						if (a.getStreamElementName().equals(e.getFullName())){
							return a.function;
						}
					}
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return null;
	}

	
	public void addAggregate(String elementIndex, String functionIndex) {
		Aggregate a = new Aggregate();
		a.setFunction(functionIndex);
		a.setStreamElementNum(elementIndex);
		
		addAggregate(a);
	}

	public void addAggregateNew(String elementName, String functionIndex){
		Aggregate a = new Aggregate();
		a.setFunction(functionIndex);
		a.setStreamElementName(elementName);
		
		addAggregate(a);
	}

	public void addAggregateFromDigester(String elementName, int functionIndex){
		Aggregate a = new Aggregate();
		a.setFunction(functionIndex);
		a.setStreamElementName(elementName);
		
		addAggregate(a);
	}

	
	/**
	 * add an Aggregate for 
	 * @param elementIndex : element from the StreamDescriptor Input
	 * @param functionIndex : funtion constant
	 */
	public void addAggregate(StreamElement element, Integer functionIndex) {
		if (functionIndex.equals(-1)){
			return;
		}
		
		Aggregate a = new Aggregate();
		a.setFunction(functionIndex);
		a.setStreamElementName(element.getFullName());
		
		addAggregate(a);
	}




	@Override
	public void removeInput(Transformation transfo) {
		super.removeInput(transfo);
		groupsByFieldNames = new ArrayList<String>();
		aggregates = new ArrayList<Aggregate>();
		fireChangedProperty();
	}
	
	public Transformation copy() {
		AggregateTransformation copy = new AggregateTransformation();
		
		copy.setDescription(description);
		copy.setName("copy of " + name);

		return copy;
	}




	
	public void swapGroupBy(Integer index1, Integer index2) {
		StreamElement element1 = null;
		StreamElement element2 = null;
		try {
			element1 = inputs.get(0).getDescriptor(this).getStreamElements().get(index1);
			element2 = inputs.get(0).getDescriptor(this).getStreamElements().get(index2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(element1 != null && element2 != null){
			int i1 = -1;
			int i2 = -1;
			
			for(int i=0; i<groupsByFieldNames.size(); i++){
				if(groupsByFieldNames.get(i).equals(element1.getFullName())){
					i1 = i;
				}
				
				if(groupsByFieldNames.get(i).equals(element2.getFullName())){
					i2 = i;
				}
			}
			
			if(i1 != -1 && i2 != -1){
				groupsByFieldNames.set(i1, element1.getFullName());
				groupsByFieldNames.set(i2, element2.getFullName());
			}
		}
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
	
		buf.append("\t Aggregations : \n");
		for(Aggregate a : getAggregates()){
			buf.append("\t- Perform " + Aggregate.FUNCTIONS[a.getFunction()] + " on field " + a.getStreamElementName() + "\n");
		}
		buf.append("\t Group By On  : \n");
		for( String  groupBy : groupsByFieldNames){
			buf.append("\t- " + groupBy + "\n");
		}
		return buf.toString();
	}

	public int getStreamElementPositionByName(String streamElementName) {
		try {
			for(int i=0; i<inputs.get(0).getDescriptor(this).getStreamElements().size(); i++){
				if(inputs.get(0).getDescriptor(this).getStreamElements().get(i).getFullName().equals(streamElementName)){
					return i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}




//	public Integer getAggregKey(Aggregate agg) {
//		try {
//			for(int i=0; i<descriptor.getStreamElements().size(); i++){
//				if(descriptor.getStreamElements().get(i).getFullName().equals(agg.getStreamElementName())){
//					return i;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return -1;
//	}

}
