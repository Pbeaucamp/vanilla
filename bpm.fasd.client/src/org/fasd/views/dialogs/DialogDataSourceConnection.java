package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.views.composites.CompositeDataSource;
import org.fasd.views.composites.CompositeDataSourceConnection;

public class DialogDataSourceConnection extends Dialog {

	private CompositeDataSourceConnection composite;
	private DataSourceConnection dataSourceConnection;

	public DialogDataSourceConnection(Shell parent) {
		super(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeDataSourceConnection(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return composite;
	}

	@Override
	protected void okPressed() {
		try {
			dataSourceConnection = composite.getDataSourceConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getShell(), LanguageText.DialogDataSourceConnection_0, ex.getMessage());
			return;
		}
		super.okPressed();
	}

	public DataSourceConnection getDataSourceConnection() {
		return dataSourceConnection;
	}

}
