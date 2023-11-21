package org.fasd.palo;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.xmla.ISchema;


public class PALOSchema implements ISchema {

	private List<ICube> cubes = new ArrayList<ICube>();
	private PALODataSourceConnection con = new PALODataSourceConnection();
	
	public PALODataSourceConnection getConnection() {
		return con;
	}

	public void setConnection(PALODataSourceConnection con) {
		this.con = con;
	}

	
	public void addCube(PALOCube cube){
		boolean exists = false;
		for(ICube c : cubes){
			if (((PALOCube)c).getName().equals(cube.getName())){
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
		return SchemaType.PALO;
	}

	@Override
	public FAModel getParent() {
		
		return null;
	}


}
