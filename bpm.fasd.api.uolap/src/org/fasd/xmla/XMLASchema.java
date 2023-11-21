package org.fasd.xmla;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;



public class XMLASchema implements ISchema{
	private List<ICube> cubes = new ArrayList<ICube>();
	private XMLADataSourceConnection con = new XMLADataSourceConnection();
	private FAModel model;
	
	
	public XMLADataSourceConnection getConnection() {
		return con;
	}

	public void setConnection(XMLADataSourceConnection con) {
		this.con = con;
	}

	
	public void addCube(XMLACube cube){
		boolean exists = false;
		for(ICube c : cubes){
			if (((XMLACube)c).getName().equals(cube.getName())){
				exists = true;
				break;
			}
		}
		if (!exists){
			cubes.add(cube);
			cube.setParent(this);
		}
		
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <xmla>\n");
		buf.append(con.getFAXML());
		for(ICube cube : cubes){
			buf.append(cube.getFAXML());
		}
		buf.append("    </xmla>\n");
		
		
		return buf.toString();
	}

	public List<ICube> getICubes() {
		return cubes;
	}

	@Override
	public SchemaType getSchemaType() {
		return SchemaType.XMLA;
	}

	@Override
	public FAModel getParent() {
		return model;
	}

}
