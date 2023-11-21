package bpm.vanilla.platform.core.beans.comments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rpy_comment_definition")
public class CommentDefinition implements Serializable {
	
	private static final long serialVersionUID = -7046521745433456625L;
	
	public static final String ITEM_ID_PARAMETER = "_ReportComment_ItemId_";
	public static final String REP_ID_PARAMETER = "_ReportComment_RepId_";
	public static final String DISPLAY_COMMENTS_PARAMETER = "_DisplayComments_";

	public enum TypeComment {
		VALIDATION(0), RESTITUTION(1), NORMAL(2);

		private int type;
		
		private static Map<Integer, TypeComment> map = new HashMap<Integer, TypeComment>();
		static {
			for (TypeComment type : TypeComment.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeComment(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeComment valueOf(int type) {
			return map.get(type);
		}
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "itemId")
	private int itemId;

	@Column(name = "name")
	private String name;

	@Column(name = "label")
	private String label;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private TypeComment type;

	@Column(name="is_limit")
	private boolean isLimit;

	@Column(name="nb_comments")
	private int nbComments;

	@Transient
	private List<CommentParameter> parameters;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public TypeComment getType() {
		return type;
	}

	public void setType(TypeComment type) {
		this.type = type;
	}

	public boolean isLimit() {
		return isLimit;
	}
	
	public void setLimit(boolean isLimit) {
		this.isLimit = isLimit;
	}

	public int getNbComments() {
		return nbComments;
	}

	public void setNbComments(int nbComments) {
		this.nbComments = nbComments;
	}

	public List<CommentParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<CommentParameter> parameters) {
		this.parameters = parameters != null ? new ArrayList<CommentParameter>(parameters) : null;
	}
}
