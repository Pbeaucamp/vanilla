package bpm.vanilla.platform.core.beans.alerts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;

/**
 * This class is an abstract class to define an alert in a repository environment
 * 
 * @author vanilla
 *
 */

@Entity
@Table (name = "rpy_alert_definition")
public class Alert implements Serializable{

	private static final long serialVersionUID = -8027909106782689644L;
	
	public enum TypeEvent {
		SYSTEM_TYPE("System Alert"), OBJECT_TYPE("Object Alert"), KPI_TYPE("Kpi Alert");
		
		private String name;
		
		private TypeEvent(String name) {
			this.name = name;
		}
		
		public String getLabel() {
			return this.name;
		}
	}
	
	

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "alert_name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "type_event")
	@Enumerated(EnumType.STRING)
	private TypeEvent typeEvent;
	
	@Column(name = "event_model", length = 99999)
	private String eventModel;
	
	@Transient
	private IAlertInformation eventObject;
	
	@Transient
	private Action action;
	
	
	
	@Column(name = "state")
	private int state;
	
	@Column(name = "type_action")
	@Enumerated(EnumType.STRING)
	private TypeAction typeAction;
	
	//This is used by an alert for an object
	
	@Column(name = "dataprovider_id")
	private int dataProviderId;
	
	@Transient
	private List<Condition> conditions = new ArrayList<Condition>();
	
	@Column(name = "operator")
	private String operator;
	
	public Alert() {
		super();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
	public void setTypeAction(TypeAction typeAction) {
		this.typeAction = typeAction;
	}
	
	public TypeAction getTypeAction() {
		return typeAction;
	}
	public int getDataProviderId() {
		return dataProviderId;
	}
	
	public void setDataProviderId(int dataProviderId) {
		this.dataProviderId = dataProviderId;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public List<Condition> getConditions() {
		if(conditions == null){
			conditions = new ArrayList<Condition>();
		}
		return conditions;
	}
	
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public void addCondition(Condition condition) {
		if(conditions == null){
			conditions = new ArrayList<Condition>();
		}
		conditions.add(condition);
	}
	
	public boolean removeCondition(Condition c) {
		return conditions.remove(c);
	}

	public TypeEvent getTypeEvent() {
		return typeEvent;
	}

	public void setTypeEvent(TypeEvent typeEvent) {
		this.typeEvent = typeEvent;
	}

	public IAlertInformation getEventObject() {
		return eventObject;
	}

	public void setEventObject(IAlertInformation eventObject) {
		this.eventObject = eventObject;
	}

	public String getEventModel() {
		return eventModel;
	}

	public void setEventModel(String eventModel) {
		this.eventModel = eventModel;
	}
}
