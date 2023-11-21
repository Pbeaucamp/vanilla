package bpm.gateway.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;

public class NodeCreationCommand extends Command {

	private ContainerPanelModel parent;
	private GIDModel gid;
	private Node newNode;
	
	/**
	 * @param parent the parent to set
	 */
	public final void setParent(ContainerPanelModel parent) {
		this.parent = parent;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public final void setParent(GIDModel parent) {
		this.gid = parent;
	}
	/**
	 * @param newNode the newNode to set
	 */
	public final void setNewObject(Node newNode) {
		this.newNode = newNode;
	}
	
	public void setLayout(Rectangle layout){
		if (newNode == null){
			return;
		}
		((Node)this.newNode).setLayout(layout);
		
	}
	
	
	public void execute(){
		if (parent != null){
			parent.addChild(newNode);
		}
		else if (gid != null){
			gid.addChild(newNode);
			
		}
		newNode.setName(newNode.getGatewayModel().getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		execute();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (parent != null){
			parent.removeChild(newNode);
		}
		else if (gid != null){
			gid.removeChild(newNode);
		}
	}
	
	
}
