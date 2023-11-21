package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.viewer.ReportViewer;
import bpm.gwt.commons.client.viewer.fmdtdriller.FMDTResponsePage;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.DisplayRowDialog.IRefreshValuesHandler;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;

public class FMDTValuesDialog extends AbstractDialogBox implements IRefreshValuesHandler {

	private static FMDTValuesDialogUiBinder uiBinder = GWT.create(FMDTValuesDialogUiBinder.class);

	interface FMDTValuesDialogUiBinder extends UiBinder<Widget, FMDTValuesDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	private Button btnRefresh;
	
	private ReportViewer viewer;

	public FMDTValuesDialog(ReportViewer viewer, final ItemMetadataTableLink link) {
		super(LabelsConstants.lblCnst.ValuesExplorer(), false, true);
		this.viewer = viewer;
		setWidget(uiBinder.createAndBindUi(this));
		
		createButton(LabelsConstants.lblCnst.Close(), cancelHandler);
		btnRefresh = createButton(LabelsConstants.lblCnst.RefreshReport(), confirmHandler);
		btnRefresh.setEnabled(false);
		
		CommonService.Connect.getInstance().getDatasourceById(link.getDatasourceId(), new GwtCallbackWrapper<Datasource>(this, true, true) {

			@Override
			public void onSuccess(Datasource result) {
				FMDTResponsePage page = new FMDTResponsePage(FMDTValuesDialog.this, FMDTValuesDialog.this, result, link.getTableName());
				contentPanel.add(page);
			}
		}.getAsyncCallback());
	}

	@Override
	public void refresh() {
		btnRefresh.setEnabled(true);
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			viewer.runItem(viewer.getItemInfo());
			
			hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
