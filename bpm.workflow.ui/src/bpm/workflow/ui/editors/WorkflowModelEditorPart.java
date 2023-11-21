package bpm.workflow.ui.editors;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.LinkEditPart;
import bpm.workflow.ui.gef.part.LoopEditPart;
import bpm.workflow.ui.gef.part.MacroProcessEditPart;
import bpm.workflow.ui.gef.part.NodeEditPartFactory;
import bpm.workflow.ui.gef.part.NodePart;
import bpm.workflow.ui.preferences.PreferencesConstants;
import bpm.workflow.ui.views.ModelViewPart;
import bpm.workflow.ui.views.ViewPalette;

/**
 * The model-editor of the workspace
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class WorkflowModelEditorPart extends GraphicalEditorWithPalette implements ITabbedPropertySheetPageContributor {
	public static final String ID = "bpm.workflow.ui.editors.workfloweditorpart"; //$NON-NLS-1$

	private ISelectionChangedListener selectionListener;
	private ScrolledComposite sc;
	private WorkflowMultiEditorPart parent;
	public static int currentX;
	public static int currentY;
	public static RootEditPart part;

	public WorkflowModelEditorPart(WorkflowMultiEditorPart parent) {
		setEditDomain(new DefaultEditDomain(this));
		this.parent = parent;

	}

	public EditDomain getDomain() {
		return getEditDomain();
	}

	@Override
	protected void createPaletteViewer(Composite parent) {

	}

	@Override
	protected PaletteViewer getPaletteViewer() {
		if(super.getPaletteViewer() == null) {
			ViewPalette v = (ViewPalette) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewPalette.ID);
			setPaletteViewer(v.getPaletteViewer());
		}
		return super.getPaletteViewer();
	}

	@Override
	public void createPartControl(Composite parent) {
		createGraphicalViewer(parent);
	}

	@Override
	public void setPaletteViewer(PaletteViewer viewer) {
		super.setPaletteViewer(viewer);
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		this.setPartName(input.getName());

	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		WorkflowPaletteRoot root = new WorkflowPaletteRoot();

		return root;
	}

	@Override
	protected void initializeGraphicalViewer() {
		double[] zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0 };

		ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart();

		ArrayList<String> zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));
		manager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		ContainerPanelModel root = new ContainerPanelModel(((WorkflowEditorInput) getEditorInput()));

		getGraphicalViewer().setRootEditPart(rootEditPart);

		getGraphicalViewer().setContents(root);
		getSite().setSelectionProvider(getGraphicalViewer());

		selectionListener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				List<EditPart> l = getGraphicalViewer().getSelectedEditParts();

				if(l.isEmpty()) {
					return;
				}

				ModelViewPart view = (ModelViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
				if(view != null) {
					view.setSelection(l.get(0).getModel());
				}

			}

		};

		getGraphicalViewer().addSelectionChangedListener(selectionListener);
		getGraphicalViewer().getControl().addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {
				currentX = e.x;
				currentY = e.y;
				WorkflowModelEditorPart.part = getGraphicalViewer().getRootEditPart();
			}

		});

	}

	public void refresh() {
		initializeGraphicalViewer();

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		WorkflowEditorInput doc = Activator.getDefault().getCurrentInput();

		String path = null;
		if(doc.getFileName().equals("")) { //$NON-NLS-1$
			FileDialog fd = new FileDialog(sh, SWT.SAVE);
			path = fd.open();

		}
		else {
			path = doc.getFileName();
		}

		if(path != null) {

			try {
				if(!path.endsWith(".biw")) { //$NON-NLS-1$
					path += ".biw"; //$NON-NLS-1$
				}
				FileOutputStream fos = new FileOutputStream(path);

				doc.getWorkflowModel().saveToXml(fos);

			} catch(Exception e) {
				e.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionSave_4, Messages.ActionSave_5 + e.getMessage());
			}

		}
		getCommandStack().flush();
		firePropertyChange(PROP_DIRTY);

	}

	@Override
	public void doSaveAs() {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		WorkflowEditorInput doc = Activator.getDefault().getCurrentInput();

		String path = null;
		FileDialog fd = new FileDialog(sh, SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.biw", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
		path = fd.open();

		if(path != null) {

			try {
				if(!path.endsWith(".biw")) { //$NON-NLS-1$
					path += ".biw"; //$NON-NLS-1$
				}
				FileOutputStream fos = new FileOutputStream(path);

				doc.getWorkflowModel().saveToXml(fos);
				addFileToList(path);
				doc.setFileName(path);

			} catch(Exception e) {
				e.printStackTrace();
				MessageDialog.openError(sh, Messages.ApplicationActionBarAdvisor_9, Messages.ApplicationActionBarAdvisor_10 + e.getMessage());
			}

		}
		getCommandStack().flush();
		firePropertyChange(PROP_DIRTY);
	}

	private void addFileToList(String path) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		boolean isEverListed = false;
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals(path))
				isEverListed = true;
		}

		if(!isEverListed) {
			list[4] = list[3];
			list[3] = list[2];
			list[2] = list[1];
			list[1] = list[0];
			list[0] = path;
		}

		store.setValue(PreferencesConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferencesConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferencesConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferencesConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferencesConstants.P_RECENTFILE5, list[4]);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setEditPartFactory(new NodeEditPartFactory());

		WorkflowContextMenuProvider provider = new WorkflowContextMenuProvider(getGraphicalViewer(), parent.getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
	}

	public String getContributorId() {
		return ID;
	}

	@Override
	// to provide the propertySheet associated to that Part
	public Object getAdapter(Class adapter) {
		if(adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);

		if(adapter == IContentOutlinePage.class) {
			return new OutlinePage(getGraphicalViewer());
		}
		if(adapter == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}

		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getGraphicalViewer()
	 */
	@Override
	protected GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	public GraphicalViewer getViewer() {
		return super.getGraphicalViewer();
	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(false);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));

		super.createGraphicalViewer(sc);

		sc.setContent(getGraphicalViewer().getControl());
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		sc.setMinWidth(Display.getDefault().getPrimaryMonitor().getBounds().width);
		sc.setMinHeight(Display.getDefault().getPrimaryMonitor().getBounds().height);
		sc.setMinSize(getGraphicalViewer().getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT));
		getGraphicalViewer().getControl().setSize(getGraphicalViewer().getControl().computeSize(SWT.MAX, SWT.MAX));
	}

	protected class OutlinePage extends ContentOutlinePage {

		private Canvas canvas;

		public OutlinePage(EditPartViewer viewer) {
			super(viewer);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {

			canvas = new Canvas(parent, SWT.BORDER);
			canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
			LightweightSystem lws = new LightweightSystem(canvas);

			final ScrollableThumbnail thumbnail = new ScrollableThumbnail((Viewport) ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getFigure());

			thumbnail.setSource(((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));

			lws.setContents(thumbnail);

			getSelectionSynchronizer().addViewer(getViewer());

		}

		public Control getControl() {
			return canvas;
		}

		public void dispose() {
			getSelectionSynchronizer().removeViewer(getViewer());

			super.dispose();
		}

	}

	/**
	 * Set the Object o as selected
	 * 
	 * @param o
	 *            : Object to set as selected
	 */
	public void setSelection(Object o) {

		getGraphicalViewer().removeSelectionChangedListener(selectionListener);

		EditPart root = getGraphicalViewer().getRootEditPart();

		for(Object part : ((EditPart) root.getChildren().get(0)).getChildren()) {
			if(o instanceof IActivity) {

				if(part instanceof LoopEditPart) {
					if(((LoopModel) ((LoopEditPart) part).getModel()).getWorkflowObject() == o) {
						getGraphicalViewer().select((EditPart) part);
						getGraphicalViewer().getSelectionManager().appendSelection((EditPart) part);
					}
				}
				else if(part instanceof MacroProcessEditPart) {

					if(((MacroProcessModel) ((MacroProcessEditPart) part).getModel()).getWorkflowObject() == o) {
						getGraphicalViewer().select((EditPart) part);
						getGraphicalViewer().getSelectionManager().appendSelection((EditPart) part);
					}
				}
				else {

					if(((Node) ((NodePart) part).getModel()).getWorkflowObject().getName().equalsIgnoreCase(((IActivity) o).getName())) {

						getGraphicalViewer().select((EditPart) part);
						getGraphicalViewer().getSelectionManager().appendSelection((EditPart) part);
					}
				}
			}
			else if(o instanceof String) {

				for(Object _c : ((NodeEditPart) part).getTargetConnections()) {
					if(_c instanceof LinkEditPart) {
						LinkEditPart l = (LinkEditPart) _c;

						String lName = (l.getSource() != null ? ((Node) l.getSource().getModel()).getName() : "") + " ----> " + (l.getTarget() != null ? ((Node) l.getTarget().getModel()).getName() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

						if(lName.equals(o)) {
							getGraphicalViewer().select((EditPart) _c);
						}

					}
				}

			}
		}

		getGraphicalViewer().addSelectionChangedListener(selectionListener);

	}

	@Override
	public void commandStackChanged(EventObject event) {
		super.commandStackChanged(event);
		if(isDirty()) {
			firePropertyChange(PROP_DIRTY);
		}
	}
}
