package bpm.faweb.client.projection.data;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A class used to fill the editable data table
 * Represent a value
 * @author Marc Lanquetin
 *
 */
public class DataField implements IsSerializable {

	private String uname;
	private String name;
	private String value;
	
	public DataField(){}
	
	public DataField(String name, String value, String uname) {
		this.name = name;
		this.uname = uname;
		this.value = value;
	}
	
	public String getUname() {
		return uname;
	}
	
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isEditable() {
		if(uname != null && uname.startsWith("[Measures]")) {
			return true;
		}
		return false;
	}
}
