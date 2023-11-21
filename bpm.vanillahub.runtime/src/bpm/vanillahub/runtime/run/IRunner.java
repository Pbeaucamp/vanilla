package bpm.vanillahub.runtime.run;

import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Result;

public interface IRunner {

	public ResultActivity runActivity(Locale locale, ResultActivity parentResult) throws Exception;

	public List<Variable> getVariables();

	public List<Parameter> getParameters();
	
	public void addInfo(String info);
	
	public void addDebug(String debug);
	
	public void addWarning(String warning);
	
	public void addError(String error);
	
	public boolean isLoop();
	
	public void setLoop(boolean isLoop);
	
	public boolean isLoopEnd();
	
	public void setLoopEnd(boolean loopEnd);
	
	public void setResult(Result resultat);
	
	public ResultActivity getResult();
}
