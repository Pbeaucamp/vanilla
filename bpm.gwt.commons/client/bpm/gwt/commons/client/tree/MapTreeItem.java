package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.map.core.design.MapServer.TypeServer;

import com.google.gwt.user.client.ui.TreeItem;

public class MapTreeItem<T> extends TreeItem {

	private TreeObjectWidget<T> objectWidget;
	
	private boolean loaded = false;
	private boolean canOnlySelectServer;

	public MapTreeItem(TreeObjectWidget<T> objectWidget, boolean canOnlySelectServer) {
		super(objectWidget);
		this.canOnlySelectServer = canOnlySelectServer;
		
		buildItem(objectWidget);
	}
	
	public void buildItem(TreeObjectWidget<T> objectWidget) {
		this.objectWidget = objectWidget;
		
		removeItems();
		
		T item = objectWidget.getItem();
		if (item instanceof MapServer) {
			MapServer server = (MapServer) item;
			if (server.getLayers() != null && !server.getLayers().isEmpty()) {
				this.loaded = true;
				for (MapLayer object : server.getLayers()) {
					this.addItem(new MapTreeItem<MapLayer>(new TreeObjectWidget<MapLayer>(null, object), canOnlySelectServer));
				}
			}

			if (loaded) {
				setState(true);
			}
			else if (server.getType() != TypeServer.WMTS) {
				this.addItem(new WaitTreeItem());
			}
		}
	}
	
	public void rebuildItem(MapServer server, boolean error) {
		removeItems();
		
		this.loaded = true;
		for (MapLayer object : server.getLayers()) {
			this.addItem(new MapTreeItem<MapLayer>(new TreeObjectWidget<MapLayer>(null, object, error), canOnlySelectServer));
		}

		if (loaded) {
			setState(true);
		}
		else if (server.getType() != TypeServer.WMTS) {
			this.addItem(new WaitTreeItem());
		}
	}

	@Override
	public void setSelected(boolean selected) {
		T item = objectWidget.getItem();
		if (!canOnlySelectServer || item instanceof MapServer) {
			if (!selected) {
				this.objectWidget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
			}
			else {
				this.objectWidget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
			}
		}
		super.setSelected(selected);
	}

	public T getItem() {
		return objectWidget.getItem();
	}
	
	public boolean isLoaded() {
		return loaded;
	}
}
