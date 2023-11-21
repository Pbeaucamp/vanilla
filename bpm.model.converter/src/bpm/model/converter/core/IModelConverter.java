package bpm.model.converter.core;


/**
 * an interface to convert Model Object of S class into other model object
 * @author ludo
 *
 * @param <S>
 */
public interface IModelConverter<S> {
	
	/**
	 * 
	 * @return the class of the Object returned by the method convert()
	 */
	public Class<?> getTargetClass();
	
	/**
	 * perform the conversion
	 * @param sourceObject
	 * @return
	 * @throws Exception
	 */
	public Object convert(S sourceObject) throws Exception;
	
	public String getName();
	
	public String getDescription();
	
	public void configure(Object configurationObject) throws Exception;
	
	
}