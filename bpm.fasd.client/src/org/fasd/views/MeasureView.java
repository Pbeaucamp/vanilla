package org.fasd.views;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.views.actions.ActionAddMeasure;
import org.fasd.views.actions.ActionAddMesGrp;
import org.fasd.views.actions.ActionDeleteMeasure;
import org.fasd.views.actions.ActionEditMeasure;
import org.freeolap.FreemetricsPlugin;

import bpm.fasd.expressions.ui.viewer.DialogColorFormula;

public class MeasureView extends ViewPart {
	public static final String ID = "org.fasd.views.measureView"; //$NON-NLS-1$
	private TreeViewer viewer;
	private Composite par;
	private MeasureView site = this;
	private ToolBar toolBar;
	private Action addMes, delMes, addGroup, editMes;
	private ToolItem addMesIt, delMesIt, addGroupIt;
	private Observer schObserver;

	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private UndoContext undoContext;
	private ToolItem undoIt;
	private ToolItem redoIt;
	private ToolItem colorRules;

	private ToolBarManager manager;

	public MeasureView() {
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

		manager = new ToolBarManager();
		manager.add(new GroupMarker("MeasureToolBar")); //$NON-NLS-1$

		toolBar = manager.createControl(par);
		toolBar.setLayoutData(new GridData(SWT.FILL));

		FreemetricsPlugin.getDefault().registerMeasureView(this);

		viewer = new TreeViewer(par, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());

		viewer.setLabelProvider(new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setUseHashlookup(true);

		createActionHandlers();
		createActions();

		fillToolbar();
		// actions
		fillTreeViewerContextMenu(viewer);

		// dnd
		setDnD(viewer);

		// listenner
		setListenner(viewer);

		getSite().setSelectionProvider(viewer);
		refresh();
		viewer.setAutoExpandLevel(3);
	}

	private void createActions() {
		addMes = new ActionAddMeasure(this, undoContext);
		addGroup = new ActionAddMesGrp(this, viewer, undoContext);
		delMes = new ActionDeleteMeasure(this, viewer, undoContext);
		editMes = new ActionEditMeasure(this, undoContext);
	}

	private void fillToolbar() {
		addMesIt = new ToolItem(toolBar, SWT.PUSH);
		addMesIt.setToolTipText(LanguageText.MeasureView_AddMeasure);
		addMesIt.setImage(addMes.getImageDescriptor().createImage());
		addMesIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addMes.run();
			}
		});

		delMesIt = new ToolItem(toolBar, SWT.PUSH);
		delMesIt.setToolTipText(LanguageText.MeasureView_DelMeasure);
		delMesIt.setImage(delMes.getImageDescriptor().createImage());
		delMesIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				delMes.run();
			}
		});

		undoIt = new ToolItem(toolBar, SWT.PUSH);
		undoIt.setToolTipText(LanguageText.MeasureView_0);
		undoIt.setImage(undoAction.getImageDescriptor().createImage());
		undoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undoAction.run();
			}
		});

		redoIt = new ToolItem(toolBar, SWT.PUSH);
		redoIt.setToolTipText(LanguageText.MeasureView_2);
		redoIt.setImage(redoAction.getImageDescriptor().createImage());
		redoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				redoAction.run();
			}
		});

		addGroupIt = new ToolItem(toolBar, SWT.PUSH);
		addGroupIt.setToolTipText(LanguageText.MeasureView_AddMeasureGp);
		addGroupIt.setImage(addGroup.getImageDescriptor().createImage());
		addGroupIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addGroup.run();
			}
		});

		colorRules = new ToolItem(toolBar, SWT.PUSH);
		colorRules.setToolTipText(LanguageText.MeasureView_1);
		colorRules.setImage(FreemetricsPlugin.getDefault().getImageRegistry().get("coloring")); //$NON-NLS-1$
		colorRules.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Object o = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				if (!(o instanceof TreeMes)) {
					return;
				}

				OLAPMeasure m = ((TreeMes) o).getOLAPMeasure();

				DialogColorFormula dial = null;
				if (m.getColorScript() != null) {
					dial = new DialogColorFormula(getSite().getShell(), FreemetricsPlugin.getDefault().getFAModel(), m.getColorScript());
				} else {
					dial = new DialogColorFormula(getSite().getShell(), FreemetricsPlugin.getDefault().getFAModel());
				}
				if (dial.open() == DialogColorFormula.OK) {
					m.setColorScript(dial.getRule());
					viewer.refresh();
				}

			}
		});
	}

	private void setDnD(TreeViewer tviewer) {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		tviewer.addDragSupport(ops, transfers, new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				// nothing, DnD always possible
			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeMes) {
					event.data = "MES/" + ((TreeMes) o).getOLAPMeasure().getId(); //$NON-NLS-1$
				} else if (o instanceof TreeMesGroup) {
					event.data = "GMES/" + ((TreeMesGroup) o).getOLAPMeasureGroup().getId(); //$NON-NLS-1$
				}
			}

			public void dragFinished(DragSourceEvent event) {
				// nothing
			}
		});

		DropTarget dropTable = new DropTarget(tviewer.getControl(), ops);
		dropTable.setTransfer(transfers);
		dropTable.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			public void dragOver(DropTargetEvent event) {
				//
			}

			public void dragOperationChanged(DropTargetEvent event) {
				// nothing
			}

			public void dragLeave(DropTargetEvent event) {
				// nothing
			}

			public void dropAccept(DropTargetEvent event) {
				Object o = ((TreeItem) event.item).getData();
				if (!(o instanceof TreeItem))
					event = null;
			}

			public void drop(DropTargetEvent event) {
				String[] buf = ((String) event.data).split("/"); //$NON-NLS-1$
				Object o = ((TreeItem) event.item).getData();

				// column so it s a measure
				if (o instanceof TreeRoot && buf[0].equals("COL")) { //$NON-NLS-1$
					TreeRoot item = ((TreeRoot) o);
					OLAPSchema sch = item.getOLAPSchema();

					DataObjectItem i = sch.getParent().findDataObjectItem(buf[1]);
					OLAPMeasure m = new OLAPMeasure();
					m.setOrigin(i);
					m.setName(i.getName());
					sch.addMeasure(m);
				} else if (o instanceof TreeMesGroup && buf[0].equals("COL")) { //$NON-NLS-1$
					OLAPMeasureGroup g = (((TreeMesGroup) o)).getOLAPMeasureGroup();

					OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
					DataObjectItem i = sch.getParent().findDataObjectItem(buf[1]);
					OLAPMeasure m = new OLAPMeasure();
					m.setOrigin(i);
					m.setName(i.getName());
					m.setGroup(g);

					sch.addMeasure(m);
				}

				else if (o instanceof TreeMesGroup && buf[0].equals("MES")) { //$NON-NLS-1$
					OLAPMeasureGroup g = (((TreeMesGroup) o)).getOLAPMeasureGroup();
					OLAPMeasure m = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().findMeasure(buf[1]);
					if (m != null) {
						m.setGroup(g);
					}
				} else if (o instanceof TreeRoot && buf[0].equals("MES")) { //$NON-NLS-1$
					for (OLAPMeasure d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getMeasures()) {
						if (d.getId().equals(buf[1])) {
							d.setGroup(null);
							break;
						}
					}
				}
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});
	}

	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.MeasureView_3 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.MeasureView_4);
		}

	}

	private void fillTreeViewerContextMenu(final TreeViewer viewer) {
		final MenuManager menuMgr = new MenuManager("", "unMenuPourTest"); //$NON-NLS-1$ //$NON-NLS-2$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeMes) {
					((ActionEditMeasure) editMes).setMeasure(((TreeMes) o).getOLAPMeasure());
					menuMgr.add(editMes);
					menuMgr.add(delMes);
				} else if (o instanceof TreeRoot) {
					menuMgr.add(addMes);
					menuMgr.add(delMes);
				} else if (o instanceof TreeMesGroup) {
					menuMgr.add(delMes);
				}
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
					colorRules.setEnabled(false);
					if (o instanceof TreeMes) {
						addMesIt.setEnabled(true);
						delMesIt.setEnabled(true);
						addGroupIt.setEnabled(true);
						colorRules.setEnabled(((TreeMes) o).getOLAPMeasure().getOrigin() == null);
					} else if (o instanceof TreeMesGroup) {
						addMesIt.setEnabled(true);
						delMesIt.setEnabled(true);
						addGroupIt.setEnabled(true);
					} else {
						addMesIt.setEnabled(true);
						addGroupIt.setEnabled(true);
						delMesIt.setEnabled(false);
					}

					DetailView view = ((DetailView) site.getSite().getWorkbenchWindow().getActivePage().findView(DetailView.ID));
					view.selectionChanged(site, selection);
				}
			}
		});

		schObserver = new Observer() {
			public void update(Observable arg0, Object arg1) {
				if (!viewer.getControl().isDisposed() && viewer.getContentProvider() != null) {
					refresh();
				}
			}

		};
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

	private TreeParent createModel(OLAPSchema schema) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		TreeRoot rr = new TreeRoot(LanguageText.MeasureView_Measures, schema);

		// dims outside groups
		if (schema != null && schema.getMeasures() != null) {
			List<OLAPMeasure> dims = schema.getMeasures();
			for (int i = 0; i < dims.size(); i++) {
				if (dims.get(i).getGroupId() == null || dims.get(i).getGroupId().equals("")) { //$NON-NLS-1$
					TreeMes tdim = new TreeMes(dims.get(i));
					rr.addChild(tdim);
				}
			}
			root.addChild(rr);

			// mes inside groups
			for (OLAPMeasureGroup group : schema.getMeasureGroups()) {
				if (group.getLevel() == 0)
					root.addChild(createGroups(group));
			}
		}

		return root;
	}

	private TreeMesGroup createGroups(OLAPMeasureGroup group) {
		TreeMesGroup tdg = new TreeMesGroup(group);

		for (OLAPMeasure m : group.getMeasures()) {
			TreeMes tmes = new TreeMes(m);
			tdg.addChild(tmes);
		}
		for (OLAPGroup g : group.getChilds()) {
			tdg.addChild(createGroups((OLAPMeasureGroup) g));
		}
		return tdg;
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