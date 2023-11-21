package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.shared.beans.Allergy;
import bpm.es.web.shared.beans.Appointment;
import bpm.es.web.shared.beans.NameDateItem;
import bpm.es.web.shared.beans.Vaccination;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HealthPanel extends Composite {

	private static HealthPanelUiBinder uiBinder = GWT.create(HealthPanelUiBinder.class);

	interface HealthPanelUiBinder extends UiBinder<Widget, HealthPanel> {
	}
	
	@UiField(provided=true)
	NameDateGrid gridAllergies, gridVaccinations, gridAppointment, gridVigilance;

	public HealthPanel() {
		gridAllergies = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.DetectionDate(), null);
		gridVaccinations = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.LastVaccination(), Labels.lblCnst.Rappel());
		gridAppointment = new NameDateGrid(true, Labels.lblCnst.Appointment(), Labels.lblCnst.Date(), null);
		gridVigilance = new NameDateGrid(false, Labels.lblCnst.Comment(), Labels.lblCnst.Date(), null);
		initWidget(uiBinder.createAndBindUi(this));
		
		createContent();
	}
	
	private void createContent() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
		DateTimeFormat dtfh = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");
		
		Vaccination vac1 = new Vaccination();
		vac1.setName(Labels.lblCnst.Vac1());
		vac1.setDate(dtf.parse("11/02/2004"));
		vac1.setDate2(dtf.parse("12/06/2019"));
		
		Vaccination vac2 = new Vaccination();
		vac2.setName(Labels.lblCnst.Vac2());
		vac2.setDate(dtf.parse("11/02/2004"));
		vac2.setDate2(dtf.parse("27/11/2016"));
		
		Vaccination vac3 = new Vaccination();
		vac3.setName(Labels.lblCnst.Vac3());
		vac3.setDate(dtf.parse("05/01/2005"));
		
		Vaccination vac4 = new Vaccination();
		vac4.setName(Labels.lblCnst.Vac4());
		vac4.setDate(dtf.parse("22/05/2006"));

		List<NameDateItem> vacs = new ArrayList<>();
		vacs.add(vac1);
		vacs.add(vac2);
		vacs.add(vac3);
		vacs.add(vac4);
		
		gridVaccinations.loadItems(vacs);
		

		Allergy allergy1 = new Allergy();
		allergy1.setName(Labels.lblCnst.All1());

		List<NameDateItem> alls = new ArrayList<>();
		alls.add(allergy1);
		
		gridAllergies.loadItems(alls);
		
		
		Appointment app1 = new Appointment();
		app1.setName("Rendez-vous Docteur DUMARAIS");
		app1.setDate(dtfh.parse("11:30 05/07/2016"));

		List<NameDateItem> apps = new ArrayList<>();
		apps.add(app1);
		
		gridAppointment.loadItems(apps);
	}
}
