package org.fasd.aggwizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.ActionGetPath;
import org.fasd.utils.Path;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.views.actions.ActionBrowseTable;
import org.fasd.views.dialogs.DialogSelectDataObject;
import org.freeolap.FreemetricsPlugin;

public class PageDefinition extends WizardPage {

	private CheckboxTreeViewer dimTree, mesTree, colsTree;
	private Text creationText;
	private Text factTable, rowCount, name;
	private Button ddl, fill, browse, kettle, big, createDdl;

	private DataObject table, aggTable;
	private String insert = null;
	private boolean canCreate = false, canFill = false;

	private String factCountName;
	private String creationTableSql;

	private List<OLAPMeasure> listMes = new ArrayList<OLAPMeasure>();
	private List<OLAPLevel> listLvl = new ArrayList<OLAPLevel>();

	protected PageDefinition(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);
	}

	private void createPageContent(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite treeComp = new Composite(container, SWT.NONE);
		treeComp.setLayout(new GridLayout(2, true));
		treeComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));

		Label l = new Label(treeComp, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.PageDefinition_0);

		Label l2 = new Label(treeComp, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.PageDefinition_1);

		dimTree = new CheckboxTreeViewer(treeComp, SWT.BORDER);
		dimTree.setLabelProvider(new TreeLabelProvider());
		dimTree.setContentProvider(new TreeContentProvider());
		dimTree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		dimTree.setInput(createDimModel(FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema()));

		mesTree = new CheckboxTreeViewer(treeComp, SWT.BORDER);
		mesTree.setLabelProvider(new TreeLabelProvider());
		mesTree.setContentProvider(new TreeContentProvider());
		mesTree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		mesTree.setInput(createMesModel(FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema()));

		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.PageDefinition_2);

		factTable = new Text(c, SWT.BORDER);
		factTable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		factTable.setEditable(false);

		Button b = new Button(c, SWT.NONE);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectDataObject dial = new DialogSelectDataObject(getShell(), null);
				if (dial.open() == DialogSelectDataObject.OK) {
					table = dial.getDataObject();
					factTable.setText(table.getName());
					colsTree.setInput(createColsModel());
					computeFlags();

				}
			}

		});

		Composite bar1 = new Composite(container, SWT.NONE);
		bar1.setLayout(new GridLayout());
		bar1.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 2));

		Button check = new Button(bar1, SWT.PUSH);
		check.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		check.setText(LanguageText.PageDefinition_4);
		check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				colsTree.setCheckedElements(((IStructuredContentProvider) colsTree.getContentProvider()).getElements(colsTree.getInput()));
			}
		});
		Button uncheck = new Button(bar1, SWT.PUSH);
		uncheck.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		uncheck.setText(LanguageText.PageDefinition_5);
		uncheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Object el : ((IStructuredContentProvider) colsTree.getContentProvider()).getElements(colsTree.getInput())) {
					colsTree.setChecked(el, false);
				}
				colsTree.refresh();
			}
		});

		Label l9 = new Label(container, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l9.setText(LanguageText.PageDefinition_6);

		colsTree = new CheckboxTreeViewer(container, SWT.BORDER);
		colsTree.setLabelProvider(new TreeLabelProvider());
		colsTree.setContentProvider(new TreeContentProvider());
		colsTree.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label l11 = new Label(container, SWT.NONE);
		l11.setLayoutData(new GridData());
		l11.setText(LanguageText.PageDefinition_7);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		name.setText(LanguageText.PageDefinition_8);
		name.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				computeFlags();
			}

		});

		Label l10 = new Label(container, SWT.NONE);
		l10.setLayoutData(new GridData());
		l10.setText(LanguageText.PageDefinition_9);

		rowCount = new Text(container, SWT.BORDER);
		rowCount.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		rowCount.setText(LanguageText.PageDefinition_10);
		rowCount.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				computeFlags();
			}

		});

		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		l4.setText(LanguageText.PageDefinition_11);

		creationText = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		creationText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 2));

		Composite c1 = new Composite(container, SWT.NONE);
		c1.setLayout(new GridLayout(4, true));
		c1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		createDdl = new Button(c1, SWT.NONE);
		createDdl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		createDdl.setText(LanguageText.PageDefinition_12);
		createDdl.setEnabled(false);
		createDdl.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				insert = buildDDL();
				computeFlags();
				table.setCreationStatement(creationText.getText());
				try {
					creationTableSql = creationText.getText();

				} catch (Exception e1) {
					MessageDialog.openError(getShell(), LanguageText.PageDefinition_13, e1.getMessage());
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}

		});

		ddl = new Button(c1, SWT.NONE);
		ddl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ddl.setText(LanguageText.PageDefinition_14);
		ddl.setEnabled(false);
		ddl.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					table.createTableInDataBase();
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), LanguageText.PageDefinition_15, e1.getMessage());
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}

		});

		fill = new Button(c1, SWT.NONE);
		fill.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fill.setText(LanguageText.PageDefinition_16);
		fill.setEnabled(false);
		fill.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				aggTable.setFillingStatement(insert);
				try {
					aggTable.fillTableInDataBase();
					aggTable.buildSelectStatement();
				} catch (Exception e1) {
					MessageDialog.openError(getShell(), LanguageText.PageDefinition_17, e1.getMessage());
					e1.printStackTrace();
				}

			}

		});

		browse = new Button(c1, SWT.NONE);
		browse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		browse.setText(LanguageText.PageDefinition_18);
		browse.setEnabled(false);
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new ActionBrowseTable(getShell(), aggTable, 100).run();
			}

		});

		Composite bar = new Composite(c1, SWT.NONE);
		bar.setLayout(new GridLayout(2, true));
		bar.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 4, 1));

		kettle = new Button(bar, SWT.NONE);
		kettle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		kettle.setText(LanguageText.PageDefinition_19);
		kettle.setEnabled(false);
		kettle.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				String fileName = fd.open();
				if (fileName != null) {
					new KettleAction(fileName, insert, aggTable).run();
					MessageDialog.openInformation(getShell(), "Information", LanguageText.PageDefinition_21 + (fileName.endsWith(".ktr") ? fileName : fileName + ".ktr") + LanguageText.PageDefinition_24); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

			}

		});

		big = new Button(bar, SWT.NONE);
		big.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		big.setText(LanguageText.PageDefinition_25);
		big.setEnabled(false);
		big.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					AggregateHelper.generate(aggTable.getPhysicalName(), insert, creationTableSql, aggTable.getDataSource().getDriver());
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), LanguageText.PageDefinition_26, e1.getMessage());
				}
			}

		});
	}

	private TreeParent createMesModel(OLAPSchema schema) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeRoot rr = new TreeRoot(LanguageText.PageDefinition_28, schema);

		// dims outside groups
		List<OLAPMeasure> dims = schema.getMeasures();
		for (int i = 0; i < dims.size(); i++) {
			TreeMes tdim = new TreeMes(dims.get(i));
			rr.addChild(tdim);
		}
		root.addChild(rr);

		return root;
	}

	private TreeParent createColsModel() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		for (DataObjectItem i : table.getColumns()) {
			TreeColumn tc = new TreeColumn(i);
			root.addChild(tc);
		}
		return root;
	}

	private TreeParent createDimModel(OLAPSchema schema) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeRoot rr = new TreeRoot("Dimensions", schema); //$NON-NLS-1$
		// dims outside groups
		List<OLAPDimension> dims = schema.getDimensions();
		for (int i = 0; i < dims.size(); i++) {
			TreeDim tdim = new TreeDim(dims.get(i));
			for (int j = 0; j < dims.get(i).getHierarchies().size(); j++) {
				OLAPHierarchy hiera = dims.get(i).getHierarchies().get(j);
				TreeHierarchy thiera = new TreeHierarchy(hiera);

				TreeLevel lvl = null;

				for (int k = 0; k < hiera.getLevels().size(); k++) {
					lvl = new TreeLevel(hiera.getLevels().get(k));
					thiera.addChild(lvl);
				}

				tdim.addChild(thiera);

				rr.addChild(tdim);
			}
		}
		root.addChild(rr);

		return root;
	}

	private String buildDDL() {
		Object[] checkedDims = dimTree.getCheckedElements();
		List<OLAPLevel> lvls = new ArrayList<OLAPLevel>();
		List<DataObjectItem> beRemoved = new ArrayList<DataObjectItem>();
		HashMap<OLAPLevel, DataObjectItem> lvlMap = new HashMap<OLAPLevel, DataObjectItem>();

		StringBuffer bufWhere = new StringBuffer();
		boolean first = true;
		for (Object o : checkedDims) {
			if (o instanceof TreeLevel) {
				OLAPLevel l = ((TreeLevel) o).getOLAPLevel();
				lvls.add(l);
				lvlMap.put(l, null);

				// relations
				ActionGetPath a = new ActionGetPath(FreemetricsPlugin.getDefault().getFAModel(), new DataObject[] { table, l.getItem().getParent() });
				a.run();
				Path path = a.getPath();
				if (path != null && path.getRelations().size() != 0) {
					beRemoved.add(path.getRelationship(0).getLeftObjectItem());

				}
			}
		}

		HashMap<OLAPMeasure, DataObjectItem> mesMap = new HashMap<OLAPMeasure, DataObjectItem>();

		Object[] checkedMes = mesTree.getCheckedElements();
		for (Object o : checkedMes) {
			if (o instanceof TreeMes) {
				OLAPMeasure l = ((TreeMes) o).getOLAPMeasure();
				if (l.getType().equals("physical") && table.getColumns().contains(l.getOrigin())) { //$NON-NLS-1$
					mesMap.put(l, null);
					beRemoved.add(l.getOrigin());
				}
			}
		}

		Object[] checkedCols = colsTree.getCheckedElements();
		for (Object o : checkedCols) {
			if (o instanceof TreeColumn) {
				beRemoved.add(((TreeColumn) o).getColumn());
			}
		}

		aggTable = new DataObject();
		aggTable.setDataSource(table.getDataSource());
		aggTable.setName(name.getText());

		for (DataObjectItem i : table.getColumns()) {
			if (!beRemoved.contains(i)) {
				DataObjectItem it = new DataObjectItem();
				it.setAttribut(i.getAttribut());
				it.setClasse(i.getClasse());
				it.setName(i.getName());
				it.setOrigin(i.getOrigin());
				it.setParent(aggTable);
				it.setSqlType(i.getSqlType());
				it.setType(i.getType());

				aggTable.addDataObjectItem(it);
			}
		}

		for (OLAPLevel i : lvlMap.keySet()) {
			DataObjectItem it = new DataObjectItem();
			it.setAttribut(i.getItem().getAttribut());
			it.setClasse(i.getItem().getClasse());
			it.setName(i.getItem().getName());
			it.setOrigin(i.getItem().getOrigin());
			it.setParent(aggTable);
			it.setSqlType(i.getItem().getSqlType());
			it.setType(i.getItem().getType());

			lvlMap.put(i, it);
			aggTable.addDataObjectItem(it);
		}

		for (OLAPMeasure i : mesMap.keySet()) {
			DataObjectItem it = new DataObjectItem();
			it.setAttribut(i.getOrigin().getAttribut());
			it.setClasse(i.getOrigin().getClasse());
			it.setName(i.getOrigin().getName());
			it.setOrigin(i.getOrigin().getOrigin());
			it.setParent(aggTable);
			it.setSqlType(i.getOrigin().getSqlType());
			it.setType(i.getOrigin().getType());

			mesMap.put(i, it);
			aggTable.addDataObjectItem(it);
		}

		aggTable.buildSelectStatement();

		// creation statement
		StringBuffer buf = new StringBuffer();
		buf.append("create table " + aggTable.getName()); //$NON-NLS-1$
		first = true;
		for (DataObjectItem i : aggTable.getColumns()) {
			if (first) {
				first = !first;
				buf.append(" (" + i.getName() + " " + i.getSqlType()); //$NON-NLS-1$ //$NON-NLS-2$

			} else {
				buf.append(", " + i.getName() + " " + i.getSqlType()); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (i.getSqlType().equalsIgnoreCase("VARCHAR")) { //$NON-NLS-1$
				buf.append("(50)"); //$NON-NLS-1$
			}
		}
		buf.append(", " + rowCount.getText() + " INTEGER);"); //$NON-NLS-1$ //$NON-NLS-2$
		creationText.setText(buf.toString());

		// filling statement
		StringBuffer bufCols = new StringBuffer();
		first = true;
		for (DataObjectItem i : aggTable.getColumns()) {
			if (first) {
				first = !first;
				bufCols.append("(" + i.getOrigin()); //$NON-NLS-1$
			} else {
				bufCols.append(", " + i.getOrigin()); //$NON-NLS-1$
			}
		}
		bufCols.append(", " + rowCount.getText() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

		StringBuffer bufLvl = new StringBuffer();
		StringBuffer bufGroup = new StringBuffer();
		first = true;
		for (DataObjectItem i : aggTable.getColumns()) {
			boolean found = false;
			for (OLAPLevel l : lvlMap.keySet()) {

				ActionGetPath a = new ActionGetPath(FreemetricsPlugin.getDefault().getFAModel(), new DataObject[] { table, l.getItem().getParent() });
				a.run();
				Path path = a.getPath();

				if (l.getItem().getName().equals(i.getName())) {
					if (first) {
						first = false;
						if (path != null) {
							bufLvl.append(l.getParent().getParent().getName() + "." + i.getOrigin() + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							bufLvl.append("BASE." + i.getOrigin() + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$
						}

					} else {
						if (path != null) {
							bufLvl.append(", " + l.getParent().getParent().getName() + "." + i.getOrigin() + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						} else {
							bufLvl.append(", BASE." + i.getOrigin() + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$
						}

					}
					found = true;
					if (path != null) {
						bufGroup.append(", " + l.getParent().getParent().getName() + "." + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						bufGroup.append(", BASE." + i.getOrigin()); //$NON-NLS-1$
					}

					break;
				}
			}
			// for measures
			for (OLAPMeasure l : mesMap.keySet()) {
				if (l.getOrigin().getName().equals(i.getName())) {
					if (first) {
						first = false;
						bufLvl.append(l.getAggregator() + "(BASE." + i.getOrigin() + ")" + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						bufLvl.append(", " + l.getAggregator() + "(BASE." + i.getOrigin() + ")" + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
					found = true;

					break;
				}
			}

			if (!found) {
				if (first) {
					first = false;
					bufLvl.append("BASE." + i.getOrigin() + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					bufLvl.append(", BASE." + i.getOrigin() + " AS " + i.getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				bufGroup.append(", BASE." + i.getOrigin()); //$NON-NLS-1$
			}
		}
		bufLvl.append(", COUNT(*) AS " + rowCount.getText()); //$NON-NLS-1$

		StringBuffer fromBuf = new StringBuffer();
		List<OLAPDimension> dims = new ArrayList<OLAPDimension>();
		for (OLAPLevel l : lvlMap.keySet()) {
			if (!dims.contains(l.getParent().getParent()))
				dims.add(l.getParent().getParent());
		}

		fromBuf.append(table.getName() + " AS BASE"); //$NON-NLS-1$
		first = true;
		for (OLAPDimension d : dims) {

			ActionGetPath a = new ActionGetPath(FreemetricsPlugin.getDefault().getFAModel(), new DataObject[] { table, d.getHierarchies().get(0).getLevels().get(0).getItem().getParent() });
			a.run();
			Path path = a.getPath();
			if (path != null) {
				fromBuf.append(", " + d.getHierarchies().get(0).getLevels().get(0).getItem().getParent().getPhysicalName() + " AS " + d.getName()); //$NON-NLS-1$ //$NON-NLS-2$

				if (first) {
					first = false;
					bufWhere.append("BASE." + path.getRelationship(0).getLeftObjectItem().getOrigin() + "=" + d.getName() + "." + path.getRelationship(0).getRightObjectItem().getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else {
					bufWhere.append(" AND BASE." + path.getRelationship(0).getLeftObjectItem().getOrigin() + "=" + d.getName() + "." + path.getRelationship(0).getRightObjectItem().getOrigin()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}

		}

		// add the rowcountcol
		DataObjectItem it = new DataObjectItem();
		it.setAttribut("U"); //$NON-NLS-1$
		it.setClasse("java.lang.Object"); //$NON-NLS-1$
		it.setName(rowCount.getText());
		it.setOrigin(rowCount.getText());
		it.setParent(aggTable);
		it.setSqlType("INTEGER"); //$NON-NLS-1$
		it.setType("physical"); //$NON-NLS-1$

		aggTable.addDataObjectItem(it);
		String insertQuery = "INSERT INTO " + name.getText() + " " + bufCols + "\n SELECT " + bufLvl + "\n FROM " + fromBuf + (bufWhere.toString().trim().length() > 0 ? "\nWHERE " + bufWhere.toString() : "") + "\nGROUP BY " + bufGroup.substring(1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		System.out.println(insertQuery);
		return "INSERT INTO " + name.getText() + " " + bufCols + "\n SELECT " + bufLvl + "\n FROM " + fromBuf + (bufWhere.toString().trim().length() > 0 ? "\nWHERE " + bufWhere.toString() : "") + "\nGROUP BY " + bufGroup.substring(1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	}

	private void computeFlags() {
		if (table == null || rowCount.getText().trim().equals("") || name.getText().trim().equals("") //$NON-NLS-1$ //$NON-NLS-2$
				|| dimTree.getCheckedElements().length == 0 || mesTree.getCheckedElements().length == 0) {
			canCreate = false;
		} else {
			canCreate = true;
		}

		if (insert == null) {
			canFill = false;
		} else {
			canFill = true;
		}

		browse.setEnabled(canFill);
		ddl.setEnabled(canCreate);
		createDdl.setEnabled(canCreate);
		fill.setEnabled(canFill);
		kettle.setEnabled(canFill);
		big.setEnabled(canFill);
	}

	@Override
	public boolean canFlipToNextPage() {
		return canFill;
	}

	protected DataObject getAggTable() {
		return aggTable;
	}

	protected List<OLAPMeasure> getSelectedMeasures() {
		return listMes;
	}

	protected List<OLAPLevel> getSelectedLevels() {
		return listLvl;
	}

	protected DataObjectItem getRowCountCol() {
		return aggTable.findItemNamed(factCountName);
	}

	@Override
	public IWizardPage getNextPage() {
		factCountName = rowCount.getText();
		// get the measures
		listMes.clear();
		for (Object o : mesTree.getCheckedElements()) {
			if (o instanceof TreeMes) {
				listMes.add(((TreeMes) o).getOLAPMeasure());
			}
		}

		// the levels
		listLvl.clear();
		for (Object o : dimTree.getCheckedElements()) {
			if (o instanceof TreeLevel) {
				listLvl.add(((TreeLevel) o).getOLAPLevel());
			}
		}

		PageCubeAttachment cubePage = (PageCubeAttachment) ((WizardAggregate) getWizard()).getPage("Attachment"); //$NON-NLS-1$
		cubePage.fillCombos();

		return super.getNextPage();
	}
}
