package bpm.mdm.ui.diff;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.ManagedForm;

import bpm.mdm.model.Entity;
import bpm.mdm.model.helper.StorageHelper;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;

public class DialogDiff extends Dialog{
	private IEntityStorage store;
	private DiffResult diff;
	private Entity entity;
	
	private DiffMasterDetails master;
	
	public DialogDiff(Shell parentShell, IEntityStorage store, DiffResult res, Entity entity) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		diff = res;
		this.store = store;
		this.entity = entity;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		ManagedForm form = new ManagedForm(parent);
		master = new DiffMasterDetails(entity);
		master.createContent(form);
		
		form.getForm().setLayoutData(new GridData(GridData.FILL_BOTH));
		return form.getForm();
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		master.bind(diff);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText(Messages.DialogDiff_0);
		getButton(IDialogConstants.CANCEL_ID).setEnabled(!Activator.getDefault().getControler().isDirty());
		getButton(IDialogConstants.CANCEL_ID).setText(Messages.DialogDiff_1);
	}
	
	@Override
	protected void cancelPressed() {
		try{
			StorageHelper.store(diff, store, entity);
		}catch(Throwable t){
			t.printStackTrace();
			return;
		}
		super.cancelPressed();
	}

}
