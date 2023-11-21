package groupviewer.generator;

import groupviewer.CrossRefDataLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fexgenerator.generator.FlexGenerator;
import org.flexgenerator.elements.FlexProperty;
import org.flexgenerator.elements.Node;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class InpactFlexGenerator extends FlexGenerator {
	//private CrossRefDataLoader loader;
	private Map<Integer, Node>nodeList = new HashMap<Integer, Node>();
	public InpactFlexGenerator(){
		//this.setGraphTitle("Inpact View");
		super();
	}
	public InpactFlexGenerator(CrossRefDataLoader loader) {
		super();
		//this.loader = loader;
		//generateItems();
		//setItemExtraPropertys();
		//this.setGraphTitle("Inpact View");
		//this.setOrientation(RavisParameters.ORIENT_TOP_DOWN);
	}
	private void generateItems(Collection<RepositoryItem> directoryItemList) {
		//nodeList = new HashMap<Integer, Node>();
		for (RepositoryItem item : directoryItemList) {
			Node node = createNode(item);
			nodeList.put(item.getId(), node);
			addNode(node);
		}
	}
	
	private Node createNode (RepositoryItem item){
		String type = getKeyForType(item.getType(), item);
		Node node = new Node(item.getItemName(),item.getComment() ,type ,"16");
		node.setData(item);
		return node;
	}
	
	public void generateNeededItem(RepositoryItem root, List<RepositoryItem> needsList){
		setRootNode(createNode(root));
		generateItems(needsList);
		for (RepositoryItem dirit : needsList){
			Node child = nodeList.get(dirit.getId());
			if (dirit.getOwnerId() != null){
				Node parent = nodeList.get(dirit.getOwnerId());
				if (parent != null)
					child.addParentNode(parent);
			}
		}
	}
	public void generateDependItem(RepositoryItem root, List<RepositoryItem> dependsList){	
		setRootNode(createNode(root));
		generateItems(dependsList);
		for (RepositoryItem dirit : dependsList){
			Node child = nodeList.get(dirit.getId());
			if (dirit.getOwnerId() != null){
				Node parent = nodeList.get(dirit.getOwnerId());
				if (parent != null)
					child.addParentNode(parent);
			}
		}
		
	}
	/*public void generateAllItems(){
		Collection<DirectoryItem> diritem = loader.getFullItemList();
		generateItems(diritem);	
		for (Integer nodeId : nodeList.keySet()){
			DirectoryItem dirIt = loader.getItemByID(nodeId);
			Collection<DirectoryItem> depands = loader.getDepandsItemList(dirIt);
			Node parentNode = nodeList.get(nodeId);
			System.out.println("parent node : " + parentNode);
			if (depands != null){
				for (DirectoryItem dependDirIt : depands) {
					Node childNode = nodeList.get(dependDirIt.getItem().getId());
					System.out.println("has child node : "+ childNode);
					if (childNode != null)
						childNode.addParentNode(parentNode);
				}
			}
		}	
	}*/
	private String getKeyForType(int i, RepositoryItem item){
		switch(i){
		case IRepositoryApi.CUST_TYPE:
			switch(item.getSubtype()){
				case IRepositoryApi.XACTION_SUBTYPE:
					return "xaction";
				case IRepositoryApi.JASPER_REPORT_SUBTYPE:
					if (!item.isOn()){
						return "jasper_stop";
					}
					else{
						return "jasper_run";
					}	
				case IRepositoryApi.BIRT_REPORT_SUBTYPE:
					if (!item.isOn()){
						return "birt_stop";
					}
					else{
						return "birt_run";
					}
				case IRepositoryApi.ORBEON_XFORMS:
					return "orbeon_16";	
			}	
		case IRepositoryApi.FAR_TYPE:
			return "far";
		case IRepositoryApi.FASD_TYPE:
			if (!item.isOn()){
				return "fasd_stop";
			}
			else{
				return "fasd_run";
			}
		case IRepositoryApi.FAV_TYPE:
			return "fav";
		case IRepositoryApi.FD_TYPE:
			
			if (!item.isOn()){
				return "fd_deployed_stop";
			}
			else{
				return "fd_deployed_run";
			}

		case IRepositoryApi.FMDT_TYPE:
			if (!item.isOn()){
				return "fmdt_stop";
			}
			else{
				return "fmdt_run";
			}
		case IRepositoryApi.FWR_TYPE:
			if (!item.isOn()){
				return "fwr_stop";
			}
			else{
				return "fwr_run";
			}	
		case IRepositoryApi.GED_TYPE:
			return "ged";
		case IRepositoryApi.WKB_TYPE:
			return "wkb";
		case IRepositoryApi.FD_DICO_TYPE:
			return "dico";
		case IRepositoryApi.BIW_TYPE:
			return "biw";
		case IRepositoryApi.MAP_TYPE:
			return "md";
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			return "doc";
		case IRepositoryApi.GTW_TYPE:
			if (!item.isOn()){
				return "gtw_stop";
			}
			else{
				return "gtw_run";
			}
		}
		return "";
	}
	@Override
	public Map<Class<?>, List<FlexProperty>> createExtraAtrributes() {
		Map<Class<?>, List<FlexProperty>> list = new HashMap<Class<?>, List<FlexProperty>>();
		List<FlexProperty> usersProp = new ArrayList<FlexProperty>();
		//usersProp.add(new FlexProperty("delegator", "getDelegator"));
		usersProp.add(new FlexProperty("itemId", "getId"));//
		//usersProp.add(new FlexProperty("isDeportable", "getIsDeportable"));
		//usersProp.add(new FlexProperty("isOn", "getIsOn"));
		usersProp.add(new FlexProperty("modification", "getModification"));//
		//usersProp.add(new FlexProperty("modifiedBy", "getModifiedBy"));
		usersProp.add(new FlexProperty("modifier", "getModifier"));//
		usersProp.add(new FlexProperty("type", "getType"));//
		usersProp.add(new FlexProperty("creation", "getCreation"));//
		usersProp.add(new FlexProperty("creator", "getCreator"));//
		list.put(RepositoryItem.class, usersProp);
		return list; 		
	}
}
