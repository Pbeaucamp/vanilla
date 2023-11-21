package bpm.fd.design.ui.project.views.actions;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.wizard.migration.datasource.MigrationWizard;

public class DataSourceMigrationAction extends Action implements IViewActionDelegate{

	
	private Viewer viewer;
	public DataSourceMigrationAction(String label, String id, Viewer viewer){
		super(label);
		setId(id);
	

	}
	@Override
	public boolean isEnabled() {
		
		return super.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		
		
		
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		try {
			MigrationWizard wiz = new MigrationWizard();
//			wiz.init(Activator.getDefault().getWorkbench(), (IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
			WizardDialog d = new WizardDialog(shell, wiz);
			if (d.open() == WizardDialog.OK){
			}
			
			viewer.refresh();
		} catch (OdaException e) {
			e.printStackTrace();
		}
	}
	public void init(IViewPart view) {
		
		
	}
	public void run(IAction action) {
		run();
		
	}
	public void selectionChanged(IAction action, ISelection selection) {
		
		
	}
	
	
}
