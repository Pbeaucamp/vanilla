package bpm.faweb.shared.infoscube;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Calcul implements IsSerializable {
	private List<String> fields;
	private int operator;
	private float constant = 0.0f;
	private String orientation;
	private String title = "";
	
	
	public Calcul() {
		super();
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public float getConstant() {
		return constant;
	}

	public void setConstant(float constant) {
		this.constant = constant;
	}
	
	public void setConstant(String constant) {
		this.constant = Float.valueOf(constant.trim()).floatValue();
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public void setOperator(String operator) {
		this.operator = Integer.valueOf(operator).intValue();
	}
	
	public void addField(int i) {
		this.fields.add(i+"");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
