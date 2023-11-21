package bpm.fd.design.ui.wizard.migration.datasource;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class DataSourceWrapperPage extends WizardPage{
	

	private IWizardPage wrapped;
	private Composite main;
	
	protected DataSourceWrapperPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		if (main != null){
			main.dispose();
		}
		
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new FillLayout());
		
		if (wrapped != null && wrapped.getControl() == null){
			wrapped.createControl(main);
		}
		
		setControl(main);
		
	}
	
	public void setWrappedPage(IWizardPage page){
		Composite parent  = getContainer().getCurrentPage().getControl().getParent();
		
		if (page != this.wrapped){
			this.wrapped = page;
			
			createControl(parent);
			parent.layout();
		}
			
		
		
	}
	
	@Override
	public boolean isPageComplete() {
		return wrapped != null && wrapped.isPageComplete();
	}
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
}
