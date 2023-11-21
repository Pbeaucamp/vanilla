package bpm.sqldesigner.query.model.filter;

public class ColumnFilter {
	public static final String[] OPERATORS = new String[] { "=", "!=", ">",
			"<", ">=", "<=", "LIKE", " IS NULL", " IS NOT NULL" };
	private String name;
	private String value;
	private boolean needsApostrophe = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean needsApostrophe() {
		return needsApostrophe;
	}

	public void setNeedsApostrophe(boolean needsApostrophe) {
		this.needsApostrophe = needsApostrophe;
	}
}
