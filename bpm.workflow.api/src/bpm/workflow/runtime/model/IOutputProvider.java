package bpm.workflow.runtime.model;

public interface IOutputProvider extends IOutput {
	
	public String getPathToStore();
	
	public void setPathToStore(String pathToStore);
	
	
	//XXX
	/**
	 * use only if the output is a unique file (see ConcatPDF)
	 * @return
	 */
	public String getOutputName();

	/**
	 * use only if the output is a unique file (see ConcatPDF)
	 * @param outputName
	 */
	public void setOutputName(String outputName);


}
