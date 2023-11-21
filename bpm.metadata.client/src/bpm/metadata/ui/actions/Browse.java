package bpm.metadata.ui.actions;

import metadata.client.model.dialog.DialogBusinessTable;
import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeDataStream;
import metadataclient.Activator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;

public class Browse implements IWorkbenchWindowActionDelegate {
	private Shell shell;
	private IStructuredSelection selection;
	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
		this.shell = window.getShell();

	}

	public void run(IAction action) {
		IDataStream str = null;
		if (selection.getFirstElement()  instanceof TreeDataStream){
			//create a fakeBusinessTable to use the Dialog
			str = ((TreeDataStream)selection.getFirstElement()).getDataStream();
			
		}
		else if (selection.getFirstElement()  instanceof IDataStream){
			str = (IDataStream)selection.getFirstElement();
		}
		
		if (str != null){
			AbstractBusinessTable t = null;
			if(str instanceof UnitedOlapDataStream) {
				t = new UnitedOlapBusinessTable(str.getName());
			}
			else {
				t = new SQLBusinessTable(str.getName());
			}
			for(IDataStreamElement c : str.getElements()){
				t.addColumn(c);
			}
			
			BusinessModel mod = new BusinessModel();
			mod.setName("__internal_dummy_model"); //$NON-NLS-1$
			mod.addBusinessTable(t);
			Activator.getDefault().getModel().addBusinessModel(mod);
			try{
				DialogBusinessTable dial = new DialogBusinessTable(shell, t);
				dial.open();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			Activator.getDefault().getModel().removeBusinessModel(mod);
			return;
		}
		
		AbstractBusinessTable busT = null;
		if (selection.getFirstElement() instanceof TreeBusinessTable){
			busT = (AbstractBusinessTable)((TreeBusinessTable)selection.getFirstElement()).getTable();
			
		}
		else if (selection.getFirstElement() instanceof AbstractBusinessTable){
			busT = (AbstractBusinessTable)selection.getFirstElement();
		}

		if (busT != null){
			DialogBusinessTable dial = new DialogBusinessTable(shell, busT);
			
			dial.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = (IStructuredSelection)selection;
		
		

	}

}
