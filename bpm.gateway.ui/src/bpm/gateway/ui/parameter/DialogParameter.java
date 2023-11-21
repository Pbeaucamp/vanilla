package bpm.gateway.ui.parameter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.i18n.Messages;

public class DialogParameter extends Dialog {

	
	private Text name;
	private Text defaultValue;
	private Combo type;
	private ModifyListener listener;
	
	private Parameter parameter;
	
	public DialogParameter(Shell parentShell) {
		super(parentShell);
	}
	
	public DialogParameter(Shell parentShell, Parameter parameter) {
		super(parentShell);
		this.parameter = parameter;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogParameter_0);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogParameter_1);
		
		type = new Combo(main, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(Parameter.PARAMETER_TYPES);
		type.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (type.getSelectionIndex() == Parameter.DATE){
					defaultValue.setToolTipText(Messages.DialogParameter_2);
					if (defaultValue.getText().equals("")){ //$NON-NLS-1$
						defaultValue.setText("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
					}
				}
				
										
				try{
					Parameter p = new Parameter();
					p.setType(type.getSelectionIndex());
					p.getValue();
					defaultValue.setBackground(null);
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}catch(Exception ex){
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					defaultValue.setBackground(null);
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					Color color = reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
					
					defaultValue.setBackground(color);
				}
				
			}
			
		});
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.DialogParameter_5);
		
		defaultValue = new Text(main, SWT.BORDER);
		defaultValue.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		if(parameter != null) {
			name.setText(parameter.getName() != null ? parameter.getName() : ""); //$NON-NLS-1$
			name.setEnabled(false);
			type.select(parameter.getType() != -1 ? parameter.getType() : 0);
			defaultValue.setText(parameter.getDefaultValue() != null ? parameter.getDefaultValue() : ""); //$NON-NLS-1$
		}
		
		createListener();
		return main;
	}
	
	
	private void createListener(){
		listener = new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				if (defaultValue.getText().equals("") ||  //$NON-NLS-1$
					name.getText().equals("") || //$NON-NLS-1$
					type.getSelectionIndex() == -1){
					getButton(IDialogConstants.OK_ID).setEnabled(false);

				}
				else{

					parameter = new Parameter();
					parameter.setType(type.getSelectionIndex());
					parameter.setDefaultValue(defaultValue.getText());
					
					/*
					 * test to get the value from the defaultValue string
					 * if it failed, the default is not correct
					 */
					try{
						parameter.getValue();
						defaultValue.setBackground(null);
						getButton(IDialogConstants.OK_ID).setEnabled(true);
						
					}catch(Exception es){
						es.printStackTrace();
						ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
						Color color = reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
						
						defaultValue.setBackground(color);
						getButton(IDialogConstants.OK_ID).setEnabled(false);
						
					}
					
					
				}
				
			}
			
		};
		defaultValue.addModifyListener(listener);
		name.addModifyListener(listener);
		
	}

	@Override
	protected void okPressed() {
		defaultValue.removeModifyListener(listener);
		name.removeModifyListener(listener);
		
		parameter = new Parameter();
		parameter.setName(name.getText());
		parameter.setDefaultValue(defaultValue.getText());
		parameter.setType(type.getSelectionIndex());
		
		super.okPressed();
	}
	
	public Parameter getParameter(){
		return parameter;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogParameter_8);
		
		getShell().setSize(400, 300);
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
	}

	
}
