package bpm.fd.design.ui.project.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.wizard.fmdt.WizardFmdtResourceConversion;

public class FmdtResourceConversionAction extends Action implements IViewActionDelegate{
	private Viewer viewer;
	public FmdtResourceConversionAction(Viewer viewer, String label, String id){
		super(label);
		
		setId(id);
		this.viewer = viewer;
	}
	@Override
	public void init(IViewPart view) {
		
		
	}

	@Override
	public void run(IAction action) {
		run();
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
		
	}

	@Override
	public void run() {
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		DataSource dataSource = (DataSource)ss.getFirstElement();
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		WizardFmdtResourceConversion wiz = new WizardFmdtResourceConversion(dataSource);
		WizardDialog d = new WizardDialog(shell, wiz);
		d.open();
	}
}
