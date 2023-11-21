package bpm.fd.design.ui.editors;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.structure.gef.commands.AddComponentCommand;
import bpm.fd.design.ui.structure.gef.commands.RemoveComponentCommand;
import bpm.fd.design.ui.structure.gef.editparts.StructureEditPartFactory;

public class StructureEditor extends GraphicalEditorWithPalette implements /*ITabbedPropertySheetPageContributor, */IEditDomainProvider{

	public static final String ID = "bpm.fd.design.ui.editors.StructureEditor"; //$NON-NLS-1$

	private FdEditor parent;
	
	public StructureEditor(FdEditor parent){
		this.parent = parent;
	}
	
	public ZoomManager getZoomManager(){
		return ((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
	 */
	@Override
	public void commandStackChanged(EventObject event) {
		
		super.commandStackChanged(event);
		if (isDirty()){
			firePropertyChange(PROP_DIRTY);
		}
		
	}

	public EditDomain getDomain(){

		return super.getEditDomain();
	}
	


	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		FdProject fdProject = Activator.getDefault().getProject();
		IProject p = root.getProject(fdProject.getProjectDescriptor().getProjectName());
		
		monitor.beginTask(Messages.StructureEditor_1, IProgressMonitor.UNKNOWN);
		monitor.subTask(Messages.StructureEditor_2);
		Document doc = null;
		try{
			
			
			Dictionary d = Activator.getDefault().getProject().getDictionary();
			
			doc = DocumentHelper.createDocument(d.getElement());
		}catch(Exception e){
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_3, Messages.StructureEditor_4, 
					new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_5, e));
			return;
		}
		
		monitor.subTask(Messages.StructureEditor_6);
		
		try{
			XMLWriter writer = new XMLWriter(new FileOutputStream(Platform.getLocation().append(p.getFile(fdProject.getProjectDescriptor().getDictionaryName() +".dictionary").getFullPath()).toOSString()), OutputFormat.createPrettyPrint()); //$NON-NLS-1$
			writer.write(doc);
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_8, Messages.StructureEditor_9, 
					new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_10, e));
			return;
		}
		
		
		
		
		
		monitor.beginTask(Messages.StructureEditor_11, IProgressMonitor.UNKNOWN);
		monitor.subTask(Messages.StructureEditor_12);
		FdModel d = null;
		
		try{
			
			
			 d = ((FdProjectEditorInput)getEditorInput()).getModel();
			
			doc = DocumentHelper.createDocument(d.getElement());
		}catch(Exception e){
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_13, Messages.StructureEditor_14, 
					new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_15, e));
			return;
		}
		
		monitor.subTask(Messages.StructureEditor_16);
		
		try{
			XMLWriter writer = new XMLWriter(new FileOutputStream(Platform.getLocation().append(p.getFile(d.getName() +Messages.StructureEditor_17).getFullPath()).toOSString()), OutputFormat.createPrettyPrint());
			writer.write(doc);
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
			monitor.setCanceled(true);
			ErrorDialog.openError(getSite().getShell(), Messages.StructureEditor_18, Messages.StructureEditor_19, 
					new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.StructureEditor_20, e));
			return;
		}
		
		monitor.subTask(Messages.StructureEditor_21);
		try{
			IFile f = p.getFile("components.properties"); //$NON-NLS-1$

			Properties lang = new Properties();
			for(IComponentDefinition def : Activator.getDefault().getProject().getDictionary().getComponents()){
				for(IComponentOptions opt : def.getOptions()){
					for(String key : opt.getInternationalizationKeys()){
						String value  = opt.getDefaultLabelValue(key);
						if (value != null){
							lang.setProperty(def.getName() + "." + key, value); //$NON-NLS-1$
						}
						else{
							lang.setProperty(def.getName() + "." + key, ""); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
			
			
			lang.store(new FileOutputStream(Platform.getLocation().toOSString() + f.getFullPath().toOSString()), Messages.StructureEditor_26);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		getCommandStack().flush();
		
		firePropertyChange(PROP_DIRTY);

		
	}

	

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setEditPartFactory(new StructureEditPartFactory());
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this); 
	}
	
	@Override
	protected void createGraphicalViewer(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
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
	@Override
	protected void initializeGraphicalViewer() {
		StructureEditorContextMenuProvider provider = new StructureEditorContextMenuProvider(getGraphicalViewer(), parent.getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		
		
		

		ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart();
		
		//install zoom
		double[] zoomLevels = new double[]{0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0};
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
		
		
		getGraphicalViewer().setRootEditPart(rootEditPart);
	
		FdProjectEditorInput ei = (FdProjectEditorInput)getEditorInput();
		
		getGraphicalViewer().setContents(ei.getModel()/*.getProject().getFdModel()*/);
		getSite().setSelectionProvider(getGraphicalViewer());
		
				
		getCommandStack().addCommandStackEventListener(new CommandStackEventListener() {
			
			public void stackChanged(CommandStackEvent event) {
				if (event.isPreChangeEvent()){
					if (event.getCommand() instanceof CompoundCommand){
						AddComponentCommand add = null;
						RemoveComponentCommand rem = null;
						for(Object o : ((CompoundCommand)event.getCommand()).getCommands()){
							if (o instanceof AddComponentCommand){
								add = ((AddComponentCommand)o);
							}
							else if (o instanceof RemoveComponentCommand){
								rem = ((RemoveComponentCommand)o);
							}
						}
						
						if (add != null && rem != null){
							add.enabled(true);
							add.setComponentConfig(rem.getComponentConfig());
							rem.enabled(true);
						}
					}
				}
				
			}
		});
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		Assert.isTrue(input instanceof FdProjectEditorInput);
		
				setEditDomain(new DefaultEditDomain(this));
		this.setPartName(((FdProjectEditorInput)input).getModel().getProject().getFdModel().getName());
//		try{
//			getGraphicalViewer().setContents(((FdProjectEditorInput)input).getModel());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
		
	}

	@Override
	public void dispose() {
		
		super.dispose();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
	 */
	@Override
	public String getTitleToolTip() {
		
		return ((FdProjectEditorInput)getEditorInput()).getName();
	}
	
//	public ActionRegistry getActionRegistry(){
//		return super.getActionRegistry();
//	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#isDirty()
//	 */
//	@Override
//	public boolean isDirty() {
//		return super.isDirty() || dictionaryDirty;
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class type) {
//		if (type == IPropertySheetPage.class){
//			return new TabbedPropertySheetPage(this);
//		}
		if (type == ZoomManager.class){
			return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
		}  

		return super.getAdapter(type);
	}

	public String getContributorId() {
		return ID;
	}


	@Override
	public void setPaletteViewer(PaletteViewer viewer) {
		super.setPaletteViewer(viewer);
		
	}

	@Override
	protected void createPaletteViewer(Composite parent) {
	
	}
	@Override
	public void createPartControl(Composite parent) {
		Composite splitter = new Composite(parent, SWT.NONE);
		splitter.setLayoutData(new GridData(GridData.FILL_BOTH));
		splitter.setLayout(new GridLayout());
		createGraphicalViewer(splitter);
	}
	@Override
	protected PaletteRoot getPaletteRoot() {
		if (getPaletteViewer() == null){
			return null;
		}else{
			return getPaletteViewer().getPaletteRoot();
		}
	
		
	}

	
	
}
