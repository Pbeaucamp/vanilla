package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.tree.MdmTree;
import bpm.mdm.model.supplier.MdmDirectory;

public class MdmDirectoryDialog extends AbstractDialogBox {

	private static MdmDirectoryDialogUiBinder uiBinder = GWT.create(MdmDirectoryDialogUiBinder.class);

	interface MdmDirectoryDialogUiBinder extends UiBinder<Widget, MdmDirectoryDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	SimplePanel treePanel;

	private MdmTree mdmTree;

	private boolean confirm = false;

	public MdmDirectoryDialog() {
		super(LabelsConstants.lblCnst.ExportToRepository(), false, true);

		setWidget(uiBinder.createAndBindUi(this));

		mdmTree = new MdmTree(null);
		treePanel.add(mdmTree);

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (mdmTree.getSelectedItem() == null) {
				return;
			}
			
			confirm = true;
			hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}

	public MdmDirectory getSelectedDirectory() {
		return mdmTree.getSelectedItem() != null ? (MdmDirectory) mdmTree.getSelectedItem() : null;
	}
}
