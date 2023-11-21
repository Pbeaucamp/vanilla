package bpm.gateway.core;

import java.util.List;

import org.dom4j.Element;


/**
 * This interface describe the metadata of stream that a Transformation
 * will provide as output
 * @author LCA
 *
 */
public interface StreamDescriptor {
	/**
	 * 
	 * @return the number of columns
	 */
	public int getColumnCount();
	
//	/**
//	 * @return all ColumnNames
//	 */	
//	public List<String> getColumnNames();
	
	/**
	 * 
	 * @param i
	 * @return the column in pos i (i start at 1)
	 */
	public String getColumnName(int i);

	
	public String getJavaClass(int i);
	
	public int getType(int i);
	
	public Integer getSize(int i);
	
	public String getTransformationName(int i);
	
	public String getTableName(int i);
	
	
	public String getOriginName(int i );
	public boolean isNullable(int i );
	public String getDefaultValue(int i );
	public String getTypeName(int i );
	public boolean isPrimaryKey(int i) ;
	
	public List<StreamElement> getStreamElements();
	
	/**
	 * swap datas from one position to another 
	 * @param index1
	 * @param index2
	 * @throws Exception if one of parameters is out of bounds
	 */
	public void swapColumns(int index1, int index2) throws Exception;

	
	public Element getElement();
	
	/**
	 * Get a column index by its name
	 * @param name
	 * @return
	 */
	public int getElementIndex(String name);
}
