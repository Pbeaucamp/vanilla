package bpm.fa.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fa.ui.Messages;

public class DialogMdx extends Dialog{
	

	private Text text;
	private String mdx;
	public DialogMdx(Shell parentShell, String mdx) {
		super(parentShell);
		this.mdx =  mdx;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		text = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(!text.getText().trim().isEmpty());
				
			}
		});
		return text;
	}
	
	@Override
	protected void okPressed() {
		mdx = text.getText();
		super.okPressed();
	}
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogMdx_0);
		if (mdx != null){
			text.setText(mdx);
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText(Messages.DialogMdx_1);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	public String getMdx(){
		return mdx;
	}
}
