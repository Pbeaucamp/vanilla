package org.fasd.xmla;

import java.util.List;

import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;


public interface ISchema {

	public static enum SchemaType{
		XMLA, PALO, MONDRIAN, UNITED_OLAP
	}
	
	public List<ICube> getICubes();
	public String getFAXML();
	
	public SchemaType getSchemaType();
	public FAModel getParent();
}
