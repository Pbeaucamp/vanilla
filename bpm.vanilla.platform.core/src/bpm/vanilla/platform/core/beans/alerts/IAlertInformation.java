package bpm.vanilla.platform.core.beans.alerts;

import java.io.Serializable;

public interface IAlertInformation extends Serializable {
	
	public boolean equals(Object o);
	
	public String getSubtypeEventName();
	
	public String getSubtypeEventLabel();
	
}
