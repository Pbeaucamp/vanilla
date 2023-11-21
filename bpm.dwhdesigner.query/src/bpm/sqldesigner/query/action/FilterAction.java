package bpm.sqldesigner.query.action;

import java.util.List;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.Action;

import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.part.ColumnPart;

public class FilterAction extends Action {

	private EditPartViewer viewer;

	public FilterAction(EditPartViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void run() {
		List<?> selected = viewer.getSelectedEditParts();
		Column model = (Column) ((ColumnPart) selected.iterator().next())
				.getModel();

		((SQLDesignerComposite) viewer.getControl().getParent().getParent()
				.getParent().getParent()).addFilterComposite(model);
	}

}
