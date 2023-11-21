package bpm.vanilla.server.ui.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.server.ui.views.composite.RunningOptionComposite;

public class RunningOptionPage extends WizardPage{
	
	private RunningOptionComposite runningComposite;
	
	public RunningOptionPage(String pageName) {
		super(pageName);	
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		runningComposite = new RunningOptionComposite(main, SWT.NONE);
		runningComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		setControl(main);
	}
	
	public Properties getRunProperties(){
		return runningComposite.getRunProperties();
	}
	
	@Override
	public boolean isPageComplete() {
		return runningComposite.isPageComplete();
	}
}
