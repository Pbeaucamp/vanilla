package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;

public class AddMetadataLinkDialog extends AbstractDialogBox {

	private static DimensionMeasureDialogUiBinder uiBinder = GWT.create(DimensionMeasureDialogUiBinder.class);

	interface DimensionMeasureDialogUiBinder extends UiBinder<Widget, AddMetadataLinkDialog> {
	}
	
	@UiField
	LabelTextBox txtName, txtTableName;
	
	@UiField
	ListBoxWithButton<Datasource> lstDatasources;

	private IRefreshProvider refreshProvider;
	private User user;
	private int itemId;
	
	public AddMetadataLinkDialog(IRefreshProvider refreshProvider, User user, int itemId) {
		super(LabelsConstants.lblCnst.MetadataLink(), false, true);
		this.refreshProvider = refreshProvider;
		this.user = user;
		this.itemId = itemId;
		
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
						if (ds.getType() == DatasourceType.FMDT) {
							datasources.add(ds);
						}
					}
				}

				lstDatasources.setList(datasources);
			}
		}.getAsyncCallback());
	}

	@UiHandler("lstDatasources")
	public void onDatasourceChange(ChangeEvent event) {
//		Datasource selected = (Datasource) lstDatasources.getSelectedObject();
//		lstDatasets.setList(selected.getDatasets());
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
			String name = txtName.getText();
			Datasource datasource = lstDatasources.getSelectedObject();
			String tableName = txtTableName.getText();
			
			if (name == null || name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedName());
				return;
			}
			
			if (datasource == null ) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedSelectDatasource());
				return;
			}
			
			if (tableName == null || tableName.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedTableName());
				return;
			}
			
			showWaitPart(true);
			
			ItemMetadataTableLink link = new ItemMetadataTableLink();
			link.setName(name);
			link.setItemId(itemId);
			link.setCreationDate(new Date());
			link.setDatasource(datasource);
			link.setDatasourceId(datasource.getId());
			link.setTableName(tableName);

			CommonService.Connect.getInstance().addItemMetadataTableLink(link, new GwtCallbackWrapper<Void>(AddMetadataLinkDialog.this, true) {

				@Override
				public void onSuccess(Void result) {
					refreshProvider.refresh();
					
					AddMetadataLinkDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			AddMetadataLinkDialog.this.hide();
		}
	};
	
	public interface IRefreshProvider {
		
		public void refresh();
	}
}
