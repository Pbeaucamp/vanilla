package bpm.fd.design.ui.views;

import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public class DeleteDictionaryComponentCommand extends Command{
	private IComponentDefinition def;
	
	public DeleteDictionaryComponentCommand(IComponentDefinition def){
		this.def = def;
	}

	@Override
	public void execute() {
		def.getDictionary().removeComponent(def);
		
	}

	
	
}
