package bpm.es.clustering.ui.registration.wizard;

import org.eclipse.jface.wizard.Wizard;

import bpm.vanilla.platform.core.beans.Server;
/**
 * @deprecated modules cannot be registered manually anymore. They register themselves
 * on the VanillaPlatform when they start.
 * @author ludo
 *
 */
public class ModuleRegistrationWizard extends Wizard{

	private ModuleUrlPage page;
	
	@Override
	public boolean performFinish() {
		try {
			Server s = page.getServer();
			bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSystemManager().registerServerNode(s);
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public void addPages() {
		page = new ModuleUrlPage();
		addPage(page);
		
		
		
		
		super.addPages();
	}

}
