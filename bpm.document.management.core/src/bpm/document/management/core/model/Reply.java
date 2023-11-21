package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Reply implements Serializable{

	private static final long serialVersionUID = 1L;

	public enum TypeReply {
		MAIL(0), 
		PHONE(1), 
		DOCUMENT(2),
		NORESPONSE(3);
		
		private int type;

		private static Map<Integer, TypeReply> map = new HashMap<Integer, TypeReply>();
		static {
			for (TypeReply type : TypeReply.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeReply(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeReply valueOf(int type) {
			return map.get(type);
		}
	}

	private int id;
	private String name;
	private String description;
	private TypeReply type;
	
	private Boolean askReceipt;
	private Boolean numericSign;
	
	private Date dateDeadline;
	
	private String value;
	private String copies;
	
	private boolean canBeDisplay = true;
	
	private Integer responseDocument;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		if(type == null) {
			return -1;
		}
		return type.getType();
	}
	
	public TypeReply getTypeReply() {
		return type;
	}

	public void setType(TypeReply type) {
		this.type = type;
	}

	public void setType(int type) {
		this.type = TypeReply.valueOf(type);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean isAskReceipt() {
		return askReceipt;
	}

	public void setAskReceipt(Boolean askReceipt) {
		this.askReceipt = askReceipt;
	}

	public Boolean isNumericSign() {
		return numericSign;
	}

	public void setNumericSign(Boolean numericSign) {
		this.numericSign = numericSign;
	}

	public Date getDateDeadline() {
		return dateDeadline;
	}

	public void setDateDeadline(Date dateDeadline) {
		this.dateDeadline = dateDeadline;
	}

	public String getCopies() {
		return copies;
	}

	public void setCopies(String copies) {
		this.copies = copies;
	}

	public boolean isCanBeDisplay() {
		return canBeDisplay;
	}

	public void setCanBeDisplay(boolean canBeDisplay) {
		this.canBeDisplay = canBeDisplay;
	}

	public Integer getResponseDocument() {
		return responseDocument;
	}

	public void setResponseDocument(Integer responseDocument) {
		this.responseDocument = responseDocument;
	}
}
