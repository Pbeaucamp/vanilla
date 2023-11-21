package bpm.metadata.ui.actions;

import metadata.client.model.dialog.fields.DialogFieldsValues;
import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeDataStream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStream;

public class BrowseColumn implements IWorkbenchWindowActionDelegate {
	private Shell shell;
	
	private IStructuredSelection selection;
	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
		this.shell = window.getShell();

	}

	public void run(IAction action) {
		Dialog d = null;
		if ((selection.getFirstElement() instanceof TreeDataStream)){
			d = new DialogFieldsValues(shell,((TreeDataStream)selection.getFirstElement()).getDataStream());
		}
		if (selection.getFirstElement() instanceof IDataStream){
			d = new DialogFieldsValues(shell,((IDataStream)selection.getFirstElement()));
		}
		if (selection.getFirstElement() instanceof IBusinessTable){
			d = new DialogFieldsValues(shell,((IBusinessTable)selection.getFirstElement()));
		}
		if ((selection.getFirstElement() instanceof TreeBusinessTable)){
			d = new DialogFieldsValues(shell,((TreeBusinessTable)selection.getFirstElement()).getTable());
		}
		
		d.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
		
		

	}

}
