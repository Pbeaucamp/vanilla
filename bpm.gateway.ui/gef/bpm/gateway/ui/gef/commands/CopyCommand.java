package bpm.gateway.ui.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import bpm.gateway.core.Transformation;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.NodeException;

public class CopyCommand extends Command {

	private Node model;

	
	
	
	@Override
	public void execute() {
		Transformation t = model.getGatewayModel().copy();
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
	public final void setModel(Node model) {
		this.model = model;
	}



	
	
	
	
	
}
