package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemReportsGroup extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private List<PortailRepositoryItem> reports = new ArrayList<PortailRepositoryItem>();
	
	public PortailItemReportsGroup() { }

	public PortailItemReportsGroup(RepositoryItem item, String typeName) {
		super(item, typeName);
	}

	public List<PortailRepositoryItem> getReports() {
		return reports;
	}

	public void addReport(PortailRepositoryItem report) {
		if(reports == null) {
			this.reports = new ArrayList<PortailRepositoryItem>();
		}
		
		report.setParent(this);
		
		this.reports.add(report);
	}
}
