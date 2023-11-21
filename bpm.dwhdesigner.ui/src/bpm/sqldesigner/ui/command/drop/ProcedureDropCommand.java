package bpm.sqldesigner.ui.command.drop;

import org.eclipse.jface.dialogs.MessageDialog;

import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.RequestsView;

public class ProcedureDropCommand extends NodeDropCommand {

	@Override
	public String getText() {
		return "procedure " + node.getName(); //$NON-NLS-1$
	}

	@Override
	public void removeNode() {
		SQLProcedure procedure = (SQLProcedure) node;
		if (!procedure.getDropStatement().equals("")) { //$NON-NLS-1$

			procedure.getSchema().removeProcedure(procedure.getName());

			RequestsView view = (RequestsView) Activator.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(RequestsView.ID);
			view.getTab(node.getCluster()).nodeDropped(node);
		} else {
			MessageDialog
					.openError(
							Activator.getDefault().getWorkbench().getDisplay()
									.getActiveShell(),
							Messages.ProcedureDropCommand_2,
							Messages.ProcedureDropCommand_3);
		}
	}

	@Override
	public void undo() {
		SQLProcedure procedure = (SQLProcedure) node;
		procedure.getSchema().addProcedure(procedure);
	}
}