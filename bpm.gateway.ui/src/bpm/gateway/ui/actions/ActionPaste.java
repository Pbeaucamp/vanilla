package bpm.gateway.ui.actions;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.action.Action;

import bpm.gateway.ui.gef.commands.PasteCommand;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.ContentPanelEditPart;

public class ActionPaste extends Action {

	private EditPartViewer viewer;
	
	public ActionPaste(EditPartViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void run() {
		
		RootEditPart root = viewer.getRootEditPart();
		for(Object e : root.getChildren()){
			if (e instanceof ContentPanelEditPart){
				PasteCommand cmd = new PasteCommand();
				cmd.setContainerModel((ContainerPanelModel)((ContentPanelEditPart)e).getModel());
				cmd.execute();
				
			}
		}
		
		
		
	}

	@Override
	public boolean isEnabled() {
		return Clipboard.getDefault().getContents()!= null && Clipboard.getDefault().getContents() instanceof Node;
	}
}
