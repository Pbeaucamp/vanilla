package bpm.fd.design.ui.rcp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import bpm.fd.design.ui.rcp.action.ActionCloseActiveProject;
import bpm.fd.design.ui.rcp.action.ActionExportWizard;
import bpm.fd.design.ui.rcp.action.ActionImportWizard;
import bpm.fd.design.ui.rcp.action.ActionNewWizard;
import bpm.fd.design.ui.rcp.action.ActionOpenFdModel;
import bpm.fd.design.ui.rcp.action.ActionOpenFdProject;
import bpm.fd.design.ui.rcp.action.ActionSaveAs;
import bpm.fd.design.ui.rcp.dialogs.DialogOpenProject;
import bpm.fd.design.ui.rcp.icons.IconsNames;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exit, preferences, about;;
	private IWorkbenchAction save, closeAll, saveAll;
	private Action saveAs, newObject, importObject, exportObject, openModel, openProject;//, testConver;
	private Action closeActiveProject;
	
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	createFileActions(window);
    	createHelpActions(window);
    }
    
    
    private void createHelpActions(IWorkbenchWindow window){
    	about = ActionFactory.ABOUT.create(window);
    	register(about);
    }
    
    private void createFileActions(final IWorkbenchWindow window){
    	closeAll = ActionFactory.CLOSE_ALL.create(window);
        register(closeAll);
        closeAll.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.closeAll));
        
    	saveAll = ActionFactory.SAVE_ALL.create(window);
        register(saveAll);
       
    	exit = ActionFactory.QUIT.create(window);
        register(exit);
        
        preferences = ActionFactory.PREFERENCES.create(window);
        register(preferences);
        
        newObject = new ActionNewWizard();
        newObject.setText(Messages.ApplicationActionBarAdvisor_0);
       
        
        importObject =new ActionImportWizard();
        
        importObject.setText(Messages.ApplicationActionBarAdvisor_1);
        
        exportObject = new ActionExportWizard();
        exportObject.setText(Messages.ApplicationActionBarAdvisor_2);
        try{
        	importObject.setImageDescriptor(ActionFactory.IMPORT.create(window).getImageDescriptor());
            exportObject.setImageDescriptor(ActionFactory.EXPORT.create(window).getImageDescriptor());
        }catch(Throwable t){
        	
        }
        
        save = ActionFactory.SAVE.create(window);
        register(save);
         
         openModel = new ActionOpenFdModel();
         openModel.setText(Messages.ApplicationActionBarAdvisor_4);//Messages.ApplicationActionBarAdvisor_3);
         
         openProject = new Action(Messages.ApplicationActionBarAdvisor_4){ 
        	 public void run(){
        		 DialogOpenProject d = new DialogOpenProject(window.getShell());
        		 d.open();
        	 }
         };
         
         saveAs = new ActionSaveAs();
         saveAs.setImageDescriptor( ActionFactory.SAVE_AS.create(window).getImageDescriptor());
         
         closeActiveProject = new ActionCloseActiveProject();
         closeActiveProject.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.close));
         

    }
    
    
    
	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
	 */
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolbar = new ToolBarManager(SWT.RIGHT | SWT.FLAT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main"));    //$NON-NLS-1$
		toolbar.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
		

		toolbar.add(save);
		toolbar.add(saveAll);
		toolbar.add(saveAs);
		toolbar.add(closeActiveProject);
		toolbar.add(closeAll);
		toolbar.add(new Separator());
	}

	protected void fillMenuBar(IMenuManager menuBar) {
    	
    	    	
    	MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_5, IWorkbenchActionConstants.M_FILE); 
    	fileMenu.add(newObject);
    	fileMenu.add(new Separator());
//    	fileMenu.add(openProject);
    	fileMenu.add(openModel);
    	fileMenu.add(createOpenRecent());
    	fileMenu.add(new Separator());
    	fileMenu.add(save);
    	fileMenu.add(saveAs);
    	fileMenu.add(saveAll);
    	fileMenu.add(new Separator());
    	fileMenu.add(closeActiveProject);
    	fileMenu.add(closeAll);
    	fileMenu.add(new Separator());
    	fileMenu.add(importObject);
    	fileMenu.add(exportObject);
        fileMenu.add(new Separator());
        fileMenu.add(preferences);
        fileMenu.add(exit);

        
        menuBar.add(fileMenu);
        
        MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_6, IWorkbenchActionConstants.M_HELP); 
        helpMenu.add(about);
        
        menuBar.add(helpMenu);
        
    }
    
    private MenuManager createOpenRecent(){
    	MenuManager recent = new MenuManager(Messages.ApplicationActionBarAdvisor_7, IWorkbenchActionConstants.M_FILE + ".recent");  //$NON-NLS-2$
    	
    	for(IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
    		try {
    			if (!p.isOpen()){
    				p.open(null);
    			}
				recent.add(new ActionOpenFdProject(p));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return recent;
    	
    }
    
}
