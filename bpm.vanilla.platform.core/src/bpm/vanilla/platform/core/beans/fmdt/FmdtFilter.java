package bpm.vanilla.platform.core.beans.fmdt;

import java.util.List;

public class FmdtFilter extends FmdtData {

	private static final long serialVersionUID = 1L;

	private String type;
	private String query;
	private boolean granted = false;
	private FmdtColumn column;
	private String operator;
	private List<String> values;
	private boolean create = false;

	public FmdtFilter(String name, String label, String description, String query, Boolean granted) {
		super(name, label, description);
		this.query = query;
		this.granted = granted;
	}

	public FmdtFilter(String name, String label, String description, FmdtColumn column, String operator, List<String> values, String type) {
		super(name, label, description);
		this.column = column;
		this.operator = operator;
		this.values = values;
		this.type = type;
	}

	public FmdtFilter(String name, String label, String description, String query, FmdtColumn column, String type) {
		super(name, label, description);
		this.query = query;
		this.column = column;
		this.type = type;
	}

	public FmdtFilter() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FmdtColumn getColumn() {
		return column;
	}

	public void setColumn(FmdtColumn column) {
		this.column = column;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void addValue(String value) {
		this.values.add(value);
	}

	public void removeValue(String value) {
		this.values.remove(value);
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtFilter other = (FmdtFilter) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		}
		else if (!column.equals(other.column))
			return false;
		if (create != other.create)
			return false;
		if (granted != other.granted)
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		}
		else if (!operator.equals(other.operator))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		}
		else if (!query.equals(other.query))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		}
		else if (!values.equals(other.values))
			return false;
		return true;
	}

}
