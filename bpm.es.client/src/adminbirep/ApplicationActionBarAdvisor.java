package adminbirep;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.dialog.DialogRepository;
import bpm.birep.admin.client.views.ViewGroup;
import bpm.birep.admin.client.views.ViewLogs;
import bpm.birep.admin.client.views.ViewRole;
import bpm.birep.admin.client.views.ViewUser;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction preferenceAction;
    private Action showLogs, selectDataBase;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(final IWorkbenchWindow window) {
    	exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);
        
        showLogs = new Action(Messages.ApplicationActionBarAdvisor_0){
        	public void run(){
        		IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IViewPart v = page.showView(ViewLogs.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
					
					if (v != null){
						((ViewLogs)v).setInput();
					}
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
        	}
        };
    
        selectDataBase = new Action(Messages.ApplicationActionBarAdvisor_6){
        	public void run(){
        		try {
        			DialogRepository dial = new DialogRepository(window.getShell());
        			if (dial.open() == DialogRepository.OK){
        				ViewUser vu = (ViewUser)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewUser.ID);
        				if (vu != null){
//        					vu.createInput();
        				}
        				
        				ViewGroup vg = (ViewGroup)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
        				if (vg != null){
//        					vg.createInput();
        				}
        				
        				ViewRole vr = (ViewRole)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewRole.ID);
        				if (vr != null){
        					vr.createInput();
        				}
        				
        				ViewTree vc = (ViewTree)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
        				if (vc != null){
        					vc.createInput();
        				}
        			}
        			
    				
    				
    			} catch (Exception e) {
    				e.printStackTrace();
    				MessageDialog.openError(window.getShell(), Messages.ApplicationActionBarAdvisor_7, e.getMessage());
    			}
        	}
        };
    
    
        
    };

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_8, IWorkbenchActionConstants.M_FILE);

    	fileMenu.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
    	 
        fileMenu.add(preferenceAction);
//        fileMenu.add(new Separator());
//        fileMenu.add(showLogs);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
    	
        
        MenuManager toolMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_9, IWorkbenchActionConstants.OPEN_EXT);
        toolMenu.add(selectDataBase);
        toolMenu.add(new Separator());
        
    	MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_10, IWorkbenchActionConstants.M_HELP);
    	helpMenu.add(aboutAction);
//    	helpMenu.add(licence);
    	
    	menuBar.add(fileMenu);
    	menuBar.add(toolMenu);
    	menuBar.add(helpMenu);
    }


	    
    
    
}
