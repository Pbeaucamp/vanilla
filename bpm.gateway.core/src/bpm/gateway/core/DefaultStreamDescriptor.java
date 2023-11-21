package bpm.gateway.core;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DefaultStreamDescriptor implements StreamDescriptor {

//	private List<String> columnNames = new ArrayList<String>();
//	private List<Integer> columnTypes = new ArrayList<Integer>();
//	private List<String> columnClasses = new ArrayList<String>();
//	private List<String> tableNames = new ArrayList<String>();
//	private List<String> transfoNames = new ArrayList<String>();
//	private List<String> originTransfo = new ArrayList<String>();
	
	
	private List<StreamElement> elements = new ArrayList<StreamElement>();
	public void addColumn(StreamElement col){
		elements.add(col);
	}
	
	public void addColumn(String transfoName, String tableName, String colName, int type, String colClass, String originName, boolean isNullable, String typeName, String defaultValue, boolean isPrimaryKey){
		StreamElement s = new StreamElement();
		s.className = colClass;
		s.name = colName;
		s.tableName = tableName;
		s.transfoName = transfoName;
		s.type = type;
		s.originTransfo = originName;
		s.isNullable = isNullable;
		s.defaultValue = defaultValue;
		s.typeName = typeName;
		s.isPrimaryKey = isPrimaryKey;
		
		elements.add(s);
//		transfoNames.add(transfoName);
//		tableNames.add(tableName);	
//		columnNames.add(colName);
//		columnTypes.add(type);
//		columnClasses.add(colClass);
//		originTransfo.add(originName);
	}
	
	public void removeColumn(int i){
//		transfoNames.remove(i);
//		tableNames.remove(i);
//		columnNames.remove(i);
//		columnTypes.remove(i);
//		columnClasses.remove(i);
//		originTransfo.remove(i);
		try{
			elements.remove(i);
		}catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		
	}
	
	public void removeColumns(List<StreamElement> toRemove){
		elements.removeAll(toRemove);
	}
	
	/**
	 * swap datas from one position to another 
	 * @param index1
	 * @param index2
	 * @throws Exception if one of parameters is out of bounds
	 */
	public void swapColumns(int index1, int index2) throws Exception{
		if (index1 >= getColumnCount() || index2 >= getColumnCount()){
			throw new Exception("Cannot swap to columns having indices out of bounds");
		}
		
		/*
		 * buffer the index1 values
		 */		
		
		String colNameBuf = elements.get(index1).name;
		String colClassBuf = elements.get(index1).className;
		Integer colTypeBuf = elements.get(index1).type;
		String tableBuf = elements.get(index1).tableName;
		String tranfoBuf = elements.get(index1).transfoName;
		
		/*
		 * swap index2 in index1
		 */
		elements.get(index1).name = elements.get(index2).name;
		elements.get(index1).className = elements.get(index2).className;
		elements.get(index1).type = elements.get(index2).type ;
		elements.get(index1).tableName = elements.get(index2).tableName;
		elements.get(index1).transfoName = elements.get(index2).transfoName ;
		
		/*
		 * buffer to index2
		 */
		
		elements.get(index2).name = colNameBuf;
		elements.get(index2).className = colClassBuf;
		elements.get(index2).type = colTypeBuf ;
		elements.get(index2).tableName = tableBuf;
		elements.get(index2).transfoName = tranfoBuf ;
	
	}
	
	public String getOriginName(int i ){
		return elements.get(i).originTransfo;
	}
	
	public String getTransformationName(int i){
		return elements.get(i).transfoName;
	}
	
	
	public String getTableName(int i){
		return elements.get(i).tableName;
	}
	
	public int getColumnCount() {
		return elements.size();
	}

	public String getColumnName(int i) {
		return elements.get(i).name;
	}


	public String getJavaClass(int i) {
		return elements.get(i).className;
	}



	public int getType(int i) {
		return elements.get(i).type;
	}

	public List<StreamElement> getStreamElements() {
		return elements;
	}

	public String getDefaultValue(int i) {
		return elements.get(i).defaultValue;
	}

	public String getTypeName(int i) {
		return elements.get(i).typeName;
	}

	public boolean isNullable(int i) {
		return elements.get(i).isNullable;
	}
	
	public boolean isPrimaryKey(int i) {
		return elements.get(i).isPrimaryKey;
	}

	public Integer getSize(int i) {
		return elements.get(i).size;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("fields");
		
		for(StreamElement o : elements){
			e.addElement("fieldName").setText(o.name);
			
		}
		return e;
	}

	public void removeColumn(StreamElement e) {
		elements.remove(e);
		
	}

	@Override
	public int getElementIndex(String name) {
		int i = 0;
		for(StreamElement elem : elements) {
			if(elem.name.equals(name)) {
				return i;
			}
			i++;
		}
		return -1;
	}

}
