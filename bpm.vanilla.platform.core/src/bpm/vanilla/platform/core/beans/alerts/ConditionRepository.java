package bpm.vanilla.platform.core.beans.alerts;




public class ConditionRepository implements IConditionInformation {

	private static final long serialVersionUID = -3806440683920367931L;

	//@Column(name = "right_op_type")
	private boolean rightOperandField;
	
	public boolean isRightOperandField() {
		return rightOperandField;
	}
	public void setRightOperandField(boolean rightOperandField) {
		this.rightOperandField = rightOperandField;
	}
	
	@Override
	public boolean equals(Object o) {
		return rightOperandField == ((ConditionRepository)o).isRightOperandField();
	}
}
