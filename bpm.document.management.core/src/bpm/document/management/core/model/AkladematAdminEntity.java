package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AkladematAdminEntity<T extends IAdminDematObject> implements Serializable {

	public enum DocType {
		ACTE(0), 
		ARCHIVE(1), 
		PESV2(2), 
		CHORUS(3), 
		SIGN(4),
		COMPTA(5),
		AWS(6),
		MAIL(7);
		
		private int type;

		private static Map<Integer, DocType> map = new HashMap<Integer,DocType>();
		static {
	        for (DocType docType : DocType.values()) {
	            map.put(docType.getType(), docType);
	        }
	    }
		
		private DocType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static DocType valueOf(int docType) {
	        return map.get(docType);
	    }
	}
	
	public enum Status {
		NEW(0),
		PENDING(1),
		ASSIGNED(2), 
		VALIDATED(3), //Performed service (service fait) for Chorus
		REJECTED(4),
		PENDING_SUSPENSION(5),
		SUSPENDED(6),
		PAYMENT_ISSUED(7),
		SENT(8),
		SIGN(9);

		private int type;

		private static Map<Integer, Status> map = new HashMap<Integer, Status>();
		static {
	        for (Status statusType : Status.values()) {
	            map.put(statusType.getType(), statusType);
	        }
	    }
		
		private Status(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static Status valueOf(int statusType) {
	        return map.get(statusType);
	    }
	}
	
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private DocType type;
	private Status status;
	
	private Date dateCreation;
	private Date dateModification;
	
	private int idDoc;
	
	//This field is also used to put BergerLevrault ID when we get bills directly from the software (not Chorus Pro)
	private String pastellId;
	
	private String lastAction;
	private Date lastActionDate;
	
	private String model;
	
	private String cegidId;
	
	/*Transient*/
	private T object;

	public AkladematAdminEntity() {
		super();
		this.dateCreation = new Date();
	}

	public AkladematAdminEntity(String name, DocType type, int itemId, T object) {
		super();
		this.name = name;
		this.type = type;
		this.status = Status.NEW;
		this.dateCreation = new Date();
		this.dateModification = dateCreation;
		this.idDoc = itemId;
		setObject(object);
	}

	public AkladematAdminEntity(String name, DocType type, Date dateCreation, Date dateModification, String pastellId, String lastAction, Date lastActionDate, T object) {
		super();
		this.name = name;
		this.type = type;
		this.status = Status.NEW;
		this.dateCreation = dateCreation;
		this.dateModification = dateModification;
		this.pastellId = pastellId;
		this.lastAction = lastAction;
		this.lastActionDate = lastActionDate;
		setObject(object);
	}

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

	public DocType getDocType() {
		return type;
	}
	
	public int getType() {
		try {
			return type.getType();
		} catch (Exception e) {
			return 4; 
		}
	}

	public void setDocType(DocType type) {
		this.type = type;
	}
	
	public void setType(int type) {
		this.type = DocType.valueOf(type);
	}

	public Status getDocumentStatus() {
		return status;
	}
	
	public int getStatus() {
		try {
			return status.getType();
		} catch (Exception e) {
			return 9;
		}
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	public Date getDateModification() {
		return dateModification;
	}
	
	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public int getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(int idDoc) {
		this.idDoc = idDoc;
	}
	
	public String getPastellId() {
		return pastellId;
	}
	
	public void setPastellId(String pastellId) {
		this.pastellId = pastellId;
	}
	
	public String getLastAction() {
		return lastAction;
	}
	
	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}
	
	public Date getLastActionDate() {
		return lastActionDate;
	}
	
	public void setLastActionDate(Date lastActionDate) {
		this.lastActionDate = lastActionDate;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		object.setParent(this);
		this.object = object;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCegidId() {
		return cegidId;
	}

	public void setCegidId(String cegidId) {
		this.cegidId = cegidId;
	}
}
