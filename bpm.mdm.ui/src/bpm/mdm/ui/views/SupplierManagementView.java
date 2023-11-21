package bpm.mdm.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.model.composites.SupplierManagementDetails;

public class SupplierManagementView extends ViewPart {

	public static final String ID = "bpm.mdm.ui.views.SupplierManagementView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	private List<Supplier> suppliers;
	private SupplierManagementDetails masterDetails;
	
	@Override
	public void createPartControl(Composite parent) {
		
		parent.setLayout(new GridLayout());
//		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		ScrolledForm form = formToolkit.createScrolledForm(parent);
		formToolkit.paintBordersFor(form);
		form.setText(Messages.SupplierManagementView_0);

//		form.setLayout(new GridLayout());
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		masterDetails = new SupplierManagementDetails(this);
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		masterDetails.createContent(managedForm);
		masterDetails.refresh();
	}

	public void init() {
		if(suppliers == null) {
			try {
				suppliers = Activator.getDefault().getMdmProvider().getSuppliers();
			} catch (Exception e) {
				e.printStackTrace();
				suppliers = new ArrayList<Supplier>();
			}
		}
		
		masterDetails.initData(suppliers);
		
	}

	@Override
	public void setFocus() {
		
		
	}

}
