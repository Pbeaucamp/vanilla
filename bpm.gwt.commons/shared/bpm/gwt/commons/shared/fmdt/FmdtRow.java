package bpm.gwt.commons.shared.fmdt;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FmdtRow implements IsSerializable {

	public static enum Type {
		UNMODIFIED, UPDATED, ADDED, DELETED
	}

	private Type type = Type.UNMODIFIED;
	private List<String> values;
	private List<String> originalValues;
	public static final String LABEL = "label";
	public static final String VALUE = "value";
	private String typeRow = VALUE;
	private int level;

	public List<String> getValues() {
		if (values == null) {
			values = new ArrayList<String>();
		}
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<String> getOriginalValues() {
		return originalValues;
	}

	public void setOriginalValues(List<String> originalValues) {
		this.originalValues = originalValues;
	}

	public String getTypeRow() {
		return typeRow;
	}

	public void setTypeRow(String typeRow) {
		this.typeRow = typeRow;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
