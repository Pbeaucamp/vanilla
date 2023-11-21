package bpm.gateway.core.transformations.vanilla;

import javax.imageio.ImageTranscoder;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.MappingException;

/**
 * a simple interface to define direct mapping defintion
 * between fields from Descriptor and its inputs
 * @author ludo
 *
 */
public interface IVanillaDirectMappable extends Transformation{
	/**
	 * if the current mapping does not exist already create it 
	 * @param input
	 * @param transfoColNum
	 * @param colNum
	 */
	public void createMapping(int transfoColNum, int colNum) throws MappingException;
	
	public void deleteMapping(int transfoColNum);
	
	/**
	 * 
	 * @param colNum
	 * @return the columnField number matching to the given input field position
	 */
	public Integer getMappingValueForInputNum(int colNum);
	
	
	/**
	 * 
	 * @param colNum
	 * @return the input's  columnField number matching to the given field position
	 */
	public Integer getMappingValueForThisNum(int colNum);

}
