package bpm.united.olap.api.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExternalQueryIdentifier implements IExternalQueryIdentifier {

	private HashMap<String, String> selects;
	private List<String> wheres;
	
	@Override
	public HashMap<String, String> getSelectElements() {
		return selects;
	}

	@Override
	public List<String> getWhereElements() {
		return wheres;
	}
	
	public void setSelectElements(HashMap<String, String> selects) {
		this.selects = selects;
	}
	
	public void setWhereElements(List<String> wheres) {
		this.wheres = wheres;
	}
	
	public void addSelectElement(String level, String memberName) {
		if(selects == null) {
			selects = new HashMap<String, String>();
		}
		selects.put(level, memberName);
	}
	
	public void addWhereElement(String where) {
		if(wheres == null) {
			wheres = new ArrayList<String>();
		}
		wheres.add(where);
	}

}
