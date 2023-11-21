package bpm.metadata;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.logical.AbstractDataSource;

/**
 * This Exception is meant to store DataSources from a model taht cannot 
 * be built because none of their IConnection can establish a connection 
 * for some reason
 * 
 * @author ludo
 *
 */
public class BuilderException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8694912472478199397L;
	private List<AbstractDataSource> dataSources = new ArrayList<AbstractDataSource>();
	
	public BuilderException(List<AbstractDataSource> dataSources){
		if (dataSources != null){
			this.dataSources.addAll(dataSources);
		}
	}
	
	/**
	 * 
	 * @return all the DataSources that cannot be rebuilt from the physical DataSource(OLAPCube or SQLConnection)
	 */
	public List<AbstractDataSource> getNotRebuildableDataSources(){
		return dataSources;
	}

}
