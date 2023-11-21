package org.fasd.views.actions;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.sql.SQLConnection;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeSchema;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.SQLView;
import org.freeolap.FreemetricsPlugin;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.ListDriver;

public class ActionModifyConnection extends Action {

	private SQLView view;
	private static int pageNum = 0;
	private Shell shell;

	private Button finish, cancel, next, previous;

	private DataSourceConnection sock;

	private Text txt1;
	private Combo cbo;

	// database
	private Text text;
	private Text text1;
	private Text text2;
	private Combo cbo1, cbo2;

	// tree
	private CheckboxTreeViewer viewer;

	private String[] drivers;
	private String[] urls = { "jdbc:hsqldb:file:data/foodmart/foodmart", "jdbc:oracle:thin:@hp:1521:xe", "jdbc:mysql://localhost/[dbname]", "jdbc:mysql://localhost/freeolap_mondrian" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private DataSource ds;

	public ActionModifyConnection(SQLView view, DataSource ds) {
		super(LanguageText.ActionModifyConnection_Modify_DS);
		this.view = view;
		this.ds = ds;
	}

	public void run() {
		pageNum = 0;

		try {

			int k = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo().size();

			drivers = new String[k];
			int i = 0;
			for (String inf : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversName()) {
				drivers[i] = inf;
				i++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		shell = new Shell(view.getSite().getShell());
		int x = (view.getSite().getShell().getDisplay().getBounds().width - 450) / 2;
		int y = (view.getSite().getShell().getDisplay().getBounds().height - 600) / 2;
		shell.setBounds(x, y, 450, 600);
		shell.setText(LanguageText.ActionModifyConnection_Modify_exsisting);

		// create the composite that the pages will share
		final Composite contentPanel = new Composite(shell, SWT.NONE);
		contentPanel.setBounds(10, 10, 425, 600 - 30 - 10 - 25 - 25);
		final StackLayout layout = new StackLayout();
		contentPanel.setLayout(layout);

		// create the first page's content
		final Composite page0 = new Composite(contentPanel, SWT.NONE);
		page0.setLayout(new GridLayout());

		Label lbl = new Label(page0, SWT.None);
		lbl.setImage(new Image(shell.getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/test1.jpg")); //$NON-NLS-1$

		Label label = new Label(page0, SWT.NONE);
		label.setText(LanguageText.ActionModifyConnection_DS_Name_);

		txt1 = new Text(page0, SWT.BORDER);
		txt1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lbl1 = new Label(page0, SWT.NONE);
		lbl1.setText(LanguageText.ActionModifyConnection_DS_Type_);

		cbo = new Combo(page0, SWT.NONE);
		cbo.setItems(new String[] { LanguageText.ActionModifyConnection_DB });
		cbo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbo.select(0);

		final Group gr1 = new Group(page0, SWT.NONE);
		gr1.setText(LanguageText.ActionModifyConnection_DB);
		gr1.setLayoutData(new GridData(GridData.FILL_BOTH));
		gr1.setLayout(new GridLayout());

		Label l1 = new Label(gr1, SWT.None);
		l1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l1.setText(LanguageText.ActionModifyConnection_Schema_Name_);

		text = new Text(gr1, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l2 = new Label(gr1, SWT.None);
		l2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l2.setText(LanguageText.ActionModifyConnection_Driver_);

		cbo1 = new Combo(gr1, SWT.BORDER);
		cbo1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbo1.setItems(drivers);
		cbo1.select(0);

		Label l3 = new Label(gr1, SWT.None);
		l3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l3.setText(LanguageText.ActionModifyConnection_URL_);

		cbo2 = new Combo(gr1, SWT.BORDER);
		cbo2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbo2.setItems(urls);
		cbo2.select(0);

		Label l4 = new Label(gr1, SWT.None);
		l4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l4.setText(LanguageText.ActionModifyConnection_User_);

		text1 = new Text(gr1, SWT.SINGLE | SWT.BORDER);
		text1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l5 = new Label(gr1, SWT.None);
		l5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l5.setText(LanguageText.ActionModifyConnection_Password_);

		text2 = new Text(gr1, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		text2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button test = new Button(gr1, SWT.PUSH);
		test.setText(LanguageText.ActionModifyConnection_Test);
		test.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		test.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					connect();
					MessageDialog.openInformation(shell, LanguageText.ActionModifyConnection_Information, LanguageText.ActionModifyConnection_Succes);
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					MessageDialog.openError(shell, LanguageText.ActionModifyConnection_Error, ex.getMessage());
				} catch (SQLException ex) {
					ex.printStackTrace();
					MessageDialog.openError(shell, LanguageText.ActionModifyConnection_Error, ex.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(shell, LanguageText.ActionModifyConnection_Error, ex.getMessage());
				}
			}
		});

		// create the second page's content
		final Composite page1 = new Composite(contentPanel, SWT.NONE);
		page1.setLayout(new GridLayout());

		Composite holder = new Composite(page1, SWT.NONE);
		holder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		holder.setLayout(new GridLayout(2, true));

		Button selall = new Button(holder, SWT.PUSH);
		selall.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selall.setText(LanguageText.ActionModifyConnection_Sel_All);
		selall.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.expandAll();
				viewer.setAllChecked(true);
			}
		});

		Button dselall = new Button(holder, SWT.PUSH);
		dselall.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dselall.setText(LanguageText.ActionModifyConnection_Desel_All);
		dselall.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		});

		viewer = new CheckboxTreeViewer(page1, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(2);

		viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					viewer.setSubtreeChecked(event.getElement(), true);
					TreeObject obj = (TreeObject) event.getElement();
					if (obj.getParent() != null) {
						viewer.setChecked(obj.getParent(), true);
						if (obj.getParent().getParent() != null) {
							viewer.setChecked(obj.getParent().getParent(), true);
						}
					}
				} else if (!event.getChecked()) {
					viewer.setSubtreeChecked(event.getElement(), false);
				}
			}
		});

		// create the button that will switch between the pages
		previous = new Button(shell, SWT.PUSH);
		previous.setText(LanguageText.ActionModifyConnection_Back);
		previous.setBounds(90, 600 - 30 - 10 - 25, 80, 25);
		previous.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				finish.setEnabled(false);
				next.setEnabled(true);
				previous.setEnabled(false);

				pageNum = ++pageNum % 2;
				layout.topControl = (pageNum == 0 ? page0 : page1);
				contentPanel.layout();
			}
		});
		previous.setEnabled(false);

		next = new Button(shell, SWT.PUSH);
		next.setText(LanguageText.ActionModifyConnection_Next);
		next.setBounds(170, 600 - 30 - 10 - 25, 80, 25);
		next.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					connect();
					TreeParent root = createModel(sock);
					viewer.setInput(root);

					finish.setEnabled(true);
					next.setEnabled(false);
					previous.setEnabled(true);

					pageNum = ++pageNum % 2;
					layout.topControl = pageNum == 0 ? page0 : page1;
					contentPanel.layout();
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					MessageDialog.openError(shell, LanguageText.ActionModifyConnection_Error, ex.getMessage());
				} catch (SQLException ex) {
					ex.printStackTrace();
					MessageDialog.openError(shell, LanguageText.ActionModifyConnection_Error, ex.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(shell, LanguageText.ActionModifyConnection_Error, ex.getMessage());
				}
			}
		});

		finish = new Button(shell, SWT.PUSH);
		finish.setText(LanguageText.ActionModifyConnection_Finish);
		finish.setBounds(260, 600 - 30 - 10 - 25, 80, 25);
		finish.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// delete old one
				FreemetricsPlugin.getDefault().getFAModel().removeSQLDriver(ds);

				Object[] o = viewer.getCheckedElements();
				DataObject tab = null;

				DataSource ds = null;

				for (int i = 0; i < o.length; i++) {
					if (o[i] instanceof TreeDatabase) {
						ds = new DataSource(txt1.getText(), ((TreeDatabase) o[i]).getDriver().getDriver());
					} else if (o[i] instanceof TreeTable) {
						DataObject t = ((TreeTable) o[i]).getTable();
						tab = new DataObject(t.getName());
						ds.addDataObject(tab);
					} else if (o[i] instanceof TreeColumn) {
						tab.addDataObjectItem(((TreeColumn) o[i]).getColumn());
					}
				}

				for (int i = 0; i < ds.getDataObjects().size(); i++) {
					System.out.println("    " + ds.getDataObjects().get(i).getName()); //$NON-NLS-1$

					ArrayList<DataObjectItem> cols = ds.getDataObjects().get(i).getColumns();
					for (int j = 0; j < cols.size(); j++) {
						System.out.println("        " + cols.get(j).getName()); //$NON-NLS-1$
					}
				}
				System.out.println(">> Driver = " + sock.getDriver()); //$NON-NLS-1$
				ds.setDriver(sock);

				FreemetricsPlugin.getDefault().getFAModel().addDataSource(ds);
				view.refresh(true);

				shell.close();
			}
		});
		finish.setEnabled(false);

		cancel = new Button(shell, SWT.PUSH);
		cancel.setText(LanguageText.ActionModifyConnection_Cancel);
		cancel.setBounds(350, 600 - 30 - 10 - 25, 80, 25);
		cancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
			}
		});

		layout.topControl = (pageNum == 0 ? page0 : page1);
		contentPanel.layout();

		// we are modifying so fill up
		fillData();
		shell.setVisible(true);
		shell.open();
		shell.setActive();
	}

	private TreeParent createModel(DataSourceConnection sock) throws FileNotFoundException, Exception {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeDatabase dbitem = new TreeDatabase(sock.getParent());

		List<String> schemaNames = sock.getSchemas();
		if (schemaNames.isEmpty()) {
			ArrayList<DataObject> tab = sock.getTables(null);

			for (int j = 0; j < tab.size(); j++) {
				TreeTable item = new TreeTable(tab.get(j));
				for (int k = 0; k < tab.get(j).getColumns().size(); k++) {
					TreeColumn itm = new TreeColumn(tab.get(j).getColumns().get(k));
					item.addChild(itm);
				}

				dbitem.addChild(item);
			}
		} else {
			for (String schName : schemaNames) {

				TreeSchema tsc = new TreeSchema(sock, schName);

				ArrayList<DataObject> tab = sock.getTables(schName);

				for (int j = 0; j < tab.size(); j++) {
					TreeTable item = new TreeTable(tab.get(j));
					for (int k = 0; k < tab.get(j).getColumns().size(); k++) {
						TreeColumn itm = new TreeColumn(tab.get(j).getColumns().get(k));
						item.addChild(itm);
					}

					tsc.addChild(item);
				}
				dbitem.addChild(tsc);
			}
		}

		root.addChild(dbitem);

		return root;
	}

	private void fillData() {
		txt1.setText(ds.getDSName());

		text.setText(ds.getDriver().getName());

		cbo1.add(ds.getDriver().getDriver());
		cbo1.select(cbo1.getItemCount() - 1);

		cbo2.add(ds.getDriver().getUrl());
		cbo2.select(cbo2.getItemCount() - 1);

		text1.setText(ds.getDriver().getUser());
		text2.setText(ds.getDriver().getPass());
	}

	private DataSourceConnection connect() throws Exception {
		String schema = text.getText();
		SQLConnection con = new SQLConnection(cbo2.getText(), text1.getText(), text2.getText(), cbo1.getText(), ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(cbo1.getText()).getName(), txt1.getText(), cbo.getText());

		sock = new DataSourceConnection(text.getText(), con);
		sock.setSchemaName(schema);
		sock.connect();
		return sock;
	}

}
