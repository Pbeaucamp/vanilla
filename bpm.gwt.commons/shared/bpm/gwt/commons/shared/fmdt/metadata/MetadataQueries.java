package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataQueries implements IsSerializable {
	
	private MetadataPackage parent;
	private List<MetadataQuery> queries;
	
	public MetadataQueries() { }

	public MetadataQueries(List<MetadataQuery> queries) {
		setQueries(queries);
	}
	
	public MetadataPackage getParent() {
		return parent;
	}
	
	public void setParent(MetadataPackage parent) {
		this.parent = parent;
	}
	
	public List<MetadataQuery> getQueries() {
		return queries;
	}
	
	public void setQueries(List<MetadataQuery> queries) {
		this.queries = queries;
		if (queries != null) {
			for (MetadataQuery query : queries) {
				query.setParent(this);
			}
		}
	}
	
	public void addQuery(MetadataQuery query) {
		if (queries == null) {
			this.queries = new ArrayList<MetadataQuery>();
		}
		query.setParent(this);
		this.queries.add(query);
	}
	
	@Override
	public String toString() {
		return "Queries";
	}
}
