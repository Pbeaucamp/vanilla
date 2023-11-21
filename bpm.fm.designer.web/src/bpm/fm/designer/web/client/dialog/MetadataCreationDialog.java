package bpm.fm.designer.web.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog;
import bpm.gwt.commons.client.dialog.RepositorySaveDialog.SaveHandler;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class MetadataCreationDialog extends AbstractDialogBox implements SaveHandler {

	private static MetadataCreationDialogUiBinder uiBinder = GWT.create(MetadataCreationDialogUiBinder.class);

	interface MetadataCreationDialogUiBinder extends UiBinder<Widget, MetadataCreationDialog> {
	}

//	@UiField
//	LabelTextBox txtName;

	@UiField
	SimplePanel panelMetrics;

	private Theme theme;

	private MultiSelectionModel<Metric> selectionModel;

	public MetadataCreationDialog(Theme theme) {
		super(LabelsConstants.lblCnst.CreateMetadata(), false, true);
		this.theme = theme;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		List<Metric> metrics = theme.getMetrics();

		selectionModel = new MultiSelectionModel<Metric>();
		panelMetrics.setWidget(new CustomDatagrid<Metric>(metrics, selectionModel, 430, "", Messages.lbl.Metrics()));
	}

	private List<Metric> getSelectedObjects(List<Metric> items) {
		List<Metric> selectedItems = new ArrayList<Metric>();
		for (Metric item : items) {
			if (selectionModel.isSelected(item)) {
				selectedItems.add(item);
			}
		}
		return selectedItems;
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
//			final String metadataName = txtName.getText();
			final List<Metric> metrics = getSelectedObjects(theme.getMetrics());

//			if (metadataName.isEmpty()) {
//				return;
//			}

			if (metrics.isEmpty()) {
				return;
			}

//			final bpm.gwt.commons.client.dialog.RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.FMDT_TYPE);
//			dial.center();
//			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
//
//				@Override
//				public void onClose(CloseEvent<PopupPanel> event) {
//					if (dial.folderSelected()) {
//						RepositoryDirectory target = dial.getSelectedDirectory();
//						createMetadata(theme, metadataName, metrics, target);
//					}
//				}
//			});
			
			Group group = ClientSession.getInstance().getGroup();
			List<Group> groups = new ArrayList<Group>();
			groups.add(group);
			
			RepositorySaveDialog<String> dial = new RepositorySaveDialog<String>(MetadataCreationDialog.this, "", IRepositoryApi.FMDT_TYPE, "", "", groups, false);
			dial.center();
		}
	};

	@Override
	public void saveItem(SaveItemInformations itemInfos, boolean close) {
		List<Metric> metrics = getSelectedObjects(theme.getMetrics());
		RepositoryDirectory target = itemInfos.getSelectedDirectory();
		createMetadata(theme, itemInfos.getName(), metrics, target);
	}

	private void createMetadata(Theme theme, String metadataName, List<Metric> metrics, RepositoryDirectory target) {
		MetricService.Connection.getInstance().createMetadata(theme, metadataName, metrics, target, new GwtCallbackWrapper<Integer>(this, true, true) {

			@Override
			public void onSuccess(Integer result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Messages.lbl.TheMetadataHasBeenCreated());
				hide();
			}
		}.getAsyncCallback());
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
