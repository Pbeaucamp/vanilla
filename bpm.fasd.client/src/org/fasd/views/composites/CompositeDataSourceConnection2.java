package org.fasd.views.composites;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.sql.SQLConnection;
import org.freeolap.FreemetricsPlugin;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

public class CompositeDataSourceConnection2 extends Composite {

	private Combo url;
	private Text name, password, user, host, port, dataBaseName;
	private ComboViewer driverName;
	private Button useFullUrl;

	private DataSourceConnection dataSourceConnection;

	public CompositeDataSourceConnection2(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));

		createContent();
		loadDrivers();
	}

	public CompositeDataSourceConnection2(Composite container, DataSourceConnection elem, IViewSite viewSite) {
		this(container, SWT.NONE);
		this.dataSourceConnection = elem;

		Composite buttonBar = new Composite(container, SWT.NONE);
		buttonBar.setLayout(new GridLayout(2, false));
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button ok = new Button(buttonBar, SWT.PUSH);
		ok.setText(LanguageText.CompositeDataSourceConnection2_0);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					getDataSourceConnection();
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), LanguageText.CompositeDataSourceConnection2_1, LanguageText.CompositeDataSourceConnection2_2 + ex.getMessage());
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		Button cancel = new Button(buttonBar, SWT.PUSH);
		cancel.setText(LanguageText.CompositeDataSourceConnection2_3);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
		});

		fillDatas();
	}

	public void fillDatas() {
		if (dataSourceConnection == null || dataSourceConnection.getType().equals("oda")) { //$NON-NLS-1$
			return;
		}

		try {
			for (DriverInfo inf : ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml").getDriversInfo()) { //$NON-NLS-1$
				if (inf.getClassName().equals(dataSourceConnection.getDriver()) || inf.getName().equals(dataSourceConnection.getDriver())) {
					driverName.setSelection(new StructuredSelection(inf));
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		url.setText(dataSourceConnection.getUrl());
		url.setEnabled(false);
		user.setText(dataSourceConnection.getUser());
		password.setText(dataSourceConnection.getPass());
		name.setText(dataSourceConnection.getName());

		/*
		 * split the url
		 */
		String fullUrl = dataSourceConnection.getUrl();
		String s = fullUrl.substring(fullUrl.indexOf("://") + 3); //$NON-NLS-1$

		if (s.contains(":")) { //$NON-NLS-1$
			host.setText(s.substring(0, s.indexOf(":"))); //$NON-NLS-1$
			String _s = s.substring(s.indexOf(":") + 1); //$NON-NLS-1$
			port.setText(_s.substring(0, _s.indexOf("/"))); //$NON-NLS-1$
			dataBaseName.setText(_s.substring(_s.indexOf("/") + 1)); //$NON-NLS-1$
		} else if (s.contains("/")) { //$NON-NLS-1$
			host.setText(s.substring(0, s.indexOf("/"))); //$NON-NLS-1$
			dataBaseName.setText(s.substring(s.indexOf("/") + 1)); //$NON-NLS-1$
		} else if (s.contains("/")) { //$NON-NLS-1$
			try {
				url.setText(s.substring(0, s.indexOf("@"))); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();

			}
		}

	}

	private void loadDrivers() {
		try {
			driverName.setInput(ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml").getDriversInfo()); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), LanguageText.CompositeDataSourceConnection2_18, LanguageText.CompositeDataSourceConnection2_19 + e.getMessage());
		}
	}

	private void createContent() {

		Label l2 = new Label(this, SWT.None);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeDataSourceConnection2_20);

		name = new Text(this, SWT.SINGLE | SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		l2 = new Label(this, SWT.None);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeDataSourceConnection2_21);

		driverName = new ComboViewer(this, SWT.READ_ONLY);
		driverName.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		driverName.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray();
			}
		});
		driverName.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DriverInfo) element).getName();
			}
		});

		useFullUrl = new Button(this, SWT.CHECK);
		useFullUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useFullUrl.setText(LanguageText.CompositeDataSourceConnection2_22);
		useFullUrl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				url.setEnabled(useFullUrl.getSelection());
				host.setEnabled(!useFullUrl.getSelection());
				port.setEnabled(!useFullUrl.getSelection());
				dataBaseName.setEnabled(!useFullUrl.getSelection());
			}
		});

		Label l3 = new Label(this, SWT.None);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeDataSourceConnection2_23);

		url = new Combo(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		url.setItems(CompositeDataSource.K_URLS);
		url.select(0);
		url.setEnabled(false);

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(LanguageText.CompositeDataSourceConnection2_24);

		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(LanguageText.CompositeDataSourceConnection2_25);

		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(LanguageText.CompositeDataSourceConnection2_26);

		dataBaseName = new Text(this, SWT.BORDER);
		dataBaseName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l4 = new Label(this, SWT.None);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeDataSourceConnection2_27);

		user = new Text(this, SWT.SINGLE | SWT.BORDER);
		user.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l7 = new Label(this, SWT.None);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.CompositeDataSourceConnection2_28);

		password = new Text(this, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button test = new Button(this, SWT.PUSH);
		test.setText(LanguageText.CompositeDataSourceConnection2_29);
		test.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		test.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					connect();
					MessageDialog.openInformation((CompositeDataSourceConnection2.this).getShell(), LanguageText.CompositeDataSourceConnection2_30, LanguageText.CompositeDataSourceConnection2_31);
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					MessageDialog.openError(CompositeDataSourceConnection2.this.getShell(), LanguageText.CompositeDataSourceConnection2_32, ex.getMessage());
				} catch (SQLException ex) {
					ex.printStackTrace();
					MessageDialog.openError(CompositeDataSourceConnection2.this.getShell(), LanguageText.CompositeDataSourceConnection2_33, ex.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError((CompositeDataSourceConnection2.this).getShell(), LanguageText.CompositeDataSourceConnection2_34, ex.getMessage());
				}
			}
		});
	}

	public void connect() throws Exception {
		DriverInfo inf = (DriverInfo) ((IStructuredSelection) driverName.getSelection()).getFirstElement();

		SQLConnection con = null;
		if (useFullUrl.getSelection()) {

			con = new SQLConnection(url.getText(), user.getText(), password.getText(), inf.getFile(), inf.getClassName(), null, ""); //$NON-NLS-1$
		} else {

			String fullUrl = inf.getUrlPrefix();
			fullUrl += host.getText();

			if (!port.getText().equals("")) { //$NON-NLS-1$
				fullUrl += ":" + port.getText(); //$NON-NLS-1$
			}

			fullUrl += "/" + dataBaseName.getText(); //$NON-NLS-1$

			con = new SQLConnection(fullUrl, user.getText(), password.getText(), inf.getFile(), inf.getClassName(), null, ""); //$NON-NLS-1$
		}

		VanillaJdbcConnection s = con.getConnection();
		ConnectionManager.getInstance().returnJdbcConnection(s);
	}

	public DataSourceConnection getDataSourceConnection() throws Exception {
		if (dataSourceConnection == null) {
			dataSourceConnection = new DataSourceConnection();

		}

		dataSourceConnection.setPass(password.getText());
		dataSourceConnection.setUser(user.getText());
		dataSourceConnection.setName(name.getText());
		DriverInfo inf = (DriverInfo) ((IStructuredSelection) driverName.getSelection()).getFirstElement();
		dataSourceConnection.setDriver(inf.getClassName());
		dataSourceConnection.setDriverFile(inf.getFile());

		SQLConnection con = null;
		if (useFullUrl.getSelection()) {

			con = new SQLConnection(url.getText(), user.getText(), password.getText(), inf.getFile(), inf.getClassName(), null, ""); //$NON-NLS-1$
		} else {

			String fullUrl = inf.getUrlPrefix();
			fullUrl += host.getText();

			if (!port.getText().equals("")) { //$NON-NLS-1$
				fullUrl += ":" + port.getText(); //$NON-NLS-1$
			}

			fullUrl += "/" + dataBaseName.getText(); //$NON-NLS-1$

			con = new SQLConnection(fullUrl, user.getText(), password.getText(), inf.getFile(), inf.getClassName(), null, ""); //$NON-NLS-1$
		}
		dataSourceConnection.setUser(con.getUser());
		dataSourceConnection.setPass(con.getPass());
		dataSourceConnection.setUrl(con.getUrl());
		dataSourceConnection.setConnection(con);
		return dataSourceConnection;
	}
}
