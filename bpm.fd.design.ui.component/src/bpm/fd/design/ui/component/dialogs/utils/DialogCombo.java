package bpm.fd.design.ui.component.dialogs.utils;

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

import bpm.fd.design.ui.component.Messages;

public class DialogCombo extends Dialog{
	

	private List<String> values;
	private Combo combo;
	private String title, label = Messages.DialogCombo_0;
	private String value;
	
	public DialogCombo(Shell parentShell, List<String> values, String title, String label) {
		super(parentShell);
		this.values = values;
		this.title = title;
		this.label = label;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(label);
		
		combo = new Combo(c, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo.setItems(values.toArray(new String[values.size()]));
		return c;
	}
	
	
	@Override
	protected void okPressed() {
		value = combo.getText();
		super.okPressed();
	}

	public String getValue(){
		return value;
	}
}
