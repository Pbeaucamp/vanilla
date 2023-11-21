package bpm.faweb.client.tree;

import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.vanilla.platform.core.IRepositoryApi;

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class FaWebTreeItem extends TreeItem {

	private String name;
	private String uname;
	private ItemDim itemDim;
	private int id;
	private boolean isChartItem = false;
	
	private TreeParentDTO item;
	
	private boolean load = false;
	
	public FaWebTreeItem(Widget w, String uname){
		super(w);
		this.uname = uname;
	}
	
	public FaWebTreeItem(Widget w, ItemDim d){
		super(w);
		this.itemDim = d;
	}
	
	public FaWebTreeItem(Widget w, String uname, TreeParentDTO item) {
		super(w);
		this.uname = uname;
		this.id = item.getId();
		this.item = item;
	}
	
	public TreeParentDTO getItem() {
		return item;
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

	public ItemDim getItemDim() {
		return itemDim;
	}

	public void setItemDim(ItemDim itemDim) {
		this.itemDim = itemDim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setChartItem(boolean isChartItem) {
		this.isChartItem = isChartItem;
	}

	public boolean isChartItem() {
		return isChartItem;
	}

	public boolean isCube() {
		return item != null && item.getType() != null && item.getType().equals(IRepositoryApi.FASD);
	}
	
	public boolean isLoad() {
		return load;
	}
	
	public void setLoad(boolean load) {
		this.load = load;
	}
}

