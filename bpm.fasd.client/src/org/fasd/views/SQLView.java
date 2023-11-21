package org.fasd.views;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPRelation;
import org.fasd.preferences.PreferenceConstants;
import org.fasd.utils.trees.TreeColumn;
import org.fasd.utils.trees.TreeConnection;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRelation;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.actions.ActionAddOdaConnection;
import org.fasd.views.actions.ActionAddOdaDataset;
import org.fasd.views.actions.ActionBrowseColumn;
import org.fasd.views.actions.ActionBrowseTable;
import org.fasd.views.actions.ActionDeleteConnection;
import org.fasd.views.actions.ActionEditOdaConnection;
import org.fasd.views.actions.ActionEditOdaDataset;
import org.fasd.views.dialogs.DialogBrowseColumn;
import org.fasd.views.dialogs.DialogGraphRelations;
import org.fasd.views.dialogs.DialogRelation;
import org.freeolap.FreemetricsPlugin;

public class SQLView extends ViewPart {
	public static final String ID = "org.fasd.views.sqlView"; //$NON-NLS-1$
	private TreeViewer viewer;
	private Composite par;
	private SQLView site = this;
	private ToolBar toolBar;
	private Action addOdaDs, addOdaObject, editOdaDs, editOdaObject, delConnexion, rel;
	private Action editSqlView, setDefaultConnection;
	private ToolItem browseIt, addOdaDsIt, addOdaObjectIt, delConnexionIt, relIt, browseX, graphRel;
	private Observer modObserver;

	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private UndoContext undoContext;
	private ToolItem undoIt, redoIt;

	public SQLView() {
		super();
		initializeOperationHistory();
	}

	public void registerObservable(Observable o) {
		o.addObserver(modObserver);
	}

	public void createPartControl(Composite parent) {
		par = new Composite(parent, SWT.NONE);
		GridLayout g = new GridLayout(1, false);
		par.setLayout(g);

		toolBar = new ToolBar(par, SWT.NONE);
		toolBar.setLayoutData(new GridData(SWT.FILL));

		FreemetricsPlugin.getDefault().registerSQLView(this);

		viewer = new TreeViewer(par, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setUseHashlookup(true);

		createActionHandlers();
		createActions();
		fillToolbar();

		// listener
		setListenner(viewer);

		// actions
		fillTreeViewerContextMenu(viewer);

		// dnd set
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		DragSource dragSource = new DragSource(viewer.getControl(), ops);
		dragSource.setTransfer(transfers);
		dragSource.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				// nothing, DnD always possible
			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				if (ss.getFirstElement() instanceof TreeTable) {
					event.data = "TBL/" + ((TreeTable) ss.getFirstElement()).getTable().getId(); //$NON-NLS-1$
				}
				if (ss.getFirstElement() instanceof TreeColumn) {
					event.data = "COL/" + ((TreeColumn) ss.getFirstElement()).getColumn().getId(); //$NON-NLS-1$
				}
			}

			public void dragFinished(DragSourceEvent event) {
				// nothing
			}
		});

		getSite().getShell().setMaximized(true);
		getSite().setSelectionProvider(viewer);
		refresh(true);
		viewer.setAutoExpandLevel(3);
	}

	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.SQLView_0 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.SQLView_1);
		}
	}

	public UndoContext getUndoContext() {
		return undoContext;
	}

	protected void createActions() {

		setDefaultConnection = new Action("Set as default connection") { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				DataSourceConnection c = (DataSourceConnection) ((TreeConnection) ss.getFirstElement()).getDriver();

				c.getParent().setDriver(c);
				viewer.refresh();
			}
		};

		editSqlView = new Action("Edit Sql") { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				DataObject o = ((TreeTable) ss.getFirstElement()).getTable();

				DialogSqlSelect dial = new DialogSqlSelect(getSite().getShell(), o.getDataSource().getDriver(), o.getSelectStatement(), o.getName());
				if (dial.open() != DialogSqlSelect.OK) {
					return;
				}

				o.setSelectStatement(dial.getQuery());
				o.setName(dial.getName());

				viewer.refresh();
			}
		};
		editSqlView.setToolTipText(LanguageText.SQLView_4);

		addOdaDs = new ActionAddOdaConnection(site, undoContext);

		addOdaObject = new ActionAddOdaDataset(site, undoContext);

		editOdaDs = new ActionEditOdaConnection(site, undoContext, viewer);

		editOdaObject = new ActionEditOdaDataset(site, undoContext, viewer);

		delConnexion = new ActionDeleteConnection(site, viewer, undoContext);
		Image img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_relation.png"); //$NON-NLS-1$
		rel = new Action(LanguageText.SQLView_New_Relation, ImageDescriptor.createFromImage(img)) {
			public void run() {
				DialogRelation dial = new DialogRelation(SQLView.this.getSite().getShell());
				FAModel m = FreemetricsPlugin.getDefault().getFAModel();
				if (dial.open() == DialogRelation.OK) {
					for (OLAPRelation r : dial.getRelations()) {
						if (!m.getRelations().contains(r))
							m.addRelation(r);
					}

					List<OLAPRelation> toDel = new ArrayList<OLAPRelation>();
					for (OLAPRelation r : m.getRelations()) {
						if (!dial.getRelations().contains(r))
							toDel.add(r);
					}

					for (OLAPRelation r : toDel) {
						m.removeRelation(r);
					}
					m.getListDataSource().setChanged();
				}
			}
		};

		img = new Image(this.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_file.png"); //$NON-NLS-1$

	}

	protected void fillToolbar() {

		addOdaDsIt = new ToolItem(toolBar, SWT.PUSH);
		addOdaDsIt.setToolTipText(LanguageText.ActionAddConnectionODA);
		addOdaDsIt.setImage(addOdaDs.getImageDescriptor().createImage());
		addOdaDsIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addOdaDs.run();
			}
		});

		addOdaObjectIt = new ToolItem(toolBar, SWT.PUSH);
		addOdaObjectIt.setToolTipText(LanguageText.ActionAddDatasetODA);
		addOdaObjectIt.setImage(addOdaObject.getImageDescriptor().createImage());
		addOdaObjectIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addOdaObject.run();
			}
		});

		relIt = new ToolItem(toolBar, SWT.PUSH);
		relIt.setToolTipText(LanguageText.SQLView_Add_Relation);
		relIt.setImage(rel.getImageDescriptor().createImage());
		relIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				rel.run();
			}
		});

		delConnexionIt = new ToolItem(toolBar, SWT.PUSH);
		delConnexionIt.setToolTipText(LanguageText.SQLView_Remove_Item);
		delConnexionIt.setImage(delConnexion.getImageDescriptor().createImage());
		delConnexionIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				delConnexion.run();
			}
		});

		browseIt = new ToolItem(toolBar, SWT.PUSH);
		browseIt.setToolTipText(LanguageText.SQLView_Browse_100_lines);
		browseIt.setImage(new Image(this.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/search.png")); //$NON-NLS-1$
		browseIt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();

				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Object o = ss.getFirstElement();

					if (o instanceof TreeTable) {
						int nblines = 100;
						new ActionBrowseTable((SQLView.this).getSite().getShell(), ((TreeTable) o).getTable(), nblines).run();
					} else if (o instanceof TreeColumn) {
						DataObjectItem item = ((TreeColumn) o).getColumn();
						int nblines = 100;
						ActionBrowseColumn a = new ActionBrowseColumn(item, nblines);
						a.run();
						if (a.getValues().size() > 0 && a.getDistincts().size() > 0) {
							DialogBrowseColumn dial = new DialogBrowseColumn(site.getSite().getShell(), a.getValues(), a.getDistincts(), item);
							dial.open();
						}

					}

				}
			}

		});

		browseX = new ToolItem(toolBar, SWT.PUSH);
		browseX.setToolTipText(LanguageText.SQLView_Browse_X_Lines);
		browseX.setImage(new Image(this.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/search_n.png")); //$NON-NLS-1$
		browseX.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					Object o = ss.getFirstElement();

					if (o instanceof TreeTable) {
						int X = Integer.parseInt(FreemetricsPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BROWSERFIRSTXLINES));
						new ActionBrowseTable((SQLView.this).getSite().getShell(), ((TreeTable) o).getTable(), X).run();
					} else if (o instanceof TreeColumn) {
						DataObjectItem item = ((TreeColumn) o).getColumn();
						int nblines = Integer.parseInt(FreemetricsPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BROWSERFIRSTXLINES));
						ActionBrowseColumn a = new ActionBrowseColumn(item, nblines);
						a.run();
						if (a.getValues().size() > 0 && a.getDistincts().size() > 0) {
							DialogBrowseColumn dial = new DialogBrowseColumn((SQLView.this).getSite().getShell(), a.getValues(), a.getDistincts(), item);
							dial.open();
						}
					} else {
						MessageDialog.openInformation((SQLView.this).getSite().getShell(), LanguageText.SQLView_Information, LanguageText.SQLView_Select_a_tbl_To_Browse_It);
					}
				}
			}

		});

		undoIt = new ToolItem(toolBar, SWT.PUSH);
		undoIt.setToolTipText(LanguageText.SQLView_2);
		undoIt.setImage(undoAction.getImageDescriptor().createImage());
		undoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undoAction.run();
			}
		});

		redoIt = new ToolItem(toolBar, SWT.PUSH);
		redoIt.setToolTipText(LanguageText.SQLView_3);
		redoIt.setImage(redoAction.getImageDescriptor().createImage());
		redoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				redoAction.run();
			}
		});

		graphRel = new ToolItem(toolBar, SWT.PUSH);
		graphRel.setToolTipText(LanguageText.SQLView_Relation_Graphe);
		graphRel.setImage(new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/gr_rel.png")); //$NON-NLS-1$
		graphRel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				DataObject table = ((TreeTable) ss.getFirstElement()).getTable();
				DialogGraphRelations dial = new DialogGraphRelations(FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), table);
				dial.open();
			}
		});

	}

	public void refresh(boolean saveState) {
		if (false) {
			viewer.refresh();
		} else {
			fillTree();
		}

	}

	/**
	 * fillTree : fill the tree viewer with fresh info
	 * 
	 */
	private void fillTree() {
		if (viewer.getControl().isDisposed()) {
			return;
		}

		FAModel model = FreemetricsPlugin.getDefault().getFAModel();

		if (model == null) {
			return; // no data
		}

		try {
			TreeParent root = createModel(model);
			viewer.setInput(root);

		} catch (SQLException ex) {
			ex.printStackTrace();
			MessageDialog.openError(viewer.getTree().getShell(), LanguageText.SQLView_Error, ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(viewer.getTree().getShell(), LanguageText.SQLView_Error, ex.getMessage());
		}
	}

	private TreeParent createModel(FAModel model) throws SQLException {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeRoot rr = new TreeRoot(LanguageText.SQLView_DataSource, FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema());
		List<DataSource> socks = model.getDataSources();

		for (int i = 0; i < socks.size(); i++) {
			ArrayList<DataObject> tab = socks.get(i).getDataObjects();
			TreeDatabase dbitem = new TreeDatabase(socks.get(i));

			for (int j = 0; j < tab.size(); j++) {
				TreeTable item = new TreeTable(tab.get(j));
				System.out.println("	>>>> " + tab.get(j).getName()); //$NON-NLS-1$

				for (int k = 0; k < tab.get(j).getColumns().size(); k++) {

					TreeColumn itm = new TreeColumn(tab.get(j).getColumns().get(k));
					item.addChild(itm);

				}
				item.setLoaded();
				dbitem.addChild(item);
			}

			for (DataSourceConnection c : socks.get(i).getDrivers()) {
				TreeConnection tc = new TreeConnection(c);
				dbitem.addChild(tc);

			}

			rr.addChild(dbitem);
		}

		TreeRoot rels = new TreeRoot(LanguageText.SQLView_Relations, model.getOLAPSchema());
		for (OLAPRelation r : model.getRelations()) {
			TreeRelation tr = new TreeRelation(r);
			rels.addChild(tr);
		}
		root.addChild(rr);
		root.addChild(rels);
		return root;
	}

	private void fillTreeViewerContextMenu(final TreeViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeDatabase) {

					if (((TreeDatabase) o).getDriver() instanceof DatasourceOda) {
						menuMgr.add(editOdaDs);
						menuMgr.add(addOdaObject);
					}
					menuMgr.add(delConnexion);
				} else if (o instanceof TreeTable) {

					if (((TreeTable) o).getTable() instanceof DataObjectOda) {
						menuMgr.add(editOdaObject);
					}

					menuMgr.add(delConnexion);

					if (((TreeTable) o).getTable().isView()) {
						menuMgr.add(new Separator());
						menuMgr.add(editSqlView);
						menuMgr.add(new Separator());
					}
				} else if (o instanceof TreeRelation) {
					menuMgr.add(delConnexion);
				} else if (o instanceof TreeRoot) {
					menuMgr.add(addOdaDs);

				} else if (o instanceof TreeConnection) {
					menuMgr.add(setDefaultConnection);
				}
				menuMgr.add(rel);
				menuMgr.add(new Separator());
				menuMgr.add(undoAction);
				menuMgr.add(redoAction);

				menuMgr.update();

			}
		});
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

	}

	private void setListenner(TreeViewer tree) {
		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					// set detail to empty
				}

				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					// we only take one
					Object o = selection.getFirstElement();
					if (o instanceof TreeDatabase) {
						delConnexionIt.setEnabled(true);
						browseIt.setEnabled(false);
						browseX.setEnabled(false);
						graphRel.setEnabled(false);
						relIt.setEnabled(true);
					} else if (o instanceof TreeConnection) {
						delConnexionIt.setEnabled(true);
					} else if (o instanceof TreeTable) {
						graphRel.setEnabled(true);
						delConnexionIt.setEnabled(true);
						browseIt.setEnabled(true);
						browseX.setEnabled(true);
						relIt.setEnabled(true);
					} else if (o instanceof TreeRelation) {
						graphRel.setEnabled(false);
						relIt.setEnabled(true);
						browseX.setEnabled(false);
						browseIt.setEnabled(false);
						delConnexionIt.setEnabled(true);
					} else if (o instanceof TreeColumn) {
						graphRel.setEnabled(false);
						if (((TreeColumn) o).getColumn().getType().equals("calculated")) //$NON-NLS-1$
							delConnexionIt.setEnabled(true);
						else
							delConnexionIt.setEnabled(false);

						browseIt.setEnabled(true);
						browseX.setEnabled(true);
						relIt.setEnabled(true);
					} else {
						graphRel.setEnabled(false);
						delConnexionIt.setEnabled(false);
						browseIt.setEnabled(false);
						browseX.setEnabled(false);
						relIt.setEnabled(true);
					}
					DetailView view = ((DetailView) site.getSite().getWorkbenchWindow().getActivePage().findView(DetailView.ID));
					if (view != null)
						view.selectionChanged(site, selection);
				}
			}
		});

		modObserver = new Observer() {
			public void update(Observable arg0, Object arg1) {
				if (!viewer.getControl().isDisposed() && viewer.getContentProvider() != null) {
					refresh(true);
				}

			}

		};
	}

	private void createActionHandlers() {
		undoAction = new UndoActionHandler(this.getSite(), undoContext);
		redoAction = new RedoActionHandler(this.getSite(), undoContext);
	}

	private void initializeOperationHistory() {
		undoContext = new ObjectUndoContext(this);
	}

	public IOperationHistory getOperationHistory() {
		return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
	}
}