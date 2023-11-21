package bpm.norparena.ui.menu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import bpm.norparena.ui.menu.client.dialog.DialogRepository;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
//	private IWorkbenchAction createnew;
	private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private Action loginAction;
    //private IWorkbenchAction prefAction; //ere taken out

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

//		createnew = ActionFactory.NEW.create(window);
//		createnew.setText("New Object");
//		register(createnew);
		  
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		
//		prefAction = ActionFactory.PREFERENCES.create(window);
//		register(prefAction);
		
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
        loginAction = new Action("Change connection") {
        	@Override
	        public void run() {
        		DialogRepository dial = new DialogRepository(Display.getDefault().getActiveShell());
        		dial.open();
	        }
		};
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_0,
				IWorkbenchActionConstants.M_FILE);

    	fileMenu.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
    	
//    	fileMenu.add(createnew);
//      fileMenu.add(preferenceAction);
//    	fileMenu.add(prefAction);
    	fileMenu.add(loginAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
    	
        
        MenuManager toolMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_1, IWorkbenchActionConstants.OPEN_EXT);
//      toolMenu.add(selectDataBase);
        toolMenu.add(new Separator());
//      toolMenu.add(upgrateDataBase);
        
    	MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_2, IWorkbenchActionConstants.M_HELP);
    	helpMenu.add(aboutAction);
    	
    	menuBar.add(fileMenu);
    	menuBar.add(toolMenu);
    	menuBar.add(helpMenu);
	}

}
