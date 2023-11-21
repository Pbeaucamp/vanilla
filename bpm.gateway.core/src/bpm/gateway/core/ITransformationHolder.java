package bpm.gateway.core;

import java.util.List;

import org.dom4j.Element;


/**
 * base interface for special Transformation Holder : 
 * A transformation Holder hold multiples Transformations
 * 
 * It cannot have outputs.
 * 
 *  Each incoming Transformation may be associated to an Object to be used
 *  
 * @author ludo
 *
 */
public interface ITransformationHolder{

	public Transformation getTransformation(Object adapter) ;
	
	public void adaptInput(String inputName, Object adapter) throws Exception;
	
	
	/**
	 * 
	 * @return the position x
	 */
	public int getPositionX();
	
	/**
	 * 
	 * @return the position y
	 */
	public int getPositionY();
	
	/**
	 * 
	 * @return the transformation name
	 */
	public String getName();
	
	/**
	 * 
	 * @return the Transformation description
	 */
	public String getDescription();
	
	public void setPositionX(int x);
	public void setPositionY(int y);
	
	public Element getElement();

	public void setName(String name);

	public void setDescription(String description);
	
	public boolean addInput(Transformation input) throws Exception;
	public void removeInput(Transformation input);
	
	public List<Transformation> getInputs();
	
}
