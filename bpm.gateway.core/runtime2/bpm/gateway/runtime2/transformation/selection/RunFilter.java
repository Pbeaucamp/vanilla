package bpm.gateway.runtime2.transformation.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.utils.Condition;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.tools.StringParser;

public class RunFilter extends RuntimeStep{

	private boolean isExclusive = true;
	private HashMap<RuntimeStep, List<Condition>> conditionsByOutput = new HashMap<RuntimeStep, List<Condition>>(); 
	private RuntimeStep trash;
		
	public RunFilter(Filter transformation, int bufferSize) {
		super(null, transformation, bufferSize);

	}

	@Override
	public void init(Object adapter) throws Exception {
		Filter filter = (Filter)getTransformation();
		isExclusive = filter.isExclusive();
		
		isInited = true;
		
		
		if (filter.getTrashTransformation() != null){
			for(RuntimeStep s : getOutputs()){
				if (s.getTransformation() == filter.getTrashTransformation()){
					trash = s;
					break;
				}
			}
		}
		
		
		for(Condition c : filter.getConditions()){
			for(RuntimeStep s : getOutputs()){
				if (s.getTransformation() == c.getOutput()){
					if (conditionsByOutput.get(s) == null){
						conditionsByOutput.put(s, new ArrayList<Condition>());
						
					}
					conditionsByOutput.get(s).add(c);
					break;
				}
			}
		}
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
				
		Row row = readRow();
		
		
		Boolean validated = null;
		List<RuntimeStep> conditionToWrite = new ArrayList<RuntimeStep>();
		
		for(RuntimeStep rs : conditionsByOutput.keySet()){
			Condition prev = null;
			for(Condition c : conditionsByOutput.get(rs)){
				if (prev != null && prev.getLogical() != 0){
					switch(prev.getLogical()){
					case Condition.AND:
						if (validated == null){
							validated = true;
						}
						validated = validated && isValidated(c, row); 
						break;
					case Condition.OR:
						if (validated == null){
							validated = false;
						}
						validated = validated || isValidated(c, row);
						break;
					}
				}
				else{
					validated = isValidated(c, row);
				}
				prev = c;
			}
			if (validated){
				
				conditionToWrite.add(rs);

			}
			if (isExclusive && validated){
				//XXX : wont work if multiple condition on the same column
				break;
			}
			
			
			
		}
		
		
		if (conditionToWrite.isEmpty()){
			writeRowOnDefault(row);
		}
		else{
			writeRow(row, conditionToWrite);
		}
		
	}
	
	/**
	 * write the row for the given Steps
	 * @param row
	 * @param conditions
	 */
	private void writeRow(Row row, List<RuntimeStep> conditions) throws Exception{
		for(RuntimeStep rs : conditions){
			rs.insertRow(row, this);
		}
		writedRows++;
	}
	
	/**
	 * write the given in the tarsh output if it is defined
	 * @param row
	 * @throws InterruptedException
	 */
	private void writeRowOnDefault(Row row) throws InterruptedException{
		if (trash  == null){
			return;
		}

		trash.insertRow(row, this);
		writedRows++;
	}
	

	@Override
	public void releaseResources() {
	
		info("Resources released");
		
	}

	
	private boolean isValidated(Condition condition, Row row) {
		
		int colNumber = ((Filter)getTransformation()).getColumnNumberFromName(condition.getStreamElementName());
		DocumentGateway doc = getTransformation().getDocument();
		
		Object value = row.get(colNumber);
		
		try{
			switch(condition.getOperatorConstant()){
		
		case Condition.NULL:
			
			return value == null;
			
		case Condition.IN:
			
			if (value == null){
				for (String s : condition.getValue().split("]")){
					return s.equals("null");
				}
			}
			for (String s : condition.getValue().split("]")){
				int compare = ((Comparable)value).compareTo(convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, s), value.getClass().getName()));

				if (compare == 0){
					return true;
				}
			}
			return false;
				
		case Condition.CONTAINS:
			if (value == null){
				return false;
			}
			
			return value.toString().contains(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()));
			
		case Condition.ENDSWIDTH:
			if (value == null){
				return false;
			}
			
			return value.toString().endsWith(getTransformation().getDocument().getStringParser().getValue( doc, condition.getValue()));
			
		case Condition.STARTSWIDTH:
			
			if (value == null){
				return false;
			}
			
			return value.toString().startsWith(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()));
			
		case Condition.DIFFERENT:
			
			if (value == null 
				&& 
				( getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()) == null || getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()).equalsIgnoreCase("null"))){
				return ! (value == null);
			}
			
			if (value instanceof Comparable){
				int compare = ((Comparable)value).compareTo(convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()), /*transfo.getDescriptor().getJavaClass(colNumber)*/value.getClass().getName()));

				return compare != 0 ; 
			}
			
			return !value.equals(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()));
			

		case Condition.EQUAL:
			
			if (value == null){ 
				return ( getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()) == null || getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()).equalsIgnoreCase("null"));
			}
			
			if (value instanceof Comparable){
				int compare = ((Comparable)value).compareTo(convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()), /*transfo.getDescriptor().getJavaClass(colNumber)*/value.getClass().getName()));
				
//				System.out.println("number " + conditionNumber + " " + value.toString() + ">" + condition.getValue() + "=" + (compare == 0 || compare == 1));
				
				return compare == 0 ; 
			}
			return value.equals(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()));
			
			
		case Condition.GREATER_EQ_THAN:
			
			if (value instanceof Comparable){
				int compare = ((Comparable)value).compareTo(convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()), /*transfo.getDescriptor().getJavaClass(colNumber)*/value.getClass().getName()));
				
//				System.out.println("number " + conditionNumber + " " + value.toString() + ">" + condition.getValue() + "=" + (compare == 0 || compare == 1));
				
				return compare == 0 || compare == 1; 
			}
				
		

		case Condition.GREATER_THAN:
			
			if (value instanceof Comparable){
				Object o = convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()), 
						//transfo.getDescriptor().getJavaClass(colNumber)
						value.getClass().getName()
						);
				int compare = ((Comparable)value).compareTo(o);
//				System.out.println("number " + conditionNumber + " " +value.toString() + ">=" + condition.getValue() + "=" + (compare == 1));
				return compare == 1; 
			}
			
		case Condition.LESSER_EQ_THAN:

			if (value instanceof Comparable){
				int compare = ((Comparable)value).compareTo(convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()), 
						//transfo.getDescriptor().getJavaClass(colNumber)
						value.getClass().getName()
						));
				return compare == 0 || compare == -1; 
			}
			
		case Condition.LESSER_THAN:
			if (value instanceof Comparable){
				int compare = ((Comparable)value).compareTo(convertStringToNumber(getTransformation().getDocument().getStringParser().getValue(doc, condition.getValue()), 
//						transfo.getDescriptor().getJavaClass(colNumber)
						value.getClass().getName()
						));
//				System.out.println("number " + conditionNumber + " " +value.toString() + "<" + condition.getValue() + "=" + (compare == -1));
				return compare == -1; 
			}
			
		
		}

		}catch(Exception e){
			e.printStackTrace();
			error(" Error when comparing  " + value + " " + condition.dump(), e);
			
			return false;
		}
		
		return false;
	}
	
	private Comparable convertStringToNumber(String value, String  className) throws Exception{
		
		Class  c = Class.forName(className);
		
		Comparable n = null;
		
		if (java.util.Date.class.isAssignableFrom(c)){
			
		}else{
			try {
				n =  (Comparable) c.getConstructor(new Class[]{String.class}).newInstance(new Object[]{value});
			}catch(Exception e){
				
				try{
					c = Integer.class; 
					n =  (Comparable) c.getConstructor(new Class[]{String.class}).newInstance(new Object[]{value});
				}catch(Exception ex){
					error(" conversion error " , ex);
				}
				
				
			}
		}
		     
		
		if (n == null)
			throw new Exception("unable to convert to number");
		
		return n;
	
	}
}
