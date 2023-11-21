package bpm.database.ui.viewer.relations.views;

import java.util.ArrayList;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import bpm.database.ui.viewer.relations.Activator;
import bpm.database.ui.viewer.relations.gef.editparts.AppEditPartFactory;
import bpm.database.ui.viewer.relations.model.Node;



abstract public class AbstractViewRelation extends ViewPart implements ISelectionListener{

	protected ToolBar toolbar;
	protected ZoomManager zoomManager;
	protected ScrollingGraphicalViewer editor;

	protected EditDomain domain = new EditDomain();
	protected Node model ;
	
	public AbstractViewRelation() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		toolbar = new ToolBar(composite, SWT.NONE);
		toolbar.setLayout(new GridLayout());
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		initEditor(composite);
		createToolBar();
		createThumbnail(composite);
	}
	
	private void createThumbnail(Composite parent){
		Canvas canvas = new Canvas(parent, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.FILL, GridData.END, true,false));
		LightweightSystem lws = new LightweightSystem(canvas);
		
		ScrollableThumbnail thumbnail = new ScrollableThumbnail((Viewport) ((ScalableFreeformRootEditPart) editor.getRootEditPart()).getFigure());
		thumbnail.setSource(((ScalableFreeformRootEditPart) editor.getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));
		
		lws.setContents(thumbnail);
	}
	
	
	protected void createToolBar() {
		Listener zoomInListener = new Listener() {
			public void handleEvent(Event event) {
				ZoomManager zm = zoomManager;
				if (zm.canZoomIn()) {
					zm.zoomIn();
				}
			}
		};

		Listener zoomOutListener = new Listener() {
			public void handleEvent(Event event) {
				ZoomManager zm = zoomManager;
				if (zm.canZoomOut()) {
					zm.zoomOut();
				}
			}
		};
	/*	
		Listener zoomSelectionListener = new Listener() {
			 public void widgetSelected(SelectionEvent e) {
				c.get
				ZoomManager zm = zoomManager;
				if (zm.canZoomOut()) {
					zm.zoomOut();
				}
			}
		};
*/
		new ToolItem(toolbar, SWT.SEPARATOR);

		
		
		ToolItem tool = new  ToolItem(toolbar, SWT.SEPARATOR);
		
		final Combo c = new Combo(toolbar, SWT.NONE);
		   // c.setBounds(50, 50, 150, 65);
			//c.setSize(150, 65);
		    String items[] = { "10%", "25%", "50%", "75%", "100%", "150%", "200%", "300%", "400%", "500%"};
		    c.setItems(items);
		    c.setToolTipText("Zoom Selection");
		  //  c.addListener(SWT.Selection, zoomSelectionListener);
		    c.addSelectionListener(new SelectionAdapter() {
		        public void widgetSelected(SelectionEvent e) {
		        	String value = c.getText();
		        	double zoomValue=  Double.parseDouble(value.substring(0, value.lastIndexOf("%")));
		        	ZoomManager zm = zoomManager;
		        	zm.setZoom(zoomValue/100);        	
		        }
		    });
		    c.addKeyListener(new KeyListener() {
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode==SWT.CR){
						try{
							String value = c.getText();
				        	double zoomValue=  Double.parseDouble(value);
				        	ZoomManager zm = zoomManager;
				        	zm.setZoom(zoomValue/100);  
						} catch(Exception ex){
							
						}
					}
					
				}
			});
		    
		c.pack();
		tool.setWidth(c.getSize().x);
		tool.setControl(c);	
		
		toolbar.pack();
		
		ToolItem zoomIn = new ToolItem(toolbar, SWT.PUSH);
		zoomIn.setImage(Activator.getDefault().getImageRegistry().get("zoomIn"));
		zoomIn.setToolTipText("Zoom In");
		zoomIn.addListener(SWT.Selection, zoomInListener);

		ToolItem zoomOut = new ToolItem(toolbar, SWT.PUSH);
		zoomOut.setImage(Activator.getDefault().getImageRegistry().get("zoomOut"));
		zoomOut.setToolTipText("Zoom Out");
		zoomOut.addListener(SWT.Selection, zoomOutListener);
	

		
		ToolItem saveAsPicture = new ToolItem(toolbar, SWT.PUSH);
		saveAsPicture.setToolTipText("Save as Picture File");
		saveAsPicture.setImage(Activator.getDefault().getImageRegistry().get("save_as_picture"));
		saveAsPicture.addSelectionListener(new SelectionAdapter(){

			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[]{"*.bmp", "*.tif", "*.png", "*.gif", "*.jpg"});
				String filePath = fd.open();
				
				if (fd == null){
					return;
				}
				
				
				IFigure figure = ((ScalableFreeformRootEditPart)editor.getRootEditPart()).getLayer(LayerConstants.SCALABLE_LAYERS);

				Image img1 = new Image(null,new org.eclipse.swt.graphics.Rectangle(0, 0, figure.getBounds().width,figure.getBounds().height + 1));
				GC gc1 = new GC(img1);

				Graphics grap1 = new SWTGraphics(gc1);
				figure.paint(grap1);

				ImageLoader loader1 = new ImageLoader();
				loader1.data = new ImageData[] { img1.getImageData() };
				
				if (filePath.endsWith("bmp")){
					if (!filePath.endsWith(".bmp")){
						filePath += ".bmp";
					}
					loader1.save(filePath, SWT.IMAGE_BMP);
				}
				else if (filePath.endsWith("tif")){
					if (!filePath.endsWith(".tif")){
						filePath += ".tif";
					}
					loader1.save(filePath, SWT.IMAGE_TIFF);
				}
				else if (filePath.endsWith("png")){
					if (!filePath.endsWith(".png")){
						filePath += ".png";
					}
					loader1.save(filePath, SWT.IMAGE_PNG);
				}
				else if (filePath.endsWith("gif")){
					if (!filePath.endsWith(".gif")){
						filePath += ".gif";
					}
					loader1.save(filePath, SWT.IMAGE_GIF);
				}
				else if (filePath.endsWith("jpg")){
					if (!filePath.endsWith(".jpg")){
						filePath += ".jpg";
					}
					loader1.save(filePath, SWT.IMAGE_JPEG);
				}
				else{
					img1.dispose();
					gc1.dispose();
					return;
				}
				
				
				img1.dispose();
				gc1.dispose();
				
				MessageDialog.openInformation(getSite().getShell(), "Saving Relations", "File " + filePath + " saved.");
				
			}
			
		});
		
	}

	@Override
	public void setFocus() {
	}
	
	
	protected void initEditor(Composite parent){
		editor = new ScrollingGraphicalViewer();  
		editor.createControl(parent);
		editor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		editor.setEditPartFactory(new AppEditPartFactory());
		
		ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart(){
			/*
			@Override
			protected Viewport createViewport() {
				Viewport v =  super.createViewport();
				v.setContentsTracksHeight(true);
				v.setContentsTracksWidth(true);
				return v;
			}
			@Override
			protected void refreshVisuals() {
				super.refreshVisuals();
			}
			
			@Override
			protected void refreshChildren() {
				super.refreshChildren();
			}
			*/
		};
		editor.setRootEditPart(rootEditPart);
		editor.setEditDomain(domain);
		domain.addViewer(editor);
		
		/***********************************************************************
		 * Zoom
		 **********************************************************************/
		double[] zoomLevels;
		ArrayList<String> zoomContributions;

		zoomManager = rootEditPart.getZoomManager();

		zoomLevels = new double[] { 0.10, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0,
				4.0, 5.0, 10.0, 15.0, 20.0 };
		zoomManager.setZoomLevels(zoomLevels);

		zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		zoomManager.setZoomLevelContributions(zoomContributions);


	}
	
	
	abstract protected  void initContent(Object object);
	
	abstract public void selectionChanged(IWorkbenchPart part, ISelection selection);
	
	
	

}
