package bpm.sqldesigner.ui.command.drop;

import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.RequestsView;

public class ViewDropCommand extends NodeDropCommand {

	@Override
	public String getText() {
		return "view " + node.getName(); //$NON-NLS-1$
	}

	@Override
	public void removeNode() {
		SQLView sqlView = (SQLView) node;
		sqlView.getSchema().removeView(sqlView.getName());

		RequestsView view = (RequestsView) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(RequestsView.ID);
		view.getTab(node.getCluster()).nodeDropped(node);
	}

	@Override
	public void undo() {
		SQLView view = (SQLView) node;
		view.getSchema().addView(view);
	}
}