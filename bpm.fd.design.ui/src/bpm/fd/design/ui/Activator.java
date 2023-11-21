package bpm.fd.design.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillInfo;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.design.ui.editor.figures.svg.SvgUtility;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.icons.Icons;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "bpm.fd.design.ui"; //$NON-NLS-1$

	public static final String PROJECT_CHANGED = "bpm.fd.design.ui.projectchanged"; //$NON-NLS-1$
	public static final String PROJECT_CONTENT = "bpm.fd.design.ui.projectcontent"; //$NON-NLS-1$


	// The shared instance
	private static Activator plugin;
	
	// listener about the Project Loading
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		SvgUtility.release();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * open the given project 
	 * @param project
	 */
	public void openProject(FdProject project) {
		
		if (project instanceof MultiPageFdProject){
			

			
			try{
				getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( new FdProjectEditorInput(project.getFdModel()), FdEditor.ID);
			}catch(Exception e){
				e.printStackTrace();
				ErrorDialog d = new ErrorDialog(getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.Activator_3, Messages.Activator_4, new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()), IStatus.ERROR);
				d.open();
			}
			
//			for(FdModel model : ((MultiPageFdProject)project).getPagesModels()){
//				try{
//					IEditorPart o = getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( new FdProjectEditorInput(model), FdEditor.ID);
//					
//				}catch(Exception e){
//					e.printStackTrace();
//					ErrorDialog d = new ErrorDialog(getWorkbench().getActiveWorkbenchWindow().getShell(),
//							Messages.Activator_5, Messages.Activator_6, new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()), IStatus.ERROR);
//					d.open();
//				}
//			}
			
			for (IComponentDefinition dg : project.getDictionary().getComponents(ComponentDataGrid.class)){
				((ComponentDataGrid)dg).getDrillInfo().setModelPage(((MultiPageFdProject)project).getPageModel(((ComponentDataGrid)dg).getDrillInfo().getModelPageName()));
			}
			for (IComponentDefinition dg : project.getDictionary().getComponents(ComponentMap.class)){
				((ComponentMap)dg).getDrillInfo().setModelPage(((MultiPageFdProject)project).getPageModel(((ComponentMap)dg).getDrillInfo().getModelPageName()));
			}
			for (IComponentDefinition dg : project.getDictionary().getComponents(ComponentChartDefinition.class)){
				ChartDrillInfo drill = ((ComponentChartDefinition)dg).getDrillDatas();
				if (drill.getTypeTarget() == TypeTarget.TargetPopup && drill.getUrl() != null){
					drill.setTargetModelPage(((MultiPageFdProject)project).getPageModel(drill.getUrl()));
				}
			}
//			for(IComponentDefinition dg : project.getDictionary().getComponents(ComponentOsmMap.class)) {
//				for(OsmDataSerie serie : ((OsmData)((ComponentOsmMap)dg).getDatas()).getSeries()) {
//					if(serie instanceof OsmDataSerieMarker) {
//						if(((OsmDataSerieMarker)serie).getTargetPageName() != null) {
//							String target = ((OsmDataSerieMarker)serie).getTargetPageName();
//							((OsmDataSerieMarker)serie).setTargetPage(((MultiPageFdProject) project).getPageModel(target));
//						}
//					}
//				}
//			}
			
		}
		else{
			//open the Content Editor
			try{
				getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FdProjectEditorInput(project.getFdModel()), FdEditor.ID);
			}catch(Exception e){
				e.printStackTrace();
				ErrorDialog d = new ErrorDialog(getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.Activator_7, Messages.Activator_8, new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()), IStatus.ERROR);
				d.open();
			}
			
//			try{
//				getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FdProjectEditorInput(project.getFdModel()), "bpm.fd.design.ui.editor.part.LayoutEditor");
//			}catch(Exception e){
//				e.printStackTrace();
//				ErrorDialog d = new ErrorDialog(getWorkbench().getActiveWorkbenchWindow().getShell(),
//						Messages.Activator_7, Messages.Activator_8, new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()), IStatus.ERROR);
//				d.open();
//			}
		}
		
		
		
	}
	
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propertyName, oldValue, newValue);
	}

	public FdProject getProject() {
		try{
			IEditorPart part = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			
			if (part == null){
				return null;
			}
			IEditorInput in = part.getEditorInput();
			
			if (in instanceof FdProjectEditorInput){
				return ((FdProjectEditorInput)in).getModel().getProject();
			}
		}catch(NullPointerException ex){
			
		}
		
		return null;
	}
	
	
	public IProject getResourceProject(){
		FdProject p = getProject();
		
		if (p == null){
			return null;
		}
		
		return ResourcesPlugin.getWorkspace().getRoot().getProject(p.getProjectDescriptor().getProjectName());
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		for(Field f : Icons.class.getFields()){
			
			try {
				reg.put((String)f.get(null), ImageDescriptor.createFromFile(Activator.class, "icons/" + f.get(null))); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
	}

}
