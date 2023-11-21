package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.DisplayRowDialog.IRefreshValuesHandler;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;

public class FMDTResponsePage extends Composite {

	private static DatasetFmdtPageUiBinder uiBinder = GWT.create(DatasetFmdtPageUiBinder.class);

	interface DatasetFmdtPageUiBinder extends UiBinder<Widget, FMDTResponsePage> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	private IWait waitPanel;
	private IRefreshValuesHandler refreshValuesHandler;
	
	private FMDTResponsePanel responsePanel;
	
	private FmdtQueryDriller driller;
	
	public FMDTResponsePage(IWait waitPanel, IRefreshValuesHandler refreshValuesHandler, Datasource datasource, final String tableName) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.refreshValuesHandler = refreshValuesHandler;
		
		driller = new FmdtQueryDriller();
		driller.setMetadataId(((DatasourceFmdt)datasource.getObject()).getItemId());
		driller.setModelName(((DatasourceFmdt)datasource.getObject()).getBusinessModel());
		driller.setPackageName(((DatasourceFmdt)datasource.getObject()).getBusinessPackage());
		driller.addBuilders(new FmdtQueryBuilder("Request_1"));
		
		FmdtServices.Connect.getInstance().getMetadataData(driller, ((DatasourceFmdt)datasource.getObject()), new GwtCallbackWrapper<FmdtQueryDatas>(waitPanel, true, false) {

			@Override
			public void onSuccess(final FmdtQueryDatas result) {
				if (result.getTables() != null) {
					for (FmdtTableStruct table : result.getTables()) {
						if (table.getName().equals(tableName)) {
							List<FmdtColumn> columns = table.getColumns();
							
							driller.getBuilder().setColumns(columns);
							loadRequest();
						}
					}
				}
			}
		}.getAsyncCallback());
		
	}
	
	private void loadRequest() {
		FmdtServices.Connect.getInstance().getRequestValue(driller.getBuilder(), driller, true, new GwtCallbackWrapper<List<FmdtTable>>(waitPanel, false, true) {
			
			@Override
			public void onSuccess(List<FmdtTable> table) {
				responsePanel = new FMDTResponsePanel(table, driller, waitPanel, refreshValuesHandler);
				mainPanel.add(responsePanel);
			}
		}.getAsyncCallback());
	}
}
