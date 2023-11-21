package bpm.model.converter.core;

public interface IModelConverterFactory {

	public String getConverterName();
	
	public String getConverterDescription();
	
	public String getTargetClassName();
	
	
	public IModelConverter createConverter() throws Exception;
	
}
