package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class SupplierDialog extends AbstractDialogBox {

	private static RepositorySaveDialogUiBinder uiBinder = GWT.create(RepositorySaveDialogUiBinder.class);

	interface RepositorySaveDialogUiBinder extends UiBinder<Widget, SupplierDialog> {
	}
	
	interface MyStyle extends CssResource {
		String disabled();
		String btnHover();
	}
	
	@UiField
	MyStyle style;

	@UiField
	LabelTextBox txtName, txtExternalSource, txtExternalIdentifier;

	@UiField
	SimplePanel panelGroups;

	private ContractDialog parent;
	
	private Supplier supplier;
	private List<Group> availableGroups;
	private MultiSelectionModel<Group> multiSelectionModel;
	
	private boolean edit = false;
	
	public SupplierDialog(ContractDialog parent, Supplier supplier, List<Group> availableGroups) {
		super(LabelsConstants.lblCnst.SaveItem(), false, true);
		this.parent = parent;
		this.supplier = supplier;
		this.availableGroups = availableGroups;
		this.edit = supplier != null;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		txtName.setText(supplier != null ? supplier.getName() : "");
		txtExternalSource.setText(supplier != null ? supplier.getExternalSource() : "");
		txtExternalIdentifier.setText(supplier != null ? supplier.getExternalId() : "");

		multiSelectionModel = new MultiSelectionModel<Group>();
		panelGroups.setWidget(new CustomDatagrid<Group>(availableGroups, multiSelectionModel, 150, LabelsConstants.lblCnst.NoGroupAvailable()));
		
		if (supplier != null) {
			loadSupplierSecurity(supplier);
		}
	}

	private void loadSupplierSecurity(Supplier supplier) {
		showWaitPart(true);

		ArchitectService.Connect.getInstance().getSupplierSecurity(supplier.getId(), new GwtCallbackWrapper<List<Integer>>(this, true) {

			@Override
			public void onSuccess(List<Integer> result) {
				if (result != null) {
					for (Integer groupId : result) {
						for (Group group : availableGroups) {
							if (groupId == group.getId()) {
								multiSelectionModel.setSelected(group, true);
								break;
							}
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String externalSource = txtExternalSource.getText();
			String externalId = txtExternalIdentifier.getText();

			if (name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedName());
				return;
			}

			List<Group> selectedGroups = new ArrayList<Group>();
			if (availableGroups != null) {
				for (Group group : availableGroups) {
					if (multiSelectionModel.isSelected(group)) {
						selectedGroups.add(group);
					}
				}
			}

			if (selectedGroups.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedSelectGroupForItem());
				return;
			}
			
			Supplier supplier = SupplierDialog.this.supplier;
			if (edit) {
				supplier.setName(name);
				supplier.setExternalId(externalId);
				supplier.setExternalSource(externalSource);
			}
			else {
				supplier = new Supplier(null, name, externalId, externalSource);
			}
			
			showWaitPart(true);
			
			ArchitectService.Connect.getInstance().saveOrUpdateSupplier(supplier, selectedGroups, new GwtCallbackWrapper<Void>(SupplierDialog.this, true) {

				@Override
				public void onSuccess(Void result) {
					parent.refreshSuppliers();
					SupplierDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			SupplierDialog.this.hide();
		}
	};
}
