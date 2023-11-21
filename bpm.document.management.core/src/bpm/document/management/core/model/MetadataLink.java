package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MetadataLink implements Serializable {

	private static final long serialVersionUID = 1L;

	//It can only be one MetadataLink with the type Market
	public enum LinkType {
		DOCUMENT(0), 
		FOLDER(1),
		MARKET(2),
		DEED(3),
		CHORUS(4);
		
		private int type;

		private static Map<Integer, LinkType> map = new HashMap<Integer, LinkType>();
		static {
	        for (LinkType type : LinkType.values()) {
	            map.put(type.getType(), type);
	        }
	    }
		
		private LinkType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static LinkType valueOf(int type) {
	        return map.get(type);
	    }
	}

	private int id;
	private int itemId;
	private int formId;
	private LinkType type;
	private boolean applyToChild;
	
	private Form form;
	
	public MetadataLink() { }
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public LinkType getType() {
		return type;
	}

	public void setType(LinkType type) {
		this.type = type;
	}

	public boolean isApplyToChild() {
		return applyToChild;
	}
	
	public void setApplyToChild(boolean applyToChild) {
		this.applyToChild = applyToChild;
	}
	
	public Form getForm() {
		return form;
	}
	
	public void setForm(Form form) {
		this.form = form;
	}
}
