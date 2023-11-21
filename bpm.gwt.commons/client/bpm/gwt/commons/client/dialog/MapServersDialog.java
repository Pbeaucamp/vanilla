package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.MapServerTree;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.map.core.design.IMapDefinitionService.ManageAction;
import bpm.vanilla.map.core.design.MapServer.TypeServer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MapServersDialog extends AbstractDialogBox implements SelectionHandler<TreeItem> {

	private static MapServersDialogUiBinder uiBinder = GWT.create(MapServersDialogUiBinder.class);

	interface MapServersDialogUiBinder extends UiBinder<Widget, MapServersDialog> {
	}
	
	@UiField
	Image btnAdd, btnEdit, btnRemove;
	
	@UiField
	SimplePanel panelTree;
	
	private MapServerTree mapTree;

	public MapServersDialog() {
		super(LabelsConstants.lblCnst.MapServers(), false, true);

		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		mapTree = new MapServerTree(true);
		panelTree.setWidget(mapTree);
		
		mapTree.addSelectionHandler(this);
	}

	private void updateUi(MapServer server) {
		btnEdit.setVisible(server != null);
		btnRemove.setVisible(server != null);
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		MapServer server = mapTree.getSelectedMapServer();
		updateUi(server);
	}
	
	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		final AddMapServerDialog dial = new AddMapServerDialog();
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					MapServer server = dial.getServer();
					manageServer(server, ManageAction.ADD);
				}
			}
		});
	}
	
	@UiHandler("btnEdit")
	public void onEdit(ClickEvent event) {
		MapServer server = mapTree.getSelectedMapServer();
		if (server == null) {
			return;
		}
		
		final AddMapServerDialog dial = new AddMapServerDialog(server);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					MapServer server = dial.getServer();
					manageServer(server, ManageAction.EDIT);
				}
			}
		});
	}
	
	@UiHandler("btnRemove")
	public void onRemove(ClickEvent event) {
		MapServer server = mapTree.getSelectedMapServer();
		if (server == null) {
			return;
		}
		
		manageServer(server, ManageAction.REMOVE);
	}

	private void manageServer(MapServer server, ManageAction action) {
		CommonService.Connect.getInstance().manageMapServer(server, action, new GwtCallbackWrapper<MapServer>(this, true, true) {

			@Override
			public void onSuccess(MapServer result) {
				mapTree.refresh();
			}
		}.getAsyncCallback());	
	}
	
	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
