package bpm.gateway.ui.tools.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.ui.i18n.Messages;

public class DialogComboString extends Dialog {

	private List<String> values;
	private String originalValue;
	private Combo combo;
	private String selectedValue; 
	
	public DialogComboString(Shell parentShell, List<String> contents, String originalValue) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		values = contents;
		this.originalValue = originalValue;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(composite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogComboString_0);
		
		combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo.setItems(values.toArray(new String[values.size()]));
		
		for(int i = 0; i < values.size(); i++){
			if (values.get(i).equals(originalValue)){
				combo.select(i);
				break;
			}
		}
		
		return composite;
	}

	@Override
	protected void okPressed() {
		selectedValue = combo.getText();
		super.okPressed();
	}
	
	
	public String getValue(){
		return selectedValue;
	}

	
	
}
