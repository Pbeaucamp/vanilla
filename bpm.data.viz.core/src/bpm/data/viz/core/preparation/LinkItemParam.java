package bpm.data.viz.core.preparation;

import java.io.Serializable;

public class LinkItemParam implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String paramName;
	private String dataPrepColumnName;
	
	public LinkItemParam() {
	}
	
	public LinkItemParam(String paramName) {
		this.paramName = paramName;
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public String getDataPrepColumnName() {
		return dataPrepColumnName;
	}
	
	public void setDataPrepColumnName(String dataPrepColumnName) {
		this.dataPrepColumnName = dataPrepColumnName;
	}
}
