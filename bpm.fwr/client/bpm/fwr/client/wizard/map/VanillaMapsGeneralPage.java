package bpm.fwr.client.wizard.map;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.components.OptionsFusionMap;
import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.dialogs.JoinDatasetDialogBox;
import bpm.fwr.client.draggable.dragcontrollers.DataDragController;
import bpm.fwr.client.draggable.dropcontrollers.TextDropController;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.client.services.WysiwygService;
import bpm.fwr.client.tree.DatasetsTree;
import bpm.fwr.client.tree.MetadataTree;
import bpm.fwr.client.utils.ServletURL;
import bpm.fwr.client.widgets.TextBoxWidget;
import bpm.fwr.client.widgets.TextBoxWidget.TextBoxType;
import bpm.fwr.client.wizard.IManageTextBoxData;
import bpm.fwr.shared.models.FusionMapDTO;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class VanillaMapsGeneralPage extends Composite implements IGwtPage, IManageTextBoxData {

	private static VanillaMapsGeneralPageUiBinder uiBinder = GWT.create(VanillaMapsGeneralPageUiBinder.class);

	interface VanillaMapsGeneralPageUiBinder extends UiBinder<Widget, VanillaMapsGeneralPage> {
	}

	interface MyStyle extends CssResource {
		String txtColumn();

		String frameMap();
	}

	@UiField
	AbsolutePanel rootPanel, previewPanel;

	@UiField
	ListBox lstMaps;

	@UiField
	Label lblMaps, lblHeight, lblWidth, lblColumnValues, lblColumnID, lblUnit;

	@UiField
	TextBox txtHeight, txtWidth, txtUnit;

	@UiField
	SimplePanel datasetTreePanel, datasetPanel, panelColumnValues, panelColumnID;

	@UiField
	Button btnTestMap;

	@UiField
	MyStyle style;

	private IGwtWizard wizParent;
	private PickupDragController puDragCtrl;
	private TextBoxWidget txtColumnID, txtColumnValues;
	private TextDropController txtIDDropCtrl, txtValueDropCtrl;

	private List<FwrMetadata> metadatas;
	private List<FusionMapDTO> maps;

	private DataSet dataset;

	public VanillaMapsGeneralPage(IGwtWizard wizParent, List<FwrMetadata> metadatas, List<DataSet> dsAvailable, final VanillaMapComponent mapComponent) {
		this.initWidget(uiBinder.createAndBindUi(this));
		this.wizParent = wizParent;
		this.metadatas = metadatas;

		this.puDragCtrl = new DataDragController(rootPanel, false);

		// Add datasetTree part
		MetadataTree metadataTree = new MetadataTree(wizParent, puDragCtrl, null, false);
		metadataTree.setHeight("100%");
		metadataTree.setMetadatas(metadatas);
		datasetTreePanel.add(metadataTree);

		DatasetsTree datasetsTree = new DatasetsTree(dsAvailable, puDragCtrl);
		datasetsTree.setHeight("100%");
		datasetPanel.add(datasetsTree);

		lblMaps.setText(Bpm_fwr.LBLW.MapsAvailables() + ": ");
		lblColumnID.setText(Bpm_fwr.LBLW.ColumnID() + ": ");
		lblColumnValues.setText(Bpm_fwr.LBLW.ColumnValues() + ": ");
		lblUnit.setText(Bpm_fwr.LBLW.Unit() + ": ");

		txtColumnID = new TextBoxWidget(this, TextBoxType.ID);
		txtColumnID.setEnabled(false);
		txtColumnID.addStyleName(style.txtColumn());
		panelColumnID.setWidget(txtColumnID);
		txtIDDropCtrl = new TextDropController(txtColumnID);

		txtColumnValues = new TextBoxWidget(this, TextBoxType.VALUES);
		txtColumnValues.setEnabled(false);
		txtColumnValues.addStyleName(style.txtColumn());
		panelColumnValues.setWidget(txtColumnValues);
		txtValueDropCtrl = new TextDropController(txtColumnValues);

		lblWidth.setText(Bpm_fwr.LBLW.Width());
		txtWidth.setText("420");

		lblHeight.setText(Bpm_fwr.LBLW.Height());
		txtHeight.setText("225");

		btnTestMap.setText(Bpm_fwr.LBLW.Preview());

		WysiwygService.Connect.getInstance().getVanillaMapsAvailables(new AsyncCallback<List<FusionMapDTO>>() {

			@Override
			public void onSuccess(List<FusionMapDTO> result) {
				if (result != null) {
					maps = result;

					int index = 0;
					for (int i = 0; i < maps.size(); i++) {
						lstMaps.addItem(maps.get(i).getName(), maps.get(i).getId() + "");
						if (mapComponent != null && mapComponent.getSwfUrl().equals(maps.get(i).getSwfUrl())) {
							index = i;
						}
					}

					if (index != 0) {
						lstMaps.setSelectedIndex(index);
					}
				}

				if (result == null || result.isEmpty()) {
					MessageHelper.openMessageDialog(Bpm_fwr.LBLW.Error(), "There is no map availables.");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				MessageHelper.openMessageError("An error append during map recuperation process: ", caught);
			}
		});

		if (mapComponent != null) {
			txtColumnID.setColumn(mapComponent.getColumnId());
			txtColumnID.setText(mapComponent.getColumnId().getName());

			txtColumnValues.setColumn(mapComponent.getColumnValues());
			txtColumnValues.setText(mapComponent.getColumnValues().getName());

			txtWidth.setText(mapComponent.getWidth() + "");
			txtHeight.setText(mapComponent.getHeight() + "");
			txtUnit.setText(mapComponent.getUnit());

			dataset = mapComponent.getDataset();
		}
	}

	@Override
	protected void onAttach() {
		puDragCtrl.registerDropController(txtIDDropCtrl);
		puDragCtrl.registerDropController(txtValueDropCtrl);
		super.onAttach();
	}

	@Override
	protected void onDetach() {
		puDragCtrl.unregisterDropControllers();
		super.onDetach();
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return true;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@UiHandler("btnTestMap")
	public void onTestMapClick(ClickEvent e) {
		VanillaMapComponent map = generateMap(true);
		if (map != null) {

			previewPanel.clear();

			final GreyAbsolutePanel greyPanel = new GreyAbsolutePanel();
			final WaitAbsolutePanel waitPanel = new WaitAbsolutePanel();

			previewPanel.add(greyPanel);
			previewPanel.add(waitPanel);
			previewPanel.setWidgetPosition(waitPanel, 145, 100);

			FwrServiceMetadata.Connect.getInstance().saveComponentForPreview(map, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {

					Frame frameMap = new Frame(GWT.getHostPageBaseURL() + ServletURL.PREVIEW_COMPONENT_URL + "?" + ServletURL.PARAM_TYPE + "=html" + "&" + ServletURL.PARAM_CUSTOM_SIZE + "=225]340");
					frameMap.addStyleName(style.frameMap());
					frameMap.addLoadHandler(new LoadHandler() {
						@Override
						public void onLoad(LoadEvent event) {
							previewPanel.remove(greyPanel);
							previewPanel.remove(waitPanel);
						}
					});
					previewPanel.add(frameMap);
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}

	private FusionMapDTO findMap(long id) {
		if (maps != null) {
			for (FusionMapDTO map : maps) {
				if (map.getId() == id) {
					return map;
				}
			}
		}

		return null;
	}

	@Override
	public void manageWidget(DraggableColumn widget, TextBoxType type) {
		checkOrCreateDataset(widget.getColumn());
		if (type == TextBoxType.ID) {
			txtColumnID.setColumn(widget.getColumn());
			txtColumnID.setText(widget.getColumn().getName());
		}
		else if (type == TextBoxType.VALUES) {
			txtColumnValues.setColumn(widget.getColumn());
			txtColumnValues.setText(widget.getColumn().getName());
		}
		wizParent.updateBtn();
	}

	private void checkOrCreateDataset(Column column) {
		if (dataset == null) {
			dataset = createFwrDataSet(column);
			dataset.addColumn(column);
			column.setDatasetParent(dataset);
		}
		else {
			if (!dataset.getColumns().isEmpty()) {
				if (!column.getBusinessPackageParent().equals(dataset.getColumns().get(0).getBusinessPackageParent())) {
					DataSet dataset1 = dataset;
					DataSet dataset2 = createFwrDataSet(column);
					dataset2.addColumn(column);
					column.setDatasetParent(dataset2);

					createJoinDataset(dataset1, dataset2);
				}
				else {
					dataset.addColumn(column);
					column.setDatasetParent(dataset);
				}
			}
			else {
				dataset.addColumn(column);
				column.setDatasetParent(dataset);
			}
		}
	}

	private DataSet createFwrDataSet(Column column) {

		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

		// Datasource creation
		DataSource dataS = new DataSource();
		dataS.setBusinessModel(column.getBusinessModelParent());
		dataS.setBusinessPackage(column.getBusinessPackageParent());
		dataS.setConnectionName("Default");
		dataS.setGroup(infoUser.getGroup().getName());
		dataS.setItemId(column.getMetadataId());
		dataS.setRepositoryId(infoUser.getRepository().getId());
		dataS.setName("datasource_" + System.currentTimeMillis());
		dataS.setPassword(infoUser.getUser().getPassword());
		dataS.setUrl(infoUser.getRepository().getUrl());
		dataS.setUser(infoUser.getUser().getLogin());
		dataS.setOnOlap(searchedModel(column.getBusinessModelParent()).isOnOlap());

		DataSet dataset = new DataSet();
		dataset.setLanguage("fr");
		dataset.setDatasource(dataS);
		dataset.setName("dataset_" + System.currentTimeMillis());
		return dataset;
	}

	private FwrBusinessModel searchedModel(String modelName) {
		for (FwrMetadata metadata : metadatas) {
			if (metadata.isBrowsed()) {
				for (FwrBusinessModel model : metadata.getBusinessModels()) {
					if (model.getName().equals(modelName)) {
						return model;
					}
				}
			}
		}

		return null;
	}

	private void createJoinDataset(DataSet dataset1, DataSet dataset2) {
		JoinDatasetDialogBox dial = new JoinDatasetDialogBox(dataset1, dataset2);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof DataSet) {
				dataset = (DataSet) result;
			}

		}
	};

	public VanillaMapComponent generateMap(boolean preview) {
		FusionMapDTO selectedMap = findMap(Long.parseLong(lstMaps.getValue(lstMaps.getSelectedIndex())));
		if (selectedMap != null) {
			VanillaMapComponent map = new VanillaMapComponent();
			map.setId(selectedMap.getId() + "");
			map.setName(selectedMap.getName());
			map.setColumnId(txtColumnID.getColumn());
			map.setColumnValues(txtColumnValues.getColumn());
			if (preview) {
				map.setHeight(225);
				map.setWidth(420);
			}
			else {
				try {
					map.setHeight(Integer.parseInt(txtHeight.getText()));
				} catch (Exception e) {
					e.printStackTrace();
					map.setHeight(225);
				}
				try {
					map.setWidth(Integer.parseInt(txtWidth.getText()));
				} catch (Exception e) {
					e.printStackTrace();
					map.setWidth(420);
				}
			}
			map.setDataset(dataset);
			map.setSwfUrl(selectedMap.getSwfUrl());
			map.setUnit(txtUnit.getText());
			map.setDataset(dataset);

			if (preview) {
				OptionsFusionMap mapOptions = new OptionsFusionMap();
				mapOptions.setShowLabels(false);
				map.setOptions(mapOptions);

				List<ColorRange> colors = new ArrayList<ColorRange>();
				colors.add(new ColorRange("Color", "#E6E6E6", Integer.MIN_VALUE, Integer.MAX_VALUE));
				map.setColors(colors);
			}

			return map;
		}
		else {
			return null;
		}
	}
}
