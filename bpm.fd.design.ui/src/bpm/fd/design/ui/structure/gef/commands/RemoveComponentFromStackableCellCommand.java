package bpm.fd.design.ui.structure.gef.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.StackableCell;

public class RemoveComponentFromStackableCellCommand extends Command{
	private List<IComponentDefinition> components = new ArrayList<IComponentDefinition>();
	private StackableCell target;
	private CommandStack commandStack;
	
	private boolean canbeExecuted = false;
	public RemoveComponentFromStackableCellCommand(CommandStack commandStack){
		this.commandStack = commandStack;
	}
	
	/**
	 * @param components the components to set
	 */
	public void addComponents(IComponentDefinition component) {
		this.components.add(component);
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(StackableCell target) {
		this.target = target;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {

		return target != null && components != null && target instanceof StackableCell;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (!canbeExecuted){
			return;
		}
		for(IComponentDefinition d : components){
			target.removeBaseElementToContent(d);
		}
				
		canbeExecuted = false;
	}

	public void enabled(boolean value){
		canbeExecuted = value;
	}
	
	public List<IComponentDefinition> getComponents() {
		return components;
	}

	public ComponentConfig getComponentConfig() {
		
		return target.getConfig(components.get(0));
	}
	
	


}
