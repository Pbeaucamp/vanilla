package org.fasd.views.composites;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.fasd.cubewizard.DialogSelectRepositoryItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.inport.ParserPentahoJDBCFile;
import org.fasd.olap.ServerConnection;
import org.fasd.sql.SQLConnection;
import org.freeolap.FreemetricsPlugin;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeDataSource extends Composite {
	private DataSource ds;
	private Composite info;
	private CompositeDataSourceConnection2 connection;
	private Button ok, cancel, browse, browse2;
	private Label l10, l9;
	private TabFolder tabFolder;
	// info
	private static final String[] K_TYPE = { "DataBase", "XML", "FreeMetaData" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final String[] K_LOCATION = { "local", "server" }; //$NON-NLS-1$ //$NON-NLS-2$

	private Text name, transUrl, fileLoc, desc;
	private Combo type, location, server;

	protected Label labelDsName;

	private HashMap<String, ServerConnection> map = new HashMap<String, ServerConnection>();

	// connection
	public static final String[] K_URLS = { "jdbc:hsqldb:file:datas/foodmart/foodmart", //$NON-NLS-1$
			"jdbc:mysql://localhost/freeolap_mondrian", //$NON-NLS-1$
			"jdbc:postgresql://localhost/freeolap_mondrian", //$NON-NLS-1$
			"jdbc:oracle:thin:@127.0.0.1:1521:[dbname]", //$NON-NLS-1$
			"jdbc:jtds:sybase://127.0.0.1:5000/[dbname];useLOBs=false;TDS=4.2", //$NON-NLS-1$
			"jdbc:mysql://localhost:3306/[dbname]", //$NON-NLS-1$
			"jdbc:jtds:sqlserver://127.0.0.1:5000/[dbname]" }; //$NON-NLS-1$

	private SQLConnection con = null;

	private boolean canSave = false;

	private RepositoryItem directoryItem;

	public void selectOtherFolder() {
		if (tabFolder.getSelectionIndex() == 0)
			tabFolder.setSelection(1);
		else
			tabFolder.setSelection(0);
	}

	public int getSelectdFolder() {
		return tabFolder.getSelectionIndex();
	}

	public boolean isDataBase() {
		if (type.getSelectionIndex() == 0)
			return true;
		return false;
	}

	public CompositeDataSource(Composite parent, int style, DataSource d, boolean canSave) {
		super(parent, style);
		this.canSave = canSave;

		ds = d;
		for (ServerConnection c : FreemetricsPlugin.getDefault().getSecurity().getServerConnections()) {
			map.put(c.getName(), c);
		}
		this.setLayout(new GridLayout(2, true));

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		TabItem info = new TabItem(tabFolder, SWT.NONE, 0);
		info.setText(LanguageText.CompositeDataSource_12);
		info.setControl(createInfo(tabFolder));

		TabItem connexion = new TabItem(tabFolder, SWT.NONE, 1);
		connexion.setText(LanguageText.CompositeDataSource_13);
		connexion.setControl(createConnection(tabFolder));

		ok = new Button(this, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.setText(LanguageText.CompositeDataSource_14);
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!ds.getDSName().equals(name.getText())) {
					ds.setDSName(name.getText());
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
					FreemetricsPlugin.getDefault().getFAModel().getListDataSource().renameDataSource(ds, name.getText());
				}

				DataSourceConnection sock = null;

				try {
					sock = connection.getDataSourceConnection();
				} catch (Exception ex) {
					ex.getMessage();
					MessageDialog.openError(getShell(), LanguageText.CompositeDataSource_15, LanguageText.CompositeDataSource_16 + ex.getMessage());
					return;
				}

				ds.setDriver(sock);
				sock.setDesc(desc.getText());
				sock.setDataSourceLocation(location.getText());
				sock.setFileLocation(fileLoc.getText());

				if (location.getText().equals(K_LOCATION[1]))
					sock.setServer(map.get(server.getText()));
				else
					sock.setServer(null);
				sock.setTransUrl(transUrl.getText());
				sock.setType(type.getText());

				FreemetricsPlugin.getDefault().getFAModel().setChanged();

			}

		});

		cancel = new Button(this, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.setText(LanguageText.CompositeDataSource_17);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}

		});
		fillData();
	}

	public void setDataSource() {
		ds.setDSName(name.getText());
		DataSourceConnection sock = null;

		try {
			sock = connection.getDataSourceConnection();
			ds.setDriver(sock);
		} catch (Exception ex) {
			ex.printStackTrace();
			sock = ds.getDriver();
		}
		sock.setDesc(desc.getText());
		sock.setDataSourceLocation(location.getText());
		sock.setFileLocation(fileLoc.getText());
		if (location.getText().equals(K_LOCATION[1])) {
			sock.setServer(map.get(server.getText()));
			sock.setDirectoryItemId(directoryItem.getId() + ""); //$NON-NLS-1$
		}

		else {
			sock.setServer(null);
			sock.setDirectoryItemId(null);
			sock.setServer(null);
		}

		sock.setTransUrl(transUrl.getText());
		sock.setType(type.getText());
	}

	private Control createInfo(TabFolder parent) {
		info = new Composite(parent, SWT.NONE);
		info.setLayout(new GridLayout(3, false));

		labelDsName = new Label(info, SWT.NONE);
		labelDsName.setLayoutData(new GridData());
		labelDsName.setText(LanguageText.CompositeDataSource_19);

		name = new Text(info, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l11 = new Label(info, SWT.NONE);
		l11.setLayoutData(new GridData());
		l11.setText(LanguageText.CompositeDataSource_20);

		desc = new Text(info, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l5 = new Label(info, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.CompositeDataSource_21);

		location = new Combo(info, SWT.BORDER);
		location.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		location.setItems(K_LOCATION);
		location.select(0);
		location.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (location.getText().equals(K_LOCATION[0])) {
					server.setText(""); //$NON-NLS-1$
					server.setEnabled(false);
				} else if (location.getText().equals(K_LOCATION[1])) {
					server.setEnabled(true);
				}
			}

		});

		Label l6 = new Label(info, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.CompositeDataSource_23);

		server = new Combo(info, SWT.BORDER);
		server.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		server.setItems(map.keySet().toArray(new String[map.size()]));

		Label lbl1 = new Label(info, SWT.NONE);
		lbl1.setLayoutData(new GridData());
		lbl1.setText(LanguageText.CompositeDataSource_24);

		type = new Combo(info, SWT.BORDER | SWT.READ_ONLY);
		type.setItems(K_TYPE);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		type.select(0);
		type.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				switch (type.getSelectionIndex()) {
				case 0:
					fileLoc.setVisible(false);
					browse.setVisible(false);
					l9.setVisible(false);
					l10.setVisible(false);
					transUrl.setVisible(false);
					browse2.setVisible(false);
					transUrl.setText(""); //$NON-NLS-1$
					fileLoc.setText(""); //$NON-NLS-1$
					break;

				case 1:
					fileLoc.setVisible(false);
					browse.setVisible(false);
					l10.setVisible(false);
					transUrl.setText(""); //$NON-NLS-1$
					fileLoc.setText(""); //$NON-NLS-1$
					l9.setVisible(true);
					transUrl.setVisible(true);
					browse2.setVisible(true);
					break;
				case 2:
					fileLoc.setVisible(true);
					browse.setVisible(true);
					l9.setVisible(false);
					l10.setVisible(true);
					transUrl.setVisible(false);
					browse2.setVisible(false);
					transUrl.setText(""); //$NON-NLS-1$
					fileLoc.setText(""); //$NON-NLS-1$
				}
			}

		});

		l10 = new Label(info, SWT.NONE);
		l10.setLayoutData(new GridData());
		l10.setText(LanguageText.CompositeDataSource_31);

		fileLoc = new Text(info, SWT.BORDER);
		fileLoc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		browse = new Button(info, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		browse.setText("..."); //$NON-NLS-1$
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (location.getSelectionIndex() == 0) {
					FileDialog d = new FileDialog(info.getShell());

					switch (type.getSelectionIndex()) {
					case 1:
						d.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
						break;
					case 2:
						d.setFilterExtensions(new String[] { "*.freemetadata" }); //$NON-NLS-1$
						break;
					default:
						d.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$
					}

					fileLoc.setText(d.open());
				} else {

					ServerConnection serverCon = null;
					for (String s : map.keySet()) {
						if (s.equals(server.getText())) {
							serverCon = map.get(s);
						}
					}

					if (serverCon == null) {
						MessageDialog.openInformation(getShell(), LanguageText.CompositeDataSource_36, LanguageText.CompositeDataSource_37);
						return;
					}
					if (type.getSelectionIndex() == 2) {
						try {
							DialogSelectRepositoryItem dial = new DialogSelectRepositoryItem(getShell(), IRepositoryApi.FMDT_TYPE, -1);

							if (dial.open() == DialogSelectRepositoryItem.OK) {
								directoryItem = dial.getItem();
								fileLoc.setText(directoryItem.getItemName());
							}
						} catch (Exception ex) {
							MessageDialog.openInformation(getShell(), LanguageText.CompositeDataSource_38, LanguageText.CompositeDataSource_39 + ex.getMessage());
						}

					}
					// import database
					else if (type.getSelectionIndex() == 0) {

					} else {
						MessageDialog.openInformation(getShell(), LanguageText.CompositeDataSource_40, LanguageText.CompositeDataSource_41);
					}

				}

			}
		});

		l9 = new Label(info, SWT.NONE);
		l9.setLayoutData(new GridData());
		l9.setText(LanguageText.CompositeDataSource_42);

		transUrl = new Text(info, SWT.BORDER);
		transUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		browse2 = new Button(info, SWT.PUSH);
		browse2.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		browse2.setText("..."); //$NON-NLS-1$
		browse2.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(info.getShell());
				d.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
				transUrl.setText(d.open());
			}
		});

		fileLoc.setVisible(false);
		browse.setVisible(false);
		l9.setVisible(false);
		l10.setVisible(false);
		transUrl.setVisible(false);
		browse2.setVisible(false);

		return info;
	}

	private Control createConnection(TabFolder parent) {

		if (ds != null && ds.getDriver() != null) {
			connection = new CompositeDataSourceConnection2(parent, ds.getDriver(), null);
		} else {
			connection = new CompositeDataSourceConnection2(parent, SWT.NONE);
		}

		connection.setLayout(new GridLayout(2, false));

		if (!canSave) {
			return connection;
		}

		Button save = new Button(connection, SWT.PUSH);
		save.setText(LanguageText.CompositeDataSource_45);
		save.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		save.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				try {
					connect();

				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					MessageDialog.openError(CompositeDataSource.this.getShell(), LanguageText.CompositeDataSource_46, ex.getMessage());
					return;
				} catch (SQLException ex) {
					ex.printStackTrace();
					MessageDialog.openError(CompositeDataSource.this.getShell(), LanguageText.CompositeDataSource_47, ex.getMessage());
					return;
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError((CompositeDataSource.this).getShell(), LanguageText.CompositeDataSource_48, ex.getMessage());
					return;
				}

				InputDialog d = new InputDialog(getShell(), LanguageText.CompositeDataSource_49, LanguageText.CompositeDataSource_50, "", null); //$NON-NLS-1$

				if (d.open() == InputDialog.OK) {
					String name = d.getValue();

					ParserPentahoJDBCFile parser = new ParserPentahoJDBCFile(Platform.getInstallLocation().getURL().getPath() + "\\system\\simple-jndi\\jdbc.properties"); //$NON-NLS-1$ //$NON-NLS-2$

					try {
						List<SQLConnection> connexions = parser.getListConnection();
						boolean exist = false;

						for (int i = 0; i < connexions.size(); i++) {
							if (connexions.get(i).getSchemaName().equals(name)) {
								exist = true;
							}
						}

						if (exist) {
							MessageDialog.openInformation(getShell(), LanguageText.CompositeDataSource_52, LanguageText.CompositeDataSource_53);
							return;
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}

					try {

						FileInputStream fis = new FileInputStream(Platform.getInstallLocation().getURL().getPath() + "\\system\\simple-jndi\\jdbc.properties"); //$NON-NLS-1$
						String jndi = IOUtils.toString(fis);
						fis.close();

						jndi += name + "/type=" + "javax.sql.DataSource\n"; //$NON-NLS-1$ //$NON-NLS-2$
						jndi += name + "/driverFile=" + con.getDriverFile() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
						jndi += name + "/driver=" + con.getDriverName() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
						jndi += name + "/url=" + con.getUrl() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
						jndi += name + "/user=" + con.getUser() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
						jndi += name + "/password=" + con.getPass() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$

						FileWriter fw = new FileWriter(Platform.getInstallLocation().getURL().getPath() + "\\system\\simple-jndi\\jdbc.properties"); //$NON-NLS-1$
						fw.write(jndi);
						fw.close();

						MessageDialog.openInformation((CompositeDataSource.this).getShell(), LanguageText.CompositeDataSource_68, LanguageText.CompositeDataSource_69);

					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(getShell(), LanguageText.CompositeDataSource_70, ex.getMessage());
					}

				}

			}
		});

		return connection;
	}

	public void fillData() {
		// fill from Info folder
		if (ds == null)
			ds = new DataSource();
		if (ds.getDriver() == null) {
			ds.setDriver(new DataSourceConnection());
			ds.getDriver().setParent(ds);
		}
		name.setText(ds.getDSName());
		desc.setText(ds.getDriver().getDesc());
		transUrl.setText(ds.getDriver().getTransUrl());
		fileLoc.setText(ds.getDriver().getFileLocation());
		type.setText(ds.getDriver().getType());
		if (ds.getDriver().getType().equals(K_TYPE[0])) {
			fileLoc.setVisible(false);
			browse.setVisible(false);
			l10.setVisible(false);
			l9.setVisible(false);
			browse2.setVisible(false);
			transUrl.setVisible(false);
		} else if (ds.getDriver().getType().equals(K_TYPE[2])) {
			fileLoc.setVisible(false);
			browse.setVisible(false);
			l10.setVisible(false);
			transUrl.setVisible(true);
			l9.setVisible(true);
			browse2.setVisible(true);
		}

		if (ds.getDriver().getServer() == null) {
			location.select(0);
			server.setEnabled(false);
		} else {
			server.setEnabled(true);
			location.select(1);
			server.setText(ds.getDriver().getServer().getName());
			try {
				IRepositoryApi sock = FreemetricsPlugin.getDefault().getRepositoryConnection();
				directoryItem = sock.getRepositoryService().getDirectoryItem(Integer.parseInt(ds.getDriver().getDirectoryItemId()));
				fileLoc.setText(directoryItem.getItemName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		connection.fillDatas();
	}

	public void connect() throws Exception {
		con = connection.getDataSourceConnection().getConnection();
		ds.getDriver().setConnection(con);

		DataSourceConnection sock = ds.getDriver();

		sock.setServerId(server.getText());
		sock.setDriver(con.getDriverName());
		sock.setDriverFile(con.getDriverFile());
		if (fileLoc.isEnabled()) {
			sock.setFileLocation(fileLoc.getText());
		} else {
			sock.setFileLocation(""); //$NON-NLS-1$
		}
		sock.setPass(con.getPass());
		sock.setUrl(con.getUrl());
		sock.setUser(con.getUser());
		sock.setParent(ds);
		sock.setServer(map.get(server.getText()));
		sock.connectAll();
	}

	public void setButtonsVisible(boolean visible) {
		ok.setVisible(visible);
		cancel.setVisible(visible);
	}

	public DataSource getDataSource() {
		return this.ds;
	}
}
