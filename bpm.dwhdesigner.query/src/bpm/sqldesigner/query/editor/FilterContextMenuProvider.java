package bpm.sqldesigner.query.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.IMenuManager;

import bpm.sqldesigner.query.action.FilterAction;

public class FilterContextMenuProvider extends ContextMenuProvider {

	public FilterContextMenuProvider(EditPartViewer viewer) {
		super(viewer);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {

		FilterAction actionFilter = new FilterAction(getViewer());
		actionFilter.setText("Edit Filter");
		actionFilter.setId("actionFilter");

		menu.add(actionFilter);
	}

}