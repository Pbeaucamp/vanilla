package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.chart.SavedChart;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveInfosDialog extends AbstractDialogBox {

	private static SaveInfosDialogUiBinder uiBinder = GWT.create(SaveInfosDialogUiBinder.class);

	interface SaveInfosDialogUiBinder extends UiBinder<Widget, SaveInfosDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	@UiField
	LabelTextBox txtName, txtAuthor, txtDesc;

	@UiField
	ListBoxWithButton<Group> lstGroups;

	@UiField
	CheckBox chkChart;

	private IWait waitPanel;
	private FmdtQueryDriller fmdtDriller;
	private SavedChart chart;

	private int selectedDirectoryId;
	private boolean update;
	private int itemId = 0;
	private Boolean confirm = false;

	public SaveInfosDialog(IWait waitPanel, String title, FmdtQueryDriller fmdtDriller, SavedChart chart, boolean checkChart, List<Group> availableGroups, int selectedDirectoryId, boolean update) {
		super(title, false, true);
		this.waitPanel = waitPanel;
		this.fmdtDriller = fmdtDriller;
		this.chart = chart;
		this.selectedDirectoryId = selectedDirectoryId;
		this.update = update;

		init(availableGroups);

		chkChart.setVisible(chart != null);
		chkChart.setValue(checkChart);
	}

	private void init(List<Group> availableGroups) {
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		if (update) {
			txtName.setText(LabelsConstants.lblCnst.CantModify());
			txtName.setEnabled(false);

			lstGroups.addItem(LabelsConstants.lblCnst.CantModify());
			lstGroups.setEnabled(false);
		}
		else {
			lstGroups.setList(availableGroups);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Group selectedGroup = null;
			try {
				if (!update) {
					selectedGroup = lstGroups.getSelectedObject();
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "You need to select a valid group.");
			}

			fmdtDriller.setAuthor(txtAuthor.getText());
			fmdtDriller.setDescription(txtDesc.getText());
			if (!update) {
				fmdtDriller.setName(txtName.getText());
				fmdtDriller.addGroup(selectedGroup != null ? selectedGroup.getName() : "");
			}

			boolean saveChart = chkChart.getValue();
			List<SavedChart> charts = new ArrayList<SavedChart>();
			if (saveChart) {
				charts = new ArrayList<SavedChart>();
				charts.add(chart);
			}

			if (update) {
				FmdtServices.Connect.getInstance().update(fmdtDriller.getId(), fmdtDriller.getMetadataId(), fmdtDriller, charts, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

					@Override
					public void onSuccess(Void result) {
						waitPanel.showWaitPart(false);
						confirm = true;
						hide();

						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.UpdateOkTitle(), LabelsConstants.lblCnst.UpdateOk());
					}
				}.getAsyncCallback());
			}
			else {
				FmdtServices.Connect.getInstance().save(selectedDirectoryId, fmdtDriller, charts, new GwtCallbackWrapper<Integer>(waitPanel, true, true) {

					@Override
					public void onSuccess(Integer result) {
						waitPanel.showWaitPart(false);
						itemId = result;
						confirm = true;
						hide();

						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.SaveOkTitle(), LabelsConstants.lblCnst.SaveOk());
					}
				}.getAsyncCallback());
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			SaveInfosDialog.this.hide();
		}
	};

	public int getItemId() {
		return itemId;
	}

	public Boolean getConfirm() {
		return confirm;
	}

}
