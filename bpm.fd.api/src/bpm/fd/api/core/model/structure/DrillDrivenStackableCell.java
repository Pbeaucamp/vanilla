package bpm.fd.api.core.model.structure;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.events.ElementsEventType;

public class DrillDrivenStackableCell extends StackableCell {
	public DrillDrivenStackableCell(String id, String name) {
		super(id, name + id);
		
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return super.getId().replace("cell_", "drillDrivenCell_");
	}


	public static boolean isSupportedAsController(IComponentDefinition def){
		if (def instanceof ComponentChartDefinition){
			return true;
		}
		else if (def instanceof ComponentFilterDefinition){
			FilterRenderer r = (FilterRenderer)((ComponentFilterDefinition)def).getRenderer();
			if (r.getRendererStyle() == FilterRenderer.RADIOBUTTON ||
				r.getRendererStyle() == FilterRenderer.DROP_DOWN_LIST_BOX ||
				r.getRendererStyle() == FilterRenderer.SLIDER
				){
				return true;
			}
		}
		return false;
	}
	
	
	public boolean addBaseElementToContent(IBaseElement element) {
		boolean b = super.addBaseElementToContent(element);
		
		if (element instanceof IComponentDefinition && b){
			DrillDrivenComponentConfig conf = new DrillDrivenComponentConfig(this, (IComponentDefinition)element);
			getConfigurations().put((IComponentDefinition)element, conf);				
		}
		else if (b){
			IComponentDefinition def = (IComponentDefinition)((IStructureElement)element).getContent().get(0);
			DrillDrivenComponentConfig conf = new DrillDrivenComponentConfig(this, def);
			getConfigurations().put(def, conf);		
		}
		return b;
			
	}

	
	public Element getElement() {
		Element e = DocumentHelper.createElement("drillDrivenStackableCell");
		e.addAttribute("id", getId().replace("stackabledrillDrivenCell_", ""));
		e.addAttribute("name", getName());
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
		
		
		for(IBaseElement baseE : getComponents()){
			if (baseE instanceof IStructureElement){
				e.add(baseE.getElement());
			}
			else if (baseE instanceof IComponentDefinition){
				Element ref = e.addElement("component-ref").addAttribute("name", ((IComponentDefinition)baseE).getName());
				ref.addAttribute("class", baseE.getClass().getName());
				
				DrillDrivenComponentConfig config = (DrillDrivenComponentConfig)getConfigurations().get((IComponentDefinition)baseE);
				if (config != null){
					for(ComponentParameter p : config.getParameters()){
						Element _e = ref.addElement("parameter").addAttribute("name", p.getName());
						_e.setText(config.getComponentNameFor(p));
						//generate second info on config(DataGrid.column) for the field
						//used by component providing multiple values like a DataGrid
						if (config.getComponentOutputName(p) != null ){
							_e.addAttribute("fieldNumber", "" + config.getComponentOutputName(p));
						}
						
						if (config.getController() != null){
							_e.addAttribute("controller-component-ref", "" + config.getController().getName());
						}
					}
				}
			}
			
		}
		
		return e;
	}

	public List<IComponentDefinition> getComponents() {
		List<IComponentDefinition> l  = new ArrayList<IComponentDefinition>();
		
		for(IBaseElement e : getContent()){
			if (e instanceof IComponentDefinition){
				l.add((IComponentDefinition)e);
			}
			else{
				for(IBaseElement ee : ((IStructureElement)e).getContent()){
					if (ee instanceof IComponentDefinition){
						l.add((IComponentDefinition)ee);
					}
				}
			}
		}
		
		
		return l;
	}




}
