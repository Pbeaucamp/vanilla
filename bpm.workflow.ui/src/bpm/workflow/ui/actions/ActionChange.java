package bpm.workflow.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.commands.ChangeCommand;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Change a node
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ActionChange extends Action {

	public ActionChange() {
		setText(Messages.ActionChange_0);
	}

	@Override
	public void run() {
		ISelection s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();

		if(s.isEmpty()) {
			return;
		}

		Object o = ((IStructuredSelection) s).getFirstElement();

		if(o instanceof NodePart) {

			Node n = (Node) ((NodePart) o).getModel();

			ChangeCommand c = new ChangeCommand();
			try {
				c.setNode(n);
				c.setModel(((NodePart) o));
				c.execute();
			} catch(Exception e) {
				e.printStackTrace();
			}

		}

	}

}
