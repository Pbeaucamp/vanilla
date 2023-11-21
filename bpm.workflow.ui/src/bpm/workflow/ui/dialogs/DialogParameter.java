package bpm.workflow.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkfowModelParameter;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

public class DialogParameter extends Dialog{

	private Text name, defaultValue;
	
	private ModifyListener listener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			boolean nameValid = !name.getText().trim().equals(""); //$NON-NLS-1$
			
			for(WorkfowModelParameter p : ((WorkflowModel)Activator.getDefault().getCurrentModel()).getParameters()){
				if (p.getName().endsWith(name.getText())){
					if(parameter != null && !parameter.getName().equals(name.getText())) {
						nameValid = false;
						break;
					}
				}
			}
			
			getButton(IDialogConstants.OK_ID).setEnabled(nameValid);
		}
	};

	private WorkfowModelParameter parameter;
	
	public DialogParameter(Shell parentShell, WorkfowModelParameter o) {
		super(parentShell);
		this.parameter = o;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogParameter_1);
		l.setLayoutData(new GridData());
		
		name = new Text(main, SWT.BORDER);
		name.setText(Messages.DialogParameter_2);
		name.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
				
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogParameter_3);
		l.setLayoutData(new GridData());
		
		defaultValue = new Text(main, SWT.BORDER);
		defaultValue.setText(Messages.DialogParameter_4);
		defaultValue.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));		
		
		if(parameter != null) {
			name.setText(parameter.getName());
			defaultValue.setText(parameter.getDefaultValue());
		}
		
		name.addModifyListener(listener);
		defaultValue.addModifyListener(listener);
		
		return main;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if(parameter == null) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}

	
	@Override
	protected void okPressed() {
		if(parameter == null) {
			parameter= new WorkfowModelParameter();
		    ((WorkflowModel)Activator.getDefault().getCurrentModel()).addParameter(parameter);
		}
		parameter.setName(name.getText().replaceAll("[^a-zA-Z0-9]", "")); //$NON-NLS-1$ //$NON-NLS-2$
		parameter.setDefaultValue(defaultValue.getText());
		
		super.okPressed();
	}
}
