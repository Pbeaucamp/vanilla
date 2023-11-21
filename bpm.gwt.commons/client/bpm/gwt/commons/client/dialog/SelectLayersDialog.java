package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.v2.CompositeCellHelper;
import bpm.gwt.commons.client.custom.v2.DisplayButtonImageCell;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.tree.MapServerTree;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapLayerOption;
import bpm.vanilla.map.core.design.MapServer;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public class SelectLayersDialog extends AbstractDialogBox {

	private static SelectLayersDialogUiBinder uiBinder = GWT.create(SelectLayersDialogUiBinder.class);

	interface SelectLayersDialogUiBinder extends UiBinder<Widget, SelectLayersDialog> {
	}

	@UiField
	SimplePanel panelTree;
	
	@UiField
	LabelTextBox lblWMTS;

	@UiField
	GridPanel<MapLayer> grid;

	private MapServerTree mapTree;
	
	private MapServer server;
	private List<MapLayer> layers;
	
	private boolean isConfirm;

	public SelectLayersDialog(MapInformation mapInformation) {
		super(LabelsConstants.lblCnst.MapServers(), false, true);

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmationHandler, LabelsConstants.lblCnst.Cancel(), closeHandler);

		mapTree = new MapServerTree(false);
		panelTree.setWidget(mapTree);

		buildGrid();
		
		if (mapInformation != null) {
			setServer(mapInformation.getWmts());
			loadItems(mapInformation.getLayers());
		}
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				grid.setTopManually(30);
			}
		});
	}
	
	@UiHandler("btnClear")
	public void onClear(ClickEvent event) {
		setServer(null);
	}

	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		if (mapTree.isServerSelected()) {
			MapServer server = mapTree.getSelectedMapServer();
			setServer(server);
		}
		else {
			MapLayer layer = mapTree.getSelectedMapLayer();
			if (layer != null) {
				addLayer(layer);
			}
		}
	}

	@UiHandler("btnRemove")
	public void onRemove(ClickEvent event) {
		MapLayer layer = ((SingleSelectionModel<MapLayer>) grid.getSelectionModel()).getSelectedObject();
		if (layer != null) {
			removeLayer(layer);
		}
	}
	
	private void setServer(MapServer server) {
		this.server = server;
		lblWMTS.setText(server != null ? server.getName() : "OSM");
	}

	private void addLayer(MapLayer layer) {
		if (layers == null) {
			layers = new ArrayList<MapLayer>();
		}
		layers.add(layer);
		grid.loadItems(layers);
	}

	private void removeLayer(MapLayer layer) {
		layers.remove(layer);
		grid.loadItems(layers);
	}

	private void loadItems(List<MapLayer> items) {
		this.layers = items;
		
		grid.loadItems(items);
	}

	private void buildGrid() {
		grid.setActionPanel(LabelsConstants.lblCnst.Layers());
		
		final SingleSelectionModel<MapLayer> selectionModel = new SingleSelectionModel<MapLayer>();
		grid.setSelectionModel(selectionModel);

		TextCell cell = new TextCell();
		Column<MapLayer, String> colName = new Column<MapLayer, String>(cell) {
			@Override
			public String getValue(MapLayer object) {
				return object.getTitle();
			}
		};

		HasActionCell<MapLayer> displayCell = new HasActionCell<MapLayer>(new DisplayButtonImageCell(CommonImages.INSTANCE.checkbox_checked_24(), LabelsConstants.lblCnst.HideTheLayer(), CommonImages.INSTANCE.checkbox_unchecked_24(), LabelsConstants.lblCnst.DisplayTheLayer(), new Delegate<MapLayer>() {

			@Override
			public void execute(final MapLayer object) {
				object.setSelected(!object.isSelected());
				grid.refresh();
			}
		}));
		
		HasActionCell<MapLayer> optionsCell = new HasActionCell<MapLayer>(CommonImages.INSTANCE.viewer_tools(), LabelsConstants.lblCnst.Options(), new Delegate<MapLayer>() {

			@Override
			public void execute(final MapLayer object) {
				loadOptions(object);
			}
		});
		
		CompositeCellHelper<MapLayer> compCell = new CompositeCellHelper<MapLayer>(displayCell, optionsCell);
		Column<MapLayer, MapLayer> colAction = new Column<MapLayer, MapLayer>(compCell.getCell()) {
			@Override
			public MapLayer getValue(MapLayer object) {
				return object;
			}
		};

		grid.addColumn(LabelsConstants.lblCnst.LayerName(), colName, null, new Comparator<MapLayer>() {

			@Override
			public int compare(MapLayer o1, MapLayer o2) {
				return grid.compare(o1.getTitle(), o2.getTitle());
			}
		});
		grid.addColumn(LabelsConstants.lblCnst.Display(), colAction, "100px", null);
	}

	private void loadOptions(final MapLayer layer) {
		final MapLayerOptionsDialog dial = new MapLayerOptionsDialog(layer.getOptions());
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					MapLayerOption options = dial.getOptions();
					layer.setOptions(options);
				}
			}
		});
	}
	
	public MapInformation getMapInformation() {
		return new MapInformation(server, layers);
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	private ClickHandler confirmationHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			
			hide();
		}
	};

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
