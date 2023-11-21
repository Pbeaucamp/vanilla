package bpm.oda.driver.reader;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction actionExit, actionAbout, actionPreferences;
	


	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {

		actionPreferences = ActionFactory.PREFERENCES.create(window);
		register(actionPreferences);
		
		actionExit = ActionFactory.QUIT.create(window);
		register(actionExit);
		
		actionAbout = ActionFactory.ABOUT.create(window);
		register(actionAbout);
		
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
	
		fileMenu.add(actionPreferences);
		
		fileMenu.add(actionExit);
		
		menuBar.add(fileMenu);
		
		MenuManager aboutMenu = new MenuManager("?", IWorkbenchActionConstants.M_HELP);
		aboutMenu.add(actionAbout);
		menuBar.add(aboutMenu);
		
	}

}
