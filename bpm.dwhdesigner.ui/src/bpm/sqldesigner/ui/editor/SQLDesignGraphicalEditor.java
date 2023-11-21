package bpm.sqldesigner.ui.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
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
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.query.editor.SQLRootPart;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.command.creation.NodeCreationFactory;
import bpm.sqldesigner.ui.editpart.AppEditPartFactory;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.utils.FigureResizer;

public class SQLDesignGraphicalEditor extends GraphicalEditorWithPalette {

	public static final String ID = "sqldesigner"; //$NON-NLS-1$
//	private DropListener dropListener;
	private SelectionToolEntry selectionToolEntry;
	private boolean schemaLoaded = false;

	public SQLDesignGraphicalEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	public IFigure getViewPort() {
		return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart())
				.getFigure();
	}

	public GraphicalViewer getViewer() {
		return getGraphicalViewer();
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

//		dropListener = new DropListener(getGraphicalViewer(), this);
//		getGraphicalViewer().addDropTargetListener(dropListener);
//		dropListener.addPropertyChangeListener(new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent evt) {
//
//				if (evt.getNewValue() != null) {
//					setSchemaLoaded(true);
//					getPaletteViewer().deselectAll();
//					getPaletteViewer().setActiveTool(
//							getPaletteViewer().getPaletteRoot()
//									.getDefaultEntry());
//
//					Schema schema = getSchema();
//
//					if (schema instanceof SchemaNull)
//						setPartName(schema.getCatalog().getName());
//					else
//						setPartName(schema.getName());
//				} else {
//					getPaletteViewer().deselectAll();
//					getPaletteViewer().setActiveTool(
//							getPaletteViewer().getPaletteRoot()
//									.getDefaultEntry());
//					setSchemaLoaded(false);
//
//				}
//			}
//		});

		getPaletteViewer().addSelectionChangedListener(
				new CreateViewPaletteListener(this, getPaletteViewer()));
		
		if (getEditorInput() instanceof SQLDesignEditorInput){
			Schema schema  = ((SQLDesignEditorInput)getEditorInput()).getSchemaView().getSchema();
			
			if (schema.isNotFullLoaded()){
				try {
					ExtractData.extractWhenNotLoaded(schema);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			getGraphicalViewer().setContents(schema);		
			new FigureResizer().resize(schema, getGraphicalViewer());
			getGraphicalViewer().setContents(schema);
		}
		
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
		
		DesignContextualMenuProvider provider = new DesignContextualMenuProvider(getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
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
		if (type == ZoomManager.class){
			return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}
		if (type == IContentOutlinePage.class) {
            return new OutlinePage(getGraphicalViewer());
		}
			
		else
			return super.getAdapter(type);
	}

	@Override
	protected PaletteRoot getPaletteRoot() {

		PaletteRoot root = new PaletteRoot();

		PaletteGroup manipGroup = new PaletteGroup(Messages.SQLDesignGraphicalEditor_1);
		root.add(manipGroup);

		selectionToolEntry = new SelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		manipGroup.add(new MarqueeToolEntry());

		root.add(new PaletteSeparator());

		PaletteGroup instGroup = new PaletteGroup(Messages.SQLDesignGraphicalEditor_2);
		root.add(instGroup);

		PaletteDrawer createGroup = new PaletteDrawer(Messages.SQLDesignGraphicalEditor_3);
		instGroup.add(createGroup);

		ImageDescriptor imgTable = ImageDescriptor.createFromImage(Activator
				.getDefault().getImageRegistry().get("table")); //$NON-NLS-1$
		createGroup.add(new CreationToolEntry(Messages.SQLDesignGraphicalEditor_5, Messages.SQLDesignGraphicalEditor_6,
				new NodeCreationFactory(Table.class), imgTable, null));

		ImageDescriptor imgColumn = ImageDescriptor.createFromImage(Activator
				.getDefault().getImageRegistry().get("column")); //$NON-NLS-1$
		createGroup.add(new CreationToolEntry(Messages.SQLDesignGraphicalEditor_8, Messages.SQLDesignGraphicalEditor_9,
				new NodeCreationFactory(Column.class), imgColumn, null));

		ImageDescriptor imgView = ImageDescriptor.createFromImage(Activator
				.getDefault().getImageRegistry().get("view")); //$NON-NLS-1$
		createGroup.add(new CreationToolEntry(Messages.SQLDesignGraphicalEditor_11, Messages.SQLDesignGraphicalEditor_12,
				new NodeCreationFactory(SQLView.class), imgView, null));

		root.setDefaultEntry(selectionToolEntry);
		return root;
	}

	public Schema getSchema() {
//		return dropListener.getSchema();
		return ((SQLDesignEditorInput)getEditorInput()).getSchemaView().getSchema();
	}

	public boolean isSchemaLoaded() {
		return schemaLoaded;
	}

//	public void clearSchema(DatabaseCluster cluster) {
//		dropListener.clearSchema(cluster);
//	}

//	public DropListener getDropListener() {
//		return dropListener;
//	}

	@Override
	public void dispose() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		IEditorReference[] editorsRef = null;
		if (page != null)
			editorsRef = page.getEditorReferences();



//		EditorsList.remove(getSchema());
		super.dispose();
	}

	@Override
	public boolean isDirty() {
//		if (dropListener != null)
//			if (SavedEditors.get(getSchema()) == null)
//				return true;
		return super.isDirty();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getSchema() == null)
			return;
		
		
		
		SchemaView view = ((SQLDesignEditorInput)getEditorInput()).getSchemaView();
		
		String fileName = view.getSchema().getCatalog().getDatabaseCluster().getFileName();
		
		if (fileName == null || fileName.equals("")){ //$NON-NLS-1$
			FileDialog fd = new FileDialog(getSite().getShell());
			fd.setFilterExtensions(new String[]{"*.*", ".sqldesign"}); //$NON-NLS-1$ //$NON-NLS-2$
			
			fileName = fd.open();
		}
		if (fileName == null || fileName.equals("")){ //$NON-NLS-1$
			
			MessageDialog.openInformation(getSite().getShell(), Messages.SQLDesignGraphicalEditor_17, Messages.SQLDesignGraphicalEditor_18);
			return;
		}
		try{
			SaveData.saveDatabaseCluster(view.getSchema().getCatalog().getDatabaseCluster(), fileName, SaveData.SAVE_PROCEDURES| SaveData.SAVE_VIEWS );
			getCommandStack().markSaveLocation();

//			if (SavedEditors.get(view.getSchema()) == null)
//				SavedEditors.addEditor(view.getSchema(), this);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.SQLDesignGraphicalEditor_19, Messages.SQLDesignGraphicalEditor_20 + ex.getMessage());
		}
		
//		SchemaPart schemaRoot = (SchemaPart) getViewer().getRootEditPart()
//				.getChildren().get(0);
//
//		Schema schema = ((Schema) schemaRoot.getModel());
//		
//		if (schema.getDatabaseConnection() != null){
//			
//			try{
//				WorkspaceSaveAndLoad.SaveSchemaPart(schemaRoot);
//			}catch(Exception ex){
//				ex.printStackTrace();
//				return;
//			}
//		}
//			
//		else{
//			try {
//				SaveData.saveDatabaseCluster(schema.getCluster(), schema
//						.getCluster().getFileName(), SaveData.SAVE_PROCEDURES
//						| SaveData.SAVE_VIEWS | SaveData.SAVE_LAYOUT);
//				WorkspaceSaveAndLoad.SaveSchemaPart(schemaRoot);
//			} catch (Exception e) {
//				e.printStackTrace();
//				return;
//			}
//
//		}
			
		

	}

	public void setSchemaLoaded(boolean schemaLoaded) {
		if (!this.schemaLoaded)
			if (schemaLoaded) {
				setImage();
			}

		this.schemaLoaded = schemaLoaded;
	}

	private void setImage() {
		Schema schema = getSchema();
		if (schema != null) {
			if (schema instanceof SchemaNull)
				if (schema.getDatabaseConnection() == null)
					setTitleImage(Activator.getDefault().getImageRegistry()
							.get("databasesMP")); //$NON-NLS-1$
				else
					setTitleImage(Activator.getDefault().getImageRegistry()
							.get("databases")); //$NON-NLS-1$
			else if (schema.getDatabaseConnection() == null)
				setTitleImage(Activator.getDefault().getImageRegistry().get(
						"schemaMP")); //$NON-NLS-1$
			else
				setTitleImage(Activator.getDefault().getImageRegistry().get(
						"schema")); //$NON-NLS-1$

		}
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
