package bpm.gateway.ui.tools.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.i18n.Messages;

public class DialogPickupParameter extends Dialog {
	


	private Combo params;
	private Parameter pSelected;
	private Button vButton, pButton, uButton;
	private List<Parameter> pList;
	
	private Text type, valueText;
	private String value;
	
	public DialogPickupParameter(Shell parentShell, List<Parameter> pList) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.pList = pList;
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		vButton = new Button(main, SWT.RADIO);
		vButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		vButton.setText(Messages.DialogPickupParameter_0);
		
		
		pButton = new Button(main, SWT.RADIO);
		pButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		pButton.setText(Messages.DialogPickupParameter_1);
		
		uButton = new Button(main, SWT.RADIO);
		uButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		uButton.setText(Messages.DialogPickupParameter_2);
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogPickupParameter_3);
		
		params = new Combo(main, SWT.READ_ONLY);
		params.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		params.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (vButton.getSelection() && params.getSelectionIndex() != -1){
					for(Variable v : ResourceManager.getInstance().getVariables()){
						if (v.getName().equals(params.getText())){
							type.setText(Variable.VARIABLES_TYPES[v.getType()]);
							break;
						}
					}
				}
				else if (pButton.getSelection() && params.getSelectionIndex() != -1){
					for(Parameter p : pList){
						if (p.getName().equals(params.getText())){
							type.setText(Parameter.PARAMETER_TYPES[p.getType()]);
							break;
						}
					}
				}
				
				getButton(IDialogConstants.OK_ID).setEnabled((params.getSelectionIndex() != -1));
			}
			
		});
		
		
		
		SelectionListener listener = new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> l = new ArrayList<String>();
				
				if (vButton.getSelection()){
					for(Variable v : ResourceManager.getInstance().getVariables()){
						l.add(v.getName());
					}
					params.setEnabled(true);
					valueText.setEnabled(false);
				}
				else if (pButton.getSelection()){
					for(Parameter p : pList){
						l.add(p.getName());
					}
					params.setEnabled(true);
					valueText.setEnabled(false);
				}
				else{
					params.setEnabled(false);
					valueText.setEnabled(true);
				}
				
				params.setItems(l.toArray(new String[l.size()]));
				

			}
			
		};
		vButton.addSelectionListener(listener);
		pButton.addSelectionListener(listener);
		uButton.addSelectionListener(listener);
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogPickupParameter_4);
		
		type = new Text(main, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l3.setText(Messages.DialogPickupParameter_5);
		
		valueText = new Text(main, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		valueText.setLayoutData(new GridData(GridData.FILL_BOTH));
		valueText.setEnabled(false);
		valueText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(valueText.getText().length() > 0);
				
			}
			
		});
		
		
		return main;
	}
	
	
	
	public String getValue(){
		return value;
	}


	@Override
	protected void okPressed() {
		
		if (pButton.getSelection()){
			for(Parameter p : pList){
				if (p.getName().equals(params.getText())){
					value = p.getOuputName();
					break;
				}
			}
		}
		else if (vButton.getSelection()){
			for(Variable v : ResourceManager.getInstance().getVariables()){
				if (v.getName().equals(params.getText())){
					value = v.getOuputName();
				}
			}
		}
		else{
			value = valueText.getText();
		}
		
		
		
		
		super.okPressed();
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogPickupParameter_6);
		
		getShell().setSize(600, 300);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}


	@Override
	protected Control createButtonBar(Composite parent) {
		Control c =  super.createButtonBar(parent);
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return c;
	}
}

