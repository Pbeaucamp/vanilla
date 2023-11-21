package bpm.vanilla.server.commons.pool;

import java.util.Calendar;
import java.util.Date;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public abstract class PoolableModel<T> {
	private Date directoryItemDate;
	private Date loadingDate;
	
	private T model;
	private String xml;
	
//	private RepositorySockKey sockKey;
	private VanillaItemKey itemKey;
	
	private RepositoryItem directoryItem;
	
	public PoolableModel(RepositoryItem directoryItem, String xml, VanillaItemKey itemKey) throws Exception{
		setXml(xml);
		if (directoryItem != null){
			this.directoryItemDate = directoryItem.getDateModification();
		}
		
		this.directoryItem = directoryItem;
		this.itemKey = itemKey;
		this.loadingDate = Calendar.getInstance().getTime();
		buildModel();
	}
	
	public RepositoryItem getDirectoryItem(){
		return directoryItem;
	}
	
	/**
	 * must be only called from subclasses constructor
	 */
	protected void setXml(String xml){
		this.xml = xml;
	}
	
	protected String getXml(){
		return xml;
	}
	
//	/**
//	 * @return the sockKey
//	 */
//	public RepositorySockKey getSockKey() {
//		return sockKey;
//	}

	/**
	 * @return the itemKey
	 */
	public VanillaItemKey getItemKey() {
		return itemKey;
	}

	protected void setModel(T model){
		this.model = model;
	}
	
	abstract protected void buildModel() throws Exception;
	
	
	/**
	 * @return the directoryItemDate
	 */
	public Date getDirectoryItemDate() {
		return directoryItemDate;
	}

	/**
	 * @return the loadingDate
	 */
	public Date getLoadingDate() {
		return loadingDate;
	}

	/**
	 * @return the model
	 */
	public T getModel() {
		return model;
	}
	
	
	
	
	
}
