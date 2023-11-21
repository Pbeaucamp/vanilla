package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;

public interface IRule extends Serializable {

	public boolean match(IRule rule);
}
