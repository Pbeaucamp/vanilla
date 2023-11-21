package bpm.vanilla.platform.core.beans.alerts;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * Action when an alert is triggered
 * 
 * @author vanilla
 * 
 */

@Entity
@Table (name = "rpy_alert_action")
public class Action implements Serializable {

	private static final long serialVersionUID = -8853664181032575164L;

	public enum TypeAction {
		MAIL("Mail"),
		WORKFLOW("Workflow"),
		GATEWAY("Gateway");
		
		
		private String name;
		
		private TypeAction(String name) {
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
	
	@Column(name = "alert_id")
	private int alertId;
	
	@Column(name = "action_type")
	@Enumerated(EnumType.STRING)
	private TypeAction actionType;
	
	@Column(name = "action_model", length = 99999)
	private String actionModel;
	
	@Transient
	private IActionInformation actionObject;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public TypeAction getActionType() {
		return actionType;
	}

	public void setActionType(TypeAction actionType) {
		this.actionType = actionType;
	}

	public IActionInformation getActionObject() {
		return actionObject;
	}

	public void setActionObject(IActionInformation actionObject) {
		this.actionObject = actionObject;
	}

	public String getActionModel() {
		return actionModel;
	}

	public void setActionModel(String actionModel) {
		this.actionModel = actionModel;
	}

}