package org.fasd.inport.mondrian.beans;

public class RoleGrantBean {
	private String name;
	private SchemaGrantBean schema;
	
	public RoleGrantBean(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SchemaGrantBean getSchema() {
		return schema;
	}

	public void setSchema(SchemaGrantBean schema) {
		this.schema = schema;
	}
	
	
}
