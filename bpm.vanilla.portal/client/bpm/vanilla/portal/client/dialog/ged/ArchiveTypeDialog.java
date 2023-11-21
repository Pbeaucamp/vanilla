package bpm.vanilla.portal.client.dialog.ged;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelValueTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveType.TypeArchive;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class ArchiveTypeDialog extends AbstractDialogBox {

	private static ArchiveTypeDialogUiBinder uiBinder = GWT.create(ArchiveTypeDialogUiBinder.class);

	interface ArchiveTypeDialogUiBinder extends UiBinder<Widget, ArchiveTypeDialog> {
	}

	private boolean isConfirm;
	private ArchiveType type;
	
	@UiField
	LabelTextBox txtName, txtPath;
	
	@UiField
	LabelValueTextBox peremptionMonths, retentionMonths, conservationMonths;
	
	@UiField
	RadioButton btnDelete, btnArchive;
	
	@UiField
	ListBoxWithButton<TypeArchive> lstArchiveAction;

	public ArchiveTypeDialog() {
		this(null);
	}
	
	public ArchiveTypeDialog(ArchiveType type) {
		super(ToolsGWT.lblCnst.ArchiveType(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		this.type = type;
		
		if(this.type == null) {
			this.type = new ArchiveType();
		}
		
		txtName.setText(this.type.getName());
		txtPath.setText(this.type.getSavePath());
		peremptionMonths.setText(this.type.getPeremptionMonths());
		retentionMonths.setText(this.type.getRetentionMonths());
		conservationMonths.setText(this.type.getConservationMonths());
		
		List<TypeArchive> typeArchives = new ArrayList<ArchiveType.TypeArchive>();
		typeArchives.add(TypeArchive.STANDARD);
		lstArchiveAction.setList(typeArchives);
		
		if(this.type.isArchive()) {
			btnArchive.setValue(true, true);
		}
		else {
			btnDelete.setValue(true, true);
		}
		
	}
	
	public ArchiveType getArchiveType() {
		return type;
	}

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(txtName.getText() == null || txtName.getText().isEmpty()) {
				final InformationsDialog dialConfirm = new InformationsDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.NameNeeded(), false);
				dialConfirm.center();
			}
			else {
				type.setName(txtName.getText());
				type.setSavePath(txtPath.getText());
				type.setConservationMonths(conservationMonths.getValue());
				type.setRetentionMonths(retentionMonths.getValue());
				type.setPeremptionMonths(peremptionMonths.getValue());
				type.setArchive(btnArchive.getValue());
				
				type.setType(lstArchiveAction.getSelectedObject());
				
				isConfirm = true;
				ArchiveTypeDialog.this.hide();
			}
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			ArchiveTypeDialog.this.hide();
		}
	};
	
	public boolean isConfirm() {
		return isConfirm;
	}
}
