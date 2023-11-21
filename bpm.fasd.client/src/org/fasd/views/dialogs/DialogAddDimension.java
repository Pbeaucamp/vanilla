package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;

public class DialogAddDimension extends Dialog {
	private OLAPDimension dimension;
	private Text name, desc;
	private Button isDate;
	private Combo loadMethod;

	public DialogAddDimension(Shell parentShell) {
		super(parentShell);

	}

	protected Control createDialogArea(Composite par) {
		par.setLayout(new GridLayout());
		Composite parent = new Composite(par, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));
		parent.setLayout(new GridLayout(3, false));

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogAddDimension_Name_);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogAddDimension_Desc_);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.DialogAddDimension_Lood_Method);

		loadMethod = new Combo(parent, SWT.BORDER);
		loadMethod.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		loadMethod.setItems(new String[] { "cube_startup", "server_startup", "cube_open", "on_demand" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		loadMethod.select(0);

		isDate = new Button(parent, SWT.CHECK);
		isDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		isDate.setText(LanguageText.DialogAddDimension_Is_Date);

		return parent;

	}

	@Override
	protected void okPressed() {
		dimension = new OLAPDimension();
		dimension.setName(name.getText());
		dimension.setDate(isDate.getSelection());
		dimension.setDesc(desc.getText());

		dimension.setLoadMethod(loadMethod.getText());
		super.okPressed();
	}

	public OLAPDimension getDimension() {
		return dimension;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(LanguageText.DialogAddDimension_New);
		this.getShell().setSize(400, 350);
	}
}
