package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.center.IColor;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.infoscube.MapInfo;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapVanilla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapDialog extends AbstractDialogBox implements IColor {

	private static MapDialogUiBinder uiBinder = GWT.create(MapDialogUiBinder.class);

	interface MapDialogUiBinder extends UiBinder<Widget, MapDialog> {
	}

	@UiField
	VerticalPanel colorPanel;

	@UiField
	ListBox lstMap, lstDataset, lstMeasure, lstDimension;

	private MainPanel mainPanel;

	private String uname;
	private String element;

	private MapInfo mapInfo;
	private MapInformation mapInformation;
	private List<List<String>> colors = new ArrayList<List<String>>();

	private FlexTable flexTable;

	public MapDialog(MainPanel mainPanel, String uname, String element, MapInfo mapInfo, MapInformation mapInformation, List<List<String>> colors) {
		super(FreeAnalysisWeb.LBL.DialogChoiceMapOption(), false, true);
		this.mainPanel = mainPanel;
		this.uname = uname;
		this.element = element;
		this.mapInfo = mapInfo;
		this.mapInformation = mapInformation;
		this.colors = colors != null ? colors : new ArrayList<List<String>>();

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		loadMaps();
		setTableColorPart(colorPanel);
	}

	private void loadMaps() {
		boolean first = true;
		for (MapVanilla map : mapInfo.getMaps()) {
			lstMap.addItem(map.getName(), map.getId() + "");
			if (first) {
				first = false;
				try {
					for (MapDataSet ds : map.getDataSetList()) {
						lstDataset.addItem(ds.getName(), ds.getId() + "");
					}
				} catch(Exception e) {
//					e.printStackTrace();
				}
			}
		}
		for (String mes : mapInfo.getMeasures()) {
			lstMeasure.addItem(mes, mes);
		}
		for (String dim : mapInfo.getDimensions()) {
			lstDimension.addItem(dim, dim);
		}
	}

	@UiHandler("lstMap")
	public void onMapChange(ChangeEvent event) {
		int id = Integer.parseInt(lstMap.getValue(lstMap.getSelectedIndex()));
		
		MapVanilla map = null;
		for (MapVanilla m : mapInfo.getMaps()) {
			if (m.getId() == id) {
				map = m;
				break;
			}
		}
		lstDataset.clear();
		try {
			for (MapDataSet ds : map.getDataSetList()) {
				lstDataset.addItem(ds.getName(), ds.getId() + "");
			}
		} catch(Exception e) {
		}
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			String selectedMeasure = lstMeasure.getValue(lstMeasure.getSelectedIndex());
			String selectedDimension = lstDimension.getValue(lstDimension.getSelectedIndex());
			int datasetId = Integer.parseInt(lstDataset.getValue(lstDataset.getSelectedIndex()));
			
			MapOptions mapOptions = new MapOptions(uname, element, datasetId, selectedMeasure, selectedDimension, colors, mapInformation);
			mainPanel.getDisplayPanel().loadMap(mapInfo, mapOptions);
			
			MapDialog.this.hide();
		}
	};

	private void setTableColorPart(VerticalPanel parent) {
		// Create a Flex Table
		flexTable = new FlexTable();
		flexTable.addStyleName("gwt-colorTable");
		flexTable.setWidth("600px");

		// Add a button that will add more rows to the table
		Image icone_add_color = new Image(FaWebImage.INSTANCE.add());
		PushButton addRowButton = new PushButton(icone_add_color, new ClickHandler() {
			public void onClick(ClickEvent event) {
				ColorDialog dial = new ColorDialog(MapDialog.this);
				dial.show();
				dial.setPopupPosition(-event.getX(), -event.getY());
			}
		});
		addRowButton.setStyleName("gwt-rowButton");

		Image icone_del_color = new Image(FaWebImage.INSTANCE.del());
		PushButton removeRowButton = new PushButton(icone_del_color, new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeRow(flexTable);
			}
		});
		removeRowButton.setStyleName("gwt-rowButton");

//		Image icone_refresh_color = new Image(FaWebImage.INSTANCE.Refresh_16());
//		PushButton refreshColorButton = new PushButton(icone_refresh_color, new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				refreshColor(flexTable);
//			}
//		});
//		refreshColorButton.setStyleName("gwt-rowButton");

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName("cw-FlexTable-buttonPanel");
		buttonPanel.add(addRowButton);
		buttonPanel.add(removeRowButton);
		// buttonPanel.add(refreshColorButton);

		parent.add(buttonPanel);
		parent.add(flexTable);

		for (List<String> color : colors) {
			addRow(color, false);
		}
	}

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			MapDialog.this.hide();
		}
	};

//	private void refreshColor(FlexTable flexTable) {
//		int numRows = flexTable.getRowCount();
//		while (numRows >= 1) {
//			flexTable.removeRow(numRows - 1);
//			colors.remove(numRows - 1);
//			numRows--;
//		}
//		for (List<String> color : colors) {
//			addRow(color, false);
//		}
//	}

	/**
	 * Remove a row from the flex table.
	 */
	private void removeRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		if (numRows >= 1) {
			flexTable.removeRow(numRows - 1);
			colors.remove(numRows - 1);
		}
	}

	@Override
	public void addRow(List<String> colorTemp, boolean addColorToList) {
		if (addColorToList) {
			colors.add(colorTemp);
		}

		int numRows = flexTable.getRowCount();
		flexTable.setWidget(numRows, 0, new HTML(colorTemp.get(0)));
		flexTable.setWidget(numRows, 1, new HTML(colorTemp.get(1)));
		flexTable.setWidget(numRows, 2, new HTML(colorTemp.get(2)));
		flexTable.setWidget(numRows, 3, new HTML(colorTemp.get(3)));
	}

}
