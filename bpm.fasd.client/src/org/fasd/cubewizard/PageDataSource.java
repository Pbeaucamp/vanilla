package org.fasd.cubewizard;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.fasd.cubewizard.composite.CompositeXMLDesign;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.metadata.FMDTMapper;
import org.fasd.preferences.PreferenceConstants;
import org.fasd.security.SecurityGroup;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeSchema;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.actions.ActionBrowseColumn;
import org.fasd.views.actions.ActionBrowseTable;
import org.fasd.views.dialogs.DialogBrowseColumn;
import org.freeolap.FreemetricsPlugin;

import xmldesigner.parse.XMLParser;
import xmldesigner.parse.item.DataXML;
import xmldesigner.xpath.Xpath;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.MetaDataDigester;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PageDataSource extends WizardPage {
	public static final int K_PAGE_RELATIONAL = 0;
	public static final int K_PAGE_XML = 2;
	public static final int K_PAGE_METADATA = 3;

	private TreeViewer viewer, xmiViewer;
	private DataSourceConnection sock;
	private ListViewer tables;
	private List<DataObject> input = new ArrayList<DataObject>();
	private HashMap<String, DataSource> map = new HashMap<String, DataSource>();
	private ToolBar toolbar;
	private TabFolder tabFolder;

	private CompositeXMLDesign XMLContent;

	private Combo modelFmdt;
	private TreeViewer fmdtViewer;
	private ListViewer fmdtTables;

	public PageDataSource(String pageName) {
		super(pageName);
	}

	@Override
	public boolean canFlipToNextPage() {
		if (sock.getType().equals("XML")) { //$NON-NLS-1$
			return false;
		}
		return true;
	}

	public void enableToolBar(boolean state) {
		toolbar.setEnabled(state);
	}

	private void createToolbar(Composite parent) {
		toolbar = new ToolBar(parent, SWT.NONE);

		ToolItem browse = new ToolItem(toolbar, SWT.PUSH);
		browse.setImage(new Image(this.getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/search.png")); //$NON-NLS-1$
		browse.setToolTipText(LanguageText.PageDataSource_Browse100Lines);
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s;
				if (viewer.getControl().isFocusControl())
					s = viewer.getSelection();
				else if (xmiViewer.getControl().isFocusControl()) {
					s = xmiViewer.getSelection();
				} else
					return;

				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Object o = ss.getFirstElement();

					if (o instanceof TreeTable) {
						new ActionBrowseTable((PageDataSource.this).getShell(), ((TreeTable) o).getTable(), 100).run();
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
						MessageDialog.openInformation((PageDataSource.this).getShell(), LanguageText.PageDataSource_Information, LanguageText.PageDataSource_SelTableToBrowseIt);
					}
				}
			}

		});

		ToolItem browseX = new ToolItem(toolbar, SWT.PUSH);
		browseX.setToolTipText(LanguageText.PageDataSource_BrowseXLines);
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
						new ActionBrowseTable((PageDataSource.this).getShell(), ((TreeTable) o).getTable(), X).run();
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
						MessageDialog.openInformation((PageDataSource.this).getShell(), LanguageText.PageDataSource_Information, LanguageText.PageDataSource_SelTableToBrowseIt);
					}
				}
			}

		});

	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// toolBar
		createToolbar(mainComposite);
		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);
	}

	private void createPageContent(Composite parent) {
		// TABFOLDER
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		TabItem itemSQL = new TabItem(tabFolder, SWT.NONE, 0);
		itemSQL.setText(LanguageText.PageDataSource_RelationalDB);
		itemSQL.setControl(createSQLContent(tabFolder));

		TabItem itemMes = new TabItem(tabFolder, SWT.NONE, 1);
		itemMes.setText("XML"); //$NON-NLS-1$
		itemMes.setControl(createXMLContent(tabFolder));

		TabItem itemFmdt = new TabItem(tabFolder, SWT.NONE, 2);
		itemFmdt.setText("FMDT"); //$NON-NLS-1$
		itemFmdt.setControl(createFMDTContent(tabFolder));

	}

	private Control createXMLContent(Composite parent) {
		XMLContent = new CompositeXMLDesign(parent, SWT.NONE);
		XMLContent.setLayoutData(new GridData(GridData.FILL_BOTH));
		return XMLContent;
	}

	private Control createSQLContent(Composite parent) {
		final Composite page1 = new Composite(parent, SWT.NONE);
		page1.setLayout(new GridLayout(3, false));

		viewer = new TreeViewer(page1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.setAutoExpandLevel(2);

		Button addFact = new Button(page1, SWT.NONE);
		addFact.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addFact.setText(LanguageText.PageDataSource_FactTable_____);
		addFact.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					data.setDataSource(dataSource);
					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement(t.getSelectStatement());
					data.setDataObjectType("fact"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					dataSource.addDataObject(data);
					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		tables = new ListViewer(page1, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tables.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
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

		Button addDim = new Button(page1, SWT.NONE);
		addDim.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addDim.setText(LanguageText.PageDataSource_DimensionTable);
		addDim.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					data.setDataSource(dataSource);
					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement(t.getSelectStatement());
					data.setDataObjectType("dimension"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					dataSource.addDataObject(data);
					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button addLab = new Button(page1, SWT.NONE);
		addLab.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addLab.setText(LanguageText.PageDataSource_LabelTable____);
		addLab.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					data.setDataSource(dataSource);
					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement(t.getSelectStatement());
					data.setDataObjectType("label"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					dataSource.addDataObject(data);
					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button addClos = new Button(page1, SWT.NONE);
		addClos.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addClos.setText(LanguageText.PageDataSource_ClosureTable___);
		addClos.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataObject data = new DataObject();

					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					data.setDataSource(dataSource);
					DataObject t = ((TreeTable) obj).getTable();
					data.setName(t.getName());
					data.setSelectStatement(t.getSelectStatement());
					data.setDataObjectType("closure"); //$NON-NLS-1$

					for (DataObjectItem i : t.getColumns()) {
						if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
							i.setAttribut("D"); //$NON-NLS-1$
						data.addDataObjectItem(i);
						i.setOrigin(i.getName());
					}

					dataSource.addDataObject(data);
					input.add(data);

					tables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button del = new Button(page1, SWT.NONE);
		del.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		del.setText(LanguageText.PageDataSource_RemoveTable___);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) tables.getSelection()).getFirstElement();
				if (obj instanceof DataObject) {
					DataObject data = (DataObject) obj;
					input.remove(data);
					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					dataSource.removeDataObject(data);
					tables.setInput(input.toArray(new DataObject[input.size()]));
					((DataSourceWizard) PageDataSource.this.getWizard()).removeRelationsFor(data);
				}

			}

		});

		return page1;
	}

	private DataObject copy(DataObject t) {
		DataObject data = new DataObject();

		data.setName(t.getName());
		data.setSelectStatement(t.getSelectStatement());

		for (DataObjectItem i : t.getColumns()) {
			DataObjectItem it = new DataObjectItem();
			if (data.getDataObjectType().equals("dimension")) //$NON-NLS-1$
				it.setAttribut("D"); //$NON-NLS-1$

			it.setDesc(i.getDesc());
			it.setName(i.getName());
			it.setOrigin(i.getOrigin());
			it.setSqlType(i.getSqlType());
			it.setType(i.getType());
			it.setClasse(i.getClasse());

			data.addDataObjectItem(it);
		}
		return data;
	}

	public void createModel() throws FileNotFoundException, Exception {

		this.sock = ((DataSourceWizard) this.getWizard()).getWokingDataSource().getDriver();

		DataSource ds = ((DataSourceWizard) this.getWizard()).getDataSource();
		ds.setDSName(sock.getParent().getDSName());
		ds.setDriver(new DataSourceConnection());
		ds.getDriver().setDriver(sock.getDriver());
		ds.getDriver().setDriverFile(sock.getDriverFile());
		ds.getDriver().setFileLocation(sock.getFileLocation());
		ds.getDriver().setName(sock.getName());
		ds.getDriver().setPass(sock.getPass());
		ds.getDriver().setServer(sock.getServer());
		ds.getDriver().setTransUrl(sock.getTransUrl());
		ds.getDriver().setType(sock.getType());
		ds.getDriver().setUrl(sock.getUrl());
		ds.getDriver().setUser(sock.getUser());
		ds.getDriver().setRepositoryDataSourceId(sock.getRepositoryDataSourceId());
		ds.getDriver().setRepositoryDataSourceUrl(sock.getRepositoryDataSourceUrl());
		ds.getDriver().setSchemaName(sock.getSchemaName());

		if (sock.getType().equals("DataBase")) { //$NON-NLS-1$
			createModelSQL();
			tabFolder.setSelection(0);
		} else if (sock.getType().equals("XML")) { //$NON-NLS-1$
			String url = "file:///" + ds.getDriver().getTransUrl(); //$NON-NLS-1$
			System.out.println("URL :" + url); //$NON-NLS-1$
			XMLParser parser = new XMLParser(url);
			parser.parser();
			DataXML dtd = parser.getDataXML();

			XMLContent.fill(dtd, new Xpath(ds.getDriver().getTransUrl(), dtd.getRoot().getElement(0).getName()));

			tabFolder.setSelection(1);
		} else if (sock.getType().equals("FreeMetaData")) { //$NON-NLS-1$
			MetaDataDigester dig = null;

			if (sock.getDirectoryItemId() != null) {
				IRepositoryApi repSock = FreemetricsPlugin.getDefault().getRepositoryConnection();
				RepositoryItem it = repSock.getRepositoryService().getDirectoryItem(Integer.parseInt(sock.getDirectoryItemId()));
				dig = new MetaDataDigester(IOUtils.toInputStream(FreemetricsPlugin.getDefault().getRepositoryConnection().getRepositoryService().loadModel(it)), new MetaDataBuilder(repSock));
			} else {
				dig = new MetaDataDigester(sock.getFileLocation(), new MetaDataBuilder(null));
			}

			String gName = null;

			try {
				int groupId = FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getGroup().getId();
				gName = FreemetricsPlugin.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(groupId).getName();
			} catch (Exception ex) {

			}

			FMDTMapper mapper = new FMDTMapper(dig.getModel(FreemetricsPlugin.getDefault().getRepositoryConnection(), gName));

			for (DataSource d : mapper.getDataSources()) {
				map.put(d.getDSName(), d);
				modelFmdt.add(d.getDSName());
			}
			((DataSourceWizard) getWizard()).setRelations(mapper.getRelations());
			tabFolder.setSelection(2);

			// add groups from FMDT
			for (String s : mapper.getGroups()) {
				SecurityGroup sg = new SecurityGroup();
				sg.setName(s);

				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().addSecurityGroup(sg);
			}
		}

	}

	private void createModelSQL() throws FileNotFoundException, Exception {
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
				item.setLoaded();
				dbitem.addChild(item);
			}
		} else {
			for (String schName : schemaNames) {

				TreeSchema tsc = new TreeSchema(sock, schName);
				dbitem.addChild(tsc);
			}
		}

		root.addChild(dbitem);
		viewer.setInput(root);
	}

	private void createModelFMDT(DataSource ds) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		ArrayList<DataObject> tab = ds.getDataObjects();

		for (int j = 0; j < tab.size(); j++) {
			TreeTable item = new TreeTable(tab.get(j));

			for (int k = 0; k < tab.get(j).getColumns().size(); k++) {
				TreeColumn itm = new TreeColumn(tab.get(j).getColumns().get(k));
				item.addChild(itm);
			}

			root.addChild(item);
		}
		fmdtViewer.setInput(root);

	}

	private Control createFMDTContent(Composite parent) {
		final Composite page1 = new Composite(parent, SWT.NONE);
		page1.setLayout(new GridLayout(3, false));

		modelFmdt = new Combo(page1, SWT.BORDER);
		modelFmdt.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 1));
		modelFmdt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				input.clear();
				fmdtTables.setInput(null);

				DataSource ds = map.get(modelFmdt.getText());
				createModelFMDT(ds);

				DataSource dataSource = ((DataSourceWizard) getWizard()).getWokingDataSource();
				dataSource.clear();

				((DataSourceWizard) getWizard()).getDataSource().setDSName(ds.getDSName());

				dataSource.setDSName(ds.getDSName());
				DataSourceConnection sock = new DataSourceConnection();
				sock.setDataSourceLocation(ds.getDriver().getDataSourceLocation());
				sock.setDesc(ds.getDriver().getDesc());
				sock.setDriver(ds.getDriver().getDriver());
				sock.setDriverFile(ds.getDriver().getDriverFile());
				sock.setFileLocation(((DataSourceWizard) getWizard()).getDataSource().getDriver().getFileLocation());
				sock.setName(ds.getDriver().getName());
				sock.setPass(ds.getDriver().getPass());
				sock.setServer(ds.getDriver().getServer());
				sock.setTransUrl(ds.getDriver().getTransUrl());
				sock.setType("FreeMetaData"); //$NON-NLS-1$
				sock.setUrl(ds.getDriver().getUrl());
				sock.setUser(ds.getDriver().getUser());

				dataSource.setDriver(sock);

				((DataSourceWizard) getWizard()).getDataSource().setDriver(sock);

			}

		});

		fmdtViewer = new TreeViewer(page1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fmdtViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		fmdtViewer.setContentProvider(new ViewContentProvider());
		fmdtViewer.setLabelProvider(new ViewLabelProvider());
		fmdtViewer.setUseHashlookup(true);
		fmdtViewer.setAutoExpandLevel(1);

		Button addFact = new Button(page1, SWT.NONE);
		addFact.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addFact.setText(LanguageText.PageDataSource_FactTable___);
		addFact.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) fmdtViewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {

					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					DataObject t = copy(((TreeTable) obj).getTable());
					t.setDataObjectType("fact"); //$NON-NLS-1$

					dataSource.addDataObject(t);
					input.add(t);

					fmdtTables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		fmdtTables = new ListViewer(page1, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		fmdtTables.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		fmdtTables.setContentProvider(new TableContentProvider());
		fmdtTables.setLabelProvider(new ILabelProvider() {

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

		Button addDim = new Button(page1, SWT.NONE);
		addDim.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addDim.setText(LanguageText.PageDataSource_DimensionTable);
		addDim.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object obj = ((StructuredSelection) fmdtViewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					DataObject t = copy(((TreeTable) obj).getTable());
					t.setDataObjectType("dimension"); //$NON-NLS-1$

					dataSource.addDataObject(t);
					input.add(t);

					fmdtTables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button addLab = new Button(page1, SWT.NONE);
		addLab.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addLab.setText(LanguageText.PageDataSource_LabelTable);
		addLab.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object obj = ((StructuredSelection) fmdtViewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					DataObject t = copy(((TreeTable) obj).getTable());
					t.setDataObjectType("label"); //$NON-NLS-1$

					dataSource.addDataObject(t);
					input.add(t);

					fmdtTables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button addClos = new Button(page1, SWT.NONE);
		addClos.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		addClos.setText(LanguageText.PageDataSource_ClosureTable___);
		addClos.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object obj = ((StructuredSelection) fmdtViewer.getSelection()).getFirstElement();
				if (obj instanceof TreeTable) {
					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					DataObject t = copy(((TreeTable) obj).getTable());
					t.setDataObjectType("closure"); //$NON-NLS-1$

					dataSource.addDataObject(t);
					input.add(t);

					fmdtTables.setInput(input.toArray(new DataObject[input.size()]));

				}
			}

		});

		Button del = new Button(page1, SWT.NONE);
		del.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		del.setText(LanguageText.PageDataSource_RemoveTable___);
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object obj = ((StructuredSelection) fmdtTables.getSelection()).getFirstElement();
				if (obj instanceof DataObject) {
					DataObject data = (DataObject) obj;
					input.remove(data);
					DataSource dataSource = ((DataSourceWizard) PageDataSource.this.getWizard()).getDataSource();
					dataSource.removeDataObject(data);
					fmdtTables.setInput(input.toArray(new DataObject[input.size()]));

				}

			}

		});

		return page1;
	}

	public class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}
	}

	public class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			if (obj instanceof TreeTable) {
				try {
					return new Image((PageDataSource.this).getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_folder.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			} else if (obj instanceof TreeColumn) {
				try {
					return new Image((PageDataSource.this).getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_file.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			} else if (obj instanceof TreeDatabase) {
				try {
					return new Image((PageDataSource.this).getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/schema.gif"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			} else if (obj instanceof TreeParent) {
				try {
					return new Image((PageDataSource.this).getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_element.png"); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			} else
				return null;
		}

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

	@Override
	public IWizardPage getNextPage() {

		PageRelation next = (PageRelation) ((DataSourceWizard) getWizard()).getNextPage(this);
		next.initialize();
		return next;
	}

	public DataObject getXMLTable() {
		return XMLContent.getDataObject();
	}

}
