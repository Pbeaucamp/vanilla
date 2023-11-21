package bpm.vanilla.server.client.rcp;

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
	private IWorkbenchAction exit, preferences, about;;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		exit = ActionFactory.QUIT.create(window);
		register(exit);

		about = ActionFactory.ABOUT.create(window);
		register(about);

		preferences = ActionFactory.PREFERENCES.create(window);
		register(preferences);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_1, IWorkbenchActionConstants.M_FILE);
		fileMenu.add(new Separator());
		fileMenu.add(preferences);
		fileMenu.add(new Separator());
		fileMenu.add(exit);
		menuBar.add(fileMenu);

		MenuManager m = new MenuManager(Messages.ApplicationActionBarAdvisor_0, IWorkbenchActionConstants.MB_ADDITIONS);
		menuBar.add(m);
		
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_2, IWorkbenchActionConstants.M_HELP);
		helpMenu.add(about);
		menuBar.add(helpMenu);
	}

}
