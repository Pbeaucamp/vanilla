package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//This class will be replacing Validation, ValidationStatus and Task in the future
public class ItemValidation implements Serializable {

	public enum ValidationStatus {
		UNVALIDATED(0), VALIDATED(1), INSTRUCTED(2), ASK_INSTRUCTION(3), ASK_VALIDATION(4), NEW(5), VALIDATED_AND_SIGNED(6);

		private int type;

		private static Map<Integer, ValidationStatus> map = new HashMap<Integer, ValidationStatus>();
		static {
			for (ValidationStatus statusType : ValidationStatus.values()) {
				map.put(statusType.getType(), statusType);
			}
		}

		private ValidationStatus(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static ValidationStatus valueOf(int statusType) {
			return map.get(statusType);
		}
	}

	public enum ItemType {
		MARKET(0), DEMAT(1);

		private int type;

		private static Map<Integer, ItemType> map = new HashMap<Integer, ItemType>();
		static {
			for (ItemType type : ItemType.values()) {
				map.put(type.getType(), type);
			}
		}

		private ItemType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static ItemType valueOf(int type) {
			return map.get(type);
		}
	}

	private static final long serialVersionUID = 1L;

	private int id;
	private ValidationStatus status;
	private ItemType itemType;

	private int askUserId;
	private int validatorUserId;

	private Date creationDate;
	private Date modificationDate;
	private Date dueDate;
	
	private String commentary;

	private int itemId;
	
	public ItemValidation() { }

	public ItemValidation(int itemId, int askUserId, ValidationStatus status, ItemType itemType) {
		this.itemId = itemId;
		this.askUserId = askUserId;
		this.status = status;
		this.itemType = itemType;
		this.creationDate = new Date();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ValidationStatus getStatus() {
		return status;
	}

	public void setStatus(ValidationStatus status) {
		this.status = status;
		this.modificationDate = new Date();
	}
	
	public ItemType getItemType() {
		return itemType;
	}
	
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public int getAskUserId() {
		return askUserId;
	}

	public void setAskUserId(int askUserId) {
		this.askUserId = askUserId;
	}

	public int getValidatorUserId() {
		return validatorUserId;
	}

	public void setValidatorUserId(int validatorUserId) {
		this.validatorUserId = validatorUserId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public String getCommentary() {
		return commentary;
	}
	
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}
}
