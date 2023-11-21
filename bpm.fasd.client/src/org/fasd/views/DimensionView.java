package org.fasd.views;

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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.exceptions.HierarchyException;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.views.actions.ActionAddDimGrp;
import org.fasd.views.actions.ActionAddDimension;
import org.fasd.views.actions.ActionAddHiera;
import org.fasd.views.actions.ActionAddLevel;
import org.fasd.views.actions.ActionAddOneColumnTimeDim;
import org.fasd.views.actions.ActionDeleteDims;
import org.fasd.views.actions.ActionEditOneColumnDateDim;
import org.fasd.views.actions.ActionPreloadConfig;
import org.fasd.views.dialogs.DialogDimBrowser;
import org.fasd.views.dialogs.DialogSelectDimension;
import org.fasd.views.dialogs.DialogTimeDimension;
import org.freeolap.FreemetricsPlugin;

public class DimensionView extends ViewPart {
	public static final String ID = "org.fasd.views.dimensionView"; //$NON-NLS-1$
	private TreeViewer viewer;
	private Composite par;
	private DimensionView site = this;
	private ToolBar toolBar;
	private Action dimTime, addDim, addDateDim, editDateDim, delItem, addHiera, addLevel, addGroup, addDimToGroup, browseDim, preloadConfigAction;// ,
																																					// secuDim;
	private ToolItem addDimIt, addDimDateIt, addHieraIt, delIt, addLevelIt, addGroupIt, browseDimIt, preloadConfigItem;
	private Observer schObserver;

	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private UndoContext undoContext;
	private ToolItem undoIt;
	private ToolItem redoIt;

	public DimensionView() {
		super();
		initializeOperationHistory();
	}

	public void registerObservable(Observable o) {
		o.addObserver(schObserver);
	}

	public void createPartControl(Composite parent) {
		par = new Composite(parent, SWT.NONE);
		GridLayout g = new GridLayout(1, false);
		par.setLayout(g);

		toolBar = new ToolBar(par, SWT.NONE);
		toolBar.setLayoutData(new GridData(SWT.FILL));

		FreemetricsPlugin.getDefault().registerDimensionView(this);

		viewer = new TreeViewer(par, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setUseHashlookup(true);

		createActionHandlers();
		createActions();
		fillToolbar();

		// actions
		fillTreeViewerContextMenu(viewer);

		// listenner
		setListenner(viewer);

		// dnd
		setDnD(viewer);

		getSite().getShell().setMaximized(true);

		getSite().setSelectionProvider(viewer);
		refresh();
		viewer.setAutoExpandLevel(4);
	}

	private void createActions() {
		addDim = new ActionAddDimension(this, undoContext);
		addHiera = new ActionAddHiera(this, viewer, undoContext);
		addLevel = new ActionAddLevel(this, viewer, undoContext);
		delItem = new ActionDeleteDims(this, viewer, undoContext);
		addGroup = new ActionAddDimGrp(this, viewer, undoContext);
		addDateDim = new ActionAddOneColumnTimeDim(this, undoContext);
		editDateDim = new ActionEditOneColumnDateDim(this, undoContext);

		addDimToGroup = new Action(LanguageText.DimensionView_AddToDimensionGroup) {
			public void run() {
				OLAPDimensionGroup g = ((TreeDimGroup) ((StructuredSelection) viewer.getSelection()).getFirstElement()).getOLAPDimensionGroup();
				DialogSelectDimension dial = new DialogSelectDimension(DimensionView.this.getSite().getShell(), null);
				if (dial.open() == Dialog.OK) {
					OLAPDimension d = dial.getDim();
					d.setGroupId(g.getId());
				}
			}
		};
		browseDim = new Action() {
			public void run() {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();

				if (o instanceof TreeDim) {
					DialogDimBrowser dial = new DialogDimBrowser(DimensionView.this.getSite().getShell(), ((TreeDim) o).getOLAPDimension());
					dial.open();
				}
			}
		};

		browseDim.setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/search.png")));; //$NON-NLS-1$

		dimTime = new Action(LanguageText.DimensionView_TimeDimensionHelper) {
			public void run() {
				DialogTimeDimension d = new DialogTimeDimension(getSite().getShell());

				if (d.open() == Dialog.OK) {
					StructuredSelection ss = (StructuredSelection) viewer.getSelection();
					OLAPDimension dim = ((TreeDim) ss.getFirstElement()).getOLAPDimension();
					dim.addHierarchy(d.getHierarchy());
					refresh();

				}
			}
		};

		preloadConfigAction = new ActionPreloadConfig(this, undoContext);
	}

	private void fillToolbar() {
		addDimIt = new ToolItem(toolBar, SWT.PUSH);
		addDimIt.setToolTipText(LanguageText.DimensionView_AddDimension);
		addDimIt.setImage(addDim.getImageDescriptor().createImage());
		addDimIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new ActionAddDimension(DimensionView.this, undoContext).run();
			}
		});

		addDimDateIt = new ToolItem(toolBar, SWT.PUSH);
		addDimDateIt.setToolTipText(LanguageText.DimensionView_0);
		addDimDateIt.setImage(addDateDim.getImageDescriptor().createImage());
		addDimDateIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDateDim.run();
			}
		});

		addHieraIt = new ToolItem(toolBar, SWT.PUSH);
		addHieraIt.setToolTipText(LanguageText.DimensionView_AddHierarchy);
		addHieraIt.setImage(addHiera.getImageDescriptor().createImage());
		addHieraIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addHiera.run();
			}
		});

		addLevelIt = new ToolItem(toolBar, SWT.PUSH);
		addLevelIt.setToolTipText(LanguageText.DimensionView_AddLevel);
		addLevelIt.setImage(addLevel.getImageDescriptor().createImage());
		addLevelIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addLevel.run();
			}
		});

		addGroupIt = new ToolItem(toolBar, SWT.PUSH);
		addGroupIt.setToolTipText(LanguageText.DimensionView_AddDimensionGroup);
		addGroupIt.setImage(addGroup.getImageDescriptor().createImage());
		addGroupIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addGroup.run();
			}
		});

		delIt = new ToolItem(toolBar, SWT.PUSH);
		delIt.setToolTipText(LanguageText.DimensionView_DelItem);
		delIt.setImage(delItem.getImageDescriptor().createImage());
		delIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				delItem.run();
			}
		});

		browseDimIt = new ToolItem(toolBar, SWT.PUSH);
		browseDimIt.setToolTipText(LanguageText.DimensionView_BrowseDimension);
		browseDimIt.setImage(browseDim.getImageDescriptor().createImage());
		browseDimIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseDim.run();
			}
		});

		undoIt = new ToolItem(toolBar, SWT.PUSH);
		undoIt.setToolTipText(LanguageText.DimensionView_4);
		undoIt.setImage(undoAction.getImageDescriptor().createImage());
		undoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undoAction.run();
			}
		});

		redoIt = new ToolItem(toolBar, SWT.PUSH);
		redoIt.setToolTipText(LanguageText.DimensionView_5);
		redoIt.setImage(redoAction.getImageDescriptor().createImage());
		redoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				redoAction.run();
			}
		});

		preloadConfigItem = new ToolItem(toolBar, SWT.PUSH);
		preloadConfigItem.setToolTipText(LanguageText.DimensionView_1);
		preloadConfigItem.setImage(preloadConfigAction.getImageDescriptor().createImage());
		preloadConfigItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				preloadConfigAction.run();
			}
		});
	}

	private void setDnD(final TreeViewer v) {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		v.addDragSupport(ops, transfers, new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				// nothing, DnD always possible
			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) v.getSelection();

				Object o = ss.getFirstElement();
				if (o instanceof TreeDim) {
					event.data = "DIM/" + ((TreeDim) o).getOLAPDimension().getId() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
				} else if (o instanceof TreeDimGroup) {
					event.data = "GPDIM/" + ((TreeDimGroup) o).getOLAPDimensionGroup().getId() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (o instanceof TreeLevel) {
					event.data = "LVL/" + ((TreeLevel) o).getOLAPLevel().getId() + "/"; //$NON-NLS-1$ //$NON-NLS-2$
				}

			}

			public void dragFinished(DragSourceEvent event) {
				// nothing
			}
		});
		v.addDropSupport(ops, transfers, new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
				// nothing

			}

			public void dragLeave(DropTargetEvent event) {
				// nothing
			}

			public void dropAccept(DropTargetEvent event) {

			}

			public void drop(DropTargetEvent event) {
				String[] buf = ((String) event.data).split("/"); //$NON-NLS-1$
				Object o = ((TreeItem) event.item).getData();

				if (o instanceof TreeHierarchy && buf[0].equals("COL")) { //$NON-NLS-1$
					TreeHierarchy item = ((TreeHierarchy) o);
					OLAPHierarchy hiera = item.getOLAPHierarchy();
					OLAPLevel l = new OLAPLevel();
					l.setItem(FreemetricsPlugin.getDefault().getFAModel().findDataObjectItem(buf[1]));
					l.setName(l.getItem().getName());
					try {
						hiera.addLevel(l);
					} catch (HierarchyException e) {
						MessageDialog.openInformation(site.getSite().getShell(), LanguageText.DimensionView_UnableToAddThisLevel, LanguageText.DimensionView_HierarchyContainingChildWithonly1Level);
						e.printStackTrace();
					}
				}

				else if (o instanceof TreeLevel && buf[0].equals("LVL")) { //$NON-NLS-1$
					TreeLevel item = ((TreeLevel) o);
					OLAPLevel lvl = item.getOLAPLevel();

					for (OLAPLevel l : lvl.getParent().getLevels()) {
						if (l.getId().equals(buf[1])) {
							lvl.getParent().swapLevels(l, lvl);
							break;
						}
					}

				} else if (o instanceof TreeLevel && buf[0].equals("COL")) { //$NON-NLS-1$
					TreeLevel item = ((TreeLevel) o);
					OLAPLevel lvl = item.getOLAPLevel();
					OLAPLevel l = new OLAPLevel();
					l.setItem(FreemetricsPlugin.getDefault().getFAModel().findDataObjectItem(buf[1]));
					l.setName(l.getItem().getName());
					try {
						lvl.getParent().addLevel(lvl.getNb(), l);
					} catch (HierarchyException e) {
						MessageDialog.openInformation(site.getSite().getShell(), LanguageText.DimensionView_UnableToAddThisLevel, LanguageText.DimensionView_HierarchyContainingChildWithonly1Level);
						e.printStackTrace();
					}

				}

				else if (o instanceof TreeDimGroup && buf[0].equals("DIM")) { //$NON-NLS-1$
					OLAPDimensionGroup g = (((TreeDimGroup) o)).getOLAPDimensionGroup();
					OLAPDimension m = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().findDimension(buf[1]);
					if (m != null) {
						m.setGroup(g);
					}
				} else if (o instanceof TreeRoot && buf[0].equals("DIM")) { //$NON-NLS-1$
					OLAPDimension d = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().findDimension(buf[1]);
					if (d != null) {
						d.setGroup(null);

					}
				} else if (o instanceof TreeRoot && buf[0].equals("COL")) { //$NON-NLS-1$
					OLAPDimension dim = new OLAPDimension();

					OLAPHierarchy h = new OLAPHierarchy();
					h.setName(LanguageText.DialogAddHiera_Default);

					OLAPLevel l = new OLAPLevel();
					l.setItem(FreemetricsPlugin.getDefault().getFAModel().findDataObjectItem(buf[1]));
					l.setName(l.getItem().getName());
					try {
						h.addLevel(l);
					} catch (HierarchyException e) {
						MessageDialog.openInformation(site.getSite().getShell(), LanguageText.DimensionView_UnableToAddThisLevel, LanguageText.DimensionView_HierarchyContainingChildWithonly1Level);
						e.printStackTrace();
					}

					dim.setName(l.getItem().getName());
					h.setAllMember(LanguageText.DimensionView_6 + " " + l.getItem().getName());
					dim.addHierarchy(h);

					((ActionAddDimension) addDim).run(dim);
				}

				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
			}
		}

		);
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
					if (o instanceof TreeDim) {
						addHieraIt.setEnabled(true);
						addLevelIt.setEnabled(false);
						delIt.setEnabled(true);
						RoleView view = ((RoleView) site.getSite().getWorkbenchWindow().getActivePage().findView(RoleView.ID));
						if (view != null)
							view.selectionChanged(site, selection);
					} else if (o instanceof TreeHierarchy) {
						addHieraIt.setEnabled(false);
						addLevelIt.setEnabled(true);
						delIt.setEnabled(true);

						RoleView view = ((RoleView) site.getSite().getWorkbenchWindow().getActivePage().findView(RoleView.ID));
						if (view != null)
							view.selectionChanged(site, selection);
					} else if (o instanceof TreeLevel) {
						addHieraIt.setEnabled(false);
						addLevelIt.setEnabled(false);
						delIt.setEnabled(true);
						RoleView view = ((RoleView) site.getSite().getWorkbenchWindow().getActivePage().findView(RoleView.ID));
						if (view != null)
							view.selectionChanged(site, selection);
					} else if (o instanceof TreeDimGroup) {
						delIt.setEnabled(true);
					} else {
						delIt.setEnabled(false);
					}

					DetailView view = ((DetailView) site.getSite().getWorkbenchWindow().getActivePage().findView(DetailView.ID));
					view.selectionChanged(site, selection);
				}
			}
		});

		schObserver = new Observer() {
			public void update(Observable arg0, Object arg1) {
				refresh();
			}

		};

	}

	private TreeParent createModel(OLAPSchema schema) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeRoot rr = new TreeRoot(LanguageText.DimensionView_Dimensions, schema);
		// dims outside groups
		if (schema != null && schema.getDimensions() != null) {
			List<OLAPDimension> dims = schema.getDimensions();
			for (int i = 0; i < dims.size(); i++) {
				if (dims.get(i).getGroupId() == null || dims.get(i).getGroupId().equals("")) { //$NON-NLS-1$
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
					}

					rr.addChild(tdim);
				}
			}
			root.addChild(rr);

			// dims inside groups
			for (OLAPDimensionGroup group : schema.getDimensionGroups())
				root.addChild(createGroups(group));

		}

		return root;
	}

	private TreeDimGroup createGroups(OLAPDimensionGroup group) {
		TreeDimGroup tdg = new TreeDimGroup(group);

		for (OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
			if (group.getId().equals(d.getGroupId())) {
				TreeDim td = new TreeDim(d);
				for (OLAPHierarchy h : td.getOLAPDimension().getHierarchies()) {
					TreeHierarchy thiera = new TreeHierarchy(h);
					for (OLAPLevel l : h.getLevels()) {
						TreeLevel lvl = new TreeLevel(l);
						thiera.addChild(lvl);
					}

					td.addChild(thiera);
				}
				tdg.addChild(td);
			}

		}

		for (OLAPGroup g : group.getChilds()) {
			tdg.addChild(createGroups((OLAPDimensionGroup) g));
		}
		return tdg;
	}

	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.DimensionView_2 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.DimensionView_3);
		}

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	private void fillTreeViewerContextMenu(final TreeViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeDim) {
					menuMgr.add(addHiera);
					menuMgr.add(delItem);

					if (FreemetricsPlugin.getDefault().getFAModel().getDataSources() != null && FreemetricsPlugin.getDefault().getFAModel().getDataSources().get(0) instanceof DatasourceOda && ((TreeDim) o).getOLAPDimension().isOneColumnDate()) {
						((ActionEditOneColumnDateDim) editDateDim).setDimension(((TreeDim) o).getOLAPDimension());
						menuMgr.add(editDateDim);
					} else {

						if (((TreeDim) o).getOLAPDimension().isDate()) {
							menuMgr.add(dimTime);
						}
					}

				} else if (o instanceof TreeHierarchy) {
					menuMgr.add(addLevel);
					menuMgr.add(delItem);
				} else if (o instanceof TreeLevel) {
					menuMgr.add(delItem);
				} else if (o instanceof TreeDimGroup) {
					menuMgr.add(addDimToGroup);

					menuMgr.add(delItem);
				} else {
					menuMgr.add(addDim);

					if (FreemetricsPlugin.getDefault().getFAModel().getDataSources() != null && FreemetricsPlugin.getDefault().getFAModel().getDataSources().get(0) instanceof DatasourceOda) {

						menuMgr.add(addDateDim);
					}

					menuMgr.add(delItem);
				}
				menuMgr.add(new Separator());

				menuMgr.add(undoAction);
				menuMgr.add(redoAction);
				menuMgr.update();

			}
		});
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

	}

	public void refresh() {
		if (viewer.getTree().isDisposed()) {
			return;
		}
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();

		if (model == null) {
			return; // there is no data
		}

		OLAPSchema schema = model.getOLAPSchema();
		TreeParent root = createModel(schema);
		viewer.setInput(root);
	}

	private void createActionHandlers() {
		undoAction = new UndoActionHandler(this.getSite(), undoContext);
		redoAction = new RedoActionHandler(this.getSite(), undoContext);
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
	}

	private void initializeOperationHistory() {
		undoContext = new ObjectUndoContext(this);
	}

	public IOperationHistory getOperationHistory() {
		return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
	}

	public void refreshWithExpand() {
		refresh();
		viewer.expandAll();
	}
}