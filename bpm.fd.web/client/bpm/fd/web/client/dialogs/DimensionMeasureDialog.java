package bpm.fd.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.component.CubeElement;
import bpm.fd.core.component.CubeElement.CubeElementType;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.properties.CubeViewOptionsProperties;
import bpm.fd.web.client.utils.DimensionMeasureImageCell;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DimensionMeasureDialog extends AbstractDialogBox {

	private static DimensionMeasureDialogUiBinder uiBinder = GWT.create(DimensionMeasureDialogUiBinder.class);

	interface DimensionMeasureDialogUiBinder extends UiBinder<Widget, DimensionMeasureDialog> {
	}
	
	interface MyStyle extends CssResource {
		String imgCell();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelContent;

	private ListDataProvider<CubeElement> dataProvider;
	private DimensionMeasureHeaderCheckbox header;

	private CubeViewOptionsProperties parent;
	
	public DimensionMeasureDialog(CubeViewOptionsProperties parent, List<CubeElement> elements) {
		super(Labels.lblCnst.DimensionMeasureSelection(), false, true);
		this.parent = parent;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		DataGrid<CubeElement> grid = buildGrid();
		panelContent.setWidget(grid);
		
		loadElements(elements);
	}
	
	private void loadElements(List<CubeElement> elements) {
		if (elements == null) {
			elements = new ArrayList<>();
		}
		
		dataProvider.setList(elements);
		header.setList(dataProvider.getList());
	}

	private DataGrid<CubeElement> buildGrid() {
		CheckboxCell cell = new CheckboxCell();
		Column<CubeElement, Boolean> isVisibleColumn = new Column<CubeElement, Boolean>(cell) {

			@Override
			public Boolean getValue(CubeElement object) {
				return object.isVisible();
			}
		};
		isVisibleColumn.setFieldUpdater(new FieldUpdater<CubeElement, Boolean>() {
			@Override
			public void update(int index, CubeElement object, Boolean value) {
				object.setVisible(value);
			}
		});

		DimensionMeasureImageCell imageCell = new DimensionMeasureImageCell(style.imgCell());
		Column<CubeElement, String> imageColumn = new Column<CubeElement, String>(imageCell) {

			@Override
			public String getValue(CubeElement object) {
				if (object.getType() == CubeElementType.DIMENSION) {
					return DimensionMeasureImageCell.DIMENSION;
				}
				else if (object.getType() == CubeElementType.MEASURE) {
					return DimensionMeasureImageCell.MEASURE;
				}
				return DimensionMeasureImageCell.DIMENSION;
			}
		};

		TextCell txtCell = new TextCell();
		Column<CubeElement, String> nameColumn = new Column<CubeElement, String>(txtCell) {

			@Override
			public String getValue(CubeElement object) {
				return object.getName();
			}
		};

		dataProvider = new ListDataProvider<CubeElement>(new ArrayList<CubeElement>());

		this.header = new DimensionMeasureHeaderCheckbox(dataProvider, new ArrayList<CubeElement>());
		Header<Boolean> headerCheck = new Header<Boolean>(header) {

			@Override
			public Boolean getValue() {
				return true;
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<CubeElement> dataGrid = new DataGrid<CubeElement>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(isVisibleColumn, headerCheck);
		dataGrid.setColumnWidth(isVisibleColumn, "40px");
		dataGrid.addColumn(imageColumn, Labels.lblCnst.Type());
		dataGrid.setColumnWidth(imageColumn, "70px");
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Column());
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoColumnAvailable()));
		
		dataProvider.addDataDisplay(dataGrid);

		return dataGrid;
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			DimensionMeasureDialog.this.hide();
			
			List<CubeElement> elements = new ArrayList<>(dataProvider.getList());
			parent.setElements(elements);
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			DimensionMeasureDialog.this.hide();
		}
	};
}
