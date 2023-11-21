package bpm.fd.design.ui.editor.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.IContainer;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.editor.commands.CreateCommand;
import bpm.fd.design.ui.editor.commands.CreateContainerCommand;
import bpm.fd.design.ui.editor.commands.LayoutCommand;


public class FreeLayoutPolicy extends XYLayoutEditPolicy{

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (child.getModel() instanceof IContainer){
			LayoutCommand command = new LayoutCommand();
			command.setCell((IContainer)child.getModel());
			command.setLayout((Rectangle)constraint);
			return command;
		}
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		 if (request.getType() == REQ_CREATE ) {
			 if (request.getNewObjectType() == IStructureElement.class){
				 CreateContainerCommand cmd = new CreateContainerCommand();
				 cmd.setParent(getHost());
				 cmd.setLayout(request.getLocation(),request.getSize());
				 
				 
				 cmd.setElement((IStructureElement)request.getNewObject());
				 return cmd;
			 }
			 else if (IComponentDefinition.class.isAssignableFrom((Class<?>)request.getNewObjectType())){
				 CreateCommand cmd = new CreateCommand();
				 cmd.setHost(getHost());
				 cmd.setLayout(request.getLocation(),request.getSize());
		         cmd.setNewObject(request.getNewObject());
		        	 
		         return cmd;
			 }
			
		 }
			 //TODO : else if newObject = class extending IComponentDefinition.class
		 
		return null;
	}

}
