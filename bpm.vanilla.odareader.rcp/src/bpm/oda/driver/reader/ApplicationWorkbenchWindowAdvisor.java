package bpm.oda.driver.reader;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);

	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(900, 550));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("Vanilla Reader");

	}

	@Override
	public void postWindowCreate() {

		super.postWindowCreate();

		IContributionItem[] mItems, mSubItems;
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		mItems = mm.getItems();

		for(int i = 0; i < mItems.length; i++) {

			if(mItems[i] instanceof MenuManager) {
				mSubItems = ((MenuManager) mItems[i]).getItems();
				for(int j = 0; j < mSubItems.length; j++) {

					if(mItems[i].getId().equals("file")) {
						((MenuManager) mItems[i]).remove("org.eclipse.ui.openLocalFile");

						((MenuManager) mItems[i]).remove("converstLineDelimitersTo");
					}

					else if(mItems[i].getId().equals("help")) {
						((MenuManager) mItems[i]).remove("group.updates");
						((MenuManager) mItems[i]).remove("org.eclipse.update.ui.updateMenu");
						((MenuManager) mItems[i]).remove("org.eclipse.ui.actions.showKeyAssistHandler");
					}

				}
			}
		}

		// remove preference pages
		PreferenceManager pm = getWindowConfigurer().getWindow().getWorkbench().getPreferenceManager();

		for(IPreferenceNode n : pm.getRootSubNodes()) {
			if(!n.getId().startsWith("bpm.")) {
				pm.remove(n.getId());
			}

		}

	}

	@Override
	public void postWindowOpen() {
		Activator.getDefault().getPreferenceStore().setValue("installFolder", Platform.getInstallLocation().getURL().toString().substring(6));

		super.postWindowOpen();

		// remove menus

		MenuManager mbManager = ((ApplicationWindow) getWindowConfigurer().getWindow()).getMenuBarManager();
		for(int i = 0; i < mbManager.getItems().length; i++) {
			IContributionItem item = mbManager.getItems()[i];
			if(item.getId().equals("repositoryMenu")) {
				item.setVisible(false);
				mbManager.remove(item);

				break;
			}
		}

	}
}
