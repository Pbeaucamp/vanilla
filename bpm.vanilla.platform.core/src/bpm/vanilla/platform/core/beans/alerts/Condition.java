 package bpm.vanilla.platform.core.beans.alerts;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_alert_condition")
public class Condition implements Serializable {
	
	private static final long serialVersionUID = -9037863053221418857L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "left_op")
	private String leftOperand;
	
	@Column(name = "right_op")
	private String rightOperand;
	
	@Column(name = "operator")
	private String operator;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "alert_id")
	private int alertId;
	
	@Column(name = "condition_model", length = 99999)
	private String conditionModel;
	
	@Transient
	private IConditionInformation conditionObject;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLeftOperand() {
		return leftOperand;
	}
	public void setLeftOperand(String leftOperand) {
		this.leftOperand = leftOperand;
	}
	public String getRightOperand() {
		return rightOperand;
	}
	public void setRightOperand(String rightOperand) {
		this.rightOperand = rightOperand;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getAlertId() {
		return alertId;
	}
	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}
	public IConditionInformation getConditionObject() {
		return conditionObject;
	}
	public void setConditionObject(IConditionInformation conditionObject) {
		this.conditionObject = conditionObject;
	}
	public String getConditionModel() {
		return conditionModel;
	}
	public void setConditionModel(String conditionModel) {
		this.conditionModel = conditionModel;
	}
	

}
