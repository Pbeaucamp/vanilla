package org.fasd.views.dialogs;

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
import org.fasd.i18N.LanguageText;

public class DialogCombo extends Dialog {

	private Combo combo;
	private String value;
	private List<String> list;
	
	public DialogCombo(Shell parentShell, List<String> list) {
		super(parentShell);
		this.list = list;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(LanguageText.DialogCombo_0);
		
		combo = new Combo(c, SWT.NONE);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		combo.setItems(list.toArray(new String[list.size()]));
		
		return c;
	}
	
	public String getValue(){
		return value;
	}

	@Override
	protected void okPressed() {
		value = combo.getText();
		super.okPressed();
	}

	
}
