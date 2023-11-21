package bpm.metadata.resource;

import bpm.metadata.layer.logical.IDataStreamElement;

public interface IFilter extends IResource {
	public IDataStreamElement getOrigin();
	
	/**
	 * 
	 * @return the SQL Code for the where clause(the where is not present only the condition)
	 */
	public String getSqlWhereClause();

	public void setName(String name);
}
