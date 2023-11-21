package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;

public class DialogAddColumn extends Dialog {
	private DataObjectItem column;
	private Text name, sql;
	private Combo attribut, sqlType;

	public DialogAddColumn(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected Control createDialogArea(Composite par) {
		Composite parent = new Composite(par, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogAddColumn_Name);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogAddColumn_SQL_Type);

		sqlType = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		sqlType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sqlType.setItems(new String[] { "NUMERIC", "VARCHAR", "TIMESTAMP" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		sqlType.select(2);
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogAddColumn_Attr);

		attribut = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		attribut.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		attribut.setItems(new String[] { "Dimension", "Measure", "Property", "Undefined" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		attribut.select(0);

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText("SQL"); //$NON-NLS-1$

		sql = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		sql.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));

		return parent;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(LanguageText.DialogAddColumn_Add_Calculated);
		this.getShell().setSize(400, 300);
	}

	@Override
	protected void okPressed() {
		column = new DataObjectItem();
		column.setType("calculated"); //$NON-NLS-1$
		column.setName(name.getText());
		column.setOrigin(sql.getText());
		column.setSqlType(sqlType.getText());
		super.okPressed();
	}

	public DataObjectItem getItem() {
		return column;
	}
}
