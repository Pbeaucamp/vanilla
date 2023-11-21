package bpm.fd.api.core.model.components.definition;

import java.io.Serializable;
import java.util.List;

public interface IFilterable extends Serializable {

	public List<IComponentDataFilter> getFilter();
	
	public void addFilter(IComponentDataFilter filter);
	
	public void removeFilter(IComponentDataFilter filter);
}
