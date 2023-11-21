package bpm.fd.design.ui.structure.gef.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;

public class AddComponentCommand extends Command{
	private List<IComponentDefinition> components = new ArrayList<IComponentDefinition>();
	private Cell target;
	private CommandStack commandStack;
	private boolean canbeExecuted = false;
	
	private ComponentConfig config;
	private DrillDrivenStackableCell ddCell;
	
	public void setComponentConfig(ComponentConfig config){
		this.config = config;
	}
	public AddComponentCommand(CommandStack commandStack){
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
	public void setTarget(Cell target) {
		this.target = target;
	}
	
	public List<IComponentDefinition> getComponents(){
		return new ArrayList<IComponentDefinition>(components);
	}
	public void enabled(boolean value){
		canbeExecuted = value;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
//				
//		for(IBaseElement e : target.getContent()){
//			if (components.contains(e)){
//				return false;
//			}
//		}
//		
		return target != null && components != null && (!components.isEmpty() || ddCell != null) && target instanceof Cell;
//		return canbeExecuted;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (!canbeExecuted){
			return;
		}
		
		if (ddCell != null){
			target.addBaseElementToContent(ddCell);
		}
		else{
			for(IComponentDefinition d : components){
				target.addBaseElementToContent(d);
			}
			

			ComponentConfig targetConfig = target.getConfig(components.get(0));
			
			for(ComponentParameter p : targetConfig.getParameters()){
				for(ComponentParameter _p : config.getParameters()){
					if (p.getName().equals(_p.getName())){
						targetConfig.setParameterOrigin(p, config.getComponentNameFor(_p));
					}
				}
			}
		}
		
		
		canbeExecuted = false;
	}
	public void setDrillCell(DrillDrivenStackableCell model) {
		ddCell = model;
		
	}
	
	
}
