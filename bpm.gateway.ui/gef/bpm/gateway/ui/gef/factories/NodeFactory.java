package bpm.gateway.ui.gef.factories;

import org.eclipse.gef.requests.CreationFactory;

import bpm.gateway.core.Comment;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.NodeException;
import bpm.gateway.ui.i18n.Messages;

public class NodeFactory implements CreationFactory {

	private Class<?> template;
	
	/**
	 * the class is the API class for this Node
	 * @param gefModel
	 */
	public NodeFactory(Class<?> gatewayModel){
		template = gatewayModel;
	}
	
	
	public Object getObjectType() {
		return template;
	}
	
	public Object getNewObject() {
		if (template == null){
			return null;
		}
		
		try{
			if (template == Comment.class){
				return new Comment();
			}
			else if (template == GlobalDefinitionInput.class){
				
				GIDModel m =  new GIDModel(Activator.getDefault().getCurrentInput());
				GlobalDefinitionInput t = new GlobalDefinitionInput();
				m.setName("new GlobalDefinitionInput"); //$NON-NLS-1$
				t.setName(m.getName());
				t.setTemporaryFilename(Activator.getDefault().getCurrentInput().getDocumentGateway().getResourceManager().getVariable(Variable.STRING, "GATEWAY_TEMP").getOuputName() + m.getName()); //$NON-NLS-1$
				m.setTransformation(t);
				return m;
			}
			return create(template);

			
			
		}catch(NodeException e){
			e.printStackTrace();
		}
		
		return null;
	}

	private Node create(Class<?> c) throws NodeException{
		if (!Transformation.class.isAssignableFrom(c)){
			throw new NodeException("Factory cannot create a node for class " + c.getName()); //$NON-NLS-1$
		}
		
		Node n = new Node();
		
		Transformation modelGateway;
		try {
			modelGateway = (Transformation)c.newInstance();
			n.setName("new " + c.getSimpleName()); //$NON-NLS-1$
			modelGateway.setName(n.getName());
			modelGateway.setTemporaryFilename(Activator.getDefault().getCurrentInput().getDocumentGateway().getResourceManager().getVariable(Variable.STRING, "GATEWAY_TEMP").getOuputName() + n.getName()); //$NON-NLS-1$
			n.setTransformation(modelGateway);
			
			return n;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NodeException(Messages.NodeFactory_5 + c.getName());
		} 
		
	}
	
	
	
}
