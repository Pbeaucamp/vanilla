package bpm.metadata.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.CustomListBox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.metadata.web.client.I18N.Labels;
import bpm.metadata.web.client.dialogs.ResourceDialog;
import bpm.metadata.web.client.dialogs.ResourceDialog.TypeResource;
import bpm.metadata.web.client.popup.ResourceTypePopup;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class ColumnPropertiesPanel extends Composite implements Handler {

	private static ColumnPropertiesPanelUiBinder uiBinder = GWT.create(ColumnPropertiesPanelUiBinder.class);

	interface ColumnPropertiesPanelUiBinder extends UiBinder<Widget, ColumnPropertiesPanel> {
	}
	
	interface MyStyle extends CssResource {
		String panelGrid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	RadioButton btnUndefined, btnDimension, btnMeasure, btnProperty;
	
	@UiField
	CustomListBox lstMeasureBehavior;

	@UiField
	Image btnAdd, btnEdit, btnRemove;
	
	@UiField
	SimplePanel panelResources;
	
	private Datasource datasource;
	private DatabaseColumn column;
	
	private CustomDatagrid<MetadataResource> gridResources;
	private SingleSelectionModel<MetadataResource> selectionResource;
	
	private List<MetadataResource> resources;
	
	public ColumnPropertiesPanel(Datasource datasource, DatabaseColumn column, List<MetadataResource> resources) {
		initWidget(uiBinder.createAndBindUi(this));
		this.datasource = datasource;
		this.column = column;

		txtName.setText(column.getName());
		txtName.setEnabled(false);
		
		lstMeasureBehavior.addItem(Labels.lblCnst.Sum(), String.valueOf(DatabaseColumn.BEHAVIOR_SUM));
		lstMeasureBehavior.addItem(Labels.lblCnst.Count(), String.valueOf(DatabaseColumn.BEHAVIOR_COUNT));
		lstMeasureBehavior.addItem(Labels.lblCnst.Average(), String.valueOf(DatabaseColumn.BEHAVIOR_AVG));
		lstMeasureBehavior.addItem(Labels.lblCnst.Min(), String.valueOf(DatabaseColumn.BEHAVIOR_MIN));
		lstMeasureBehavior.addItem(Labels.lblCnst.Max(), String.valueOf(DatabaseColumn.BEHAVIOR_MAX));

		this.resources = resources;
		
		this.selectionResource = new SingleSelectionModel<>();
		selectionResource.addSelectionChangeHandler(this);
		
		this.gridResources = new CustomDatagrid<MetadataResource>(selectionResource, "100%", Labels.lblCnst.NoResource(), Labels.lblCnst.Resources());
		gridResources.addStyleName(style.panelGrid());
		panelResources.setWidget(gridResources);
		
		loadResources();
		updateUi();
		
		loadProperties(column);
	}

	private void loadProperties(DatabaseColumn column) {
		lstMeasureBehavior.setSelectedIndex(column.getMeasureBehavior());
		
		if (column.getMetadataType() == DatabaseColumn.UNDEFINED_TYPE) {
			btnUndefined.setValue(true);
		}
		else if (column.getMetadataType() == DatabaseColumn.DIMENSION_TYPE) {
			btnDimension.setValue(true);
		}
		else if (column.getMetadataType() == DatabaseColumn.MEASURE_TYPE) {
			btnMeasure.setValue(true);
		}
		else if (column.getMetadataType() == DatabaseColumn.PROPERTY_TYPE) {
			btnProperty.setValue(true);
		}
		refreshUi(column.getMetadataType() == DatabaseColumn.MEASURE_TYPE);
	}

	private void loadResources() {
		gridResources.loadItems(resources != null ? resources : new ArrayList<MetadataResource>());
		selectionResource.clear();
		updateUi();
	}

	@UiHandler("btnUndefined")
	public void onUndefinedClick(ClickEvent event) {
		refreshUi(false);
	}

	@UiHandler("btnDimension")
	public void onDimensionClick(ClickEvent event) {
		refreshUi(false);
	}

	@UiHandler("btnMeasure")
	public void onMeasureClick(ClickEvent event) {
		refreshUi(true);
	}

	@UiHandler("btnProperty")
	public void onPropertyClick(ClickEvent event) {
		refreshUi(false);
	}

	private void refreshUi(boolean isMeasure) {
		lstMeasureBehavior.setEnabled(isMeasure);
	}

	@UiHandler("btnAdd")
	public void onAddResource(ClickEvent event) {
		ResourceTypePopup popup = new ResourceTypePopup(this);
		popup.setPopupPosition(event.getClientX(), event.getClientY());
		popup.show();
	}
	
	public void addResource(TypeResource type) {
		final ResourceDialog dial = new ResourceDialog(datasource, type, column);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					MetadataResource resource = dial.getResource();
					addResource(resource);
				}
			}
		});
	}

	@UiHandler("btnEdit")
	public void onEditResource(ClickEvent event) {
		final MetadataResource resource = selectionResource.getSelectedObject();
		if (resource != null) {
			final ResourceDialog dial = new ResourceDialog(datasource, resource);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						resources.remove(resource);

						MetadataResource resource = dial.getResource();
						addResource(resource);
					}
				}
			});
		}
	}

	@UiHandler("btnRemove")
	public void onRemoveResource(ClickEvent event) {
		MetadataResource resource = selectionResource.getSelectedObject();
		if (resource != null) {
			resources.remove(resource);
			loadResources();
		}
	}
	
	public void addResource(MetadataResource resource) {
		if (resources == null) {
			this.resources = new ArrayList<>();
		}
		resources.add(resource);
		loadResources();
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		updateUi();
	}

	private void updateUi() {
		MetadataResource resource = selectionResource.getSelectedObject();
		btnEdit.setVisible(resource != null);
		btnRemove.setVisible(resource != null);
	}

	private int getMetadataType() {
		boolean isUndefined = btnUndefined.getValue();
		boolean isDimension = btnDimension.getValue();
		boolean isMeasure = btnMeasure.getValue();
		return isUndefined ? DatabaseColumn.UNDEFINED_TYPE : isDimension ? DatabaseColumn.DIMENSION_TYPE : isMeasure ? DatabaseColumn.MEASURE_TYPE : DatabaseColumn.PROPERTY_TYPE;
	}

	private int getMeasureBehavior() {
		int measureBehavior = DatabaseColumn.BEHAVIOR_SUM;
		try {
			measureBehavior = Integer.parseInt(lstMeasureBehavior.getSelectedValue());
		} catch(Exception e) { }
		return measureBehavior;
	}

	public List<MetadataResource> getResources() {
		return resources;
	}

	public String getName() {
		return txtName.getText();
	}

	public void apply() {
		String name = getName();
		int metadataType = getMetadataType();
		int measureBehavior = getMeasureBehavior();

		column.setName(name);
		column.setMetadataType(metadataType);
		column.setMeasureBehavior(measureBehavior);
	}
}
