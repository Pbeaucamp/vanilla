package bpm.mdm.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.ui.Activator;

public class AddContractDialog extends AbstractGestionDialog {

	private Contract contract;
	private Supplier parent;

	public AddContractDialog(Shell parentShell, Contract contract, Supplier parent) {
		super(parentShell);
		this.contract = contract;
		this.parent = parent;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Control c = super.createDialogArea(parent);
		
		if(contract != null) {
			txtName.setText(contract.getName());
			txtSourceExterne.setText(contract.getExternalSource());
			txtIdentifiantExterne.setText(contract.getExternalId());
		}
		
		return c;
	}

	@Override
	protected void okPressed() {
		if(contract == null) {
			contract = new Contract();
		}
		
		contract.setName(txtName.getText());
		contract.setExternalSource(txtSourceExterne.getText());
		contract.setExternalId(txtIdentifiantExterne.getText());
		contract.setParent(parent);
		
		parent.addContract(contract);
		
		try {
			Activator.getDefault().getMdmProvider().addSupplier(parent);
		} catch(Exception e) {
			MessageDialog.openError(getParentShell(), "Error while saving the contract", "An error occured while saving the contract : " + e.getMessage());
			e.printStackTrace();
		}
		
		super.okPressed();
	}
	
}
