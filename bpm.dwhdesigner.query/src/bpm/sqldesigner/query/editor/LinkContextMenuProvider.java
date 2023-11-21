package bpm.sqldesigner.query.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.IMenuManager;

import bpm.sqldesigner.query.action.InvertAction;

public class LinkContextMenuProvider extends ContextMenuProvider {

	public LinkContextMenuProvider(EditPartViewer viewer) {
		super(viewer);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {

		InvertAction actionInvert = new InvertAction(getViewer());
		actionInvert.setText("Invert Arrow");
		actionInvert.setId("invert");

		menu.add(actionInvert);
	}

}