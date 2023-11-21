package bpm.gateway.ui.gef.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Link;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.NodeLinkerHelper;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.ViewPalette;

public class LinkCommand extends Command {
	private Link link;
	private Node source, target;
		
	public LinkCommand(Node source){
		this.source = source;
		
	}

	@Override
	public boolean canExecute() {
		for (Iterator iter = source.getSourceLink().iterator(); iter.hasNext();) {
			Link conn = (Link) iter.next();
			if (conn.getTarget().equals(target)) {
				return false;
			}
		}
		
				
		
		return source != target;
	}

	@Override
	public void execute() {
		try {
			link = new Link(source, target);

			Activator activator = Activator.getDefault();
			if (activator.isOnlyOneLink()) {
				ViewPalette v = (ViewPalette) activator.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewPalette.ID);
				v.activateToolEntry(ViewPalette.TOOL_ENTRY_SELECT);
				
				activator.setOnlyOneLink(false);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, Messages.LinkCommand_0, e.getMessage());
		}
		
	}
	
	public void setTarget(Node target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		
		if (target.getGatewayModel().getContainer() == null && source.getGatewayModel().getContainer() != null){
			throw new IllegalArgumentException();
		}
		else if (target.getGatewayModel().getContainer() != null && source.getGatewayModel().getContainer() == null){
			throw new IllegalArgumentException();
		}
		else{
			if (target.getGatewayModel().getContainer() != null && !target.getGatewayModel().getContainer().equals(source.getGatewayModel().getContainer())){
				throw new IllegalArgumentException();
			}
			else if (source.getGatewayModel().getContainer() != null && !source.getGatewayModel().getContainer().equals(target.getGatewayModel().getContainer())){
				throw new IllegalArgumentException();
			}
		}
		
		this.target = target;
	}
	
	public Link getLink(){
		return link;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		try {
			link.reconnect();
			NodeLinkerHelper.add(link);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		link.disconnect();
		NodeLinkerHelper.remove(link);
		
	}
	
	
	
	
}
