package org.fasd.views.composites;

import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.Property;
import org.fasd.views.dialogs.DialogClosure;
import org.freeolap.FreemetricsPlugin;

public class CompositeLevel extends Composite {
	private Text name, desc;
	private Label l8, l2;
	private Text closure, label, descit, nullParent;
	private Text geolocalizable;
	private Text item, sort;
	private Text ordinalColumn;

	private DataObjectItem it, labIt, descIt, orderIt;
	// Column that we going to use for the mapping with the norparena table
	private Property geolocProp;

	private CellEditor[] cellEditors;
	private TableViewer viewer;
	private HashMap<String, DataObjectItem> cols = new HashMap<String, DataObjectItem>();

	private OLAPLevel level;

	public CompositeLevel(final Composite parent, OLAPLevel lvl) {
		super(parent, SWT.NONE);
		level = lvl;
		it = level.getItem();
		labIt = level.getLabel();
		geolocProp = level.getGeolocalizableProperty();
		descIt = level.getItDesc();
		orderIt = level.getOrderItem();

		// build cols
		for (DataObjectItem i : it.getParent().getColumns()) {
			cols.put(i.getName(), i);
		}

		this.setLayout(new GridLayout(2, true));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		TabItem itemStart = new TabItem(tabFolder, SWT.NONE, 0);
		itemStart.setText(LanguageText.CompositeLevel_0);
		itemStart.setControl(createGeneral(tabFolder));

		TabItem itemDim = new TabItem(tabFolder, SWT.NONE, 0);
		itemDim.setText(LanguageText.CompositeLevel_1);
		itemDim.setControl(createProperties(tabFolder));

		Button ok = new Button(this, SWT.PUSH);
		ok.setText(LanguageText.CompositeLevel_2);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				level.setDesc(desc.getText());
				level.setItem(it);

				if (geolocProp != null) {
					level.setGeolocalizableProperty(geolocProp);
				}
				level.setItDesc(descIt);
				if (ordinalColumn != null && !ordinalColumn.getText().equals("")) { //$NON-NLS-1$
					level.setOrderItem(orderIt);
				} else {
					level.setOrderItem(null);
					level.setOrderItemId(null);
				}

				if (label != null && !label.getText().equals("")) { //$NON-NLS-1$
					level.setLabel(labIt);
				} else {
					level.setLabel(null);
					level.setColumnLabelId(null);
				}

				if (!level.getName().equals(name.getText())) {
					level.setName(name.getText());
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		Button cancel = new Button(this, SWT.PUSH);
		cancel.setText(LanguageText.CompositeLevel_5);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		this.layout(false);
		fillData();

	}

	public void fillData() {
		name.setText(level.getName());
		desc.setText(level.getDesc());

		if (level.getItem() != null) {
			item.setText(level.getItem().getFullName());
		}

		if (level.getItDesc() != null) {
			descit.setText(level.getItDesc().getFullName());
		}

		if (level.getLabel() != null) {
			label.setText(level.getLabel().getFullName());
		}

		if (level.getGeolocalizableProperty() != null) {
			geolocalizable.setText(level.getGeolocalizableProperty().getColumn().getFullName());
		}

		if (level.isClosureNeeded()) {
			l8.setVisible(true);
			closure.setVisible(true);
			l2.setVisible(true);
			nullParent.setVisible(true);
		} else {
			l8.setVisible(false);
			closure.setVisible(false);
			l2.setVisible(false);
			nullParent.setVisible(false);
		}

		if (level.getClosureTable() != null) {
			closure.setText(level.getClosureTable().getName());
		}

		if (orderIt != null)
			ordinalColumn.setText(level.getOrderItem().getName());

		fillTable();
	}

	private void fillTable() {
		viewer.setInput(level.getProperties().toArray(new Property[level.getProperties().size()]));
	}

	private Control createProperties(TabFolder folder) {
		Composite propPanel = new Composite(folder, SWT.NONE);
		propPanel.setLayout(new GridLayout(2, true));
		propPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create and configure the "Add" button
		Button add = new Button(propPanel, SWT.PUSH | SWT.CENTER);
		add.setText(LanguageText.CompositeLevel_6);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.addSelectionListener(new SelectionAdapter() {
			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				Property p = new Property();
				p.setName(LanguageText.CompositeLevel_7);
				level.addProperty(p);
				fillTable();
			}
		});

		Button del = new Button(propPanel, SWT.PUSH | SWT.CENTER);
		del.setText(LanguageText.CompositeLevel_8);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.addSelectionListener(new SelectionAdapter() {
			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty())
					return;

				Property p = (Property) ss.getFirstElement();
				level.removeProperty(p);
				fillTable();

			}
		});

		viewer = new TableViewer(propPanel, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return ((Property) element).getName();
				case 1:
					DataObjectItem it = ((Property) element).getColumn();
					if (it != null)
						return it.getName();
					return ""; //$NON-NLS-1$
				case 2:
					return ((Property) element).getType();
				case 3:
					return ((Property) element).getFormatter();
				}
				// }
				return "invalidInput"; //$NON-NLS-1$
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
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement != null) {
					return (Property[]) inputElement;
				}
				return null;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.setCellModifier(new ICellModifier() {

			public boolean canModify(Object element, String property) {
				return true;
			}

			public Object getValue(Object element, String property) {
				// Find the index of the column
				int index = -1;

				if (property.equals("Name")) { //$NON-NLS-1$
					index = 0;
				} else if (property.equals("Column")) { //$NON-NLS-1$
					index = 1;
				} else if (property.equals("Type")) { //$NON-NLS-1$
					index = 2;
				} else if (property.equals("Formatter")) { //$NON-NLS-1$
					index = 3;
				}

				Object result = null;
				Property prop = (Property) element;

				switch (index) {
				case 0: // Name_COLUMN
					result = prop.getName();
					break;
				case 1: // column_COLUMN
					int i = -1;
					if (prop.getColumn() == null)
						return 0;
					for (String s : ((ComboBoxCellEditor) viewer.getCellEditors()[1]).getItems()) {
						i++;

						if (s.equals(prop.getColumn().getName())) {
							return i;
						}
					}
					break;
				case 2: // type_COLUMN
					int a = -1;
					for (String s : ((ComboBoxCellEditor) viewer.getCellEditors()[2]).getItems()) {
						a++;

						if (s.equals(prop.getType())) {
							return a;
						}
					}
					return 0;

				case 3: // formatter COLUMN
					result = prop.getFormatter();
					break;
				default:
					result = "bad property name"; //$NON-NLS-1$
				}
				return result;
			}

			public void modify(Object element, String property, Object value) {
				// Find the index of the column
				int index = -1;

				if (property.equals("Name")) { //$NON-NLS-1$
					index = 0;
				} else if (property.equals("Column")) { //$NON-NLS-1$
					index = 1;
				} else if (property.equals("Type")) { //$NON-NLS-1$
					index = 2;
				} else if (property.equals("Formatter")) { //$NON-NLS-1$
					index = 3;
				}

				Property prop = (Property) ((TableItem) element).getData();

				switch (index) {
				case 0: // Name_COLUMN
					prop.setName((String) value);
					break;
				case 1: // column_COLUMN
					String s = ((ComboBoxCellEditor) viewer.getCellEditors()[1]).getItems()[(Integer) value];
					prop.setColumn(cols.get(s));
					// result = prop.getColumn().getName();
					break;
				case 2: // type_COLUMN
					String s1 = ((ComboBoxCellEditor) viewer.getCellEditors()[2]).getItems()[(Integer) value];
					prop.setType(s1);
					break;
				case 3: // formatter COLUMN
					prop.setFormatter((String) value);
					break;
				}
				viewer.refresh();
			}

		});
		viewer.setColumnProperties(new String[] { "Name", "Column", "Type", "Formatter" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		// build cellEditors
		cellEditors = new CellEditor[] { new TextCellEditor(viewer.getTable()), new ComboBoxCellEditor(viewer.getTable(), cols.keySet().toArray(new String[cols.size()])), new ComboBoxCellEditor(viewer.getTable(), Property.TYPES), new TextCellEditor(viewer.getTable()) };
		viewer.setCellEditors(cellEditors);

		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setWidth(100);
		column.setText(LanguageText.CompositeLevel_3);

		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.NONE);
		column2.setWidth(100);
		column2.setText(LanguageText.CompositeLevel_4);

		TableColumn column3 = new TableColumn(viewer.getTable(), SWT.NONE);
		column3.setWidth(100);
		column3.setText(LanguageText.CompositeLevel_9);

		TableColumn column4 = new TableColumn(viewer.getTable(), SWT.NONE);
		column4.setWidth(100);
		column4.setText(LanguageText.CompositeLevel_10);

		viewer.getTable().setHeaderVisible(true);

		return propPanel;
	}

	private Control createGeneral(TabFolder folder) {
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(4, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lb1 = new Label(parent, SWT.NONE);
		lb1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lb1.setText(LanguageText.CompositeLevel_28);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		name.setText(LanguageText.CompositeLevel_29);
		name.selectAll();

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(LanguageText.CompositeLevel_30);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeLevel_31);

		item = new Text(parent, SWT.BORDER);
		item.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button b = new Button(parent, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(CompositeLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					item.setText(dial.getItem().getName());
					it = dial.getItem();
					boolean clos = false;

					for (OLAPRelation r : FreemetricsPlugin.getDefault().getFAModel().getRelations()) {
						if (r.isUsingItem(it) && r.isReflexive()) {
							clos = true;
							break;
						}
					}

					if (clos) {
						l8.setVisible(true);
						closure.setVisible(true);
						l2.setVisible(true);
						nullParent.setVisible(true);
					} else {
						l8.setVisible(false);
						closure.setVisible(false);
						l2.setVisible(false);
						nullParent.setVisible(false);
					}
				}
			}

		});

		Label l10 = new Label(parent, SWT.NONE);
		l10.setLayoutData(new GridData());
		l10.setText(LanguageText.CompositeLevel_33);

		ordinalColumn = new Text(parent, SWT.BORDER);
		ordinalColumn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button b0 = new Button(parent, SWT.PUSH);
		b0.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b0.setText("..."); //$NON-NLS-1$
		b0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(CompositeLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					ordinalColumn.setText(dial.getItem().getName());
					orderIt = dial.getItem();

				}
			}

		});

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(LanguageText.CompositeLevel_35);

		label = new Text(parent, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button b1 = new Button(parent, SWT.PUSH);
		b1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b1.setText("..."); //$NON-NLS-1$
		b1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(CompositeLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					label.setText(dial.getItem().getName());
					labIt = dial.getItem();
				}
			}

		});

		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(LanguageText.CompositeLevel_37);

		descit = new Text(parent, SWT.BORDER);
		descit.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button b2 = new Button(parent, SWT.PUSH);
		b2.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b2.setText("..."); //$NON-NLS-1$
		b2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(CompositeLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					descit.setText(dial.getItem().getName());
					descIt = dial.getItem();
				}
			}

		});

		Label lblGeolocalizableItem = new Label(parent, SWT.NONE);
		lblGeolocalizableItem.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblGeolocalizableItem.setText(LanguageText.CompositeLevel_39);

		geolocalizable = new Text(parent, SWT.BORDER);
		geolocalizable.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button btnGeolocalizable = new Button(parent, SWT.PUSH);
		btnGeolocalizable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		btnGeolocalizable.setText("..."); //$NON-NLS-1$
		btnGeolocalizable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickColGeoloc dial = new DialogPickColGeoloc(CompositeLevel.this.getShell(), cols, Property.TYPES);
				if (dial.open() == DialogPickCol.OK) {
					geolocalizable.setText(dial.getProperty().getColumn().getName());
					geolocProp = dial.getProperty();
				}
			}

		});

		l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeLevel_41);
		l2.setVisible(false);

		nullParent = new Text(parent, SWT.BORDER);
		nullParent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		nullParent.setVisible(false);

		l8 = new Label(parent, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText(LanguageText.CompositeLevel_42);

		closure = new Text(parent, SWT.BORDER);
		closure.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Button b5 = new Button(parent, SWT.PUSH);
		b5.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b5.setText("..."); //$NON-NLS-1$
		b5.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogClosure dial = new DialogClosure(CompositeLevel.this.getShell(), level);
				if (dial.open() == DialogClosure.OK) {
					closure.setText(level.getClosureTable().getName());
				}
			}

		});

		if (!level.isClosureNeeded()) {
			l8.setVisible(false);
			closure.setVisible(false);
			b5.setVisible(false);
		} else {
			l8.setVisible(true);
			closure.setVisible(true);
			b5.setVisible(true);
		}
		return parent;
	}

}
