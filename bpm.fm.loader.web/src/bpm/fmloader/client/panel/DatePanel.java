package bpm.fmloader.client.panel;

import java.util.Date;

import bpm.fm.api.model.FactTable;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.images.ImageResources;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.table.MetricValuesPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DatePanel extends VerticalPanel {
	
	private HorizontalPanel mainPanel = new HorizontalPanel();
	private TextBox lstYears;
	private ListBox lstBiannual;
	private ListBox lstQuarter;
	private ListBox lstMonth;
	private ListBox lstDay;
	
	private MetricValuesPanel parent;
	
	private Image imgValid = new Image();
	
	public DatePanel(MetricValuesPanel metricValuesPanel) {
		super();	
		
		if(Constantes.mois == null) {
			Constantes.init();
		}
		
		this.parent = metricValuesPanel;		
		this.addStyleName("datasPagePanel");
		
		Label lblDate = new Label(Constantes.LBL.chooseDate());
		this.add(lblDate);
		this.setCellVerticalAlignment(lblDate, VerticalPanel.ALIGN_MIDDLE);
		
		this.add(mainPanel);
		
		VerticalPanel anneePanel = new VerticalPanel();
		
		anneePanel.add(new Label(Constantes.LBL.Year()));
		
		mainPanel.add(anneePanel);
		mainPanel.setSpacing(10);
		
		if(InfosUser.getInstance().getSelectedPeriode().equalsIgnoreCase(FactTable.PERIODICITY_YEARLY)) {
			lstYears = new TextBox();
			lstYears.setWidth("150px");
			Date d = new Date();
			int year = d.getYear() + 1900;
//			for( ; year > 1950 ; year--) {
//				lstYears.addItem(String.valueOf(year));
//			}
			anneePanel.add(lstYears);
		}
		else if(InfosUser.getInstance().getSelectedPeriode().equalsIgnoreCase(FactTable.PERIODICITY_BIANNUAL)) {
			lstYears = new TextBox();
			lstYears.setWidth("150px");
			Date d = new Date();
			int year = d.getYear() + 1900;
			anneePanel.add(lstYears);
			
			VerticalPanel monthPanel = new VerticalPanel();
			monthPanel.add(new Label(Constantes.LBL.Semester()));
			
			lstBiannual = new ListBox(false);
			lstBiannual.addItem(Constantes.LBL.First() + " " + Constantes.LBL.Semester(), ""+1);
			lstBiannual.addItem(Constantes.LBL.Second() + " " + Constantes.LBL.Semester(), ""+2);
			lstBiannual.setWidth("150px");
			monthPanel.add(lstBiannual);
			
			mainPanel.add(monthPanel);
		}
		else if(InfosUser.getInstance().getSelectedPeriode().equalsIgnoreCase(FactTable.PERIODICITY_QUARTERLY)) {
			lstYears = new TextBox();
			lstYears.setWidth("150px");
			Date d = new Date();
			int year = d.getYear() + 1900;
			anneePanel.add(lstYears);
			
			VerticalPanel monthPanel = new VerticalPanel();
			monthPanel.add(new Label(Constantes.LBL.Trimester()));
			
			lstQuarter = new ListBox(false);
			lstQuarter.setWidth("150px");
			lstQuarter.addItem(Constantes.LBL.First() + " " + Constantes.LBL.Semester(), ""+1);
			lstQuarter.addItem(Constantes.LBL.Second() + " " + Constantes.LBL.Semester(), ""+2);
			lstQuarter.addItem(Constantes.LBL.Third() + " " + Constantes.LBL.Semester(), ""+3);
			lstQuarter.addItem(Constantes.LBL.Fourth() + " " + Constantes.LBL.Semester(), ""+4);
			monthPanel.add(lstQuarter);
			
			mainPanel.add(monthPanel);
		}
		else if(InfosUser.getInstance().getSelectedPeriode().equalsIgnoreCase(FactTable.PERIODICITY_MONTHLY)) {
			lstYears = new TextBox();
			lstYears.setWidth("150px");
			Date d = new Date();
			int year = d.getYear() + 1900;
			anneePanel.add(lstYears);
			
			VerticalPanel monthPanel = new VerticalPanel();
			monthPanel.add(new Label(Constantes.LBL.Month()));
			
			lstMonth = new ListBox(false);
			lstMonth.setWidth("150px");
			for(int key : Constantes.mois.keySet()) {
				lstMonth.addItem(Constantes.mois.get(key), ""+key);
			}
			monthPanel.add(lstMonth);
			
			mainPanel.add(monthPanel);
		}
		else if(InfosUser.getInstance().getSelectedPeriode().equalsIgnoreCase(FactTable.PERIODICITY_WEEKLY)) {
			lstYears = new TextBox();
			lstYears.setWidth("150px");
			Date d = new Date();
			int year = d.getYear() + 1900;
			anneePanel.add(lstYears);
			
			VerticalPanel monthPanel = new VerticalPanel();
			monthPanel.add(new Label(Constantes.LBL.Month()));
			
			lstMonth = new ListBox(false);
			lstMonth.setWidth("150px");
			for(int key : Constantes.mois.keySet()) {
				lstMonth.addItem(Constantes.mois.get(key), ""+key);
			}
			monthPanel.add(lstMonth);
			
			VerticalPanel dayPanel = new VerticalPanel();
			
			dayPanel.add(new Label(Constantes.LBL.Day()));
			
			lstDay = new ListBox(false);
			lstDay.setWidth("150px");
			dayPanel.add(lstDay);
			fillLstDay(1);
			lstMonth.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event) {
					int numMois = Integer.parseInt(lstMonth.getValue(lstMonth.getSelectedIndex()));
					fillLstDay(numMois);
				}
			});
			
			mainPanel.add(monthPanel);
			mainPanel.add(dayPanel);
		}
		else if(InfosUser.getInstance().getSelectedPeriode().equalsIgnoreCase(FactTable.PERIODICITY_DAILY)) {
			lstYears = new TextBox();
			lstYears.setWidth("150px");
			Date d = new Date();
			int year = d.getYear() + 1900;
			anneePanel.add(lstYears);
			
			VerticalPanel monthPanel = new VerticalPanel();
			monthPanel.add(new Label(Constantes.LBL.Month()));
			
			lstMonth = new ListBox(false);
			lstMonth.setWidth("150px");
			for(int key : Constantes.mois.keySet()) {
				lstMonth.addItem(Constantes.mois.get(key), ""+key);
			}
			monthPanel.add(lstMonth);
			
			VerticalPanel dayPanel = new VerticalPanel();
			
			dayPanel.add(new Label(Constantes.LBL.Day()));
			lstDay = new ListBox(false);
			lstDay.setWidth("150px");
			dayPanel.add(lstDay);
			fillLstDay(1);
			lstMonth.addChangeHandler(new ChangeHandler(){
				public void onChange(ChangeEvent event) {
					int numMois = Integer.parseInt(lstMonth.getValue(lstMonth.getSelectedIndex()));
					fillLstDay(numMois);
				}
			});
			
			mainPanel.add(monthPanel);
			mainPanel.add(dayPanel);
		}
		
		
		
		imgValid.setResource(ImageResources.INSTANCE.apply());
		imgValid.setTitle(Constantes.LBL.Apply());
		mainPanel.add(imgValid);
		
		imgValid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				parent.createTable(getSelectedDate());
			}
		});
	}

	public Date getSelectedDate() {
		String dateString = "";
		Date d = new Date();
		d.setYear(Integer.parseInt(lstYears.getText()) - 1900);
		dateString += lstYears.getText();
		if(lstBiannual != null) {
			if(lstBiannual.getValue(lstBiannual.getSelectedIndex()).equalsIgnoreCase("1")) {
				d.setMonth(1);
				dateString += " - " + Constantes.LBL.First() + " " + Constantes.LBL.Semester();
			}
			else {
				d.setMonth(7);
				dateString += " - " + Constantes.LBL.Second() + " " + Constantes.LBL.Semester();
			}
		}
		else if(lstQuarter != null) {
			if(lstQuarter.getValue(lstQuarter.getSelectedIndex()).equalsIgnoreCase("1")) {
				d.setMonth(1);
				dateString += " - " + Constantes.LBL.First() + " " + Constantes.LBL.Semester();
			}
			else if(lstQuarter.getValue(lstQuarter.getSelectedIndex()).equalsIgnoreCase("2")){
				d.setMonth(4);
				dateString += " - " + Constantes.LBL.Second() + " " + Constantes.LBL.Semester();
			}
			else if(lstQuarter.getValue(lstQuarter.getSelectedIndex()).equalsIgnoreCase("3")){
				d.setMonth(7);
				dateString += " - " + Constantes.LBL.Third() + " " + Constantes.LBL.Semester();
			}
			else {
				d.setMonth(10);
				dateString += " - " + Constantes.LBL.Fourth() + " " + Constantes.LBL.Semester();
			}
		}
		else if(lstMonth != null){
			d.setMonth(Integer.parseInt(lstMonth.getValue(lstMonth.getSelectedIndex())) - 1);
			dateString += " - " + lstMonth.getItemText(lstMonth.getSelectedIndex());
		}
		if(lstDay != null) {
			d.setDate(Integer.parseInt(lstDay.getItemText(lstDay.getSelectedIndex())));
			dateString += " - " + lstDay.getItemText(lstDay.getSelectedIndex());
		}
		InfosUser.getInstance().setDateString(dateString);
		return d;
	}
	
	private void fillLstDay(int numMois) {
		lstDay.clear();
		if(numMois == 1 || numMois == 3 || numMois == 5 || numMois == 7 || numMois == 8 || numMois == 10 || numMois == 12) {
			for(int i = 1 ; i <= 31 ; i++) {
				lstDay.addItem(""+i);
			}
		}
		else if(numMois == 2) {
			if(!lstYears.getText().equalsIgnoreCase("") && Integer.parseInt(lstYears.getText()) % 4 == 0) {
				for(int i = 1 ; i <= 29 ; i++) {
					lstDay.addItem(""+i);
				}
			}
			else {
				for(int i = 1 ; i <= 29 ; i++) {
					lstDay.addItem(""+i);
				}
			}
		}
		else {
			for(int i = 1 ; i <= 30 ; i++) {
				lstDay.addItem(""+i);
			}
		}
	}
}
