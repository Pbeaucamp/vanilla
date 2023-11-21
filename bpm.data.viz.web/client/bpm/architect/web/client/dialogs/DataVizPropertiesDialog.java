package bpm.architect.web.client.dialogs;

import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.LaunchContractETLDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DataVizPropertiesDialog extends AbstractDialogBox {

	private static DataVizPropertiesDialogUiBinder uiBinder = GWT.create(DataVizPropertiesDialogUiBinder.class);

	interface DataVizPropertiesDialogUiBinder extends UiBinder<Widget, DataVizPropertiesDialog> {
	}

	@UiField
	LabelTextBox txtName, txtDesc;

	@UiField
	ListBoxWithButton<Datasource> lstDatasource;

	@UiField
	ListBoxWithButton<Dataset> lstDataset;

	@UiField
	ListBoxWithButton<DataPreparation> lstDataPrep;

	@UiField
	Image btnRefresh;

	@UiField
	ListBoxWithButton<MapVanilla> lstVanillaMaps;

	@UiField
	ListBoxWithButton<MapDataSet> lstMapDatasets;

	private DataPreparation dataPrep;

	private boolean confirm;

	public DataVizPropertiesDialog(DataPreparation dp) {
		super(Labels.lblCnst.DataPreparation(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.dataPrep = dp;

		txtName.setText(dataPrep.getName());
		txtDesc.setText(dataPrep.getDescription());

		CommonService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(this, false, false) {
			@Override
			public void onSuccess(List<Datasource> result) {
				lstDatasource.setList(result, true);

				if (dataPrep.getDataset() != null) {
					lstDatasource.setSelectedObject(dataPrep.getDataset().getDatasource());
					onDatasourceChange(null);
					lstDataset.setSelectedObject(dataPrep.getDataset());
				}
			}
		}.getAsyncCallback());

		ArchitectService.Connect.getInstance().getDataPreparations(new GwtCallbackWrapper<List<DataPreparation>>(this, true) {
			@Override
			public void onSuccess(List<DataPreparation> result) {
				lstDataPrep.setList(result, true);
			}

		}.getAsyncCallback());

		loadVanillaMaps();

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	@UiHandler("lstDatasource")
	public void onDatasourceChange(ChangeEvent event) {
		Datasource datasource = lstDatasource.getSelectedObject();
		if (datasource == null) {
			lstDataset.clear();
			return;
		}

		btnRefresh.setVisible(hasInput(datasource));
		lstDataset.setList(lstDatasource.getSelectedObject().getDatasets(), true);
	}

	@UiHandler("lstVanillaMaps")
	public void onVanillaMapChange(ChangeEvent event) {
		MapVanilla map = lstVanillaMaps.getSelectedObject();
		lstMapDatasets.setList(map.getDataSetList(), true);
	}

	private boolean hasInput(Datasource datasource) {
		if (datasource.getObject() instanceof DatasourceArchitect) {
			DatasourceArchitect dsArchitect = (DatasourceArchitect) datasource.getObject();
			return dsArchitect.hasInput();
		}
		return false;
	}

	private void loadVanillaMaps() {
		ArchitectService.Connect.getInstance().getMaps(new GwtCallbackWrapper<List<MapVanilla>>(this, true, true) {
			@Override
			public void onSuccess(List<MapVanilla> result) {
				lstVanillaMaps.setList(result, true);
				if (dataPrep != null && dataPrep.getMapId() != null && dataPrep.getMapId() > 0) {
					for (MapVanilla map : result) {
						if (map.getId() == dataPrep.getMapId()) {
							lstVanillaMaps.setSelectedObject(map);
							lstMapDatasets.setList(map.getDataSetList(), true);
							for (MapDataSet ds : map.getDataSetList()) {
								if (dataPrep.getMapDatasetId() == ds.getId()) {
									lstMapDatasets.setSelectedObject(ds);
									break;
								}
							}
							break;
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnRefresh")
	public void onRefreshContract(ClickEvent event) {
		Datasource datasource = lstDatasource.getSelectedObject();
		if (hasInput(datasource)) {
			LaunchContractETLDialog dial = new LaunchContractETLDialog(((DatasourceArchitect) datasource.getObject()).getContractId());
			dial.center();
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			dataPrep.setName(txtName.getText());
			dataPrep.setDescription(txtDesc.getText());
			dataPrep.setDataset(lstDataset.getSelectedObject());
			if (lstDataPrep.getSelectedObject() != null) {
				dataPrep.setRules(lstDataPrep.getSelectedObject().getRules());
			}
			dataPrep.setMap(lstVanillaMaps.getSelectedObject());
			dataPrep.setMapDataset(lstMapDatasets.getSelectedObject());
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}

}
