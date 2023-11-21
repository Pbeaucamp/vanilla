package bpm.fd.core.component;

import java.util.ArrayList;
import java.util.List;

public enum EventType {
	onLoad(new ComponentType[]{ComponentType.DASHBOARD}), 
	onUnload(new ComponentType[]{ComponentType.DASHBOARD}), 
	onBlur(new ComponentType[]{ComponentType.FILTER}),
	onChange(new ComponentType[]{ComponentType.FILTER}),
	onFocus(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL, ComponentType.FILTER, ComponentType.BUTTON}),
	onSelect(new ComponentType[]{ComponentType.FILTER}), 
	onSubmit(new ComponentType[]{ComponentType.BUTTON}), 
	onAbort(new ComponentType[]{}), 
	onKeyDown(new ComponentType[]{ComponentType.FILTER}), 
	onKeyUp(new ComponentType[]{ComponentType.FILTER}), 
	onKeyPress(new ComponentType[]{ComponentType.FILTER}), 
	onClick(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL,  ComponentType.BUTTON, ComponentType.LABEL, ComponentType.FILTER, ComponentType.DYNAMIC_LABEL}), 
	onDblClick(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL,  ComponentType.BUTTON,  ComponentType.LABEL, ComponentType.DYNAMIC_LABEL}), 
	onMouseDown(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL, ComponentType.BUTTON}), 
	onMouseMove(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL, ComponentType.BUTTON}), 
	onMouseOut(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL, ComponentType.BUTTON, ComponentType.FILTER, ComponentType.LABEL, ComponentType.DYNAMIC_LABEL, ComponentType.URL}),
	onMouseOver(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL, ComponentType.BUTTON, ComponentType.FILTER, ComponentType.LABEL, ComponentType.DYNAMIC_LABEL, ComponentType.URL}), 
	onMouseUp(new ComponentType[]{ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL, ComponentType.BUTTON});

	private ComponentType[] allowedClasses;
	
	EventType(ComponentType[] allowedClasses){
		this.allowedClasses = allowedClasses;
	}

	public static List<EventType> getAvailablesFor(ComponentType type){
		List<EventType> l = new ArrayList<EventType>();
		for(EventType e : values()){
			for(ComponentType cl : e.allowedClasses){
				if (cl == type && !l.contains(e)){
					l.add(e);
				}
			}
		}
		return l;
	}
}