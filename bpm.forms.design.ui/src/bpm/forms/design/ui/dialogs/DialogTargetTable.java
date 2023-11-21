package bpm.forms.design.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.forms.core.design.ITargetTable;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;

public class DialogTargetTable extends Dialog{

	
	private Text label;
	private Text description;
	private Text physicalName;
	
	private ITargetTable targetTable;
	
	public DialogTargetTable(Shell parentShell) {
		super(parentShell);
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogTargetTable_0);
		
		label = new Text(main, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogTargetTable_1);
		
		
		physicalName = new Text(main, SWT.BORDER);
		physicalName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogTargetTable_2);
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		return main;
	}

	
	@Override
	protected void okPressed() {
		ITargetTable table = Activator.getDefault().getFactoryModel().createTargetTable();
		table.setDatabasePhysicalName(physicalName.getText());
		table.setDescription(description.getText());
		table.setName(label.getText());
		
		try{
			targetTable = Activator.getDefault().getServiceProvider().getDefinitionService().saveTargetTable(table);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogTargetTable_3, Messages.DialogTargetTable_4 + ex.getMessage());
			return;
		}
		
		
		super.okPressed();
	}
	
	public ITargetTable getTargetTable(){
		return targetTable;
	}
}
