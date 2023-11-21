package bpm.fd.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.Dashboard;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.DashboardPage;
import bpm.fd.core.component.ImageComponent;
import bpm.fd.core.component.LabelComponent;
import bpm.fd.web.client.I18N.Labels;

public class DashboardCreationHelper {

	public static Dashboard buildDefaultDashboard(String name, String description) {
		ImageComponent logo = new ImageComponent();
		logo.setTitle(Labels.lblCnst.Logo());
		logo.setTop(25);
		logo.setLeft(50);
		
		LabelComponent title = new LabelComponent();
		title.setTitle("<b><font size=\"6\">" + Labels.lblCnst.DashboardTitle() + "</font></b><br>");
		title.setComment("Title label");
		title.setTop(25);
		title.setLeft(450);
		title.setWidth(340);
		
//		LabelComponent subtitle = new LabelComponent();
//		subtitle.setTitle(Labels.lblCnst.Subtitle());
//		subtitle.setTop(65);
//		subtitle.setLeft(450);
		
		List<DashboardComponent> components = new ArrayList<>();
//		components.add(logo);
		components.add(title);
//		components.add(subtitle);
		
		Dashboard dashboard = new Dashboard();
		dashboard.setName(name != null ? name : Labels.lblCnst.MyDashboard());
		dashboard.setDescription(description);
		dashboard.setComponents(components);
		
		DashboardPage page = new DashboardPage();
		page.setLabel(Labels.lblCnst.MyPage());
		dashboard.addPage(page);
		
		return dashboard;
	}
}
