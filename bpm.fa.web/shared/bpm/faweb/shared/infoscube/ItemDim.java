package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemDim  implements IsSerializable, java.lang.Comparable<ItemDim>{
	
	private String name;
	private String uname;
	private String hiera;
	public boolean isGeolocalisable;
	private boolean isDate;
	
	private List<ItemHier> childs = new ArrayList<ItemHier>();
	
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
	 public ItemDim(){		  
	  }
	 
	 public ItemDim(String name, String uname, String hierarchie, boolean isGeolocalisable /*, int d*/){
		 this.name = name;
		 this.uname = uname;
		 this.hiera = hierarchie;
		 this.isGeolocalisable = isGeolocalisable;
//		 this.depth = d;
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

//	public int getDepth() {
//		return depth;
//	}
//
//	public void setDepth(int depth) {
//		this.depth = depth;
//	}

	public String getHiera() {
		return hiera;
	}

	public void setHiera(String hiera) {
		this.hiera = hiera;
	}

	public boolean isGeolocalisable() {
		return isGeolocalisable;
	}

	public void setGeolocalisable(boolean isGeolocalisable) {
		this.isGeolocalisable = isGeolocalisable;
	}

	public int compareTo(ItemDim it) {
		String n = it.getName();
		String curr = this.getName();
		return curr.compareTo(n);
	}

	public void setChilds(List<ItemHier> childs) {
		this.childs = childs;
	}

	public List<ItemHier> getChilds() {
		return childs;
	}
	
	public void addChild(ItemHier itemHier){
		this.childs.add(itemHier);
	}
	
	public ItemHier getChild(int childIndex){
		return childs.get(childIndex);
	}

	public void setDate(boolean isDate) {
		this.isDate = isDate;
	}

	public boolean isDate() {
		return isDate;
	}
}
