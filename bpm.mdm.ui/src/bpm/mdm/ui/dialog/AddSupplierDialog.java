package bpm.mdm.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.ui.Activator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Security;

public class AddSupplierDialog extends AbstractGestionDialog {

	private Supplier supplier;

	public AddSupplierDialog(Shell parentShell, Supplier supplier) {
		super(parentShell);
		this.supplier = supplier;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control c = super.createDialogArea(parent);

		if (supplier != null) {
			txtName.setText(supplier.getName());
			txtSourceExterne.setText(supplier.getExternalSource());
			txtIdentifiantExterne.setText(supplier.getExternalId());

			List<Integer> groupIds = null;
			try {
				groupIds = Activator.getDefault().getMdmProvider().getSupplierSecurity(supplier.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<Group> groups = (List<Group>) groupTable.getInput();

			for (Integer id : groupIds) {
				for (Group group : groups) {
					if (id.intValue() == group.getId().intValue()) {
						groupTable.setChecked(group, true);
						break;
					}
				}
			}
		}

		return c;
	}

	@Override
	protected void okPressed() {

		boolean update = true;
		if (supplier == null) {
			supplier = new Supplier();

			update = false;
		}

		supplier.setName(txtName.getText());
		supplier.setExternalSource(txtSourceExterne.getText());
		supplier.setExternalId(txtIdentifiantExterne.getText());

		List<Group> groups = new ArrayList<Group>();
		for (Object o : groupTable.getCheckedElements()) {
			groups.add((Group) o);
		}

		try {
			Activator.getDefault().getMdmProvider().addSupplier(supplier, groups);
		} catch (Exception e) {
			MessageDialog.openError(getParentShell(), "Error while saving the supplier", "An error occured while saving the supplier : " + e.getMessage());
			e.printStackTrace();
		}

		if (update) {
			try {
				int repositoryId = Activator.getDefault().getRepositoryApi().getContext().getRepository().getId();
				updateSecurity(supplier, groups, repositoryId);
			} catch (Exception e) {
				MessageDialog.openError(getParentShell(), "Error while updating security files", "An error occured while saving the security : " + e.getMessage());
				e.printStackTrace();
			}
		}

		super.okPressed();
	}

	private void updateSecurity(Supplier supplier, List<Group> groups, int repositoryId) throws Exception {
		List<GedDocument> files = getFiles(supplier);
		
		for (GedDocument file : files) {

			List<Security> secus = Activator.getDefault().getGedComponent().getSecuForDocument(file);
			
			List<Group> toAdd = getGroupsToAdd(secus, groups);
			List<Security> toDelete = getGroupsToDelete(secus, groups);
			
			for (Group group : toAdd) {
				Activator.getDefault().getGedComponent().addAccess(file.getId(), group.getId(), repositoryId);
			}
			
			for (Security secu : toDelete) {
				Activator.getDefault().getGedComponent().removeAccess(file.getId(), secu.getGroupId(), secu.getRepositoryId());
			}
		}
	}

	private List<Group> getGroupsToAdd(List<Security> secus, List<Group> groups) {
		List<Group> toAdd = new ArrayList<Group>();
		if (groups != null) {
			for (Group group : groups) {
				boolean found = false;
				if (secus != null) {
					for (Security secu : secus) {
						if (secu.getGroupId() == group.getId()) {
							found = true;
							break;
						}
					}
				}
				
				if (!found) {
					toAdd.add(group);
				}
			}
		}
		return toAdd;
	}

	private List<Security> getGroupsToDelete(List<Security> secus, List<Group> groups) {
		List<Security> toDelete = new ArrayList<Security>();
		if (secus != null) {
			for (Security secu : secus) {
				boolean found = false;
				if (groups != null) {
					for (Group group : groups) {
						if (secu.getGroupId() == group.getId()) {
							found = true;
							break;
						}
					}
				}
				
				if (!found) {
					toDelete.add(secu);
				}
			}
		}
		return toDelete;
	}

	private List<GedDocument> getFiles(Supplier supplier) throws Exception {
		List<GedDocument> files = new ArrayList<>();
		if (supplier.getContracts() != null) {
			for (Contract contract : supplier.getContracts()) {
				if (contract.getFileVersions() != null) {
					files.add(contract.getFileVersions());
				}
				else if (contract.getDocId() != null) {
					files.add(Activator.getDefault().getGedComponent().getDocumentDefinitionById(contract.getDocId()));
				}
			}
		}

		return files;
	} 

}
