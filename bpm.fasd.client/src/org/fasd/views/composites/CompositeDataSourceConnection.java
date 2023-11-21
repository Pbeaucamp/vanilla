package org.fasd.views.composites;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
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

public class CompositeDataSourceConnection extends Composite {

	private Combo driverName, url;
	private Text name, password, user, schName;

	private String[] drivers;

	private DataSourceConnection dataSourceConnection;

	public CompositeDataSourceConnection(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		loadDrivers();
		createContent();
	}

	public CompositeDataSourceConnection(Composite container, DataSourceConnection elem, IViewSite viewSite) {
		this(container, SWT.NONE);
		this.dataSourceConnection = elem;

		Composite buttonBar = new Composite(container, SWT.NONE);
		buttonBar.setLayout(new GridLayout(2, false));
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button ok = new Button(buttonBar, SWT.PUSH);
		ok.setText(LanguageText.CompositeDataSourceConnection_0);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					getDataSourceConnection();
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), LanguageText.CompositeDataSourceConnection_1, LanguageText.CompositeDataSourceConnection_2 + ex.getMessage());
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		Button cancel = new Button(buttonBar, SWT.PUSH);
		cancel.setText(LanguageText.CompositeDataSourceConnection_3);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
		});

		fillDatas();
	}

	public void fillDatas() {
		if (dataSourceConnection == null) {
			return;
		}

		try {
			for (DriverInfo inf : ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml").getDriversInfo()) { //$NON-NLS-1$
				if (inf.getClassName().equals(dataSourceConnection.getDriver()) || inf.getName().equals(dataSourceConnection.getDriver())) {
					driverName.setText(inf.getName());
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		url.setText(dataSourceConnection.getUrl());
		user.setText(dataSourceConnection.getUser());
		password.setText(dataSourceConnection.getPass());
		name.setText(dataSourceConnection.getName());
	}

	private void loadDrivers() {
		try {
			int k = ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml").getDriversInfo().size(); //$NON-NLS-1$

			drivers = new String[k];
			int i = 0;
			for (String inf : ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath() + "/resources/driverjdbc.xml").getDriversName()) { //$NON-NLS-1$
				drivers[i] = inf;
				i++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createContent() {

		Label l2 = new Label(this, SWT.None);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeDataSourceConnection_7);

		name = new Text(this, SWT.SINGLE | SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		l2 = new Label(this, SWT.None);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeDataSourceConnection_8);

		driverName = new Combo(this, SWT.BORDER);
		driverName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		driverName.setItems(drivers);
		driverName.select(0);

		Label l3 = new Label(this, SWT.None);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeDataSourceConnection_9);

		url = new Combo(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		url.setItems(CompositeDataSource.K_URLS);
		url.select(0);

		Label l4 = new Label(this, SWT.None);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeDataSourceConnection_10);

		user = new Text(this, SWT.SINGLE | SWT.BORDER);
		user.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l7 = new Label(this, SWT.None);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.CompositeDataSourceConnection_11);

		password = new Text(this, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.CompositeDataSourceConnection_12);

		schName = new Text(this, SWT.SINGLE | SWT.BORDER);
		schName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button test = new Button(this, SWT.PUSH);
		test.setText(LanguageText.CompositeDataSourceConnection_13);
		test.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		test.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					connect();
					MessageDialog.openInformation((CompositeDataSourceConnection.this).getShell(), LanguageText.CompositeDataSourceConnection_14, LanguageText.CompositeDataSourceConnection_15);
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					MessageDialog.openError(CompositeDataSourceConnection.this.getShell(), LanguageText.CompositeDataSourceConnection_16, ex.getMessage());
				} catch (SQLException ex) {
					ex.printStackTrace();
					MessageDialog.openError(CompositeDataSourceConnection.this.getShell(), LanguageText.CompositeDataSourceConnection_17, ex.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError((CompositeDataSourceConnection.this).getShell(), LanguageText.CompositeDataSourceConnection_18, ex.getMessage());
				}
			}
		});
	}

	public void connect() throws Exception {
		SQLConnection con = new SQLConnection(url.getText(), user.getText(), password.getText(), ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath()).getInfo(driverName.getText()).getFile(), ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath()).getInfo(driverName.getText()).getClassName(), schName.getText(), ""); //$NON-NLS-1$
		VanillaJdbcConnection s = con.getConnection();
		ConnectionManager.getInstance().returnJdbcConnection(s);
	}

	public DataSourceConnection getDataSourceConnection() throws Exception {
		if (dataSourceConnection == null) {
			dataSourceConnection = new DataSourceConnection();

		}

		dataSourceConnection.setUrl(url.getText());
		dataSourceConnection.setPass(password.getText());
		dataSourceConnection.setUser(user.getText());
		dataSourceConnection.setName(name.getText());
		dataSourceConnection.setDriver(ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath()).getInfo(driverName.getText()).getClassName());
		dataSourceConnection.setDriverFile(ListDriver.getInstance(Platform.getInstallLocation().getURL().getPath()).getInfo(driverName.getText()).getFile());
		return dataSourceConnection;
	}
}
