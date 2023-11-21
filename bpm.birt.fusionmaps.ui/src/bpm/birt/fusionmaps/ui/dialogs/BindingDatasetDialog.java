package bpm.birt.fusionmaps.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.designer.internal.ui.dialogs.BindingPage;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class BindingDatasetDialog extends Dialog{

	private ExtendedItemHandle handle;
	private BindingPage bindingPage;
	
	public BindingDatasetDialog(Shell parentShell, ExtendedItemHandle handle) {
		super(parentShell);
		this.handle = handle;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		List elements = new ArrayList();
		elements.add(handle);
		
		Composite bindingComp = new Composite(parent, SWT.FILL);
		bindingComp.setLayout(new GridLayout());
		bindingComp.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false, 2, 1));
		
		bindingPage = new BindingPage(bindingComp, SWT.NONE);			
		bindingPage.setInput(elements);
		return bindingComp;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}
}
