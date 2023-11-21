package bpm.forms.design.ui.views;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import bpm.forms.design.ui.views.content.ContentMasterDetailsBlock;

public class ContentView extends ViewPart {

	public static final String ID = "bpm.forms.design.ui.views.ContentView"; //$NON-NLS-1$
	
	private FormToolkit formToolkit;
	private ScrolledForm form;
	
	private ContentMasterDetailsBlock block;
	
	public ContentView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());
		
		form = formToolkit.createScrolledForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setLayout(new GridLayout());
		
		
		block = new ContentMasterDetailsBlock();
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		
		block.createContent(managedForm);
		
		getSite().setSelectionProvider(block.getSelectionProvider());
	}

	@Override
	public void setFocus() {
		

	}

}
