package bpm.metadata;

import java.util.List;

import bpm.metadata.resource.IFilter;

public interface IFiltered {
	public void addFilter(String groupName, IFilter filter);
	public void removeFilter(String groupName, IFilter filter);
	public List<IFilter> getFilters();
	public void removeFilter(IFilter o);
	public boolean isFilterApplyedToGroup(String groupName, IFilter e);
}
