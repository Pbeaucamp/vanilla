package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchFacet implements Serializable {

	private static final long serialVersionUID = 1L;

	// For now it is always content
	private String field;
	private List<FacetValue> values;

	// The limit is used to limit the number of facet return (the one with the more count)
	// 0 or -1 is unlimited
	private int limitResult;

	public SearchFacet() {
	}

	public SearchFacet(String field, int limitResult) {
		this.field = field;
		this.limitResult = limitResult;
	}

	public String getField() {
		return field;
	}

	public void addFacetValue(FacetValue value) {
		if (values == null) {
			values = new ArrayList<FacetValue>();
		}
		values.add(value);
	}

	public List<FacetValue> getValues() {
		return values;
	}
}
