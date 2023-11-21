package bpm.mdm.ui.views;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.model.composites.ModelMasterDetails;

public class GenericView extends ViewPart {
	public static final String ID = "bpm.mdm.ui.views.GenericView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private ModelMasterDetails masterDetails;
	
	
	public GenericView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		
		ScrolledForm form =formToolkit.createScrolledForm(parent);
		formToolkit.paintBordersFor(form);
		form.setText(Messages.GenericView_0);

//		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setLayout(new GridLayout());
		
		
		masterDetails = new ModelMasterDetails();
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		masterDetails.createContent(managedForm);
		masterDetails.refresh();
	//	getSite().setSelectionProvider(masterDetails.getSelectionProvider());
	}

	@Override
	public void setFocus() {
		

	}

}
