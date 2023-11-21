package org.fasd.olap;

import java.util.List;

import org.fasd.datasource.IConnection;
import org.fasd.xmla.ISchema;

public interface ICube {
	public String getName();
	public IConnection getConnection();
	public String getFAXML();
	public String getDescription();
	
	public List<ICubeView> getCubeViews();
	
	
	public String getFAType();
	
	public String getFAProvider();
	
	public List<Drill> getDrills();
	
	public ISchema getParent();
}
