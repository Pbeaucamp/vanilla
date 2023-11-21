package bpm.fmloader.client.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AssoMetricAppDTO implements IsSerializable {

	private int assoId;
	private MetricDTO metric;
	private List<ApplicationDTO> applications;
	
	public AssoMetricAppDTO() {
		super();
	}
	public int getAssoId() {
		return assoId;
	}
	public void setAssoId(int assoId) {
		this.assoId = assoId;
	}
	public MetricDTO getMetric() {
		return metric;
	}
	public void setMetric(MetricDTO metric) {
		this.metric = metric;
	}
	public List<ApplicationDTO> getApplications() {
		return applications;
	}
	public void setApplications(List<ApplicationDTO> applications) {
		this.applications = applications;
	}
	public void addApplication(ApplicationDTO app) {
		if(applications == null) {
			applications = new ArrayList<ApplicationDTO>();
		}
		applications.add(app);
	}
	public String getApplicationNames() {
		
		Collections.sort(applications, new Comparator<ApplicationDTO>() {
			@Override
			public int compare(ApplicationDTO o1, ApplicationDTO o2) {
				return o1.getId() - o2.getId();
			}
		});
		
		StringBuilder buf = new StringBuilder();
		for(ApplicationDTO dto : applications) {
			buf.append(dto.getName() + "_");
		}
		
		return buf.toString().substring(0, buf.length() - 1);
	}
	
	
}
