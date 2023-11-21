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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.fasd.drill.wizard.DialogDrill;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.olap.virtual.VirtualDimension;
import org.fasd.olap.virtual.VirtualMeasure;
import org.fasd.utils.trees.TreeAggregate;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeCube;
import org.fasd.utils.trees.TreeCubeView;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeMesGroup;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.fasd.utils.trees.TreeVirtualCube;
import org.fasd.views.actions.ActionAddCube;
import org.fasd.views.actions.ActionAddDimUsage;
import org.fasd.views.actions.ActionAddMeasureUsage;
import org.fasd.views.actions.ActionCheckCube;
import org.fasd.views.actions.ActionDeleteCubes;
import org.freeolap.FreemetricsPlugin;

public class CubeView extends ViewPart {
	public static final String ID = "org.fasd.views.cubeView"; //$NON-NLS-1$
	private TreeViewer viewer;
	private Composite par;
	private CubeView site = this;
	private ToolBar toolBar;
	private Action addCube, addMes, addDim, delItem, browseCube;// , addVirtual;
	private Action buildCubeView, editCubeView;
	private Action openAtStartup;
	private ToolItem addCubeIt, addMesIt, addDimIt, delIt, checkCubeIt;// ,
																		// addVirtualIt;
	private Observer schObserver;

	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private UndoContext undoContext;
	private ToolItem undoIt, redoIt;

	public CubeView() {
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

		FreemetricsPlugin.getDefault().registerCubeView(this);

		viewer = new TreeViewer(par, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());

		DecoratingLabelProvider labelDeco = new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {
			@Override
			public String getText(Object element) {
				// get the TreeCube
				TreeObject o = (TreeObject) element;
				while (!(o instanceof TreeCube) && o != null) {
					o = o.getParent();
				}

				if (o == null) {
					return super.getText(element);
				}
				TreeCube tc = (TreeCube) o;

				if (element instanceof TreeDim) {
					int pos = tc.getOLAPCube().getStartupDimensions().indexOf(((TreeDim) element).getOLAPDimension());
					if (pos >= 0) {
						return super.getText(element) + " (" + (pos + 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else if (element instanceof TreeMes) {
					if (((TreeMes) element).getName().equals(tc.getOLAPCube().getDefaultMeasure())) {
						return super.getText(element) + " (Default)"; //$NON-NLS-1$
					}
				}
				return super.getText(element);
			}

			@Override
			public Color getForeground(Object element) {
				return super.getForeground(element);
			}
		};
		viewer.setLabelProvider(labelDeco);
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
		addCube = new ActionAddCube(this, undoContext);
		addMes = new ActionAddMeasureUsage(this, viewer, undoContext);
		addDim = new ActionAddDimUsage(this, viewer, undoContext);
		delItem = new ActionDeleteCubes(this, viewer, undoContext);

		browseCube = new Action(LanguageText.CubeView_DimensionBrowser) {
			public void run() {
				ISelection s = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;

					Object o = ss.getFirstElement();

					if (o instanceof TreeCube) {
						if (FreemetricsPlugin.getDefault().getPath().contains("*")) { //$NON-NLS-1$
							MessageDialog.openInformation(CubeView.this.getSite().getShell(), LanguageText.CubeView_Information, LanguageText.CubeView_SaveProjectBeforeBrowseCube);

						} else {
							System.out.println("go"); //$NON-NLS-1$
						}
					} else {
						MessageDialog.openInformation(CubeView.this.getSite().getShell(), LanguageText.CubeView_Information, LanguageText.CubeView_SelectACubeToBrowseIt);
					}
				}

			}
		};
		browseCube.setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/search.png")));; //$NON-NLS-1$

//		buildCubeView = new Action("Build Cube View") { //$NON-NLS-1$
//			public void run() {
//				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
//
//				if (ss.isEmpty()) {
//					return;
//				}
//
//				OLAPCube cube = ((TreeCube) ss.getFirstElement()).getOLAPCube();
//
//				DialogDrill d = new DialogDrill(getSite().getShell(), cube);
//				if (d.open() == DialogDrill.OK) {
//					cube.addCubeView(d.getCubeView());
//					refresh();
//				}
//			}
//		};

		editCubeView = new Action("Edit Cube View") { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				org.fasd.olap.CubeView cubeView = ((TreeCubeView) ss.getFirstElement()).getCubeView();
				OLAPCube cube = ((TreeCube) ((TreeCubeView) ss.getFirstElement()).getParent()).getOLAPCube();

				DialogDrill d = new DialogDrill(getSite().getShell(), cube, cubeView);
				d.open();
			}
		};

		openAtStartup = new Action("Open/Hide at Startup") { //$NON-NLS-1$
			public void run() {
				Object o = ((IStructuredSelection) viewer.getSelection()).getFirstElement();

				if (o instanceof TreeDim) {
					TreeDim tDim = (TreeDim) o;
					while (!(o instanceof TreeCube) && o != null) {
						o = ((TreeObject) o).getParent();
					}

					if (o == null) {
						return;
					}
					if (((TreeCube) o).getOLAPCube().getStartupDimensions().contains(tDim.getOLAPDimension())) {
						((TreeCube) o).getOLAPCube().removeOpenedDimension(tDim.getName());
					} else {
						((TreeCube) o).getOLAPCube().addOpenedDimension(tDim.getName());
					}

					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
					FreemetricsPlugin.getDefault().getFAModel().setChanged();

				} else if (o instanceof TreeMes) {
					TreeMes tMes = (TreeMes) o;
					while (!(o instanceof TreeCube) && o != null) {
						o = ((TreeObject) o).getParent();
					}

					if (o == null) {
						return;
					}
					if (tMes.getName().equals(((TreeCube) o).getOLAPCube().getDefaultMeasure())) {
						((TreeCube) o).getOLAPCube().setDefaultMeasure(""); //$NON-NLS-1$
					} else {
						((TreeCube) o).getOLAPCube().setDefaultMeasure(tMes.getName());
					}

					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
					FreemetricsPlugin.getDefault().getFAModel().setChanged();

				}
			}
		};

	}

	private void fillToolbar() {
		addCubeIt = new ToolItem(toolBar, SWT.PUSH);
		addCubeIt.setToolTipText(LanguageText.CubeView_AddCube);
		addCubeIt.setImage(addCube.getImageDescriptor().createImage());
		addCubeIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addCube.run();
			}
		});

		addMesIt = new ToolItem(toolBar, SWT.PUSH);
		addMesIt.setToolTipText(LanguageText.CubeView_AddMeasure);
		addMesIt.setImage(addMes.getImageDescriptor().createImage());
		addMesIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addMes.run();
			}
		});

		addDimIt = new ToolItem(toolBar, SWT.PUSH);
		addDimIt.setToolTipText(LanguageText.CubeView_AddDimension);
		addDimIt.setImage(addDim.getImageDescriptor().createImage());
		addDimIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDim.run();
			}
		});

		delIt = new ToolItem(toolBar, SWT.PUSH);
		delIt.setToolTipText(LanguageText.CubeView_DeleteItem);
		delIt.setImage(delItem.getImageDescriptor().createImage());
		delIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				delItem.run();
			}
		});

		undoIt = new ToolItem(toolBar, SWT.PUSH);
		undoIt.setToolTipText(LanguageText.CubeView_0);
		undoIt.setImage(undoAction.getImageDescriptor().createImage());
		undoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undoAction.run();
			}
		});

		redoIt = new ToolItem(toolBar, SWT.PUSH);
		redoIt.setToolTipText(LanguageText.CubeView_1);
		redoIt.setImage(redoAction.getImageDescriptor().createImage());
		redoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				redoAction.run();
			}
		});

		checkCubeIt = new ToolItem(toolBar, SWT.PUSH);
		checkCubeIt.setImage(new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/check.png")); //$NON-NLS-1$
		checkCubeIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeCube) {
					ActionCheckCube checkCube = new ActionCheckCube();
					((ActionCheckCube) checkCube).setCube(((TreeCube) o).getOLAPCube());
					checkCube.run();
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
				if (ss.getFirstElement() instanceof TreeMes) {
					event.data = "MOV/" + ((TreeMes) ss.getFirstElement()).getOLAPMeasure().getName() + "/" + ((TreeMes) ss.getFirstElement()).getOLAPMeasure().getId() + "/" + ((TreeCube) (((TreeMes) ss.getFirstElement()).getParent())).getOLAPCube().getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if (ss.getFirstElement() instanceof TreeDim) {
					event.data = "MOVDIM/" + ((TreeDim) ss.getFirstElement()).getOLAPDimension().getName() + "/" + ((TreeDim) ss.getFirstElement()).getOLAPDimension().getId();; //$NON-NLS-1$ //$NON-NLS-2$
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
				// nothing
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

				OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();

				System.out.println("to add : " + buf[1]); //$NON-NLS-1$
				System.out.println("on item " + event.item.getClass()); //$NON-NLS-1$
				System.out.println(((TreeItem) event.item).getData().getClass());

				// dropping a dimension
				if (o instanceof TreeCube && buf[0].equals("DIM")) { //$NON-NLS-1$
					TreeCube item = ((TreeCube) o);
					OLAPCube cube = item.getOLAPCube();
					OLAPDimension d = sch.findDimension(buf[1]);
					if (d != null && !cube.getDims().contains(d))
						cube.addDim(d);

				}
				// swapping dimensions0
				else if (o instanceof TreeDim && buf[0].equals("MOVDIM")) { //$NON-NLS-1$
					TreeParent _o = (TreeParent) o;
					while (!(_o instanceof TreeCube)) {
						_o = _o.getParent();
					}
					TreeCube item = ((TreeCube) _o);
					OLAPCube cube = item.getOLAPCube();
					OLAPDimension d2 = null;
					for (OLAPDimension d : cube.getDims()) {
						if (d.getId().equals(buf[2])) {
							d2 = d;
							break;
						}
					}
					cube.swapDimensions(((TreeDim) o).getOLAPDimension(), d2);
				}
				// dropping a fact table, thus a cube
				else if (o instanceof TreeRoot && buf[0].equals("TBL")) { //$NON-NLS-1$
					sch.addCube(new OLAPCube(buf[1], buf[1]));

				}
				// measure
				else if (o instanceof TreeCube && buf[0].equals("MES")) { //$NON-NLS-1$
					TreeCube item = ((TreeCube) o);
					OLAPCube cube = item.getOLAPCube();
					OLAPMeasure m = sch.findMeasure(buf[1]);
					if (m != null && !cube.getMes().contains(m))
						cube.addMes(m);

				}
				// dimension group
				else if (o instanceof TreeCube && buf[0].equals("GPDIM")) { //$NON-NLS-1$
					TreeCube item = ((TreeCube) o);
					OLAPCube cube = item.getOLAPCube();
					OLAPDimensionGroup g = sch.findDimensionGroup(buf[1]);
					if (g != null && !cube.getDimGroups().contains(g))
						cube.addDimGroup(g);
				}
				// measure group
				else if (o instanceof TreeCube && buf[0].equals("GMES")) { //$NON-NLS-1$
					TreeCube item = ((TreeCube) o);
					OLAPCube cube = item.getOLAPCube();
					OLAPMeasureGroup g = sch.findMeasureGroup(buf[1]);
					if (g != null && !cube.getMesGroups().contains(g))
						cube.addMesGroup(g);

				}
				// virtualcubes dimensions
				else if (o instanceof TreeVirtualCube && (buf[0].equals("DIM") || buf[0].equals("MOVDIM"))) { //$NON-NLS-1$ //$NON-NLS-2$
					TreeVirtualCube item = ((TreeVirtualCube) o);
					VirtualCube cube = item.getVirtualCube();
					OLAPDimension d;
					if (buf[0].equals("DIM")) //$NON-NLS-1$
						d = sch.findDimension(buf[1]);
					else
						d = sch.findDimension(buf[2]);

					VirtualDimension vd = new VirtualDimension();
					vd.setName(d.getName());
					vd.setDim(d);

					if (!cube.containsDimension(d))
						cube.addVirtualDimension(vd);
				}
				// virtualcubes measures
				else if (o instanceof TreeVirtualCube && buf[0].equals("MOV")) { //$NON-NLS-1$
					TreeVirtualCube item = ((TreeVirtualCube) o);
					VirtualCube cube = item.getVirtualCube();
					OLAPMeasure m;
					m = sch.findMeasure(buf[2]);
					OLAPCube c = sch.findCube(buf[3]);

					VirtualMeasure vm = new VirtualMeasure();
					vm.setName(m.getName());
					vm.setMes(m);
					vm.setCube(c);

					if (!cube.containsMeasure(m))
						cube.addVirtualMeasure(vm);
				}

				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

	}

	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.CubeView_8 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.CubeView_9);
		}
	}

	private void fillTreeViewerContextMenu(final TreeViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeRoot) {
					menuMgr.add(addCube);
				} else if (o instanceof TreeCube) {
					menuMgr.add(addDim);
					menuMgr.add(addMes);
					menuMgr.add(delItem);
					menuMgr.add(buildCubeView);
				} else if (o instanceof TreeCubeView) {
					menuMgr.add(editCubeView);
					menuMgr.add(delItem);
				} else if (o instanceof TreeDim) {
					menuMgr.add(openAtStartup);
				} else if (o instanceof TreeMes) {
					menuMgr.add(openAtStartup);
				} else {
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

					if (o instanceof TreeRoot) {
						addMesIt.setEnabled(false);
						addDimIt.setEnabled(false);
						delIt.setEnabled(false);
					} else if (o instanceof TreeCube) {
						addMesIt.setEnabled(true);
						addDimIt.setEnabled(true);
						delIt.setEnabled(true);
					} else if (o instanceof TreeDim || o instanceof TreeMes) {
						addMesIt.setEnabled(false);
						addDimIt.setEnabled(false);
						delIt.setEnabled(true);
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

		TreeRoot rr = new TreeRoot(LanguageText.CubeView_Cubes, schema);

		if (schema != null && schema.getCubes() != null) {
			List<OLAPCube> cubes = schema.getCubes();

			for (int i = 0; i < cubes.size(); i++) {
				TreeCube cube = new TreeCube(cubes.get(i));

				for (AggregateTable t : cubes.get(i).getAggTables()) {
					TreeAggregate ta = new TreeAggregate(t);
					cube.addChild(ta);
				}

				for (int j = 0; j < cubes.get(i).getDims().size(); j++) {
					TreeDim dim = new TreeDim(cubes.get(i).getDims().get(j));
					cube.addChild(dim);
				}

				for (int j = 0; j < cubes.get(i).getMes().size(); j++) {
					TreeMes mes = new TreeMes(cubes.get(i).getMes().get(j));
					cube.addChild(mes);
				}

				for (OLAPMeasureGroup g : cubes.get(i).getMesGroups()) {
					cube.addChild(createMesGr(g));
				}
				for (OLAPDimensionGroup g : cubes.get(i).getDimGroups()) {
					cube.addChild(createDimGr(g));
				}

				for (org.fasd.olap.ICubeView c : cubes.get(i).getCubeViews()) {
					cube.addChild(new TreeCubeView((org.fasd.olap.CubeView) c));
				}

				rr.addChild(cube);
			}
			root.addChild(rr);

		}

		return root;
	}

	private TreeParent createMesGr(OLAPMeasureGroup group) {
		TreeMesGroup tg = new TreeMesGroup(group);
		for (OLAPMeasure m : group.getMeasures()) {
			TreeMes tm = new TreeMes(m);
			tg.addChild(tm);
		}
		for (OLAPGroup gg : group.getChilds()) {
			tg.addChild(createMesGr((OLAPMeasureGroup) gg));
		}
		return tg;
	}

	private TreeParent createDimGr(OLAPDimensionGroup group) {
		TreeDimGroup tg = new TreeDimGroup(group);
		for (OLAPDimension m : group.getDimensions()) {
			TreeDim tm = new TreeDim(m);
			tg.addChild(tm);
		}
		for (OLAPGroup gg : group.getChilds()) {
			tg.addChild(createDimGr((OLAPDimensionGroup) gg));
		}
		return tg;
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