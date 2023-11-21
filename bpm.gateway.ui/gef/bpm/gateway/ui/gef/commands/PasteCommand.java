package bpm.gateway.ui.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import bpm.gateway.core.Transformation;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.NodeException;

public class PasteCommand extends Command {

	private ContainerPanelModel container;
	
	
	
	@Override
	public void execute() {
		Node n = (Node)Clipboard.getDefault().getContents();
		container.addChild(n);
		Transformation t = n.getGatewayModel().copy();
		Node d = new Node();
		d.setName(t.getName());
		try {
			d.setTransformation(t);
			Clipboard.getDefault().setContents(d);
		} catch (NodeException e) {
			
			e.printStackTrace();
		}
	}



	/**
	 * @param model the model to set
	 */
	public final void setContainerModel(ContainerPanelModel container) {
		this.container = container;
	}

	
	
	
	
}
