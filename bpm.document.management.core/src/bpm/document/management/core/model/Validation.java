package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Validation implements Serializable {

	public enum TypeValidation {
		COMMENT(0), APPROBATION(1), ALL(2), VALIDATIONONLY(3);

		private int type;

		private static Map<Integer, TypeValidation> map = new HashMap<Integer, TypeValidation>();
		static {
			for (TypeValidation type : TypeValidation.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeValidation(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeValidation valueOf(int type) {
			return map.get(type);
		}
	}

	private static final long serialVersionUID = 1L;
	private int id;
	private TypeValidation type;
	private Integer commentUserId;
	private Integer approbationUserId;
	private Integer validationUserId;
	private Date peremptionDate;
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeValidation getValidationType() {
		return type;
	}

	public void setValidationType(TypeValidation type) {
		this.type = type;
	}

	public Integer getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(Integer commentUserId) {
		this.commentUserId = commentUserId;
	}

	public Integer getApprobationUserId() {
		return approbationUserId;
	}

	public void setApprobationUserId(Integer approbationUserId) {
		this.approbationUserId = approbationUserId;
	}

	public Integer getValidationUserId() {
		return validationUserId;
	}

	public void setValidationUserId(Integer validationUserId) {
		this.validationUserId = validationUserId;
	}

	public Date getPeremptionDate() {
		return peremptionDate;
	}

	public void setPeremptionDate(Date peremptionDate) {
		this.peremptionDate = peremptionDate;
	}
	
	public int getType() {
		if(type == null) {
			return -1;
		}
		return type.getType();
	}
	
	public void setType(int type) {
		this.type = TypeValidation.valueOf(type);
	}

	
	
}
