package bpm.fd.api.core.model.structure;

import java.awt.Point;
import java.awt.Rectangle;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class StackableCell extends Cell implements IStructureElement, IContainer {

	public static final int BUTTONS_BOTTOM = 0;
	
	public static final int BUTTONS_LEFT = 1;
	public static final int BUTTONS_RIGHT = 2;
	public static final int BUTTONS_TOP = 3;
	public static final String[] BUTTONS_TYPES = {"bottom", "left", "right", "top"};	
	
	private String type = "bottom";
	
	public StackableCell(String id, String name) {
		super(id, name);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return super.getId().replace("cell_", "stackablecell_");
	}
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("stackablecell");
		e.addAttribute("id", getId().substring("stackablecell_".length()));
		e.addAttribute("name", getName());
		
		e.addAttribute("rowSpan", getRowSpan() + "");
		e.addAttribute("colSpan", getColSpan() + "");
		e.addAttribute("type", type);
		
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

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		String old = getType();
		this.type = type;
		
		firePropertyChange(EVENT_LAYOUT, old, getType());
	}
	
	
	
	private Rectangle computeClientArea(){
		//compute content size
		int curType = -1;
		for(int i = 0; i < StackableCell.BUTTONS_TYPES.length; i++){
			if (StackableCell.BUTTONS_TYPES[i].equals(getType())){
				curType = i;
			}
		}
		Point pos = null;
		Point childSz = null;
		
		switch(curType){
		case BUTTONS_BOTTOM:
			childSz = new Point(getSize().x, getSize().y - 20);
			pos = new Point(getPosition().x, getPosition().y);
			break;
		case BUTTONS_LEFT:
			childSz = new Point(getSize().x - 20, getSize().y);
			pos = new Point(getPosition().x + 20, getPosition().y);
			break;
		case BUTTONS_RIGHT:
			childSz = new Point(getSize().x, getSize().y);
			pos = new Point(getPosition().x - 20, getPosition().y);
			break;
		case BUTTONS_TOP:
			childSz = new Point(getSize().x, getSize().y - 20);
			pos = new Point(getPosition().x, getPosition().y + 20);
			break;
		}
		
		return new Rectangle(pos.x, pos.y, childSz.x, childSz.y);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		
		
		for(IBaseElement e : getContent()){
			if (e instanceof Cell){
				Rectangle r = computeClientArea();
				((Cell)e).setPosition(r.x, r.y);
				((Cell)e).setSize(r.width, r.height);
			}
		}
	}
	
	@Override
	public boolean removeFromContent(IStructureElement element) {
		return super.removeFromContent(element);
	}
	@Override
	public boolean removeBaseElementToContent(IBaseElement element) {
		boolean b =  super.removeBaseElementToContent(element);
		if (b && element instanceof Cell){
			for(IBaseElement e : ((Cell)element).getContent()){
				getParentStructureElement().firePropertyChange(P_CONTENT_REMOVED, null, e);
			}
		}
		return b;
	}

	@Override
	public boolean addToContent(IStructureElement element) {
		
		boolean b =  addBaseElementToContent((IComponentDefinition)element.getContent().get(0));
		if (b && element instanceof Cell){
			Rectangle r = computeClientArea();
			((Cell)element).setPosition(r.x, r.y);
			((Cell)element).setSize(r.width, r.height);
			
			for(IBaseElement e : ((Cell)element).getContent()){
				if (getParentStructureElement() != null){
					getParentStructureElement().firePropertyChange(P_CONTENT_ADDED, null, e);
				}
				
			}
		}
		return b;
	}
}
