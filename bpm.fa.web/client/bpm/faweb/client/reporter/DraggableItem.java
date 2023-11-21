package bpm.faweb.client.reporter;

import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.gwt.dnd.client.PickupDragController;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.shared.infoscube.ItemDim;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

public class DraggableItem extends HTML {

	private String name;
	private String uname;
	private String hiera;
	private boolean isDimension;

	private TreeItem treeItem;
	private PickupDragController dragCtrl;

	public DraggableItem(String name, String uname, String hiera, TreeItem treeItem, boolean isDimension, PickupDragController dragCtrl) {
		super(name);
		this.name = name;
		this.uname = uname;
		this.hiera = hiera;
		this.treeItem = treeItem;
		this.isDimension = isDimension;
		this.dragCtrl = dragCtrl;

		if (uname.contains("[Measures]")) {
			this.addStyleName("measureGridItem");
		}
		else {
			this.addStyleName("draggableGridItem");
		}

		this.addStyleName("gridItemBorder");
		this.setHeight("30px");
		this.setWidth("100px");

		DOM.setStyleAttribute(this.getElement(), "maxWidth", "100px");

		sinkEvents(Event.ONDBLCLICK);
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

	public void setHiera(String hiera) {
		this.hiera = hiera;
	}

	public String getHiera() {
		return hiera;
	}

	public DraggableItem cloneItem() {
		DraggableItem item = new DraggableItem(name, uname, hiera, treeItem, isDimension, dragCtrl);
		return item;
	}

	public TreeItem getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (DOM.eventGetType(event)) {
		case Event.ONDBLCLICK:
			if (!(treeItem.getChildCount() > 0)) {
				isChildsExists();

				FaWebService.Connect.getInstance().getReporterSubItems(MainPanel.getInstance().getKeySession(), DraggableItem.this.getUname(), DraggableItem.this.getHiera(), new AsyncCallback<List<ItemDim>>() {
					public void onSuccess(List<ItemDim> result) {
						for (ItemDim dim : result) {
							TreeItem tItem = new TreeItem();

							DraggableItem dgIt = new DraggableItem(dim.getName(), dim.getUname(), dim.getHiera(), tItem, true, dragCtrl);
							dragCtrl.makeDraggable(dgIt);

							tItem.setWidget(dgIt);

							getTreeItem().addItem(tItem);
						}
					}

					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
			}
			break;
		}
	}

	private boolean isChildsExists() {
		List<FaWebTreeItem> items = MainPanel.getInstance().getNavigationPanel().getDimensionPanel().getExtremite();
		for (FaWebTreeItem item : items) {
			HorizontalPanel hp = (HorizontalPanel) item.getWidget();
			HTML img = (HTML) hp.getWidget(1);
			DraggableTreeItem dgti = (DraggableTreeItem) hp.getWidget(2);
			if (dgti.getUname().equals(uname)) {
				NativeEvent ne = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
				ClickEvent.fireNativeEvent(ne, img);
			}
		}
		return false;
	}

	public boolean isDimension() {
		return isDimension;
	}

}
