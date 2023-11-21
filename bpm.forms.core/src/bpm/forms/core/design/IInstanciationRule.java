package bpm.forms.core.design;

import java.util.List;

/**
 * interface to define the instanciation mode of an IForm
 * 
 * if the Mode is Mode.SCHEDULED, the groups are requested to know for which group the FormInstance is created
 * 
 * @author ludo
 *
 */
public interface IInstanciationRule {
	public static enum Mode{
		MANUAL, SCHEDULED;
	}
	
	public static enum ScheduledType{
		DAY, WEEK, MONTH 
	}

	public long getId();
	
	public Mode getMode();
	
	public void setMode(Mode mode);
	
	
	public void setScheduledType(ScheduledType scheduledType) ;
	
	public ScheduledType getScheduledType() ;
	
	public List<Integer> getGroupId();

	public void addGroupId(Integer groupId);

	public void removeGroupId(Integer groupId);

	public void setUniqueIp(boolean uniqueIp);

	public boolean isUniqueIp();

	public void setFormId(long id);

	public long getFormId();
	
	
}
