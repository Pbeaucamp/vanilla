package bpm.gwt.commons.client.dialog;

import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.map.core.design.MapServer.TypeServer;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public class AddMapServerDialog extends AbstractDialogBox {

	private static AddMapServerDialogUiBinder uiBinder = GWT.create(AddMapServerDialogUiBinder.class);

	interface AddMapServerDialogUiBinder extends UiBinder<Widget, AddMapServerDialog> {
	}
	
	interface MyStyle extends CssResource {
		String maxHeight();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	LabelTextBox txtName, txtUrl;
	
	@UiField
	ListBoxWithButton<TypeServer> lstTypes;
	
	@UiField
	HTMLPanel mainPanel, panelArcgis;

	@UiField
	GridPanel<MapServer> grid;
	
	private SingleSelectionModel<MapServer> serverSelectionModel;
	
	private MapServer server;

	private boolean edit;
	private boolean isConfirm = false;

	public AddMapServerDialog() {
		this(null);
	}

	public AddMapServerDialog(MapServer server) {
		super(LabelsConstants.lblCnst.AddMapServer(), false, true);
		this.server = server;
		this.edit = server != null;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		lstTypes.setList(TypeServer.values());
		buildGrid();
		
		if (server != null) {
			txtName.setText(server.getName());
			txtUrl.setText(server.getUrl());
			
			lstTypes.setSelectedObject(server.getType());
			lstTypes.setEnabled(false);
		}
		updateUi();
	}
	
	private void updateUi() {
		TypeServer type = lstTypes.getSelectedObject();
		panelArcgis.setVisible(!edit && type != null && (type == TypeServer.ARCGIS || type == TypeServer.WMTS));
		if (type != null && (type == TypeServer.ARCGIS || type == TypeServer.WMTS)) {
			mainPanel.addStyleName(style.maxHeight());
		}
		else {
			mainPanel.removeStyleName(style.maxHeight());
		}
	}
	
	private void buildGrid() {
		TextCell cell = new TextCell();
		Column<MapServer, String> colName = new Column<MapServer, String>(cell) {
			@Override
			public String getValue(MapServer object) {
				return object.getName();
			}
		};
		Column<MapServer, String> colType = new Column<MapServer, String>(cell) {
			@Override
			public String getValue(MapServer object) {
				return object.getType().toString();
			}
		};
		Column<MapServer, String> colUrl = new Column<MapServer, String>(cell) {
			@Override
			public String getValue(MapServer object) {
				return object.getUrl();
			}
		};

		grid.addColumn(LabelsConstants.lblCnst.Name(), colName, "100px", new Comparator<MapServer>() {

			@Override
			public int compare(MapServer o1, MapServer o2) {
				return grid.compare(o1.getName(), o2.getName());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Type(), colType, "70px", new Comparator<MapServer>() {

			@Override
			public int compare(MapServer o1, MapServer o2) {
				return grid.compare(o1.getType().toString(), o2.getType().toString());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Url(), colUrl, "150px", new Comparator<MapServer>() {

			@Override
			public int compare(MapServer o1, MapServer o2) {
				return grid.compare(o1.getUrl(), o2.getUrl());
			}
		});
		
		serverSelectionModel = new SingleSelectionModel<>();
		grid.setSelectionModel(serverSelectionModel);
	}
	
	@UiHandler("lstTypes")
	public void onTypes(ChangeEvent event) {
		updateUi();
	}

	@UiHandler("btnLoadArcgis")
	public void onLoadArcgis(ClickEvent event) {
		TypeServer type = lstTypes.getSelectedObject();
		String url = txtUrl.getText();
		
		CommonService.Connect.getInstance().getArcgisServices(new MapServer("", url, type), new GwtCallbackWrapper<List<MapServer>>(this, true, true) {

			@Override
			public void onSuccess(List<MapServer> result) {
				grid.loadItems(result);
			}
		}.getAsyncCallback());
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	public MapServer getServer() {
		if (server == null) {
			server = new MapServer();
		}
		
		String name = txtName.getText();
		String url = txtUrl.getText();
		TypeServer type = lstTypes.getSelectedObject();

		server.setName(name);
		if (!edit && (type == TypeServer.ARCGIS || type == TypeServer.WMTS)) {
			MapServer arcgisServer = serverSelectionModel.getSelectedObject();
			server.setUrl(arcgisServer.getUrl());
			server.setType(arcgisServer.getType());
		}
		else {
			server.setUrl(url);
			server.setType(type);
		}
		
		return server;
	}

	private boolean isComplete() {
		TypeServer type = lstTypes.getSelectedObject();
		if (!edit && (type == TypeServer.ARCGIS || type == TypeServer.WMTS)) {
			MapServer arcgisServer = serverSelectionModel.getSelectedObject();
			return !txtName.getText().isEmpty() && arcgisServer != null;
		}
		else {
			return !txtName.getText().isEmpty() && !txtUrl.getText().isEmpty();
		}
	}

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (isComplete()) {
				isConfirm = true;
				
				hide();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
