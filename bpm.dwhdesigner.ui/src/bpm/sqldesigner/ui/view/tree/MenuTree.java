package bpm.sqldesigner.ui.view.tree;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.internal.part.NullEditorInput;

import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.command.creation.CatalogCreateCommand;
import bpm.sqldesigner.ui.command.creation.SchemaCreateCommand;
import bpm.sqldesigner.ui.command.drop.CatalogDropCommand;
import bpm.sqldesigner.ui.command.drop.ColumnDropCommand;
import bpm.sqldesigner.ui.command.drop.ProcedureDropCommand;
import bpm.sqldesigner.ui.command.drop.SchemaDropCommand;
import bpm.sqldesigner.ui.command.drop.TableDropCommand;
import bpm.sqldesigner.ui.command.drop.ViewDropCommand;
import bpm.sqldesigner.ui.command.update.ColumnNameUpdateCommand;
import bpm.sqldesigner.ui.command.update.ColumnTypeUpdateCommand;
import bpm.sqldesigner.ui.command.update.SchemaNameUpdateCommand;
import bpm.sqldesigner.ui.command.update.TableNameUpdateCommand;
import bpm.sqldesigner.ui.editor.EditorsList;
import bpm.sqldesigner.ui.editor.SQLDesignEditorInput;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.editpart.SchemaPart;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;
import bpm.sqldesigner.ui.utils.DialogBrowseContent;
import bpm.sqldesigner.ui.view.RequestsView;
import bpm.sqldesigner.ui.view.TreeView;

public class MenuTree {

	public static void createMenu(final TreeViewer tree) {

		final Menu menuCatalog = createMenuCatalog(tree, false);
		// final Menu menuCatalogMySQL = createMenuCatalog(tree, true);

		final Menu menuCluster = createMenuCluster(tree);
		final Menu menuClusterClose = createMenuClusterClose(tree);
		final Menu menuColumn = createMenuColumn(tree);
		final Menu menuTable = createMenuTable(tree);
		final Menu menuSchema = createMenuSchema(tree);
		final Menu menuView = createMenuView(tree);
		final Menu menuProcedure = createMenuProcedure(tree);

		tree.getTree().addMenuDetectListener(new MenuDetectListener() {

			
			public void menuDetected(MenuDetectEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				Node node = (Node) treeSelection.getFirstElement();

				if (node.getCluster().getDatabaseConnection() == null) {
					if (node instanceof DatabaseCluster)
						tree.getTree().setMenu(menuClusterClose);
					return;
				}

				if (node instanceof DatabaseCluster)
					tree.getTree().setMenu(menuCluster);
				else if (node instanceof Catalog)
					tree.getTree().setMenu(menuCatalog);
				else if (node instanceof Column)
					tree.getTree().setMenu(menuColumn);
				else if (node instanceof Table)
					tree.getTree().setMenu(menuTable);
				else if (node instanceof Schema)
					tree.getTree().setMenu(menuSchema);
				else if (node instanceof SQLProcedure)
					tree.getTree().setMenu(menuProcedure);
				else if (node instanceof SQLView)
					tree.getTree().setMenu(menuView);
				else
					tree.getTree().setMenu(null);

			}

		});

	}

	private static Menu createMenuCatalog(final TreeViewer tree, boolean isMySQL) {
		final Menu menuCatalog = new Menu(tree.getTree());

		MenuItem menuItemCreateSchema = new MenuItem(menuCatalog, SWT.PUSH);
		menuItemCreateSchema.setText(Messages.MenuTree_0);
		menuItemCreateSchema.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Catalog catalog = (Catalog) treeSelection
						.getFirstElement();

				if (catalog.getCluster().getProductName().equals("MySQL")) { //$NON-NLS-1$
					MessageDialog
							.openError(tree.getControl().getShell(), Messages.MenuTree_2,
									Messages.MenuTree_3);
					return;
				}
				final SchemaCreateCommand cmd = new SchemaCreateCommand();
				cmd.setCatalog(catalog);
				cmd.execute();
				cmd.getShell().addDisposeListener(new DisposeListener() {

					
					public void widgetDisposed(DisposeEvent e) {
						tree.refresh();
						tree.getTree().update();

						RequestsView view = (RequestsView) Activator
								.getDefault().getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.findView(RequestsView.ID);
						view.getTab(catalog.getCluster()).nodeCreated(
								cmd.getSchema());
					}

				});

			}

		});

		MenuItem menuItemDropCatalog = new MenuItem(menuCatalog, SWT.PUSH);
		menuItemDropCatalog.setText(Messages.MenuTree_4);
		menuItemDropCatalog.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Catalog catalog = (Catalog) treeSelection
						.getFirstElement();

				final CatalogDropCommand cmd = new CatalogDropCommand();
				cmd.setNode(catalog);
				cmd.execute();
				tree.refresh();
			}

		});

		return menuCatalog;
	}

	private static Menu createMenuCluster(final TreeViewer tree) {
		final Menu menuCluster = new Menu(tree.getTree());

		MenuItem menuItemCreateDb = new MenuItem(menuCluster, SWT.PUSH);
		menuItemCreateDb.setText(Messages.MenuTree_5);
		menuItemCreateDb.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final DatabaseCluster cluster = (DatabaseCluster) treeSelection
						.getFirstElement();

				final CatalogCreateCommand cmd = new CatalogCreateCommand();
				cmd.setCluster(cluster);
				cmd.execute();
				cmd.getShell().addDisposeListener(new DisposeListener() {

					
					public void widgetDisposed(DisposeEvent e) {
						tree.refresh();
						tree.getTree().update();

						RequestsView view = (RequestsView) Activator
								.getDefault().getWorkbench()
								.getActiveWorkbenchWindow().getActivePage()
								.findView(RequestsView.ID);
						view.getTab(cluster).nodeCreated(cmd.getCatalog());
					}

				});

			}

		});

		createMenuClusterCloseItem(tree, menuCluster);

		return menuCluster;
	}

	private static Menu createMenuClusterClose(final TreeViewer tree) {
		final Menu menuClusterClose = new Menu(tree.getTree());
		createMenuClusterCloseItem(tree, menuClusterClose);

		return menuClusterClose;
	}

	private static MenuItem createMenuClusterCloseItem(final TreeViewer tree, Menu menu) {
		MenuItem menuItemCloseDb = new MenuItem(menu, SWT.PUSH);
		menuItemCloseDb.setText(Messages.MenuTree_6);
		menuItemCloseDb.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final DatabaseCluster cluster = (DatabaseCluster) treeSelection
						.getFirstElement();

				
				List<IEditorReference> refs  = Activator.getDefault().getEditorsFor(SQLDesignEditorInput.class);
				for(SchemaView v : cluster.getSchemaViews()){
					
					try{
						for(IEditorReference r : refs){
							if (r.getEditorInput() instanceof NullEditorInput || ((SQLDesignEditorInput)r.getEditorInput()).getSchemaView() == v){
								Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(r.getEditor(false), true);
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				refs  = Activator.getDefault().getEditorsFor(SnapshotEditorInput.class);
				for(DocumentSnapshot v : cluster.getDocumentSnapshots()){
					
					try{
						for(IEditorReference r : refs){
							if (r.getEditorInput() instanceof NullEditorInput || ((SnapshotEditorInput)r.getEditorInput()).getSnapshot() == v){
								Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(r.getEditor(false), true);
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				Activator.getDefault().getWorkspace().remove(cluster);
			}

		});

		return menuItemCloseDb;

	}

	private static Menu createMenuColumn(final TreeViewer tree) {
		final Menu menuColumn = new Menu(tree.getTree());

		MenuItem menuItemUpdateColumnName = new MenuItem(menuColumn, SWT.PUSH);
		menuItemUpdateColumnName.setText(Messages.MenuTree_7);
		menuItemUpdateColumnName.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Column column = (Column) treeSelection.getFirstElement();

				if (column.isPrimaryKey()) {
					MessageDialog
							.openError(tree.getControl().getShell(), Messages.MenuTree_8,
									Messages.MenuTree_9);
					return;
				}

				final ColumnNameUpdateCommand cmd = new ColumnNameUpdateCommand();
				cmd.setNode(column);
				cmd.execute();
				cmd.getShell().addDisposeListener(new DisposeListener() {

					
					public void widgetDisposed(DisposeEvent e) {
						if (cmd.isOk()) {
							tree.refresh();
							tree.getTree().update();

							RequestsView view = (RequestsView) Activator
									.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.findView(RequestsView.ID);
							view.getTab(column.getCluster()).nodeNameUpdated(
									column, cmd.getOldName());
						}
					}

				});

			}

		});

		MenuItem menuItemTypeColumn = new MenuItem(menuColumn, SWT.PUSH);
		menuItemTypeColumn.setText(Messages.MenuTree_10);
		menuItemTypeColumn.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Column column = (Column) treeSelection.getFirstElement();

				final ColumnTypeUpdateCommand cmd = new ColumnTypeUpdateCommand();
				cmd.setColumn(column);
				cmd.execute();
				cmd.getShell().addDisposeListener(new DisposeListener() {

					
					public void widgetDisposed(DisposeEvent e) {
						if (cmd.isOk()) {
							tree.refresh();
							tree.getTree().update();

							RequestsView view = (RequestsView) Activator
									.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.findView(RequestsView.ID);
							view.getTab(column.getCluster()).nodeTypeUpdated(
									column);
						}
					}

				});
			}

		});

		MenuItem menuItemDropColumn = new MenuItem(menuColumn, SWT.PUSH);
		menuItemDropColumn.setText(Messages.MenuTree_11);
		menuItemDropColumn.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Column column = (Column) treeSelection.getFirstElement();

				final ColumnDropCommand cmd = new ColumnDropCommand();
				cmd.setNode(column);
				cmd.execute();
				tree.refresh();
			}

		});

		return menuColumn;
	}

	private static Menu createMenuProcedure(final TreeViewer tree) {

		final Menu menuProcedure = new Menu(tree.getTree());

		MenuItem menuItemDropProcedure = new MenuItem(menuProcedure, SWT.PUSH);
		menuItemDropProcedure.setText(Messages.MenuTree_12);
		menuItemDropProcedure.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final SQLProcedure procedure = (SQLProcedure) treeSelection
						.getFirstElement();

				final ProcedureDropCommand cmd = new ProcedureDropCommand();
				cmd.setNode(procedure);
				cmd.execute();
				tree.refresh();
			}

		});
		return menuProcedure;
	}

	private static Menu createMenuSchema(final TreeViewer tree) {
		final Menu menuSchema = new Menu(tree.getTree());

		MenuItem menuItemUpdateNameSchema = new MenuItem(menuSchema, SWT.PUSH);
		menuItemUpdateNameSchema.setText(Messages.MenuTree_13);
		menuItemUpdateNameSchema.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Schema schema = (Schema) treeSelection.getFirstElement();

				final SchemaNameUpdateCommand cmd = new SchemaNameUpdateCommand();
				cmd.setNode(schema);
				cmd.execute();
				cmd.getShell().addDisposeListener(new DisposeListener() {

					
					public void widgetDisposed(DisposeEvent e) {
						if (cmd.isOk()) {
							tree.refresh();
							tree.getTree().update();

							RequestsView view = (RequestsView) Activator
									.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.findView(RequestsView.ID);
							view.getTab(schema.getCluster()).nodeNameUpdated(
									schema, cmd.getOldName());
						}
					}

				});

			}

		});

		MenuItem menuItemDropSchema = new MenuItem(menuSchema, SWT.PUSH);
		menuItemDropSchema.setText(Messages.MenuTree_14);
		menuItemDropSchema.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Schema schema = (Schema) treeSelection.getFirstElement();

				final SchemaDropCommand cmd = new SchemaDropCommand();
				cmd.setNode(schema);
				cmd.execute();
				tree.refresh();
			}

		});
		return menuSchema;
	}

	private static Menu createMenuTable(final TreeViewer tree) {
		final Menu menuTable = new Menu(tree.getTree());

		MenuItem menuItemUpdateTableName = new MenuItem(menuTable, SWT.PUSH);
		menuItemUpdateTableName.setText(Messages.MenuTree_15);
		menuItemUpdateTableName.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {

				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Table table = (Table) treeSelection.getFirstElement();

				final TableNameUpdateCommand cmd = new TableNameUpdateCommand();
				cmd.setNode(table);
				cmd.execute();
				cmd.getShell().addDisposeListener(new DisposeListener() {

					
					public void widgetDisposed(DisposeEvent e) {
						if (cmd.isOk()) {
							tree.refresh();
							tree.getTree().update();

							RequestsView view = (RequestsView) Activator
									.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.findView(RequestsView.ID);
							view.getTab(table.getCluster()).nodeNameUpdated(
									table, cmd.getOldName());
						}
					}

				});

			}

		});

		MenuItem menuItemDropTable = new MenuItem(menuTable, SWT.PUSH);
		menuItemDropTable.setText(Messages.MenuTree_16);
		menuItemDropTable.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Table table = (Table) treeSelection.getFirstElement();

				final TableDropCommand cmd = new TableDropCommand();
				cmd.setNode(table);
				cmd.execute();

				Schema schema = table.getSchema();

				
				for(IEditorReference ref : Activator.getDefault().getEditorsFor(SQLDesignEditorInput.class)){
					
					try{
						if (((SQLDesignEditorInput)ref.getEditorInput()).getSchemaView().getSchema() == schema){
							SQLDesignGraphicalEditor editor = (SQLDesignGraphicalEditor)ref.getEditor(false);

							if (editor != null){
								((SchemaPart) editor.getViewer().getRootEditPart().getChildren().get(0)).refresh();
							}
								
						}
					}catch(Exception ex){
						
					}
					
					
				}
				
				tree.refresh();
			}

		});

		MenuItem menuItemTruncateTable = new MenuItem(menuTable, SWT.PUSH);
		menuItemTruncateTable.setText(Messages.MenuTree_17);
		menuItemTruncateTable.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Table table = (Table) treeSelection.getFirstElement();

				RequestsView view = (RequestsView) Activator.getDefault()
						.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().findView(RequestsView.ID);
				view.getTab(table.getCluster()).truncateTable(table);
			}

		});

		MenuItem menuItemBrowseTable = new MenuItem(menuTable, SWT.PUSH);
		menuItemBrowseTable.setText(Messages.MenuTree_18);
		menuItemBrowseTable.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final Table table = (Table) treeSelection.getFirstElement();

				if (table.getDatabaseConnection() == null) {
					MessageDialog.openError(tree.getControl().getShell(),
							Messages.MenuTree_19,
							Messages.MenuTree_20);
					return;
				}
				
				try {
					ExtractData.extractWhenNotLoaded(table);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}

				HashMap<String, Column> hmColumns = table.getColumns();

				List<String> listColumns = new ArrayList<String>();

				for (int i = 0; i < hmColumns.size(); i++)
					listColumns.add(""); //$NON-NLS-1$
				Statement sta = null;
				ResultSet rs = null;
				try {
					sta = table.getDatabaseConnection().getSocket().createStatement();
							
					rs = sta.executeQuery("SELECT * FROM " + table.getSimplePath()); //$NON-NLS-1$
					for (String columnName : hmColumns.keySet()) {
						listColumns.set(rs.findColumn(columnName) - 1,
								columnName);
					}

					List<List<Object>> listData = new ArrayList<List<Object>>();

					while (rs.next()) {
						List<Object> listData1 = new ArrayList<Object>();
						for (String columnName : listColumns) {
							listData1.add(rs.getString(columnName));
						}
						listData.add(listData1);
					}

					DialogBrowseContent dial = new DialogBrowseContent(
							Activator.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							listColumns, listData);
					dial.open();

				} catch (SQLException e1) {
					e1.printStackTrace();
				}finally{
					if (rs != null){
						try{
							rs.close();
						}catch(Exception ex){
						
						}
					}
					if (sta != null){
						try {
							sta.close();
						} catch (SQLException e1) {
							
							e1.printStackTrace();
						}
					}
				}

			}

		});

		return menuTable;
	}

	private static Menu createMenuView(final TreeViewer tree) {
		final Menu menuView = new Menu(tree.getTree());

		MenuItem menuItemDropView = new MenuItem(menuView, SWT.PUSH);
		menuItemDropView.setText(Messages.MenuTree_23);
		menuItemDropView.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				TreeSelection treeSelection = (TreeSelection) tree
						.getSelection();
				final SQLView view = (SQLView) treeSelection.getFirstElement();

				final ViewDropCommand cmd = new ViewDropCommand();
				cmd.setNode(view);
				cmd.execute();
				tree.refresh();
			}

		});
		return menuView;
	}
}
