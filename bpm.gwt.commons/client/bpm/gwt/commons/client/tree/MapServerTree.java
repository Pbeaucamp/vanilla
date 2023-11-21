package bpm.gwt.commons.client.tree;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.v2.CompositeWaitPanel;
import bpm.gwt.commons.client.custom.v2.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapServer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MapServerTree extends CompositeWaitPanel implements OpenHandler<TreeItem> {

	private static MapServerTreeUiBinder uiBinder = GWT.create(MapServerTreeUiBinder.class);

	interface MapServerTreeUiBinder extends UiBinder<Widget, MapServerTree> {
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Tree tree;
	
	private boolean canOnlySelectMapServer;

	public MapServerTree(boolean canOnlySelectMapServer) {
		initWidget(uiBinder.createAndBindUi(this));
		this.canOnlySelectMapServer = canOnlySelectMapServer;

		tree.addOpenHandler(this);

		addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						loadServers();
					}
				});
			}
		});
	}

	private void loadServers() {
		CommonService.Connect.getInstance().getMapServers(new GwtCallbackWrapper<List<MapServer>>(this, true, true) {

			@Override
			public void onSuccess(List<MapServer> result) {
				buildMapServers(result);
			}
		}.getAsyncCallback());
	}

	private void loadLayers(final MapTreeItem<MapServer> treeItem, final MapServer server) {
		CommonService.Connect.getInstance().loadLayers(server, new GwtCallbackWrapper<List<MapLayer>>(this, true, true, false) {

			@Override
			public void onSuccess(List<MapLayer> result) {
				server.setLayers(result);
				treeItem.rebuildItem(server, false);
			}
			
			@Override
			public void onFailure(Throwable t) {
				List<MapLayer> layers = new ArrayList<MapLayer>();
				layers.add(new MapLayer(LabelsConstants.lblCnst.UnableToGetLayers(), LabelsConstants.lblCnst.UnableToGetLayers(), null));
				
				server.setLayers(layers);
				treeItem.rebuildItem(server, true);
			}
		}.getAsyncCallback());
	}

	public void refresh() {
		loadServers();
	}

	public void buildMapServers(List<MapServer> items) {
		tree.clear();

		if (items != null) {
			for (MapServer mapItem : items) {
				MapTreeItem<MapServer> item = new MapTreeItem<MapServer>(new TreeObjectWidget<MapServer>(null, mapItem), canOnlySelectMapServer);
				tree.addItem(item);
			}
		}
	}
	
	public boolean isServerSelected() {
		return tree.getSelectedItem() != null && tree.getSelectedItem() != null && tree.getSelectedItem() instanceof MapTreeItem<?> && ((MapTreeItem<?>) tree.getSelectedItem()).getItem() instanceof MapServer;
	}

	public MapServer getSelectedMapServer() {
		if (tree.getSelectedItem() != null) {
			TreeItem treeItem = tree.getSelectedItem();
			if (treeItem != null && treeItem instanceof MapTreeItem<?>) {
				Object item = ((MapTreeItem<?>) treeItem).getItem();
				if (item instanceof MapServer) {
					return (MapServer) item;
				}
			}
		}

		return null;
	}

	public MapLayer getSelectedMapLayer() {
		if (tree.getSelectedItem() != null) {
			TreeItem treeItem = tree.getSelectedItem();
			if (treeItem != null && treeItem instanceof MapTreeItem<?>) {
				Object item = ((MapTreeItem<?>) treeItem).getItem();
				if (item instanceof MapLayer) {
					return (MapLayer) item;
				}
			}
		}

		return null;
	}
	
	public void addSelectionHandler(SelectionHandler<TreeItem> handler) {
		tree.addSelectionHandler(handler);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem treeItem = event.getTarget();
		if (treeItem != null && treeItem instanceof MapTreeItem<?>) {
			Object item = ((MapTreeItem<?>) treeItem).getItem();
			if (item instanceof MapServer) {
				MapTreeItem<MapServer> itemCmis = (MapTreeItem<MapServer>) treeItem;
				if (!itemCmis.isLoaded()) {
					loadLayers(itemCmis, (MapServer) item);
				}
			}
		}
	}
	
	@Override
	protected Panel getRelativeMainPanel() {
		return mainPanel;
	}
}
