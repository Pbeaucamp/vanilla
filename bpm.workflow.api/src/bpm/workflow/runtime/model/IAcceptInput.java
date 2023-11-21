package bpm.workflow.runtime.model;

import java.util.List;

public interface IAcceptInput {
	
	public void addInput(String input);
	
	public void removeInput(String input);
	
	public void setName(String name);
	
	public String getName();

	public List<String> getInputs();
	
	public void setInputs(List<String> inputs);
	
	public String getPhrase();
	
	
}
