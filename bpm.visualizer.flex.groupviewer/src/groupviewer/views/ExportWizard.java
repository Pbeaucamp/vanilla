package groupviewer.views;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class ExportWizard extends Wizard implements INewWizard {
	private ExportLocation wizpage;
	private ExportModel model;
	

	public ExportWizard() {
		super();
		setNeedsProgressMonitor(true);
		model = ExportModel.getInstance();
	}
	protected void setModel(ExportModel model){
		this.model = model;
	}
	protected ExportModel getModel(){
		return this.model;
	}

	@Override
	public boolean performFinish() {
		if (model.isRunApp())
			launchBrowser();
		return true;
	}

	private void launchBrowser() {	
	}
	
	@Override
	public void addPages() {
		wizpage = new ExportLocation(model);
		addPage(wizpage);		
	}
	
	@Override
	public boolean canFinish() {
		return wizpage.canFlipToNextPage();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	
}
