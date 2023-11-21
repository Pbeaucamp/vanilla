package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemHier extends ItemDim implements IsSerializable{
	
	private List<ItemOlapMember> olapChilds = new ArrayList<ItemOlapMember>();
	private String allMember;
	
	public ItemHier(){		  
		super();
	}
	 
	public ItemHier(String name, String uname, String hierarchie, boolean isGeolocalisable){
		super(name, uname, hierarchie, isGeolocalisable);
	}

	public void setOlapChilds(List<ItemOlapMember> olapChilds) {
		this.olapChilds = olapChilds;
	}

	public List<ItemOlapMember> getOlapChilds() {
		return olapChilds;
	}
	
	public void addOlapChild(ItemOlapMember child){
		this.olapChilds.add(child);
	}

	public String getAllMember() {
		return allMember;
	}

	public void setAllMember(String allMember) {
		this.allMember = allMember;
	}
}
