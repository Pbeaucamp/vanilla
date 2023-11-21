package bpm.workflow.ui.actions;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Point;

import bpm.workflow.ui.editors.WorkflowModelEditorPart;
import bpm.workflow.ui.gef.commands.PasteCommand;
import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.Pool;
import bpm.workflow.ui.gef.part.ContentPanelEditPart;
import bpm.workflow.ui.gef.part.PoolEditPart;

/**
 * Paste a node
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ActionPaste extends Action {

	private EditPartViewer viewer;

	public ActionPaste(EditPartViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void run() {

		RootEditPart root = viewer.getRootEditPart();
		for(Object e : root.getChildren()) {
			if(e instanceof ContentPanelEditPart) {
				PasteCommand cmd = new PasteCommand();
				cmd.setContainerModel((ContainerPanelModel) ((ContentPanelEditPart) e).getModel());
				int x = WorkflowModelEditorPart.currentX;
				int y = WorkflowModelEditorPart.currentY;
				cmd.setPosition(new Point(x, y));
				cmd.execute();

			}
			else if(e instanceof PoolEditPart) {
				PasteCommand cmd = new PasteCommand();
				cmd.setContainerModel((Pool) ((PoolEditPart) e).getModel());
				cmd.execute();
			}
		}

	}

	@Override
	public boolean isEnabled() {
		return Clipboard.getDefault().getContents() != null && Clipboard.getDefault().getContents() instanceof Node;
	}
}
