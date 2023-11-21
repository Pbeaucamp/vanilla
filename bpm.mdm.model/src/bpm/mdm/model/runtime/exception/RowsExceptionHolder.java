package bpm.mdm.model.runtime.exception;

import java.util.List;

/**
 * exception meant to store all failed rows on a IEntityStore flush
 * it can be usefull to get the failed rows
 * @author ludo
 *
 */
public class RowsExceptionHolder extends Exception{

	private List<AbstractRowException> errors;
	public RowsExceptionHolder(List<AbstractRowException> errors){
		this.errors = errors;
	}
	
	public List<AbstractRowException> getErrors() {
		return errors;
	}
}
