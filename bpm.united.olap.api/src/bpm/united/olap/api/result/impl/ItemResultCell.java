package bpm.united.olap.api.result.impl;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Member;

public class ItemResultCell extends ResultCellImpl {

	private String name;
	private String uname;
	private String caption;
	private ElementDefinition element;
	
	public ItemResultCell(String name, String uname, String caption) {
		this.name = name;
		this.uname = uname;
		this.caption = caption;
	}
	
	public ItemResultCell(ElementDefinition element) {
		name = element.getName();
		if(element instanceof Member) {
			uname = ((Member)element).getMemberRelationsUname();
		}
		else {
			uname = element.getUname();
		}
		caption = element.getCaption();
		this.element = element;
	}
	
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	public String getUname() {
		return uname;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

	@Override
	public String getHtml() {
		return "<td class=\"gridItem gridItemElement\">" + caption + "</td>\n";
	}

	@Override
	public String getType() {
		return "item";
	}

	public void setElement(ElementDefinition element) {
		this.element = element;
	}

	public ElementDefinition getElement() {
		return element;
	}
	
}
