package bpm.sqldesigner.ui.view.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import bpm.sqldesigner.api.database.CreateData;
import bpm.sqldesigner.api.database.DropData;
import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.database.UpdateData;
import bpm.sqldesigner.api.exception.UpdateColumnException;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.command.update.RequestUpdateCommand;
import bpm.sqldesigner.ui.editor.EditorsList;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.TreeView;

public class TabRequests implements RequestListener {

	private TabItem tabItem;
	private List<RequestDescription> requests = new ArrayList<RequestDescription>();
	private HashMap<TreeItem, RequestStatement> statements = new HashMap<TreeItem, RequestStatement>();
	private DatabaseCluster cluster;
	private Tree tree;
	private int createCount = 0;
	private int updateCount = 0;
	private int dropCount = 0;
	private int migrateCount = 0;
	private List<Schema> schemasUpdated = new ArrayList<Schema>();
	private int truncateCount = 0;

	public final static int CREATE_TABLE = 1;
	public final static int CREATE_COLUMN = 2;
	public static final int CREATE_VIEW = 3;
	public static final int CREATE_CATALOG = 4;
	public static final int CREATE_SCHEMA = 5;

	public static final int UPDATE_NAME_COLUMN = 101;
	public static final int UPDATE_NAME_TABLE = 102;
	public static final int UPDATE_NAME_SCHEMA = 103;
	public static final int TRUNCATE_TABLE = 104;

	public static final int DROP_COLUMN = 1001;
	public static final int DROP_PROCEDURE = 1002;
	public static final int DROP_VIEW = 1003;
	public static final int DROP_CATALOG = 1004;
	public static final int DROP_TABLE = 1005;
	public static final int DROP_SCHEMA = 1006;

	public static final int DROP = 1000;
	public static final int CREATE = 0;
	public static final int UPDATE = 100;
	public static final int MIGRATE = 2000;
	public static final int CREATE_FK = 3000;

	public TabRequests(TabFolder tabFolder, int style, DatabaseCluster cluster) {
		tabItem = new TabItem(tabFolder, style);
		this.cluster = cluster;

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);

		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		tree = new Tree(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CHECK);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setHeaderVisible(true);

		final Menu menu = new Menu(tree);
		MenuItem menuItemEdit = new MenuItem(menu, SWT.PUSH);
		menuItemEdit.setText(Messages.TabRequests_0);
		menuItemEdit.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = tree.getSelection();
				RequestUpdateCommand cmd = new RequestUpdateCommand();
				cmd.setItem(selection[0]);
				cmd.execute();
			}
		});

		tree.addMenuDetectListener(new MenuDetectListener() {
			
			public void menuDetected(MenuDetectEvent e) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					if (!selection[0].getText(1).equals("")) { //$NON-NLS-1$
						tree.setMenu(menu);
					} else
						tree.setMenu(null);
				} else
					tree.setMenu(null);

			}
		});

		tree.addSelectionListener(new SelectionListener() {

			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK) {
					TreeItem treeItem = (TreeItem)e.item;
					boolean checked = treeItem.getChecked();

					TreeItem[] items = treeItem.getItems();
					if (items.length > 0) {
						for (TreeItem itemChild : items)
							itemChild.setChecked(checked);
					} else if (checked) {
						if (treeItem.getParentItem() != null)
							treeItem.getParentItem().setChecked(true);
					}
				}
			}

		});

		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setText("Id"); //$NON-NLS-1$
		column1.setWidth(150);
		TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
		column2.setText(Messages.TabRequests_3);
		column2.setWidth(700);

		ToolBar toolbar = new ToolBar(composite, SWT.FLAT);
		toolbar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));

		ToolItem executeBtn = new ToolItem(toolbar, SWT.NONE);
		executeBtn.setImage(Activator.getDefault().getImageRegistry().get(
				"execute_sql")); //$NON-NLS-1$
		executeBtn.setText(Messages.TabRequests_5);
		executeBtn.setToolTipText(Messages.TabRequests_6);
		executeBtn.addSelectionListener(new ExecuteSelectionListener(this));
		
		
//		ToolItem it = new ToolItem(toolbar, SWT.NONE);
//		it.setText("Dump Changes");
////		it.setToolTipText("Execute selected SQL requests");
//		it.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				StringBuilder b = new StringBuilder();
//				
//				for(TreeItem root : tree.getItems()){
//					b.append(root.getText(1) + ";\n");
//					for(TreeItem it : root.getItems()){
//						b.append(it.getText(1) + ";\n");
//					}	
//				}
//				System.out.println(b.toString());
//				
//			}
//		});

		
		tabItem.setText(Messages.TabRequests_7 + cluster.getName());

		composite.layout();

	}

	public void closeTab() {
		tabItem.dispose();
	}

	public TabItem getTabItem() {
		return tabItem;
	}

	
	public void nodeCreated(Object obj) {
		Node node = (Node) obj;
		RequestDescription desc = null;
		RequestDescription desc2 = null;

		if (node instanceof Table) {
			final Table table = (Table) node;
			schemasUpdated.add(table.getSchema());
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return CreateData.createTable(table);
				}

				
				public int getType() {
					return CREATE_TABLE;
				}
			};
		}

		else if (node instanceof Column) {
			final Column column = (Column) node;
			if (column.getTable().isCommit()) {
				schemasUpdated.add(column.getTable().getSchema());
				desc = new RequestDescription() {
					
					public RequestStatement execute() {
						return CreateData.createColumn(column).get(0);
					}

					
					public int getType() {
						return CREATE_COLUMN;
					}
				};

				if (column.isPrimaryKey())
					desc2 = new RequestDescription() {
						
						public RequestStatement execute() {
							return CreateData.createColumn(column).get(1);
						}

						
						public int getType() {
							return CREATE_COLUMN;
						}
					};
			} else
				refreshRequests();

		} else if (node instanceof SQLView) {
			final SQLView view = (SQLView) node;
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return CreateData.createView(view);
				}

				
				public int getType() {
					return CREATE_VIEW;
				}
			};

		} else if (node instanceof Catalog) {
			final Catalog catalog = (Catalog) node;
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return CreateData.createSimpleCatalog(catalog);
				}

				
				public int getType() {
					return CREATE_CATALOG;
				}
			};
		} else if (node instanceof Schema) {
			final Schema schema = (Schema) node;
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return CreateData.createSimpleSchema(schema);
				}

				
				public int getType() {
					return CREATE_SCHEMA;
				}
			};
		} else if (node instanceof LinkForeignKey) {
			final LinkForeignKey link = (LinkForeignKey) node;
			schemasUpdated.add(link.getSource().getTable().getSchema());
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return CreateData.createForeignKey(link);
				}

				
				public int getType() {
					return CREATE_FK;
				}
			};
		}

		if (desc != null) {
			RequestStatement req = desc.execute();
			if (req != null) {
				TreeItem item = new TreeItem(tree, SWT.NONE);
				item.setChecked(true);
				item.setText(new String[] { "create" + createCount, //$NON-NLS-1$
						req.getRequestString() });
				createCount++;
				requests.add(desc);
				statements.put(item, req);

				if (desc2 != null) {
					RequestStatement req2 = desc2.execute();
					if (req2 != null) {
						TreeItem item2 = new TreeItem(item, SWT.NONE);
						item2.setChecked(true);
						item2.setText(new String[] { "create" + createCount, //$NON-NLS-1$
								req2.getRequestString() });
						createCount++;
						requests.add(desc2);
						statements.put(item2, req2);
					}
				}
			}
		}

	}

	public void nodeNameUpdated(Node node, final String oldName) {
		RequestDescription desc = null;

		if (node instanceof Column) {
			final Column column = (Column) node;
			schemasUpdated.add(column.getTable().getSchema());
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					try {
						return UpdateData.updateColumnName(column, null,
								oldName);
					} catch (UpdateColumnException e) {
						e.printStackTrace();
						return null;
					}
				}

				
				public int getType() {
					return UPDATE_NAME_COLUMN;
				}
			};

		} else if (node instanceof Table) {
			final Table table = (Table) node;
			schemasUpdated.add(table.getSchema());
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return UpdateData.updateTableName(table, null, oldName);
				}

				
				public int getType() {
					return UPDATE_NAME_TABLE;
				}
			};
		} else if (node instanceof Schema) {
			final Schema schema = (Schema) node;
			schemasUpdated.add(schema);
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return UpdateData.updateSchemaName(schema, null, oldName);
				}

				
				public int getType() {
					return UPDATE_NAME_SCHEMA;
				}
			};
		}

		if (desc != null) {
			RequestStatement req = desc.execute();
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setChecked(true);
			item.setText(new String[] { "update" + updateCount, //$NON-NLS-1$
					req.getRequestString() });
			updateCount++;
			requests.add(desc);
			statements.put(item, req);
		}
	}

	public void nodeTypeUpdated(final Column column) {
		schemasUpdated.add(column.getTable().getSchema());
		RequestDescription desc = new RequestDescription() {
			
			public RequestStatement execute() {
				return UpdateData.updateColumnType(column, null);
			}

			
			public int getType() {
				return UPDATE_NAME_SCHEMA;
			}
		};

		RequestStatement req = desc.execute();
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setChecked(true);
		item.setText(new String[] { "update" + updateCount, //$NON-NLS-1$
				req.getRequestString() });
		updateCount++;
		requests.add(desc);
		statements.put(item, req);
	}

	public void truncateTable(final Table table) {
		RequestDescription desc = new RequestDescription() {
			
			public RequestStatement execute() {
				return UpdateData.truncateTable(table);
			}

			
			public int getType() {
				return TRUNCATE_TABLE;
			}
		};

		RequestStatement req = desc.execute();
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setChecked(true);
		item.setText(new String[] { "truncate" + truncateCount, //$NON-NLS-1$
				req.getRequestString() });
		truncateCount++;
		requests.add(desc);
		statements.put(item, req);
	}

	public void nodeDropped(Node node) {

		RequestDescription desc = null;

		if (node instanceof Column) {
			final Column column = (Column) node;
			schemasUpdated.add(column.getTable().getSchema());
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return DropData.dropColumn(column);
				}

				
				public int getType() {
					return DROP_COLUMN;
				}
			};
		} else if (node instanceof SQLProcedure) {
			final SQLProcedure procedure = (SQLProcedure) node;
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return DropData.dropProcedure(procedure);
				}

				
				public int getType() {
					return DROP_PROCEDURE;
				}
			};
		} else if (node instanceof SQLView) {
			final SQLView view = (SQLView) node;
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return DropData.dropView(view);
				}

				
				public int getType() {
					return DROP_VIEW;
				}
			};
		} else if (node instanceof Table) {
			final Table table = (Table) node;
			schemasUpdated.add(table.getSchema());
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return DropData.dropTable(table);
				}

				
				public int getType() {
					return DROP_TABLE;
				}
			};
		} else if (node instanceof Catalog) {
			final Catalog catalog = (Catalog) node;
			for (Schema schema : catalog.getSchemas().values()) {
				dropEditorSchema(schema);
			}
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return DropData.dropCatalog(catalog);
				}

				
				public int getType() {
					return DROP_CATALOG;
				}
			};
		} else if (node instanceof Schema) {
			final Schema schema = (Schema) node;
			dropEditorSchema(schema);
			desc = new RequestDescription() {
				
				public RequestStatement execute() {
					return DropData.dropSchema(schema);
				}

				
				public int getType() {
					return DROP_SCHEMA;
				}
			};
		}

		if (desc != null) {
			RequestStatement req = desc.execute();
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setChecked(true);
			item.setText(new String[] { "drop" + dropCount, //$NON-NLS-1$
					req.getRequestString() });
			dropCount++;
			requests.add(desc);
			statements.put(item, req);
		}
	}

	private void dropEditorSchema(Schema schema) {
//		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
//				.getActivePage().closeEditor(EditorsList.get(schema), false);
	}

	public void refreshRequests() {
		int created = 0;
		for (int i = 0; i < requests.size(); i++) {

			RequestDescription reqDesc = requests.get(i);

			int type = reqDesc.getType();

			if (type == CREATE_TABLE) {
				TreeItem item = findItem("create" + created); //$NON-NLS-1$
				RequestStatement req = statements.get(item);
				req = reqDesc.execute();
				String num = item.getText(0);
				item.setText(new String[] { num, req.getRequestString() });
				statements.put(item, req);
			}

			if (type < UPDATE) {
				created++;
			}
		}
	}

	private TreeItem findItem(String findString) {
		TreeItem[] items = tree.getItems();
		TreeItem item = null;

		for (int i = 0; i < items.length && item == null; i++) {
			TreeItem itemIt = items[i];
			if (itemIt.getText(0).equals(findString))
				item = itemIt;
		}

		return item;
	}

	public void addAll(List<RequestStatement> listReq, Node toNode) {
		if (toNode instanceof Column)
			schemasUpdated.add(((Column) toNode).getTable().getSchema());
		else if (toNode instanceof Table)
			schemasUpdated.add(((Table) toNode).getSchema());
		else if (toNode instanceof Schema)
			schemasUpdated.add((Schema) toNode);
		else if (toNode instanceof Catalog)
			schemasUpdated.addAll(((Catalog) toNode).getSchemas().values());
		else if (toNode instanceof DatabaseCluster) {
			DatabaseCluster cluster = (DatabaseCluster) toNode;
			TreeMap<String, Catalog> catalogs = cluster.getCatalogs();
			for (String catalogName : catalogs.keySet()) {
				Catalog catalog = catalogs.get(catalogName);
				schemasUpdated.addAll(catalog.getSchemas().values());
			}
		}

		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setChecked(true);
		item.setText(new String[] { "migrate" + migrateCount, "" }); //$NON-NLS-1$ //$NON-NLS-2$
		migrateCount++;

		for (RequestStatement req : listReq) {
			addMigrateReq(req, item);
		}
	}

	private void addMigrateReq(final RequestStatement req, TreeItem itemMain) {
		RequestDescription desc = new RequestDescription() {
			
			public int getType() {
				return MIGRATE;
			}

			
			public RequestStatement execute() {
				return req;
			}
		};

		TreeItem item = new TreeItem(itemMain, SWT.NONE);
		item.setChecked(true);
		item.setText(new String[] { "migrate" + migrateCount, //$NON-NLS-1$
				req.getRequestString() });
		migrateCount++;
		requests.add(desc);
	}

	protected interface RequestDescription {
		public RequestStatement execute();

		public int getType();
	}

	public List<RequestDescription> getRequests() {
		return requests;
	}

	public DatabaseCluster getCluster() {
		return cluster;
	}

	public Tree getTree() {
		return tree;
	}

	public void reset() {
		createCount = 0;
		updateCount = 0;
		dropCount = 0;
		migrateCount = 0;
		requests.clear();

		// for (Schema schema : schemasUpdated) {
		// SQLDesignGraphicalEditor editor = EditorsList.get(schema);
		// IWorkbenchPage page = Activator.getDefault().getWorkbench()
		// .getActiveWorkbenchWindow().getActivePage();
		// page.closeEditor(editor, false);
		// schema.saveAndRemoveListeners();
		// ExtractData.refreshSchema(schema);
		// schema.restoreListeners();

		// SQLDesignEditorInput input = new SQLDesignEditorInput("input"
		// + (new Date()).getTime());
		// try {
		// page.openEditor(input, SQLDesignGraphicalEditor.ID, false);
		// } catch (PartInitException e) {
		// e.printStackTrace();
		// }
		//
		// ((SQLDesignGraphicalEditor) page.getActiveEditor()).getViewer()
		// .setContents(schema);
		//
		// ((SQLDesignGraphicalEditor) page.getActiveEditor())
		// .getDropListener().setSchema(schema);
		// EditorsList.addEditor(schema, editor);
		//
		// editor.setSchemaLoaded(true);

		// if (editor != null) {
		// ((SchemaPart) editor.getViewer().getRootEditPart()
		// .getChildren().get(0)).refresh();
		// if (schema instanceof SchemaNull)
		// editor.setName(schema.getCatalog().getName());
		// else
		// editor.setName(schema.getName());
		// }
		//
		// editor.getViewer().setContents(schema);
		//
		// editor.getViewer().getRootEditPart().refresh();
		// editor.getViewer().reveal(editor.getViewer().getRootEditPart());
		// }

		((TreeView) Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						TreeView.ID)).refresh();

		tree.removeAll();
	}

	public RequestStatement getRequest(TreeItem item) {
		return statements.get(item);
	}

}
