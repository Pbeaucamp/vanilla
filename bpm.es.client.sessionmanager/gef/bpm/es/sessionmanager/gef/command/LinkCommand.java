package bpm.es.sessionmanager.gef.command;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.es.sessionmanager.Activator;
import bpm.es.sessionmanager.gef.model.FieldModel;
import bpm.es.sessionmanager.gef.model.Link;

public class LinkCommand extends Command {
	private Link link;
	private FieldModel source, target;
		
	public LinkCommand(FieldModel source){
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
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, "Error when creating link", e.getMessage());
		}
		
	}
	
	public void setTarget(FieldModel target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		
//		if (target.getGatewayModel().getContainer() == null && source.getGatewayModel().getContainer() != null){
//			throw new IllegalArgumentException();
//		}
//		else if (target.getGatewayModel().getContainer() != null && source.getGatewayModel().getContainer() == null){
//			throw new IllegalArgumentException();
//		}
//		else{
//			if (target.getGatewayModel().getContainer() != null && !target.getGatewayModel().getContainer().equals(source.getGatewayModel().getContainer())){
//				throw new IllegalArgumentException();
//			}
//			else if (source.getGatewayModel().getContainer() != null && !source.getGatewayModel().getContainer().equals(target.getGatewayModel().getContainer())){
//				throw new IllegalArgumentException();
//			}
//		}
		
		this.target = target;
	}
	
	public Link getLink(){
		return link;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
//	@Override
//	public void redo() {
//		try {
//			link.reconnect();
//			NodeLinkerHelper.add(link);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.gef.commands.Command#undo()
//	 */
//	@Override
//	public void undo() {
//		link.disconnect();
//		NodeLinkerHelper.remove(link);
//		
//	}
	
	
	
	
}
