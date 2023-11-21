package org.fasd.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;

public class DialogSqlSelect extends Dialog {

	private Text query;
	private Text name;
	private Button testQuery;
	private String sqlSelect;
	private String tableName;
	private DataSourceConnection connection;

	private boolean editing = false;
	private boolean checked = false;

	public DialogSqlSelect(Shell parentShell, DataSourceConnection connection) {
		super(parentShell);
		this.connection = connection;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public DialogSqlSelect(Shell parentShell, DataSourceConnection connection, String querySql, String name) {
		super(parentShell);
		this.connection = connection;
		this.sqlSelect = querySql;
		this.tableName = name;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		editing = true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite c = new Composite(main, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogSqlSelect_0);

		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateOkButton();

			}
		});

		query = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		query.setLayoutData(new GridData(GridData.FILL_BOTH));
		query.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateOkButton();
				checked = false;
			}
		});
		testQuery = new Button(main, SWT.PUSH);
		testQuery.setLayoutData(new GridData(GridData.END, GridData.END, false, false));
		testQuery.setText(LanguageText.DialogSqlSelect_1);
		testQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					connection.getFactTableFromQuery("dummy", query.getText()); //$NON-NLS-1$
					MessageDialog.openInformation(getShell(), LanguageText.DialogSqlSelect_3, LanguageText.DialogSqlSelect_4);
					checked = true;
				} catch (Exception ex) {
					MessageDialog.openError(getShell(), LanguageText.DialogSqlSelect_5, LanguageText.DialogSqlSelect_6 + ex.getMessage());
				}
			}
		});

		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(LanguageText.DialogSqlSelect_7);
		getShell().setSize(600, 400);
		if (sqlSelect != null) {
			query.setText(sqlSelect);
		}
		if (tableName != null) {
			name.setText(tableName);
		}
	}

	public String getQuery() {
		return sqlSelect;
	}

	public String getName() {
		return tableName;
	}

	@Override
	protected void okPressed() {
		try {
			if (!editing) {
				for (DataObject o : connection.getParent().getDataObjects()) {
					if (o.getName().equals(name.getText())) {
						throw new Exception(LanguageText.DialogSqlSelect_8);
					}
				}
			}

			sqlSelect = query.getText();

			if (!checked) {
				connection.getFactTableFromQuery("dummy", query.getText()); //$NON-NLS-1$
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getShell(), LanguageText.DialogSqlSelect_10, LanguageText.DialogSqlSelect_11 + ex.getMessage());
			return;
		}
		tableName = name.getText();
		super.okPressed();
	}

	private void updateOkButton() {
		getButton(IDialogConstants.OK_ID).setEnabled(!(name.getText().trim().equals("") || query.getText().trim().equals(""))); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
}
