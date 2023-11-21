package bpm.gateway.ui.editors;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.LinkEditPart;
import bpm.gateway.ui.gef.part.NodeEditPartFactory;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.preferences.PreferencesConstants;
import bpm.gateway.ui.views.ModelViewPart;

public class GatewayEditorPart extends GraphicalEditorWithPalette implements ITabbedPropertySheetPageContributor {

	public static final String ID = "bpm.gateway.ui.editors.gatewayeditorpart"; //$NON-NLS-1$

	private ISelectionChangedListener selectionListener;
	private ScrolledComposite sc;

	public GatewayEditorPart() {
		setEditDomain(new DefaultEditDomain(this));

	}

	public EditDomain getDomain() {
		return getEditDomain();
	}

	public ActionRegistry getMyActionRegistry() {
		return getActionRegistry();
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		this.setPartName(input.getName());

	}
	
	public void setPartName(IEditorInput input) {
		this.setPartName(input.getName());
	}

	// @Override
	protected PaletteRoot getPaletteRoot() {
		return null;
	}

	public void createPartControl(Composite parent) {
		Composite splitter = new Composite(parent, SWT.NONE);
		splitter.setLayoutData(new GridData(GridData.FILL_BOTH));
		splitter.setLayout(new GridLayout());
		createPaletteViewer(splitter);
		createGraphicalViewer(splitter);

	}

	@Override
	protected void createPaletteViewer(Composite parent) {

	}

	@Override
	public void setPaletteViewer(PaletteViewer viewer) {
		super.setPaletteViewer(viewer);
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

		ContainerPanelModel root = new ContainerPanelModel(((GatewayEditorInput) getEditorInput()));

		getGraphicalViewer().setRootEditPart(rootEditPart);

		getGraphicalViewer().setContents(root);
		getSite().setSelectionProvider(getGraphicalViewer());

		selectionListener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				List<EditPart> l = getGraphicalViewer().getSelectedEditParts();

				if (l.isEmpty()) {
					return;
				}

				ModelViewPart view = (ModelViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
				if (view != null) {
					view.setSelection(l.get(0).getModel());
				}

			}

		};

		getGraphicalViewer().addSelectionChangedListener(selectionListener);
	}

	@Override
	public void commandStackChanged(EventObject event) {

		super.commandStackChanged(event);
		if (isDirty()) {
			firePropertyChange(PROP_DIRTY);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// ActionSave a = new ActionSave();
		// a.run();
		//
		// if (a.isCancelled()){
		// monitor.setCanceled(true);
		// }
		// else{
		// firePropertyChange(PROP_DIRTY);
		// }

		GatewayEditorInput doc = Activator.getDefault().getCurrentInput();

		String path = null;
		if (doc.getName() == null || doc.getName().equals("")) { //$NON-NLS-1$
			FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
			path = fd.open();

		}
		else {
			path = doc.getName();
		}

		if (path != null) {
			File file = new File(path);
			try {
				doc.getDocumentGateway().checkWellFormed();
				
				doc.setFile(file);
				doc.getDocumentGateway().write(new FileOutputStream(path));

				addFileToList(path);
				getCommandStack().flush();
				firePropertyChange(PROP_DIRTY);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.ActionSave_2, Messages.ActionSave_3 + e.getMessage());
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setEditPartFactory(new NodeEditPartFactory());

		GatewayContextMenuProvider provider = new GatewayContextMenuProvider(getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);

	}

	public String getContributorId() {
		return ID;
	}

	@Override
	// to provide the propertySheet associated to that Part
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);

		if (adapter == IContentOutlinePage.class) {
			return new OutlinePage(getGraphicalViewer());
		}
		if (adapter == ZoomManager.class) {
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}

		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#doSaveAs()
	 */
	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public void setSelection(Object o) {
		getGraphicalViewer().removeSelectionChangedListener(selectionListener);

		EditPart root = getGraphicalViewer().getRootEditPart();

		for (Object part : ((EditPart) root.getChildren().get(0)).getChildren()) {
			if (o instanceof Transformation) {
				if (!(part instanceof NodePart)) {
					continue;
				}
				if (((Node) ((NodePart) part).getModel()).getGatewayModel() == o) {
					getGraphicalViewer().select((EditPart) part);
					// getGraphicalViewer().getSelectionManager().appendSelection((EditPart)part);
				}
			}
			else if (o instanceof String) {
				// if (!(part instanceof NodeEditPart)){
				// continue;
				// }
				for (Object _c : ((NodeEditPart) part).getTargetConnections()) {
					if (_c instanceof LinkEditPart) {
						LinkEditPart l = (LinkEditPart) _c;
						String lName = (l.getSource() != null ? ((Node) l.getSource().getModel()).getName() : "") + " ----> " + (l.getTarget() != null ? ((Node) l.getTarget().getModel()).getName() : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

						if (lName.equals(o)) {
							getGraphicalViewer().select((EditPart) _c);
							// getGraphicalViewer().getSelectionManager().appendSelection((EditPart)_c);
						}

					}
				}

			}
		}

		getGraphicalViewer().addSelectionChangedListener(selectionListener);

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
		 * 
		 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {
			// super.createControl(parent);

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

		// public Object getAdapter(Class type) {
		// if (type == ZoomManager.class){
		// return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		// }
		// else{
		// return null;
		// // return super.getAdapter(type);
		// }
		//
		// }
	}

	public Image getModelAsImage() {
		IFigure figure = ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getLayer(LayerConstants.SCALABLE_LAYERS);

		Image img1 = new Image(Display.getDefault(), new org.eclipse.swt.graphics.Rectangle(0, 0, figure.getBounds().width, figure.getBounds().height + 1));
		GC gc1 = new GC(img1);

		Graphics grap1 = new SWTGraphics(gc1);
		figure.paint(grap1);
		return img1;
	}

	private void addFileToList(String path) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		boolean isEverListed = false;
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(path))
				isEverListed = true;
		}

		if (!isEverListed) {
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
}
