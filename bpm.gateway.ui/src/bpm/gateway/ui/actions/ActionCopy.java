package bpm.gateway.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.commands.CopyCommand;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class ActionCopy extends Action {

		
	

	public ActionCopy(){
		
		setText(Messages.ActionCopy_0);
	}
	@Override
	public void run() {
		ISelection s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		if (s.isEmpty()){
			return;
		}
		
		Object o = ((IStructuredSelection)s).getFirstElement();
		
		if (o instanceof NodePart){
			Node n = (Node)((NodePart)o).getModel();
			CopyCommand c = new CopyCommand();
			
			c.setModel(n);
			c.execute();
		}
		
	}

//	@Override
//	public boolean isEnabled() {
//		try{
//			ISelection s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
//
//			return !s.isEmpty() && ((IStructuredSelection)s).getFirstElement() instanceof NodePart;
//		}catch(Exception e){
//			
//		}
//		return false;
//	}

	
}
