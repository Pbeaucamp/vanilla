package bpm.vanilla.platform.core.beans.fmdt;

import java.util.ArrayList;
import java.util.List;

public class FmdtFormula extends FmdtData{

	private String script;
	private String operator="None";
	private Boolean created =false;
	private List<String> dataStreamInvolved = new ArrayList<String>();
	
	private static final long serialVersionUID = 1L;
	
	public FmdtFormula(){}
	
	public FmdtFormula(String name, String description, String script, List<String> dataStreamInvolved) {
		super(name, name, description);
		this.script=script;
		this.dataStreamInvolved=dataStreamInvolved;
	}


	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<String> getDataStreamInvolved() {
		return dataStreamInvolved;
	}

	public void setDataStreamInvolved(List<String> dataStreamInvolved) {
		this.dataStreamInvolved = dataStreamInvolved;
	}

	public Boolean getCreated() {
		return created;
	}

	public void setCreated(Boolean created) {
		this.created = created;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtFormula other = (FmdtFormula) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		}
		else if (!created.equals(other.created))
			return false;
		if (dataStreamInvolved == null) {
			if (other.dataStreamInvolved != null)
				return false;
		}
		else if (!dataStreamInvolved.equals(other.dataStreamInvolved))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		}
		else if (!operator.equals(other.operator))
			return false;
		if (script == null) {
			if (other.script != null)
				return false;
		}
		else if (!script.equals(other.script))
			return false;
		return true;
	}
	
	
}
