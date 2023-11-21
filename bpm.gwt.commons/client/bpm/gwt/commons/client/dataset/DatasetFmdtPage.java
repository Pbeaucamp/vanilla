package bpm.gwt.commons.client.dataset;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.viewer.fmdtdriller.DesignerPanel;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DatasetFmdtPage extends DatasetPanel implements IGwtPage {

	private static DatasetFmdtPageUiBinder uiBinder = GWT.create(DatasetFmdtPageUiBinder.class);

	interface DatasetFmdtPageUiBinder extends UiBinder<Widget, DatasetFmdtPage> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	private Datasource datasource;

	private Dataset dataset;
	
	private DesignerPanel panel;

	private FmdtQueryDriller driller;

	private DatasetWizard parent;
	
	public DatasetFmdtPage(DatasetWizard parent, Datasource datasourc, Dataset dataset) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = datasourc;
		this.dataset = dataset;
		
		driller = new FmdtQueryDriller();
		driller.setMetadataId(((DatasourceFmdt)datasource.getObject()).getItemId());
		driller.setModelName(((DatasourceFmdt)datasource.getObject()).getBusinessModel());
		driller.setPackageName(((DatasourceFmdt)datasource.getObject()).getBusinessPackage());
		
		FmdtServices.Connect.getInstance().getMetadataData(driller, ((DatasourceFmdt)datasource.getObject()), new AsyncCallback<FmdtQueryDatas>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(final FmdtQueryDatas result) {		
				
				if(DatasetFmdtPage.this.dataset != null && DatasetFmdtPage.this.dataset.getId() > 0) {
					FmdtServices.Connect.getInstance().loadMetadataView(driller, ((DatasourceFmdt)datasource.getObject()), DatasetFmdtPage.this.dataset, new GwtCallbackWrapper<FmdtQueryBuilder>(null, false, false) {

						@Override
						public void onSuccess(FmdtQueryBuilder builder) {
							panel = new DesignerPanel(builder, result, ((DatasourceFmdt)datasource.getObject()).getItemId(), ((DatasourceFmdt)datasource.getObject()).getBusinessModel(), ((DatasourceFmdt)datasource.getObject()).getBusinessPackage());
							
							mainPanel.add(panel);
						}
					}.getAsyncCallback());
				}
				else {
					panel = new DesignerPanel(new FmdtQueryBuilder(), result, ((DatasourceFmdt)datasource.getObject()).getItemId(), ((DatasourceFmdt)datasource.getObject()).getBusinessModel(), ((DatasourceFmdt)datasource.getObject()).getBusinessPackage());
					
					mainPanel.add(panel);
				}
			}
		});
		
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	public DesignerPanel getDesignerPanel() {
		return panel;
	}
	
	public FmdtQueryDriller getDriller() {
		return driller;
	}

	@Override
	public List<DataColumn> getMetaColumns() {
		return null;
	}

	@Override
	public String getQuery(String datasetName) {
		return null;
	}
	
}
