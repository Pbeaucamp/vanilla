package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.shared.HistoricLog.HistoricType;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DatabaseSelectionDialog extends AbstractDialogBox {

	private static DimensionMeasureDialogUiBinder uiBinder = GWT.create(DimensionMeasureDialogUiBinder.class);

	interface DimensionMeasureDialogUiBinder extends UiBinder<Widget, DatabaseSelectionDialog> {
	}
	
	@UiField
	ListBoxWithButton<Datasource> lstDatasources;

	@UiField
	ListBoxWithButton<Dataset> lstDatasets;

	private IRefreshProvider refreshProvider;
	private User user;
	private Contract contract;
	
	public DatabaseSelectionDialog(IRefreshProvider refreshProvider, User user, Contract contract) {
		super(Labels.lblCnst.DatabaseSelection(), false, true);
		this.refreshProvider = refreshProvider;
		this.user = user;
		this.contract = contract;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		refreshDatasourceDataset();
	}
	
	public void refreshDatasourceDataset() {
		showWaitPart(true);

		CommonService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(this, true) {

			@Override
			public void onSuccess(List<Datasource> result) {
				List<Datasource> datasources = new ArrayList<>();
				if (result != null) {
					for (Datasource ds : result) {
						if (ds.getType() == DatasourceType.JDBC) {
							datasources.add(ds);
						}
					}
				}
				
				loadData(datasources);
			}
		}.getAsyncCallback());
	}

	private void loadData(List<Datasource> datasources) {
		lstDatasources.setList(datasources);
		
		if (contract.getDatasourceId() != null && contract.getDatasetId() != null ) {
			int datasourceId = contract.getDatasourceId();
			int datasetId = contract.getDatasetId();

			int i = 0;
			LOOP: for (Datasource ds : datasources) {
				if (ds.getId() == datasourceId) {
					lstDatasources.setSelectedIndex(i);
					lstDatasets.setList(ds.getDatasets());
					int j = 0;
					for (Dataset d : ds.getDatasets()) {
						if (d.getId() == datasetId) {
							lstDatasets.setSelectedIndex(j);
							break LOOP;
						}
						j++;
					}
				}
				i++;
			}

		}
	}

	@UiHandler("lstDatasources")
	public void onDatasourceChange(ChangeEvent event) {
		Datasource selected = (Datasource) lstDatasources.getSelectedObject();
		lstDatasets.setList(selected.getDatasets());
	}

	@UiHandler("lstDatasources")
	public void onDatasourceClick(ClickEvent event) {
		DatasourceDatasetManager dialog = new DatasourceDatasetManager(user);
		dialog.setModal(true);
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshDatasourceDataset();
			}
		});
		dialog.center();
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			Datasource datasource = lstDatasources.getSelectedObject();
			Dataset dataset = lstDatasets.getSelectedObject();
			
			if (datasource == null || dataset == null) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.NeedSelectDatasourceAndDataset());
				return;
			}
			
			showWaitPart(true);
			
			contract.setDatasourceId(datasource.getId());
			contract.setDatasetId(dataset.getId());
			
			ArchitectService.Connect.getInstance().saveOrUpdateContract(contract, HistoricType.CHANGE_DATABASE, new GwtCallbackWrapper<Void>(DatabaseSelectionDialog.this, true) {

				@Override
				public void onSuccess(Void result) {
					refreshProvider.refresh(contract);
					
					DatabaseSelectionDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			DatabaseSelectionDialog.this.hide();
		}
	};
	
	public interface IRefreshProvider {
		
		public void refresh(Contract contract);
	}
}
