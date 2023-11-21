package org.fasd.views.dialogs;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.preferences.PreferenceConstants;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeSchema;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.DialogSqlSelect;
import org.fasd.views.actions.ActionBrowseColumn;
import org.fasd.views.actions.ActionBrowseTable;
import org.freeolap.FreemetricsPlugin;

public class DialogAddTable extends Dialog {
	private TreeViewer viewer;
	private DataSourceConnection connection;
	private ListViewer tables;
	private List<DataObject> input = new ArrayList<DataObject>();
	private ToolBar toolbar;

	public DialogAddTable(Shell parentShell, DataSourceConnection connection) {
		super(parentShell);
		this.connection = connection;

	}

	public List<DataObject> getDataObject() {
		return input;
	}

	protected void initializeBounds() {
		this.getShell().setSize(600, 400);
		this.getShell().setText(LanguageText.DialogAddTable_New_Tbl);
		super.initializeBounds();
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());

		final Composite page1 = new Composite(parent, SWT.NONE);
		page1.setLayout(new GridLayout(3, false));
		page1.setLayoutData(new GridData(GridData.FILL_BOTH));
		// toolBar
		createToolbar(page1);

		viewer = new TreeViewer(page1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(2);

		Button addFact = new Button(page1, SWT.NONE);
		addFact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		addFact.setText(LanguageText.DialogAddTable_Fact_Table__);
		addFact.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();
					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement("SELECT * FROM " + data.getName()); //$NON-NLS-1$
					data.setDataObjectType("fact"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		tables = new ListViewer(page1, SWT.BORDER | SWT.FULL_SELECTION);
		tables.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));
		tables.setContentProvider(new TableContentProvider());
		tables.setLabelProvider(new ILabelProvider() {

			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				DataObject o = (DataObject) element;
				return o.getName();
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}

		});

		Button createFact = new Button(page1, SWT.NONE);
		createFact.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		createFact.setText(LanguageText.DialogAddTable_0);
		createFact.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DialogSqlSelect dial = new DialogSqlSelect(getShell(), connection);
				if (dial.open() != DialogSqlSelect.OK) {
					return;
				}

				String q = dial.getQuery();

				try {
					input.add(connection.getFactTableFromQuery(dial.getName(), q));
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), LanguageText.DialogAddTable_1, LanguageText.DialogAddTable_2 + e1.getMessage());
				}

				tables.setInput(input.toArray(new DataObject[input.size()]));

			}

		});

		Button addDim = new Button(page1, SWT.NONE);
		addDim.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		addDim.setText(LanguageText.DialogAddTable_Dim_table);
		addDim.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement("SELECT * FROM " + data.getName()); //$NON-NLS-1$
					data.setDataObjectType("dimension"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button addLab = new Button(page1, SWT.NONE);
		addLab.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		addLab.setText(LanguageText.DialogAddTable_Lbl_Tbl__);
		addLab.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement("SELECT * FROM " + data.getName()); //$NON-NLS-1$
					data.setDataObjectType("label"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button addClos = new Button(page1, SWT.NONE);
		addClos.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		addClos.setText(LanguageText.DialogAddTable_Closure_Table__);
		addClos.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement("SELECT * FROM " + data.getName()); //$NON-NLS-1$
					data.setDataObjectType("closure"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button del = new Button(page1, SWT.NONE);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		del.setText(LanguageText.DialogAddTable_3);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) tables.getSelection()).getFirstElement();
				if (obj instanceof DataObject) {
					DataObject data = (DataObject) obj;
					input.remove(data);
					DataSource dataSource = connection.getParent();
					dataSource.removeDataObject(data);
					tables.setInput(input.toArray(new DataObject[input.size()]));

				}

			}

		});

		createModel();

		tables.setInput(input.toArray(new DataObject[input.size()]));
		return parent;
	}

	protected void createModelSQL() throws SQLException {
		DataSource tmp = new DataSource();
		tmp.setDriver(new DataSourceConnection());

		tmp.getDriver().setDataSourceLocation(connection.getDataSourceLocation());
		tmp.getDriver().setDesc(connection.getDesc());
		tmp.getDriver().setDriver(connection.getDriver());
		tmp.getDriver().setDriverFile(connection.getDriverFile());
		tmp.getDriver().setFileLocation(connection.getFileLocation());
		tmp.getDriver().setName(connection.getName());
		tmp.getDriver().setPass(connection.getPass());
		tmp.getDriver().setUser(connection.getUser());
		tmp.getDriver().setServer(connection.getServer());
		tmp.getDriver().setTransUrl(connection.getTransUrl());
		tmp.getDriver().setType(connection.getType());
		tmp.getDriver().setUrl(connection.getUrl());
		tmp.getDriver().setSchemaName(connection.getSchemaName());
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		try {
			tmp.getDriver().connectAll();

			TreeDatabase dbitem = new TreeDatabase(tmp);

			List<String> schemaNames = tmp.getDriver().getSchemas();
			if (schemaNames.isEmpty()) {
				ArrayList<DataObject> tab = tmp.getDriver().getTables(null);

				for (int j = 0; j < tab.size(); j++) {
					TreeTable item = new TreeTable(tab.get(j));

					dbitem.addChild(item);
				}
			} else {
				for (String schName : schemaNames) {

					TreeSchema tsc = new TreeSchema(tmp.getDriver(), schName);

					ArrayList<DataObject> tab = tmp.getDriver().getTables(schName);

					for (int j = 0; j < tab.size(); j++) {
						TreeTable item = new TreeTable(tab.get(j));

						tsc.addChild(item);
					}
					dbitem.addChild(tsc);
				}
			}

			root.addChild(dbitem);

			viewer.setInput(root);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createModel() {
		if (connection.getType().equals("DataBase")) { //$NON-NLS-1$
			try {
				createModelSQL();
			} catch (SQLException e) {
				MessageDialog.openError(this.getShell(), LanguageText.DialogAddTable_Error, e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private void createToolbar(Composite parent) {
		toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		ToolItem browse = new ToolItem(toolbar, SWT.PUSH);
		browse.setImage(new Image(this.getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/search.png")); //$NON-NLS-1$
		browse.setToolTipText(LanguageText.DialogAddTable_Browse_100);
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();

				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Object o = ss.getFirstElement();

					if (o instanceof TreeTable) {
						new ActionBrowseTable((DialogAddTable.this).getShell(), ((TreeTable) o).getTable(), 100).run();
					} else if (o instanceof TreeColumn) {
						DataObjectItem item = ((TreeColumn) o).getColumn();
						int nblines = 100;
						ActionBrowseColumn a = new ActionBrowseColumn(item, nblines);
						a.run();
						if (a.getValues().size() > 0 && a.getDistincts().size() > 0) {
							DialogBrowseColumn dial = new DialogBrowseColumn(getShell(), a.getValues(), a.getDistincts(), item);
							dial.open();
						}
					} else {
						MessageDialog.openInformation((DialogAddTable.this).getShell(), LanguageText.DialogAddTable_Information, LanguageText.DialogAddTable_Select_tbl);
					}
				}
			}

		});

		ToolItem browseX = new ToolItem(toolbar, SWT.PUSH);
		browseX.setToolTipText(LanguageText.DialogAddTable_Browse_X);
		browseX.setImage(new Image(this.getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/search_n.png")); //$NON-NLS-1$
		browseX.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Object o = ss.getFirstElement();

					if (o instanceof TreeTable) {
						int X = Integer.parseInt(FreemetricsPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BROWSERFIRSTXLINES));
						new ActionBrowseTable((DialogAddTable.this).getShell(), ((TreeTable) o).getTable(), X).run();
					} else if (o instanceof TreeColumn) {
						DataObjectItem item = ((TreeColumn) o).getColumn();
						int nblines = Integer.parseInt(FreemetricsPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BROWSERFIRSTXLINES));
						ActionBrowseColumn a = new ActionBrowseColumn(item, nblines);
						a.run();
						if (a.getValues().size() > 0 && a.getDistincts().size() > 0) {
							DialogBrowseColumn dial = new DialogBrowseColumn(getShell(), a.getValues(), a.getDistincts(), item);
							dial.open();
						}
					} else {
						MessageDialog.openInformation((DialogAddTable.this).getShell(), LanguageText.DialogAddTable_Information, LanguageText.DialogAddTable_Select_tbl);
					}
				}
			}

		});

	}

	public class TableContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return ((DataObject[]) inputElement);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}
}
