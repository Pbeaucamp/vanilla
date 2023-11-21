package bpm.mdm.model.runtime.exception;

import bpm.mdm.model.runtime.Row;

/**
 * base exception when trying to perform an operation on a specific row
 * (creation, update, deletion)
 * @author ludo
 *
 */
public abstract class AbstractRowException extends Exception{
	public static enum OperationType{
		CREATE, UPDATE, DELETE;
	}
	private Row row;
	private OperationType type;
	public AbstractRowException(String message, Row row, OperationType type){
		super(message);
		this.row = row;
		this.type = type;
	}
	public OperationType getType(){
		return type;
	}
	
	public Row getRow(){
		return row;
	}
	
	public void setRow(Row row){
		this.row = row;
	}
}
