package bpm.fd.api.core.model.events;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.structure.IStructureElement;

public enum ElementsEventType {
	onLoad(new Class<?>[]{FdModel.class}), 
	onUnload(new Class<?>[]{FdModel.class}), 
	onBlur(new Class<?>[]{ComponentFilterDefinition.class}),
	onChange(new Class<?>[]{ComponentFilterDefinition.class}),
	onFocus(new Class<?>[]{IStructureElement.class, ComponentFilterDefinition.class, ComponentButtonDefinition.class}),
	onSelect(new Class<?>[]{ComponentFilterDefinition.class}), 
	onSubmit(new Class<?>[]{ComponentButtonDefinition.class}), 
	onAbort(new Class<?>[]{}), 
	onKeyDown(new Class<?>[]{ComponentFilterDefinition.class}), 
	onKeyUp(new Class<?>[]{ComponentFilterDefinition.class}), 
	onKeyPress(new Class<?>[]{ComponentFilterDefinition.class}), 
	onClick(new Class<?>[]{IStructureElement.class,  ComponentButtonDefinition.class, LabelComponent.class, ComponentFilterDefinition.class}), 
	onDblClick(new Class<?>[]{IStructureElement.class,  ComponentButtonDefinition.class,  LabelComponent.class}), 
	onMouseDown(new Class<?>[]{IStructureElement.class, ComponentButtonDefinition.class}), 
	onMouseMove(new Class<?>[]{IStructureElement.class, ComponentButtonDefinition.class}), 
	onMouseOut(new Class<?>[]{IStructureElement.class, ComponentButtonDefinition.class, ComponentFilterDefinition.class, LabelComponent.class, ComponentLink.class}),
	onMouseOver(new Class<?>[]{IStructureElement.class, ComponentButtonDefinition.class, ComponentFilterDefinition.class, LabelComponent.class, ComponentLink.class}), 
	onMouseUp(new Class<?>[]{IStructureElement.class, ComponentButtonDefinition.class});
	
	private Class<?>[] allowedClasses;
	
	ElementsEventType(Class<?>[] allowedClasses){
		this.allowedClasses = allowedClasses;
	}
	
	
	
	public static List<ElementsEventType> getAvailablesFor(Class<?> c){
		List<ElementsEventType> l = new ArrayList<ElementsEventType>();
		for(ElementsEventType e : values()){
			for(Class<?> cl : e.allowedClasses){
				if (cl.isAssignableFrom(c) && !l.contains(e)){
					l.add(e);
				}
			}
		}
		return l;
	}
}
