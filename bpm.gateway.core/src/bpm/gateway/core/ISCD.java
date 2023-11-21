package bpm.gateway.core;



/**
 * A convenient Interface de design a SCD Transformation Regarless of its ype
 * (I, II) 
 * @author LCA
 *
 * (no more uses since only one class exists for its definition)
 */
public interface ISCD {

	/**
	 * create a mapping between an input and output Field
	 * This will generate a value in the target table for the current row at runtime
	 * 
	 * @param element
	 * @param value
	 */
	public void createFieldMapping(Integer input, Integer value);
	
	/**
	 * create a mapping between an input and output Field
	 * This will generate a value in the target table for the current row at runtime
	 * 
	 * @param element
	 * @param value
	 */
	public void createFieldMapping(StreamElement element, Integer value);
	
	/**
	 * create a mapping between an input and output Field
	 * This will generate a value in the target table for the current row at runtime
	 * 
	 * @param element
	 * @param value
	 */
	public void createFieldMapping(String inputColumn, String outputColumn);
	
	/**
	 * Create a mapping between an input and output Field for the lookup operation
	 *  
	 * @param element
	 * @param value
	 */
	public void createKeyMapping(Integer input, Integer output);
	
	/**
	 * Create a mapping between an input and output Field for the lookup operation
	 *  
	 * @param element
	 * @param value
	 */
	public void createKeyMapping(StreamElement element, Integer value);
	
	/**
	 * Create a mapping between an input and output Field for the lookup operation
	 *  
	 * @param element
	 * @param value
	 */
	public void createKeyMapping(String inputColumn, String outputColumn);
	
	public void createIgnoreField(String inputColumn, boolean ignore);
	
	public boolean isIgnoreField(int i);
	
	/**
	 * 
	 * @param i
	 * @return the Input Field number for the given target field number for the mapping
	 */
	public Integer getInputIndexFieldFortargetIndex(int i) ;
	
	/**
	 * 
	 * @param i
	 * @return the Input Field number for the given target field number for the lookup
	 */
	public Integer getInputIndexKeyFortargetIndex(int i);
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public Integer getTargetIndexFieldForInputIndex(int i) ;
	
	/**
	 * 
	 * @param i
	 * @return 
	 */
	public Integer getTargetIndexKeyForInputIndex(int i) ;
	
}
