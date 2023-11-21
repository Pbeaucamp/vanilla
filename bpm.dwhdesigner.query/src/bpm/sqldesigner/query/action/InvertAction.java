package bpm.sqldesigner.query.action;

import java.util.List;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.Action;

import bpm.sqldesigner.query.part.ConnectionPart;

public class InvertAction  extends Action  {

	private EditPartViewer viewer;

	public InvertAction(EditPartViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void run() {
		List<?> linkPart = viewer.getSelectedEditParts();
		if (linkPart.size()==1){
			ConnectionPart c = (ConnectionPart) linkPart.iterator().next();
			c.invert();
		}
	}

}