package bpm.profiling.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	
	private IWorkbenchAction exit, about, preferenceAction;
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	exit = ActionFactory.QUIT.create(window);
        register(exit);
        about = ActionFactory.ABOUT.create(window);
        register(about);
        preferenceAction = ActionFactory.PREFERENCES.create(window);
        register(preferenceAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu = new MenuManager("File", IWorkbenchActionConstants.M_FILE); //$NON-NLS-1$
    	fileMenu.add(preferenceAction); 
    	fileMenu.add(new Separator());
        fileMenu.add(exit);
        menuBar.add(fileMenu);
         
         MenuManager helpMenu = new MenuManager("Help", IWorkbenchActionConstants.M_HELP);
         helpMenu.add(about);
         menuBar.add(helpMenu);
    }
    
}
