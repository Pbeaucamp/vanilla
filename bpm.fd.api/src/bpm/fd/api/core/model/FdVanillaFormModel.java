package bpm.fd.api.core.model;

import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.structure.FactoryStructure;


/**
 * Specific class to generate a VanillaForm
 * Only filters and static Components will be available,
 * (no charts, no reports)
 * @author ludo
 *
 */
public class FdVanillaFormModel extends FdModel{
	

	private static Class<? extends IComponentDefinition>[] supportedComponent = new Class[]{
		ComponentFilterDefinition.class, ComponentPicture.class, ComponentLink.class,
		LabelComponent.class, ComponentButtonDefinition.class
	};
	
	public static final String TYPE_VANILLA_FORM = "bpm.fd.api.core.model.FdVanillaFormModel";
	
	public FdVanillaFormModel(FactoryStructure factory) {
		super(factory);
		
	}
	
	@Override
	public Element getElement() {
		Element e = super.getElement();
		e.addAttribute("type", TYPE_VANILLA_FORM);
		
		Element formsParameters = e.addElement("formsOutputs");
		/*
		 * we add this only to be usable from VanillaPlatform and a mapping
		 */
		for(IComponentDefinition c : getComponents().keySet()){
			if (c instanceof ComponentFilterDefinition){
				formsParameters.addElement("output").setText(c.getId());
			}
		}
		return e;
	}

	public static Class<? extends IComponentDefinition>[] getSupportedComponentClasses(){
		return supportedComponent;
	}
}
