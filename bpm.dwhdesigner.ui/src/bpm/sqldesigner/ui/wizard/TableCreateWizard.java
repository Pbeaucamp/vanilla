package bpm.sqldesigner.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.wizard.ColumnCreateWizard;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Type;

public class TableCreateWizard extends Wizard {

	public class TableCreatePage extends WizardPage {

		private DatabaseCluster cluster;
		private Group groupColumn;
		private Text nameTable;
		private Table tableColumns;
		private Composite compoMain;
		private HashMap<TableItem, Column> hmItemsColumns = new HashMap<TableItem, Column>();
		private ToolItem removeColumnItem;
		private ToolItem editColumnItem;

		public TableCreatePage(String pageName, DatabaseCluster cluster) {
			super(pageName);
			setTitle(Messages.TableCreateWizard_0);
			setDescription(Messages.TableCreateWizard_1);
			this.cluster = cluster;
		}

		
		public void createControl(Composite parent) {
			compoMain = new Composite(parent, SWT.NONE);
			compoMain.setLayout(new GridLayout(1, false));
			compoMain.setLayoutData(new GridData(GridData.BEGINNING));

			Group groupTable = new Group(compoMain, SWT.NONE);
			groupTable.setLayout(new GridLayout(2, false));
			groupTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
					true, false, 1, 1));
			groupTable.setText(Messages.TableCreateWizard_2);

			Label label = new Label(groupTable, SWT.NONE);
			label.setText(Messages.TableCreateWizard_3);

			nameTable = new Text(groupTable, SWT.BORDER);
			nameTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			groupColumn = new Group(compoMain, SWT.NONE);
			groupColumn.setLayout(new GridLayout(1, false));
			groupColumn.setLayoutData(new GridData(GridData.FILL_BOTH));
			groupColumn.setText(Messages.TableCreateWizard_4);

			tableColumns = new Table(groupColumn, SWT.BORDER
					| SWT.FULL_SELECTION);
			tableColumns.setLayoutData(new GridData(GridData.FILL_BOTH));
			tableColumns.setHeaderVisible(true);
			tableColumns.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					removeColumnItem.setEnabled(true);
					editColumnItem.setEnabled(true);
				}

			});

			TableColumn column2 = new TableColumn(tableColumns, SWT.LEFT);
			column2.setText(Messages.TableCreateWizard_5);
			column2.setWidth(100);
			TableColumn column3 = new TableColumn(tableColumns, SWT.LEFT);
			column3.setText(Messages.TableCreateWizard_6);
			column3.setWidth(100);
			TableColumn column4 = new TableColumn(tableColumns, SWT.LEFT);
			column4.setText(Messages.TableCreateWizard_7);
			column4.setWidth(100);

			addTools();

			setControl(compoMain);

		}

		private void addTools() {
			ToolBar toolBar = new ToolBar(groupColumn, SWT.FLAT);
			toolBar.setLayout(new GridLayout(3, false));
			toolBar.setLayoutData(new GridData());

			ToolItem addColumnItem = new ToolItem(toolBar, SWT.NONE);
			addColumnItem.setText("+"); //$NON-NLS-1$

			removeColumnItem = new ToolItem(toolBar, SWT.NONE);
			removeColumnItem.setText("-"); //$NON-NLS-1$
			removeColumnItem.setEnabled(false);

			editColumnItem = new ToolItem(toolBar, SWT.NONE);
			editColumnItem.setText(Messages.TableCreateWizard_10);
			editColumnItem.setEnabled(false);

			addColumnItem.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {

					ColumnCreateWizard columnWizard = new ColumnCreateWizard(
							cluster);
					WizardDialog dialog = new WizardDialog(getShell(),
							columnWizard);

					dialog.create();
					dialog.getShell().setLayout(new GridLayout());
					dialog.getShell().pack();
					if (dialog.open() == Window.OK) {
						if (!columnExists(columnWizard.getName())) {
							Column column = new Column();
							column.setName(columnWizard.getName());

							Type type = cluster.getTypesLists().getType(
									columnWizard.getComboType());
							if (type == null) {
								MessageDialog.openError(getShell(), Messages.TableCreateWizard_11,
										Messages.TableCreateWizard_12);
								return;
							}
							column.setType(type);

							if (columnWizard.getCheckSize()) {
								int size = -1;
								try {
									size = Integer.valueOf(columnWizard
											.getTextSize());
								} catch (NumberFormatException e2) {
									MessageDialog.openError(getShell(),
											Messages.TableCreateWizard_13,
											Messages.TableCreateWizard_14);
								}

								if (size > 0) {
									column.setSize(size);
									column.setNeedsSize(true);
								} else
									MessageDialog.openError(getShell(),
											Messages.TableCreateWizard_15,
											Messages.TableCreateWizard_16);
							}

							if (columnWizard.getCheckDefault())
								column.setDefaultValue(columnWizard
										.getTextDefault());

							column.setNullable(columnWizard.getCheckNullable());
							column.setUnsigned(columnWizard.getCheckUnsigned());
							column.setPrimaryKey(columnWizard.getCheckPK());

							column.setCommit(false);
							column.setNotFullLoaded(false);

							newColumn(column);
						} else {
							MessageDialog.openError(getShell(), Messages.TableCreateWizard_17,
									Messages.TableCreateWizard_18);
						}
						dialog.close();
					}
				}
			});

			removeColumnItem.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					for (int itemIndex : tableColumns.getSelectionIndices()) {
						TableItem item = tableColumns.getItem(itemIndex);
						tableColumns.remove(itemIndex);
						hmItemsColumns.remove(item);
					}

					if (tableColumns.getItems().length == 0) {
						removeColumnItem.setEnabled(false);
						editColumnItem.setEnabled(false);
					}
				}

			});

			editColumnItem.addSelectionListener(new SelectionListener() {

				
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				
				public void widgetSelected(SelectionEvent e) {
					TableItem item = tableColumns.getSelection()[0];
					Column column = hmItemsColumns.get(item);

					ColumnCreateWizard columnWizard = new ColumnCreateWizard(
							cluster, column);
					WizardDialog dialog = new WizardDialog(getShell(),
							columnWizard);

					dialog.create();
					dialog.getShell().setLayout(new GridLayout());
					dialog.getShell().pack();
					if (dialog.open() == Window.OK) {

						column.setName(columnWizard.getName());

						Type type = cluster.getTypesLists().getType(
								columnWizard.getComboType());
						if (type == null) {
							MessageDialog.openError(getShell(), Messages.TableCreateWizard_19,
									Messages.TableCreateWizard_20);
							return;
						}
						column.setType(type);

						if (columnWizard.getCheckSize()) {
							int size = -1;
							try {
								size = Integer.valueOf(columnWizard
										.getTextSize());
							} catch (NumberFormatException e2) {
								MessageDialog.openError(getShell(), Messages.TableCreateWizard_21,
										Messages.TableCreateWizard_22);
							}

							if (size > 0) {
								column.setSize(size);
								column.setNeedsSize(true);
							} else
								MessageDialog.openError(getShell(), Messages.TableCreateWizard_23,
										Messages.TableCreateWizard_24);
						} else {
							column.setSize(-1);
							column.setNeedsSize(false);
						}

						if (columnWizard.getCheckDefault())
							column.setDefaultValue(columnWizard
									.getTextDefault());
						else
							column.setDefaultValue(null);

						column.setNullable(columnWizard.getCheckNullable());
						column.setUnsigned(columnWizard.getCheckUnsigned());
						column.setPrimaryKey(columnWizard.getCheckPK());

						item.setText(new String[] {
								column.isPrimaryKey() ? "YES" : "NO", //$NON-NLS-1$ //$NON-NLS-2$
								column.getName(), column.getType().getName() });
					}
				}

			});
		}

		private boolean columnExists(String columnName) {
			boolean found = false;
			List<Column> columns = new ArrayList<Column>(hmItemsColumns
					.values());
			Iterator<Column> it = columns.iterator();

			while (it.hasNext() && !found) {
				Column column = (Column) it.next();
				if (column.getName().equals(columnName))
					found = true;
			}
			return found;
		}

		public void newColumn(Column column) {

			final TableItem item = new TableItem(tableColumns, SWT.NONE);
			item.setText(new String[] { column.isPrimaryKey() ? "YES" : "NO", //$NON-NLS-1$ //$NON-NLS-2$
					column.getName(), column.getType().getName() });

			hmItemsColumns.put(item, column);
		}
	}

	private TableCreatePage tableCreatePage;
	private String tableName;
	private Collection<Column> listColumns;

	public TableCreateWizard(DatabaseCluster cluster) {
		super();
		tableCreatePage = new TableCreatePage("CreateTable", cluster); //$NON-NLS-1$
		addPage(tableCreatePage);
	}

	
	public boolean performFinish() {
		tableName = tableCreatePage.nameTable.getText();
		listColumns = tableCreatePage.hmItemsColumns.values();
		return true;
	}

	public String getTableName() {
		return tableName;
	}

	public List<Column> getListColumns() {
		return new ArrayList<Column>(listColumns);
	}

}
