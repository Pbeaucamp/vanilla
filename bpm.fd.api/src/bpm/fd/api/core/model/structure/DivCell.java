package bpm.fd.api.core.model.structure;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class DivCell extends Cell implements IStructureElement, IContainer {

	protected DivCell(String id, String name) {
		super(id, name);
		this.setSize(200, 200);
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("divcell");
		e.addAttribute("id", getId().substring("divcell_".length()));
		e.addAttribute("name", getName());
		
		e.addAttribute("rowSpan", getRowSpan() + "");
		e.addAttribute("colSpan", getColSpan() + "");
		
		e.addAttribute("x", getPosition().x + "");
		e.addAttribute("y", getPosition().y + "");
		e.addAttribute("width", getSize().x + "");
		e.addAttribute("height", getSize().y + "");
		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		
		for(IBaseElement baseE : getContent()){
			if (baseE instanceof IStructureElement){
				e.add(baseE.getElement());
			}
			else if (baseE instanceof IComponentDefinition){
				Element ref = e.addElement("component-ref").addAttribute("name", ((IComponentDefinition)baseE).getName());
				ref.addAttribute("class", baseE.getClass().getName());
				
				ComponentConfig config = getInternalConfigs().get((IComponentDefinition)baseE);
				if (config != null){
					for(ComponentParameter p : config.getParameters()){
						Element _e = ref.addElement("parameter").addAttribute("name", p.getName());
						_e.setText(config.getComponentNameFor(p));
						//generate second info on config(DataGrid.column) for the field
						//used by component providing multiple values like a DataGrid
						if (config.getComponentOutputName(p) != null ){
							_e.addAttribute("fieldNumber", "" + config.getComponentOutputName(p));
						}
					}
				}
			}
			
		}
		
		return e;
	}
	
	@Override
	public String getId() {
		return super.getId().replace("cell_", "divcell_");
	}
	
}
