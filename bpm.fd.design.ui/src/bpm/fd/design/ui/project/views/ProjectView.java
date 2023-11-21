package bpm.fd.design.ui.project.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.gef.figures.PictureHelper;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.tools.ColorManager;

public class ProjectView extends ViewPart {

	private class Param{
		IComponentDefinition component;
		ComponentParameter param;
		ComponentConfig conf;
		public Param(ComponentConfig conf, ComponentParameter param){
			this.component = conf.getTargetComponent();
			this.param = param;
			this.conf = conf;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Param){
				Param p = (Param)obj;
				return p.component == component && conf == p.conf && p.param == param;
			}
			return false;
		}
	}
	
	private class DeleteResourceAction extends Action{
		IResource r;
		void setResource(IResource r){
			this.r = r;
		}
		
		public void run(){
			
			try {
				Activator.getDefault().getProject().removeResource(r);
				Activator.getDefault().getResourceProject().getFile(r.getName()).delete(true, null);
				projectViewer.refresh();
			} catch (Exception e) {
				
				e.printStackTrace();
				MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ProjectView_0, e.getCause().getMessage());
			}
		}
	}
	
	private class DeleteFdModelAction extends Action{
		
		
		public void run(){
			FdModel r = (FdModel)((IStructuredSelection)projectViewer.getSelection()).getFirstElement();
			if (((MultiPageFdProject)Activator.getDefault().getProject()).getFdModel() == r){
				MessageDialog.openInformation(getSite().getShell(), Messages.ProjectView_1, Messages.ProjectView_2);
				return;
			}
			boolean b = MessageDialog.openQuestion(getSite().getShell(), Messages.ProjectView_3, Messages.ProjectView_4);
			if (!b){
				return;
			}
			try {
				if (r.getParentStructureElement() != null){
					r.getParentStructureElement().removeFromContent(r);
				}
				((MultiPageFdProject)Activator.getDefault().getProject()).removePageModel(r);
				Activator.getDefault().getResourceProject().getFile(r.getName() + ".freedashboard").delete(true, null); //$NON-NLS-1$
				
				
				
				projectViewer.refresh();
				
				
				
				for(IEditorPart e : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditors()){
					if (e.getEditorInput() instanceof FdProjectEditorInput && ((FdProjectEditorInput)e.getEditorInput()).getModel() == r){
						Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(e, false);
					}
				}
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
				MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ProjectView_6, e.getCause().getMessage());
			}
		}
	}
	
	private class AddModelAction extends Action{
		public void run(){
			final MultiPageFdProject project = (MultiPageFdProject)Activator.getDefault().getProject();
			
			InputDialog d = new InputDialog(getSite().getShell(), Messages.ProjectView_7, Messages.ProjectView_8, "model_" + (project.getPagesModels().size() + 1), new IInputValidator() { //$NON-NLS-3$
				
				public String isValid(String newText) {
					for(FdModel model : project.getPagesModels()){
						if (model.getName().equals(newText)){
							return Messages.ProjectView_10;
						}
					}
					
					if (newText.contains(" ")){ //$NON-NLS-1$
						return Messages.ProjectView_12;
					}
					if (newText.trim().equals("")){ //$NON-NLS-1$
						return Messages.ProjectView_14;
					}
					return null;
				}
			});
			if (d.open() != InputDialog.OK){
				return;
			}
			String s = d.getValue();
			if (s == null){
				return;
			}
			
			try {
				FdModel model = createModel(project, s);
				setContent(project);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.ProjectView_15, e.getMessage());
			}
			
		}
		
		private FdModel createModel(MultiPageFdProject fdProj, String modelName)throws Exception{
			IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
			IWorkspaceRoot r = workspace.getRoot();
			IProject p = r.getProject(Activator.getDefault().getProject().getProjectDescriptor().getProjectName());;
			
			
			
			IFile _f = p.getFile(modelName + ".freedashboard"); //$NON-NLS-1$
			try {
				FdModel m = new FdModel(fdProj.getFdModel().getStructureFactory(), fdProj, modelName);
				Document d = DocumentHelper.createDocument(m.getElement());
				_f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
				fdProj.addPageModel(m);
				return m;
			} catch (Exception e) {
				throw new Exception (Messages.ProjectView_18, e);
			}
		}
	}
	
	private class OpenModelAction extends Action{
		public void run(){
			
			
			if (((IStructuredSelection)projectViewer.getSelection()).getFirstElement() instanceof FdModel){
				FdModel r = (FdModel)((IStructuredSelection)projectViewer.getSelection()).getFirstElement();
				
				
				for(IEditorPart p : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditors()){
					if (p.getEditorInput() instanceof FdProjectEditorInput && ((FdProjectEditorInput)p.getEditorInput()).getModel() == r){
						return;
					}
				}
				try{
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( new FdProjectEditorInput(r), FdEditor.ID);
				}catch(Exception ex){
					MessageDialog.openError(getSite().getShell(), Messages.ProjectView_19, ex.getMessage());
				}
			}
			else if (((IStructuredSelection)projectViewer.getSelection()).getFirstElement() instanceof FileJavaScript){
				FileJavaScript r = (FileJavaScript)((IStructuredSelection)projectViewer.getSelection()).getFirstElement();
				FdProject fdP = Activator.getDefault().getProject();
				
				if (fdP == null){
					return;
				}
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(fdP.getProjectDescriptor().getProjectName());
				
				IFile f = p.getFile(r.getName());
				IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("*.js"); //$NON-NLS-1$
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( new FileEditorInput(f), desc.getId());
				} catch (PartInitException e) {
					
					e.printStackTrace();
				}
			}
		
			
			
		}
	}
	
	
	private static final String BASE_PART_NAME = Messages.ProjectView_5;
	public static final String ID = "bpm.fd.design.ui.project.views.ProjectView"; //$NON-NLS-1$
	
	private TreeViewer projectViewer;
	
	
	
	private static final String resourcesNode = new String(Messages.ProjectView_23);
	private static final String parametersNode = new String(Messages.ProjectView_24);
	private static final String modelsNode = new String(Messages.ProjectView_25);
	
	private PropertyChangeListener listener = new PropertyChangeListener() {
		
		public void propertyChange(PropertyChangeEvent arg0) {
			projectViewer.refresh();
			
		}
	};
	
	
	public ProjectView() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());

		projectViewer = new TreeViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
		projectViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		projectViewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				List<Object> l = new ArrayList<Object>();
				if (parentElement instanceof MultiPageFdProject){
					l.addAll(((MultiPageFdProject)parentElement).getPagesModels());
					l.addAll(((FdModel)parentElement).getContent());
					
				}
				if (parentElement instanceof FdModel){
					l.addAll(((FdModel)parentElement).getContent());
					l.add(parametersNode);
				}
				else if (parentElement == resourcesNode){
					l.addAll(((FdProject)projectViewer.getInput()).getResources());
				}
				else if (parentElement == parametersNode){
					List<Param> params = new ArrayList<Param>();
					HashMap<IComponentDefinition, ComponentConfig> confs = ((FdProject)projectViewer.getInput()).getFdModel().getComponents();
					for(ComponentConfig c : confs.values()){
						for(ComponentParameter p : c.getParameters()){
							params.add(new Param(c, p));
						}
					}
					
					return params.toArray(new Object[params.size()]);
				}
				else if (parentElement instanceof IStructureElement){
					l.addAll(((IStructureElement)parentElement).getContent());
				}
				else if (parentElement == modelsNode){
				
					for(FdModel m : ((MultiPageFdProject)projectViewer.getInput()).getPagesModels()){
						if (m.getParentStructureElement() == null){
							l.add(m);
						}
					}
					
				}
				return l.toArray(new Object[l.size()]);
			}

			public Object getParent(Object element) {
				
				if (element instanceof IResource){
					return resourcesNode;
				}
				if (element instanceof ComponentParameter){
					return parametersNode;
				}
				else if (element instanceof IStructureElement){
					((IStructureElement)element).getParentStructureElement();
				}
				
				return null;
			}

			public boolean hasChildren(Object element) {
				if (element == null){
					return false;
				}
				if (element instanceof FdModel){
					return true;
				}
				if (element  == resourcesNode){
					return !((FdProject)projectViewer.getInput()).getResources().isEmpty();
				}
				if (element  == parametersNode){
					try{
//						for(IComponentDefinition def : ((FdModel)getParent(parametersNode)).getComponents().keySet()){
//							if (!def.getParameters().isEmpty()){
//								return true;
//							}
//						}
						HashMap<IComponentDefinition, ComponentConfig> confs = ((FdProject)projectViewer.getInput()).getFdModel().getComponents();
						for(ComponentConfig c : confs.values()){
							for(ComponentParameter p : c.getParameters()){
								return true;
							}
						}
					}catch(Exception ex){
						
					}
					
				}
				if (element instanceof IStructureElement){
					return ((IStructureElement)element).getContent().size() > 0;
				}
				
				if (element == modelsNode){
					for(FdModel m : ((MultiPageFdProject)projectViewer.getInput()).getPagesModels()){
						if (m.getParentStructureElement() == null){
							return true;
						}
					}
				}
				
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<Object> l = new ArrayList<Object>();
				
				if (inputElement != null){
					FdProject p = (FdProject)inputElement;
					
					l.add(p.getFdModel());
					
					
					l.add(resourcesNode);
					if (p instanceof MultiPageFdProject){
						l.add(modelsNode);
					}
					
				
				}
				
				
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		LabelProvider labelProvider = new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
			 */
			@Override
			public Image getImage(Object element) {
				if (element == parametersNode || element instanceof Param){
					return Activator.getDefault().getImageRegistry().get(Icons.parameter);
				}
				else if (element == resourcesNode){
					return Activator.getDefault().getImageRegistry().get(Icons.resources);
				}
				else if (element == modelsNode){
					return Activator.getDefault().getImageRegistry().get(Icons.availablesModels);
				}
				return  PictureHelper.getIcons(element);
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof FolderPage){
					return ((FolderPage)element).getTitle();
				}
				if (element instanceof IBaseElement){
					return ((IBaseElement)element).getId();
				}
				else if (element instanceof Dictionary){
					return ((Dictionary)element).getName();
				}
				else if (element instanceof DataSource){
					return ((DataSource)element).getName();
				}
				else if (element instanceof IResource){
					return ((IResource)element).getName();
				}
				else if (element instanceof Param){
					Param p = (Param)element;
					
					return p.param.getName() + " (" + p.component.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				return super.getText(element);
			}
			
		};
		
		projectViewer.setLabelProvider(new DecoratingLabelProvider(labelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.DecoratingLabelProvider#getForeground(java.lang.Object)
			 */
			@Override
			public Color getForeground(Object element) {
				if (element instanceof Param){
					for (Object o : ((ITreeContentProvider)projectViewer.getContentProvider()).getChildren(parametersNode)){
						if (!o.equals(element) && ((Param)element).param.getName().equals(((Param)o).param.getName())){
							return ColorManager.getColorRegistry().get(ColorManager.COLOR_WRONG_VALUE_FIELD);
						}
					}
				}
				return super.getForeground(element);
			}
			
		});
		createContextMenu();
	}

	private void createContextMenu(){
		MenuManager mgr = new MenuManager();
		getSite().registerContextMenu("bpm.fd.design.ui.project.views.projectMenu", mgr, projectViewer);
		
		final DeleteResourceAction action  = new DeleteResourceAction();
		final DeleteFdModelAction delModel = new DeleteFdModelAction();
		delModel.setText(Messages.ProjectView_28);
		final AddModelAction addModel = new AddModelAction();
		addModel.setText(Messages.ProjectView_29);
		final OpenModelAction openModel = new OpenModelAction();
		openModel.setText(Messages.ProjectView_30);
		
		mgr.addMenuListener(new IMenuListener() {
			
			public void menuAboutToShow(IMenuManager manager) {
				manager.removeAll();
				ISelection s = projectViewer.getSelection();
				if (s.isEmpty()){
					return;
				}
				if (((IStructuredSelection)s).getFirstElement() instanceof IResource){
					IResource r = (IResource)((IStructuredSelection)s).getFirstElement();
					if (!(r instanceof FileProperties)){
						action.setResource(r);
						action.setText(Messages.ProjectView_31 + r.getName());
						manager.add(action);
					}
					
					if (r instanceof FileJavaScript){
						openModel.setText(Messages.ProjectView_32 + ((FileJavaScript)((IStructuredSelection)s).getFirstElement()).getName());
						manager.add(openModel);
					}
				}
								
				if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
				
					manager.add(addModel);
					
					if (((IStructuredSelection)s).getFirstElement() instanceof FdModel){
						manager.add(delModel);
						
						delModel.setEnabled(((IStructuredSelection)s).getFirstElement() != Activator.getDefault().getProject().getFdModel());
						
						
						boolean opened = false;
						for(IEditorReference e : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()){
							if (e instanceof FdEditor){
								if (((FdProjectEditorInput)((FdEditor)e).getStructureEditor().getEditorInput()).getModel() == (FdModel)((IStructuredSelection)s).getFirstElement()){
									opened = true;
									break;
								}
								
							}
						}
						if (!opened){
							
							openModel.setText(Messages.ProjectView_33 + ((FdModel)((IStructuredSelection)s).getFirstElement()).getName());
							manager.add(openModel);
						}
						
					}
				}
				
				
			}
		});
	
		projectViewer.getTree().setMenu(mgr.createContextMenu(projectViewer.getTree()));
		
		projectViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)projectViewer.getSelection();
				if ( ! ss.isEmpty() && ss.getFirstElement() instanceof FileJavaScript){
					FileJavaScript r = (FileJavaScript)ss.getFirstElement();
					FdProject fdP = Activator.getDefault().getProject();
					
					if (fdP == null){
						return;
					}
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(fdP.getProjectDescriptor().getProjectName());
					
					IFile f = p.getFile(r.getName());
					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("*.js"); //$NON-NLS-1$
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor( new FileEditorInput(f), desc.getId());
					} catch (PartInitException e) {
						
						e.printStackTrace();
					}
					return;
				}
				
				if (ss.isEmpty()  || !(ss.getFirstElement() instanceof FdModel)){
					return;
				}
				openModel.run();
				
				
				
			}
		});
	
	
		
	}
	
	
	@Override
	public void setFocus() {
		

	}

	public void setContent(FdProject project) {
		if (project != projectViewer.getInput() && project != null){
			project.getFdModel().removePropertyChangeListener(listener);
			projectViewer.setInput(project);
			project.getFdModel().addPropertyChangeListener(listener);
		}
		else if (projectViewer.getInput() != null){
			projectViewer.refresh();
		}
		
		
		if (project != null){
			this.setPartName(BASE_PART_NAME + " " + project.getProjectDescriptor().getProjectName()); //$NON-NLS-1$
		}
		else{
			this.setPartName(BASE_PART_NAME);
		}
		
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

}
