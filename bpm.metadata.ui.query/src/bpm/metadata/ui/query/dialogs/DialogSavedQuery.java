package bpm.metadata.ui.query.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.ui.query.i18n.Messages;

public class DialogSavedQuery extends Dialog {
	
	private String savedName;
	private String savedDescription;
	
	public DialogSavedQuery(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setSize(380, 270);
		getShell().setText(Messages.DialogSavedQuery_0);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		mainComp.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(mainComp, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblName.setText(Messages.DialogSavedQuery_1);
		
		final Text txtSavedName = new Text(mainComp, SWT.BORDER);
		txtSavedName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtSavedName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				savedName = txtSavedName.getText();
			}
		});
		
		Label lblDescrition = new Label(mainComp, SWT.NONE);
		lblDescrition.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		lblDescrition.setText(Messages.DialogSavedQuery_2);
		
		final Text txtDescription = new Text(mainComp, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		txtDescription.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		txtDescription.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				savedDescription = txtDescription.getText();
			}
		});
		
		return mainComp;
	}
	
	public String getSavedName() {
		return savedName;
	}
	
	public String getSavedDescription() {
		return savedDescription;
	}
}

