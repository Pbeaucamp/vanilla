package org.fasd.views;

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
import org.eclipse.swt.dnd.DropTarget;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPSchema;
import org.fasd.security.SecurityDim;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.utils.trees.TreeContentProvider;
import org.fasd.utils.trees.TreeLabelProvider;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;
import org.freeolap.FreemetricsPlugin;

public class SecurityView extends ViewPart {
	public static final String ID = "org.fasd.views.securityView"; //$NON-NLS-1$

	private TreeViewer viewer;
	private ToolBar toolBar;
	private Observer schObserver;

	private Action addDimView, addView, addGroup, delete;
	private ToolItem addDimIt, addViewIt, addGrpIt, delIt;

	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private UndoContext undoContext;
	private ToolItem undoIt, redoIt;

	public SecurityView() {
		super();
		initializeOperationHistory();
	}

	public void registerObservable(Observable o) {
		o.addObserver(schObserver);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		toolBar = new ToolBar(parent, SWT.NONE);
		toolBar.setLayoutData(new GridData(SWT.FILL));

		FreemetricsPlugin.getDefault().registerSecurityView(this);

		viewer = new TreeViewer(parent, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());

		createActionHandlers();
		createActions();
		fillToolbar();
		// actions
		fillTreeViewerContextMenu(viewer);

		// dnd
		setDnD(viewer);
		// listenner
		setListenner(viewer);
		registerObservable(FreemetricsPlugin.getDefault().getFAModel());
		viewer.setAutoExpandLevel(3);
		refresh();

	}

	public void refresh() {
		if (viewer == null) {
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
		TreeParent pp = new TreeParent(""); //$NON-NLS-1$
		TreeRoot root = new TreeRoot(LanguageText.SecurityView_Dimension_Views, schema);

		for (SecurityDim sd : schema.getDimViews()) {
			TreeDimView tdv = new TreeDimView(sd);
			for (View v : sd.getViews()) {
				TreeView tv = new TreeView(v);
				tdv.addChild(tv);
			}
			root.addChild(tdv);
		}

		TreeRoot rr = new TreeRoot(LanguageText.SecurityView_SecurityG_p, schema);
		for (SecurityGroup group : schema.getSecurityGroups()) {
			if (group.getLevel() == 0)
				rr.addChild(createGroups(group));
		}
		pp.addChild(root);
		pp.addChild(rr);
		return pp;
	}

	private TreeGroup createGroups(SecurityGroup group) {
		TreeGroup tdg = new TreeGroup(group);

		for (View m : group.getViews()) {
			TreeView tmes = new TreeView(m);
			tdg.addChild(tmes);
		}
		for (OLAPGroup g : group.getChilds()) {
			tdg.addChild(createGroups((SecurityGroup) g));
		}
		return tdg;
	}

	private void createActions() {
		Image img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_measure.png"); //$NON-NLS-1$
		addDimView = new Action(LanguageText.SecurityView_Add_Dimension_View, ImageDescriptor.createFromImage(img)) {
			public void run() {
				OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
				sch.addDimView(new SecurityDim(LanguageText.SecurityView_New_Dimension_View));
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		};
		addDimView.setToolTipText(addDimView.getText());

		img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_column.png"); //$NON-NLS-1$
		addView = new Action(LanguageText.SecurityView_Add_View, ImageDescriptor.createFromImage(img)) {
			public void run() {
				Object o = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (o instanceof TreeDimView) {
					SecurityDim d = ((TreeDimView) o).getSecuDim();
					d.addView(new View(LanguageText.SecurityView_New_View));
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				} else if (o instanceof TreeGroup) {
					SecurityGroup g = ((TreeGroup) o).getSecurityGroup();
					g.addView(new View(LanguageText.SecurityView_New_View));
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				}
			}
		};
		addView.setToolTipText(addView.getText());

		img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/folder.png"); //$NON-NLS-1$
		addGroup = new Action(LanguageText.SecurityView_Add_Security_Group, ImageDescriptor.createFromImage(img)) {
			public void run() {
				Object o = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (o instanceof TreeGroup) {
					SecurityGroup group = ((TreeGroup) o).getSecurityGroup();
					group.addChild(new SecurityGroup(LanguageText.SecurityView_New_Security_Group));
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				} else if (o instanceof TreeRoot) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().addSecurityGroup(new SecurityGroup(LanguageText.SecurityView_New_Security_Group));
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				}
			}

		};
		addGroup.setToolTipText(addGroup.getText());

		img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png"); //$NON-NLS-1$
		delete = new Action(LanguageText.SecurityView_Del_Item, ImageDescriptor.createFromImage(img)) {
			public void run() {
				Object o = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (o instanceof TreeDimView) {
					SecurityDim d = ((TreeDimView) o).getSecuDim();
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().removeDimView(d);
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				} else if (o instanceof TreeView) {
					SecurityDim dad = ((TreeDimView) ((TreeView) o).getParent()).getSecuDim();
					dad.removeView(((TreeView) o).getView());
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				} else if (o instanceof TreeGroup) {

					SecurityGroup group = ((TreeGroup) o).getSecurityGroup();
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().removeSecurityGroup(group);
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				}
			}
		};
		delete.setToolTipText(delete.getText());
	}

	private void fillToolbar() {
		addDimIt = new ToolItem(toolBar, SWT.PUSH);
		addDimIt.setToolTipText(addDimView.getToolTipText());
		addDimIt.setImage(addDimView.getImageDescriptor().createImage());
		addDimIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDimView.run();
			}
		});

		addViewIt = new ToolItem(toolBar, SWT.PUSH);
		addViewIt.setToolTipText(addView.getToolTipText());
		addViewIt.setImage(addView.getImageDescriptor().createImage());
		addViewIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addView.run();
			}
		});

		addGrpIt = new ToolItem(toolBar, SWT.PUSH);
		addGrpIt.setToolTipText(addGroup.getToolTipText());
		addGrpIt.setImage(addGroup.getImageDescriptor().createImage());
		addGrpIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addGroup.run();
			}
		});

		delIt = new ToolItem(toolBar, SWT.PUSH);
		delIt.setToolTipText(delete.getToolTipText());
		delIt.setImage(delete.getImageDescriptor().createImage());
		delIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				delete.run();
			}
		});

		undoIt = new ToolItem(toolBar, SWT.PUSH);
		undoIt.setToolTipText(LanguageText.SecurityView_2);
		undoIt.setImage(undoAction.getImageDescriptor().createImage());
		undoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undoAction.run();
			}
		});

		redoIt = new ToolItem(toolBar, SWT.PUSH);
		redoIt.setToolTipText(LanguageText.SecurityView_3);
		redoIt.setImage(redoAction.getImageDescriptor().createImage());
		redoIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				redoAction.run();
			}
		});
	}

	private void fillTreeViewerContextMenu(final TreeViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {

				menuMgr.add(new Separator());

				menuMgr.add(undoAction);
				menuMgr.add(redoAction);
				menuMgr.update();

			}
		});
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

	}

	@Override
	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.SecurityView_0 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.SecurityView_1);
		}

	}

	private void initializeOperationHistory() {
		undoContext = new ObjectUndoContext(this);
	}

	private void createActionHandlers() {
		undoAction = new UndoActionHandler(this.getSite(), undoContext);
		redoAction = new RedoActionHandler(this.getSite(), undoContext);
	}

	public IOperationHistory getOperationHistory() {
		return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
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

					if (o instanceof TreeDimView) {
						addDimIt.setEnabled(true);
						addViewIt.setEnabled(true);
						addGrpIt.setEnabled(false);
						delIt.setEnabled(true);
					} else if (o instanceof TreeGroup) {
						addDimIt.setEnabled(false);
						addViewIt.setEnabled(true);
						addGrpIt.setEnabled(true);
						delIt.setEnabled(true);
					} else if (o instanceof TreeView) {
						addDimIt.setEnabled(false);
						addViewIt.setEnabled(false);
						addGrpIt.setEnabled(false);
						delIt.setEnabled(true);
					} else {
						addDimIt.setEnabled(true);
						addViewIt.setEnabled(false);
						addGrpIt.setEnabled(true);
						delIt.setEnabled(false);
					}

					DetailView view = ((DetailView) SecurityView.this.getSite().getWorkbenchWindow().getActivePage().findView(DetailView.ID));
					view.selectionChanged(SecurityView.this, selection);
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

	private void setDnD(TreeViewer tviewer) {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		tviewer.addDragSupport(ops, transfers, new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				// nothing, DnD always possible
			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				event.data = ""; //$NON-NLS-1$
				for (Object o : ss.toList()) {
					if (o instanceof TreeView) {
						event.data = event.data + "DVU/" + ((TreeView) o).getName() + "/" + ((TreeView) ss.getFirstElement()).getView().getId() + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
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
				// todo
			}

			public void drop(DropTargetEvent event) {
				String[] buf = ((String) event.data).split("/"); //$NON-NLS-1$
				Object o = ((TreeItem) event.item).getData();

				// column so it s a measure

				if (o instanceof TreeGroup && buf[0].equals("DVU")) { //$NON-NLS-1$
					SecurityGroup g = (((TreeGroup) o)).getSecurityGroup();
					View m = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().findView(buf[2]);
					if (m != null) {
						m.setGroup(g);
					}
				} else if (o instanceof TreeDimView && buf[0].equals("DVU")) { //$NON-NLS-1$
					SecurityDim d = ((TreeDimView) o).getSecuDim();
					View m = d.findView(buf[2]);
					d.addView(m);
				}

				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});
	}

	public class TreeDimView extends TreeParent {
		private SecurityDim dim;

		public TreeDimView(SecurityDim dimview) {
			super(dimview.getName());
			dim = dimview;
		}

		public SecurityDim getSecuDim() {
			return dim;
		}

	}

	public class TreeView extends TreeObject {
		private View view;

		public TreeView(View v) {
			super(v.getName());
			view = v;
		}

		public View getView() {
			return view;
		}

	}

	public class TreeGroup extends TreeParent {
		private SecurityGroup g;

		public TreeGroup(SecurityGroup g) {
			super(g.getName());
			this.g = g;
			;
		}

		public SecurityGroup getSecurityGroup() {
			return g;
		}

	}
}
