package bpm.vanilla.platform.core.wrapper.servlets40.helper;

import java.util.List;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * simple class to avoid duplication code
 * Juste used has a holder
 * ere, promoted to full class since i need it
 * 	
 * @author ludo
 *
 */
public class Toolkit{
	
	private RepositoryItem directoryItem;
	private List<Parameter> itemParameters;
	private IRepositoryContext repContext;
	
	public Toolkit(RepositoryItem directoryItem,
			List<Parameter> itemParameters, IRepositoryContext repContext) {
		this.directoryItem = directoryItem;
		this.itemParameters = itemParameters;
		this.repContext = repContext;
	}
	/**
	 * @return the directoryItem
	 */
	public RepositoryItem getDirectoryItem() {
		return directoryItem;
	}
	/**
	 * @return the itemParameters
	 */
	public List<Parameter> getItemParameters() {
		return itemParameters;
	}
	/**
	 * @return the repContext
	 */
	public IRepositoryContext getRepContext() {
		return repContext;
	}
	
	
}
