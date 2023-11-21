package bpm.metadata.query;

import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.Prompt;


public interface IQuery {
	
	/**
	 * return the query (sql, MDX)
	 * @return
	 */
	//public String getQuery() throws MetaDataException;
	public List<Prompt> getPrompts();
	
	public String getXml();
	
	public List<IDataStreamElement> getSelect();
	
	public void setLimit(int limit);
	
	public void addPrompt(Prompt prompt);
	
	public void removePrompt(Prompt firstElement);
}
