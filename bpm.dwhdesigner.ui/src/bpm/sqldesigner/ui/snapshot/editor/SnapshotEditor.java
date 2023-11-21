package bpm.sqldesigner.ui.snapshot.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import bpm.sqldesigner.query.editor.SQLRootPart;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.editor.DesignContextualMenuProvider;
import bpm.sqldesigner.ui.editpart.AppEditPartFactory;

public class SnapshotEditor extends GraphicalEditor{

	public static final String ID = "bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor"; //$NON-NLS-1$
	
	
	public SnapshotEditor(){
		setEditDomain(new DefaultEditDomain(this));
	}
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		setPartName(input.getName());
	}

	public void setName(String title) {
		setPartName(title);
	}
	
	@Override
	protected void initializeGraphicalViewer() {
		
		if (getEditorInput() instanceof SnapshotEditorInput){
			getGraphicalViewer().setContents(((SnapshotEditorInput)getEditorInput()).getSnapshot());
			
			((ScalableRootEditPart)getGraphicalViewer()
					.getRootEditPart()).getZoomManager()
					.setZoom(((SnapshotEditorInput)getEditorInput()).getSnapshot().getScale());
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		
	}
	
	@Override
	protected CommandStack getCommandStack() {
		
		return super.getCommandStack();
	}
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new AppEditPartFactory());
		SQLRootPart rootEditPart = new SQLRootPart();
		viewer.setRootEditPart(rootEditPart);
		initZoom(rootEditPart);

		
		if (rootEditPart instanceof LayerManager) {
            ((ConnectionLayer) ((LayerManager) rootEditPart)
                    .getLayer(LayerConstants.CONNECTION_LAYER))
                    .setConnectionRouter(new ManhattanConnectionRouter());
        }

		
		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL),
				MouseWheelZoomHandler.SINGLETON);
		
		
	}
	
	protected void initZoom(ScalableRootEditPart rootEditPart) {
		
		double[] zoomLevels;
		ArrayList<String> zoomContributions;

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));

		zoomLevels = new double[] { 0.02, 0.05, 0.10, 0.15, 0.20, 0.25, 0.5,
				0.75, 1.0, 1.5, 2.0, 2.5, 3.0 };
		manager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);
	}
	
	@Override
	public Object getAdapter(Class type) {
		Activator.getDefault().refreshButton();
		
		if (type == ZoomManager.class){
			return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}
		if (type == IContentOutlinePage.class) {
            return new OutlinePage(getGraphicalViewer());
		}
			
		else
			return super.getAdapter(type);
	}
	protected class OutlinePage extends ContentOutlinePage{

		 private Canvas canvas;
		 
		public OutlinePage(EditPartViewer viewer) {
			super(viewer);
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {
//			super.createControl(parent);
			
			canvas = new Canvas(parent, SWT.BORDER);
			canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
			LightweightSystem lws = new LightweightSystem(canvas);

			final ScrollableThumbnail thumbnail = new ScrollableThumbnail((Viewport) ((ScalableRootEditPart) getGraphicalViewer()
                                  .getRootEditPart()).getFigure());
			
			thumbnail.setSource(((ScalableRootEditPart) getGraphicalViewer()
                  .getRootEditPart())
                  .getLayer(LayerConstants.PRINTABLE_LAYERS));
			
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
}
