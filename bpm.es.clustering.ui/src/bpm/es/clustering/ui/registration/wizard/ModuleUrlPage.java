package bpm.es.clustering.ui.registration.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.composites.CompositeModule;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.beans.Server;

public class ModuleUrlPage extends WizardPage{
	public static final String NAME = "bpm.es.clustering.ui.registration.wizard.ModuleUrlPage"; //$NON-NLS-1$
	public static final String TITLE = Messages.ModuleUrlPage_1;
	public static final String DESCRIPTION = Messages.ModuleUrlPage_2;
	
	
	private CompositeModule composite;
	
	protected ModuleUrlPage() {
		super(NAME);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		composite = new CompositeModule(main, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		setControl(main);
	}
	
	public Server getServer(){
		VanillaPlatformModule module = composite.getModule();
		if (module == null){
			return null;
			
		}
		
		Server s = new Server();
		s.setName(module.getName());
		s.setUrl(module.getUrl());
		
		return s;
		
	}
	
}
