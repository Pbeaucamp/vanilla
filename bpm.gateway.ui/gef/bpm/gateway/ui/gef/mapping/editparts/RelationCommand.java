package bpm.gateway.ui.gef.mapping.editparts;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.mapping.model.FieldModel;
import bpm.gateway.ui.gef.mapping.model.MappingModel;
import bpm.gateway.ui.gef.mapping.model.Relation;
import bpm.gateway.ui.i18n.Messages;

public class RelationCommand extends Command {
	private Relation link;
	private FieldModel source, target;
	private Composite parent;
	
	public RelationCommand(FieldModel source, Composite parent){
		this.source = source;
		this.parent = parent;
	}

	@Override
	public boolean canExecute() {
		for (Iterator iter = source.getSourceConnections().iterator(); iter.hasNext();) {
			Relation conn = (Relation) iter.next();
			if (conn.getTarget().equals(target)) {
				return false;
			}
		}
		
		return source != target;
	}

	@Override
	public void execute() {
		try {
			link = new Relation(source, target, parent);
			
		} catch (Exception e) {
			e.printStackTrace();
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, Messages.RelationCommand_0, e.getMessage());
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
	
	public Relation getLink(){
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
