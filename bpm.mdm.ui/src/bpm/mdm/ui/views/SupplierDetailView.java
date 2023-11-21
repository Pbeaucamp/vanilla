package bpm.mdm.ui.views;

import java.util.List;

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
import bpm.mdm.ui.model.composites.SupplierDetailComposite;

public class SupplierDetailView extends ViewPart {

	public static final String ID = "bpm.mdm.ui.views.SupplierDetailView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	
	private List<Supplier> suppliers;
	private Supplier selectedSupplier;
	
	private SupplierDetailComposite masterDetails;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
//		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ScrolledForm form = formToolkit.createScrolledForm(parent);
		formToolkit.paintBordersFor(form);
		form.setText(Messages.SupplierDetailView_0);

//		form.setLayout(new GridLayout());
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		masterDetails = new SupplierDetailComposite();
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		masterDetails.createContent(managedForm);
		masterDetails.refresh();
		
		Activator.getDefault().setSupplierDetail(masterDetails);
	}

	@Override
	public void setFocus() {

	}
	
	public void refresh(List<Supplier> suppliers , Supplier selectedSupplier) {
		this.suppliers = suppliers;
		this.selectedSupplier = selectedSupplier;
		try {
			masterDetails.initData(suppliers, selectedSupplier, null, null);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
