package bpm.faweb.shared.infoscube;

import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemMes implements IsSerializable{
		
	private String name;
	private String uname;
		
	/**
	* This field is a Set that must always contain Strings.
	* 
	* @gwt.typeArgs <java.lang.String>
	*/
	public Set setOfStrings;

	/**
	* This field is a Map that must always contain Strings as its keys and
	* values.
	* 
	* @gwt.typeArgs <java.lang.String,java.lang.String>
	*/
	public Map mapOfStringToString;

	/**
	* Default Constructor. The Default Constructor's explicit declaration
	* is required for a serializable class.
	*/  
	public ItemMes(){ }
		 
	public ItemMes(String n, String u){
		this.name = n;
		this.uname = u;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
}
