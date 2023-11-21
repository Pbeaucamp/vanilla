package bpm.fmloader.client.table;

import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import bpm.fmloader.client.dto.ApplicationDTO;
import bpm.fmloader.client.dto.AssoMetricAppDTO;

public class ApplicationCell extends SimplePanel {
	
	private List<ApplicationDTO> applications;
	
	public ApplicationCell(List<ApplicationDTO> applications) {
		this.applications = applications;
		StringBuilder buf = new StringBuilder();
		for(ApplicationDTO ap : applications) {
			buf.append(ap.getName() + "_");
		}
		Label lbl = new Label(buf.toString().substring(0, buf.length() - 1));
		lbl.setWordWrap(false);
		lbl.addStyleName("labelTableau");
		lbl.setSize("100%", "100%");
		this.add(lbl);
	}

	public List<ApplicationDTO> getApplications() {
		return applications;
	}

	public void setApplications(List<ApplicationDTO> applications) {
		this.applications = applications;
	}

	public boolean isSameAsso(AssoMetricAppDTO asso) {
		
		for(ApplicationDTO app : applications) {
			boolean finded = false;
			for(ApplicationDTO appasso : asso.getApplications()) {
				if(appasso.getId() == app.getId()) {
					finded = true;
					break;
				}
			}
			if(!finded) {
				return false;
			}
		}
		
		return true;
	}
	
}
