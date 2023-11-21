package bpm.mdm.ui.dialog;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.ui.Activator;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;

public class AddNewFileDialog extends Dialog {

	private Text txtName;
	private Text txtFile;
	private Button btnFile;
//	private Supplier supplier;
	private Contract contract;
	
	private List<Supplier> suppliers;
	
	public AddNewFileDialog(Shell parentShell, Contract contract, List<Supplier> suppliers) {
		super(parentShell);
//		this.supplier = supplier;
		this.contract = contract;
		this.suppliers = suppliers;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 200);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		if(contract.getFileVersions() == null) {
			getShell().setText("Add a Contract Document");
			Label lblName = new Label(comp, SWT.NONE);
			lblName.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
			lblName.setText("Name");
			
			txtName = new Text(comp, SWT.BORDER);
			txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		}
		else {
			getShell().setText("Add a new version of Document");
		}
		
		Label lblFile = new Label(comp, SWT.NONE);
		lblFile.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		lblFile.setText("File");
		
		txtFile = new Text(comp, SWT.BORDER);
		txtFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFile.setEnabled(false);
		
		btnFile = new Button(comp, SWT.PUSH);
		btnFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnFile.setText("...");
		
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(getShell());
				String file = dial.open();
				txtFile.setText(file);
			}
		});
		
		return comp;
	}
	
	@Override
	protected void okPressed() {
		try {
			String file = txtFile.getText();
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Problem with the file", "The file cannot be found.");
				return;
			}
			if(contract.getFileVersions() != null) {
				try {
					DocumentVersion vers = Activator.getDefault().getGedComponent().addVersionToDocument(contract.getFileVersions(),file.substring(file.lastIndexOf("."), file.length()), in);
					contract.getFileVersions().addDocumentVersion(vers);
				} catch (Exception e) {
					DocumentVersion vers = Activator.getDefault().getGedComponent().addVersionToDocument(contract.getFileVersions(),"", in);
					contract.getFileVersions().addDocumentVersion(vers);
					e.printStackTrace();
				}
			}
			else {
				GedDocument doc = new GedDocument();
				
				doc.setDirectoryId(0);
				doc.setName(txtName.getText());
				doc.setCreationDate(new Date());
				int userId = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserByLogin(Activator.getDefault().getVanillaApi().getVanillaContext().getLogin()).getId();
				doc.setCreatedBy(userId);
				doc.setMdmAttached(true);
				
				String format = file.substring(file.lastIndexOf(".")+1, file.length());
				
				List<Integer> groupIds = Activator.getDefault().getMdmProvider().getSupplierSecurity(contract.getParent().getId());
				
				ComProperties com = new ComProperties();
				
				com.setSimpleProperty(RuntimeFields.TITLE.getName(), txtName.getText());
				
				GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(com, 
						userId, -1, groupIds, -1, format, null, -1);
				config.setMdmAttached(true);
				
				int id = Activator.getDefault().getGedComponent().index(config, in);
				
//				int id = Activator.getDefault().getGedComponent().addGedDocument(doc, in, format);
				
				for(Integer groupId : groupIds) {
					Activator.getDefault().getGedComponent().addAccess(id, groupId, Activator.getDefault().getRepositoryApi().getContext().getRepository().getId());
				}
				
				doc = Activator.getDefault().getGedComponent().getDocumentDefinitionById(id);
				
				contract.setFileVersions(doc);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error while creating document", e.getMessage());
			return;
		}
		
		try {
			Activator.getDefault().getMdmProvider().saveSuppliers(suppliers);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		super.okPressed();
	}
	
}
