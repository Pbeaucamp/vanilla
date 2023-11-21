package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;

public class DocumentSchema implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private int contractId;
	private String schema;
	
	public DocumentSchema() { }
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getContractId() {
		return contractId;
	}
	
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
}
