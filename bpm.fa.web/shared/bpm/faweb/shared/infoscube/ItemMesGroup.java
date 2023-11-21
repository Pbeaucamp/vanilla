package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemMesGroup implements IsSerializable{
		
	private String name;
	private String uname;
	
	private List<ItemMes> childs = new ArrayList<ItemMes>();

	/**
	* Default Constructor. The Default Constructor's explicit declaration
	* is required for a serializable class.
	*/  
	public ItemMesGroup(){ }
		 
	public ItemMesGroup(String n, String u){
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

	public void setChilds(List<ItemMes> childs) {
		this.childs = childs;
	}

	public List<ItemMes> getChilds() {
		return childs;
	}
	
	public void addChild(ItemMes measure){
		this.childs.add(measure);
	}
}
