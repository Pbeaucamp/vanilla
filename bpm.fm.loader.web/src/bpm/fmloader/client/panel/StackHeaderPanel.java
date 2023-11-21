package bpm.fmloader.client.panel;

import bpm.fm.api.model.FactTable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class StackHeaderPanel extends AbsolutePanel {
	private static final String CSS_YEAR = "btnViewYear";
	private static final String CSS_SEM = "btnViewSem";
	private static final String CSS_QUARTER = "btnViewQuarter";
	private static final String CSS_MONTH = "btnViewMonth";
	private static final String CSS_WEEK = "btnViewWeek";
	private static final String CSS_DAY = "btnViewDay";
	private static final String CSS_LABEL = "lblStackHeader";
	private static final String CSS_CKALL = "ckAll";
	private static final String CSS_UNCKALL = "unckAll";
	
	private Label lbl;
	
	private FMDataPanel parent;
	
	private ToggleButton btnYear, btnSemestrial, btnQuarter, btnMonth, btnWeek, btnDay;
	
	private CheckBox ckAll, unckAll;
	
	public StackHeaderPanel(FMDataPanel parent, String tabText, boolean isReducible) {
		super();
		
		this.parent = parent;
		
		lbl = new Label(tabText);
		lbl.setWordWrap(false);
		lbl.addStyleName(CSS_LABEL);
		
		this.add(lbl);
		
//		if(isReducible) {
//		
//			ckAll = new CheckBox();
//			ckAll.addStyleName(CSS_CKALL);
//			ckAll.setValue(true);
//			ckAll.addClickHandler(handler);
//			
//			unckAll = new CheckBox();
//			unckAll.addStyleName(CSS_UNCKALL);
//			unckAll.setValue(false);
//			unckAll.addClickHandler(handler);
//			
//			btnYear = new ToggleButton(Constantes.LBL.year(), Constantes.LBL.year());
//			btnYear.setTitle(Constantes.LBL.year());
//			btnYear.addStyleName(CSS_YEAR);
////			btnYear.setDown(true);
//			
//			btnSemestrial = new ToggleButton("S", "S");
//			btnSemestrial.setTitle(Constantes.LBL.biannual());
//			btnSemestrial.addStyleName(CSS_SEM);
//			
//			btnQuarter = new ToggleButton("Q", "Q");
//			btnQuarter.setTitle(Constantes.LBL.quarter());
//			btnQuarter.addStyleName(CSS_QUARTER);
//			
//			btnMonth = new ToggleButton(Constantes.LBL.month(), Constantes.LBL.month());
//			btnMonth.setTitle(Constantes.LBL.month());
//			btnMonth.addStyleName(CSS_MONTH);
//			
//			btnWeek = new ToggleButton("W", "W");
//			btnWeek.setTitle(Constantes.LBL.week());
//			btnWeek.addStyleName(CSS_WEEK);
//			
//			btnDay = new ToggleButton(Constantes.LBL.day(), Constantes.LBL.day());
//			btnDay.setTitle(Constantes.LBL.day());
//			btnDay.addStyleName(CSS_DAY);
//	
//			this.add(ckAll);
//			this.add(unckAll);
//			this.add(btnDay);
////			this.add(btnWeek);
//			this.add(btnMonth);
////			this.add(btnQuarter);
////			this.add(btnSemestrial);
//			this.add(btnYear);
//			
//			btnYear.addClickHandler(handler);
//			btnSemestrial.addClickHandler(handler);
//			btnQuarter.addClickHandler(handler);
//			btnMonth.addClickHandler(handler);
//			btnWeek.addClickHandler(handler);
//			btnDay.addClickHandler(handler);
//		}
		
	}

	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			event.stopPropagation();
			Object btnClicked = event.getSource();
			
			if(btnClicked instanceof ToggleButton) {
				//Update button states
				if(!(btnClicked.equals(btnYear))) {
					btnYear.setDown(false);
				}
				
				if(!(btnClicked.equals(btnSemestrial))) {
					btnSemestrial.setDown(false);
				}
				
				if(!(btnClicked.equals(btnQuarter))) {
					btnQuarter.setDown(false);
				}
				
				if(!(btnClicked.equals(btnMonth))) {
					btnMonth.setDown(false);
				}
				
				if(!(btnClicked.equals(btnWeek))) {
					btnWeek.setDown(false);
				}
				
				if(!(btnClicked.equals(btnDay))) {
					btnDay.setDown(false);
				}
			}
			
			if(btnClicked.equals(ckAll)) {
				parent.checkUnCheckMetrics(true);
				ckAll.setValue(true);
			}
			
			if(btnClicked.equals(unckAll)) {
				parent.checkUnCheckMetrics(false);
				unckAll.setValue(false);
			}
			
			if(btnClicked instanceof ToggleButton) {
				((ToggleButton)btnClicked).setDown(true);
				parent.refreshMetricList(getSelectedPeriod());
			}
			
		}
	};

	public String getSelectedPeriod() {
		if(btnYear.isDown()) {
			return FactTable.PERIODICITY_YEARLY;
		}
		else if(btnSemestrial.isDown()) {
			return FactTable.PERIODICITY_BIANNUAL;
		}
		else if(btnQuarter.isDown()) {
			return FactTable.PERIODICITY_QUARTERLY;
		}
		else if(btnMonth.isDown()) {
			return FactTable.PERIODICITY_MONTHLY;
		}
		else if(btnWeek.isDown()) {
			return FactTable.PERIODICITY_WEEKLY;
		}
		else if(btnDay.isDown()) {
			return FactTable.PERIODICITY_DAILY;
		}
		return "";
	}
	
}
