package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.shared.beans.Document;
import bpm.es.web.shared.beans.NameDateItem;
import bpm.es.web.shared.beans.Vaccination;
import bpm.es.web.shared.beans.Document.TypeDocument;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AbsenceMovementsPanel extends Composite {

	private static AbsenceMovementsPanelUiBinder uiBinder = GWT.create(AbsenceMovementsPanelUiBinder.class);

	interface AbsenceMovementsPanelUiBinder extends UiBinder<Widget, AbsenceMovementsPanel> {
	}
	
	@UiField(provided=true)
	NameDateGrid gridHospitalisations, gridFugues, gridVacations, gridSequentialTime, griddDistancingStay;

	public AbsenceMovementsPanel() {
		gridHospitalisations = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.ArrivalDate(), Labels.lblCnst.DepartureDate());
		gridFugues = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.ArrivalDate(), Labels.lblCnst.DepartureDate());
		gridVacations = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.ArrivalDate(), Labels.lblCnst.DepartureDate());
		gridSequentialTime = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.ArrivalDate(), Labels.lblCnst.DepartureDate());
		griddDistancingStay = new NameDateGrid(false, Labels.lblCnst.Name(), Labels.lblCnst.ArrivalDate(), Labels.lblCnst.DepartureDate());
		initWidget(uiBinder.createAndBindUi(this));
		
		createContent();
	}
	
	private void createContent() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
		
		NameDateItem hos1 = new NameDateItem();
		hos1.setName(Labels.lblCnst.Hos1());
		hos1.setDate(dtf.parse("25/11/2013"));
		hos1.setDate2(dtf.parse("28/11/2013"));

		List<NameDateItem> hos = new ArrayList<>();
		hos.add(hos1);
		
		gridHospitalisations.loadItems(hos);
		

		
//		NameDateItem hos1 = new NameDateItem();
//		hos1.setName(Labels.lblCnst.Hos1());
//		hos1.setDate(dtf.parse("11/02/2004"));
//		hos1.setDate2(dtf.parse("12/06/2019"));
//
//		List<NameDateItem> hos = new ArrayList<>();
//		hos.add(hos1);
//		
//		gridFugues.loadItems(hos);
		

		
		NameDateItem vac1 = new NameDateItem();
		vac1.setName(Labels.lblCnst.Vacancy1());
		vac1.setDate(dtf.parse("01/07/2014"));
		vac1.setDate2(dtf.parse("08/07/2014"));
		
		NameDateItem vac2 = new NameDateItem();
		vac2.setName(Labels.lblCnst.Vacancy2());
		vac2.setDate(dtf.parse("05/02/2015"));
		vac2.setDate2(dtf.parse("15/02/2015"));

		List<NameDateItem> vacs = new ArrayList<>();
		vacs.add(vac1);
		vacs.add(vac2);
		
		gridVacations.loadItems(vacs);
		
	}
}
