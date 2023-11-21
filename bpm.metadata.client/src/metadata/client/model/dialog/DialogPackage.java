package metadata.client.model.dialog;

import metadata.client.model.composites.CompositePackage;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;

public class DialogPackage extends Dialog {
	
	private BusinessModel model;
	private IBusinessPackage pack;
	private CompositePackage composite;
	
	
	public DialogPackage(Shell parentShell, BusinessModel model, IBusinessPackage pack) {
		super(parentShell);
		this.model = model;
		this.pack = pack;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public DialogPackage(Shell parentShell, BusinessModel model) {
		super(parentShell);
		this.model = model;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositePackage(parent, SWT.NONE, model, pack);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				getButton(IDialogConstants.OK_ID).setEnabled(composite.isFilled());
				
			}
		});
		return parent;
	}

	@Override
	protected void okPressed() {
		composite.setPackageDatas();
		if (pack == null){
			pack = composite.getBusinessPackage();
		}
		super.okPressed();
	}
	
	
	public IBusinessPackage getBusinessPackage(){
		return pack;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		
		if (pack == null){
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}
}
