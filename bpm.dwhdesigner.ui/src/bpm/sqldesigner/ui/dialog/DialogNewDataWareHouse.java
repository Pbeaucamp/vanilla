package bpm.sqldesigner.ui.dialog;

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

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.i18N.Messages;

public class DialogNewDataWareHouse extends Dialog {
	
	private DatabaseCluster fCl;
	
	private Text txtName;
	private Label lblError;
	
	private String name = ""; //$NON-NLS-1$
	private boolean isValid = false;
	
	public DialogNewDataWareHouse(Shell parentShell, DatabaseCluster fCl) {
		super(parentShell);
		this.fCl = fCl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblName = new Label(main, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		lblName.setText(Messages.DialogNewDataWareHouse_1);
		
		txtName = new Text(main, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		txtName.setText(Messages.DialogNewDataWareHouse_2);
		txtName.addModifyListener(listener);
		
		lblError = new Label(main, SWT.NONE);
		lblError.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		lblError.setText(""); //$NON-NLS-1$
		
		refreshValid(txtName);
		
		return main;
	}
	
	protected void initializeBounds() {
		this.getShell().setText(Messages.DialogNewDataWareHouse_4);
		getShell().setSize(400, 150);
	}
	
	@Override
	protected void okPressed() {
		if(isValid){
			super.okPressed();
		}
	}
	
	public String getValue(){
		return name;
	}
	
	private ModifyListener listener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			refreshValid((Text)e.getSource());
		}
	};
	
	private void refreshValid(Text source){
		name = source.getText();
		for(DocumentSnapshot s : fCl.getDocumentSnapshots()){
			if (s.getName().equals(name)){
				lblError.setText(Messages.DialogNewDataWareHouse_5);
				isValid = false;
				return;
			}
		}
		
		lblError.setText(""); //$NON-NLS-1$
		isValid = true;
	}
}

