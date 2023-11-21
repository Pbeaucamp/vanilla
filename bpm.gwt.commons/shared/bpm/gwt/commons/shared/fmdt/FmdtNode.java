package bpm.gwt.commons.shared.fmdt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FmdtNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String defaultChildId="";
	private String field = "";
	private String operator = "";
	private String value = "";
	private List<FmdtNode> childs = new ArrayList<FmdtNode>();
	private List<Score> content = new ArrayList<Score>();
	private String parentId="";

	public FmdtNode() {
	}

	public FmdtNode(String id, String field, String operator, String value) {
		super();
		this.id = id;
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public FmdtNode(String id, String field, String operator, String value, List<FmdtNode> childs, List<Score> content, String parentId) {
		super();
		this.id = id;
		this.field = field;
		this.operator = operator;
		this.value = value;
		this.childs = childs;
		this.content = content;
		this.parentId=parentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<FmdtNode> getChilds() {
		return childs;
	}

	public void setChilds(List<FmdtNode> childs) {
		this.childs = childs;
	}
	
	public void addChild(FmdtNode child) {
		this.childs.add(child);
	}

	public List<Score> getContent() {
		return content;
	}

	public void setContent(List<Score> content) {
		this.content = content;
	}
	
	public void addContent(Score score) {
		this.content.add(score);
	}
	
	public void addScore(String value, String count) {
		this.content.add(new Score(value, count));
	}

	public String getDefaultChildId() {
		return defaultChildId;
	}

	public void setDefaultChildId(String defaultChildId) {
		this.defaultChildId = defaultChildId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
