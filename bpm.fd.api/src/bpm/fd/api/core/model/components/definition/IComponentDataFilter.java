package bpm.fd.api.core.model.components.definition;

import java.io.Serializable;


public interface IComponentDataFilter extends Serializable {

	public boolean isSatisfied(Object value);
}
