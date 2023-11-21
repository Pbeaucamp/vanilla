package bpm.vanilla.platform.core.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportsGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Integer> reports;
	
	public ReportsGroup() { }

	public List<Integer> getReports() {
		return reports;
	}
	
	public void addReport(Integer reportId) {
		if(reports == null) {
			reports = new ArrayList<Integer>();
		}
		reports.add(reportId);
	}
}
