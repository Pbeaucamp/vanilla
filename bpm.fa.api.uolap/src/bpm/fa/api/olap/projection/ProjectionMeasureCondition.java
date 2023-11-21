package bpm.fa.api.olap.projection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectionMeasureCondition implements Serializable {

	private List<String> memberUnames;
	private String formula;
	
	public void addMemberUname(String uname) {
		if(memberUnames == null) {
			memberUnames = new ArrayList<String>();
		}
		memberUnames.add(uname);
	}

	public List<String> getMemberUnames() {
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
	
	
	
}
