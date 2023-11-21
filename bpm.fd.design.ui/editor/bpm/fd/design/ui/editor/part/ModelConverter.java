package bpm.fd.design.ui.editor.part;

import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.IStructureElement;

public class ModelConverter {

	public void convert(FdModel model){
		List<IBaseElement> content = model.getContent();
		
		//remove the elements from the model
		for(IBaseElement e : content){
			model.removeFromContent((IStructureElement)e);
		}
		HashMap<IComponentDefinition, Cell> components = new HashMap<IComponentDefinition, Cell>();
		
		//extract components
		for(IBaseElement e : content){
			extractComponents(components, (IStructureElement)e);
		}
		
		
		//create a cell per Components and set its configs from the original
		int count = 0;
		FactoryStructure f = model.getStructureFactory();
		for(IComponentDefinition def : components.keySet()){
			Cell c = f.createCell("", 1, 1);
			c.addBaseElementToContent(def);
			c.setPosition(10, count * 50);
			c.setSize(100, 30);
			
			ComponentConfig conf = components.get(def).getConfig(def);;
			for(ComponentParameter p : conf.getParameters()){
				c.getConfig(def).setComponentOutputName(p, conf.getComponentOutputName(p));
				c.getConfig(def).setParameterOrigin(p, conf.getTargetComponent().getName());
			}
			
			model.addToContent(c);
		}
		
		
	}
	
	private void extractComponents(HashMap<IComponentDefinition, Cell> components, IStructureElement struct){
		if (struct instanceof Cell){
			for(IBaseElement e : ((Cell)struct).getContent()){
				if (e instanceof IComponentDefinition){
					components.put((IComponentDefinition)e, (Cell)struct);
				}
				else{
					extractComponents(components, (IStructureElement)e);
				}
			}
		}
		else{
			for(IBaseElement e : struct.getContent()){
				extractComponents(components, (IStructureElement)e);
			}
		}
	}
}
