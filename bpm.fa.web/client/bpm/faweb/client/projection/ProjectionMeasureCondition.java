package bpm.faweb.client.projection;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProjectionMeasureCondition implements IsSerializable {

	private List<String> memberUnames;
	private String formula;
	private boolean isDefault;
	
	public void addMemberUname(String uname) {
		if(memberUnames == null) {
			memberUnames = new ArrayList<String>();
		}
		memberUnames.add(uname);
	}

	public List<String> getMemberUnames() {
		if(memberUnames == null) {
			memberUnames = new ArrayList<String>();
		}
		return memberUnames;
	}

	public void setMemberUnames(List<String> memberUnames) {
		this.memberUnames = memberUnames;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}
	
}
