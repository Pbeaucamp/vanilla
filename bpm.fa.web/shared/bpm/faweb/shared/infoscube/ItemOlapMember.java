package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemOlapMember extends ItemDim implements IsSerializable{
	
	private List<ItemOlapMember> olapMemberChilds = new ArrayList<ItemOlapMember>();
	
	public ItemOlapMember(){		  
		super();
	}
	 
	public ItemOlapMember(String name, String uname, String hierarchie, boolean isGeolocalisable){
		super(name, uname, hierarchie, isGeolocalisable);
	}

	public void setOlapMemberChilds(List<ItemOlapMember> olapMemberChilds) {
		this.olapMemberChilds = olapMemberChilds;
	}

	public List<ItemOlapMember> getOlapMemberChilds() {
		return olapMemberChilds;
	}
	
	public void addOlapMemberChild(ItemOlapMember child){
		this.olapMemberChilds.add(child);
	}
}
