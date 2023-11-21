package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;

public class SplitedField {
	private String spliter;
	private List<Integer> streamElementNumbers = new ArrayList<Integer>();
	
	private List<String> bufferedNames = null;
	
	private FieldSplitter owner;
	private StreamElement splited;
	private Integer splitedIndex;
	
	public Integer getSplitedIndex(){
		return splitedIndex;
	}
	
	public void addColumn(String colName){
		if (bufferedNames == null){
			bufferedNames = new ArrayList<String>();
		}
		
		bufferedNames.add(colName);
	}
	
	protected void destroyBuffer(){
		bufferedNames = null;
	}
	
	public List<String> getBufferedNames(){
		return bufferedNames;
	}
	
	public void setSplitedIndex(String index){
		try{
			splitedIndex = Integer.parseInt(index);
		}catch(NumberFormatException e){
			
		}
	}
	
	public void setSplited(StreamElement e){
		splitedIndex = null;
		splited = e;
	}
	
	public String getSpliter() {
		return spliter;
	}

	public void setSpliter(String spliter) {
		this.spliter = spliter;
	}

	public FieldSplitter getOwner() {
		return owner;
	}

	public void setOwner(FieldSplitter owner) {
		this.owner = owner;
	}
	
	
	public List<StreamElement> getStreamElements(){
		List<StreamElement> l = new ArrayList<StreamElement>();
		StreamDescriptor desc = null;
		
		try {
			desc = owner.getDescriptor(null);
		} catch (ServerException e1) {
			
			e1.printStackTrace();
		}
		
		List<Integer> toRemove = new ArrayList<Integer>();
		for(Integer i : streamElementNumbers){
			try{
				l.add(desc.getStreamElements().get(i));
			}catch(IndexOutOfBoundsException e){
				toRemove.add(i);
			}
			
		}
		
		streamElementNumbers.removeAll(toRemove);
		
		return l;
		

	}

	public StreamElement getSplited() {
		return splited;
	}

	public void addField(Integer i) {
		
		for(Integer ind : streamElementNumbers){
			if (ind.equals(i)){
				return;
			}
		}
		
		streamElementNumbers.add(i);
		
	}
	
	

	public Element getElement() {
		Element e = DocumentHelper.createElement("split");
		
		e.addElement("splitSequence").setText(spliter);
		
		
		int i;
		try {
			i = owner.getDescriptor(null).getStreamElements().indexOf(splited);
			e.addElement("streamElementIndex").setText(i + "");
		} catch (ServerException e1) {
			
		}
		
		
		
		for(StreamElement se : getStreamElements()){
			e.addElement("colName").setText(se.name);
		}
		
		return e;
	}

	public int removeColumn(StreamElement o) {
		int i =0;
		for (StreamElement e : getStreamElements()){
			if (e == o){
				streamElementNumbers.remove(i);
				int j = 0;
				for(Integer k : streamElementNumbers){
					if (k > i){
						streamElementNumbers.set(j, k - 1);
						
					}
					j++;
				}
				
				
			}
			else{
				addColumn(e.name);
			}
			i++;
		}
		return i;
		
	}
	
	/**
	 * 
	 * @param e
	 * @return the position of the given StreamElement in the field defined here 
	 */
	public int getPosInNewFields(StreamElement e){
		try {
			List<StreamElement> l = owner.getDescriptor(null).getStreamElements();
			int offset = owner.getNonSplitedStreamElements().size();
			
			return l.indexOf(e) - offset;
		} catch (ServerException e1) {
			return -1;
		}
		
	
	}

	protected boolean containsIndex(int indexField) {
		for(Integer i : streamElementNumbers){
			if ( indexField == i){
				return true;
			}
		}
		return false;
	}
}
