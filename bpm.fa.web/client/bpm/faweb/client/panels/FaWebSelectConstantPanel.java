package bpm.faweb.client.panels;

import java.util.ArrayList;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.utils.ForCalcul;
import bpm.faweb.shared.infoscube.Calcul;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class FaWebSelectConstantPanel extends HTMLPanel {
	private static final String CSS_MAIN_PANEL = "selectConstantMainPanel";
	private static final String CSS_BTN_VALID = "btnValid";
	private static final String CSS_BTN_CANCEL = "btnCancel";
	private static final String CSS_TEXT = "textBoxConstant";
	
	private MainPanel mainPanel;
	
	private TextBox constant;
	private Button valid, cancel; 
	private int operator;
	
	private boolean isDisplay = false;
	
	public FaWebSelectConstantPanel(MainPanel mainPanel, int type, int left, int top) {
		super("");
		this.mainPanel = mainPanel;
		this.operator = type;
		
		this.addStyleName(CSS_MAIN_PANEL);
		this.getElement().getStyle().setTop(top, Unit.PX);
		this.getElement().getStyle().setLeft(left, Unit.PX);
		
		constant = new TextBox();
		constant.addStyleName(CSS_TEXT);
		
		valid = new Button("OK");
		valid.addStyleName(CSS_BTN_VALID);
		valid.addClickHandler(clickHandler);
		
		cancel = new Button(FreeAnalysisWeb.LBL.Cancel());
		cancel.addStyleName(CSS_BTN_CANCEL);
		cancel.addClickHandler(clickHandler);
		
		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.add(valid);
		bottomPanel.add(cancel);

		
		if (type == ForCalcul.DIV) {
			this.add(new Label(FreeAnalysisWeb.LBL.SelDiv()));
		}
		else if (type == ForCalcul.MUL) {
			this.add(new Label(FreeAnalysisWeb.LBL.SelMul()));
		}
		this.add(constant);
		this.add(bottomPanel);
	}
	
	public void clearText(){
		this.constant.setText("");
	}
	
	private ClickHandler clickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(valid)){
				String cons = constant.getText();
				float f = -1;
				try {
					f = Float.parseFloat(cons);
				}
				catch (Exception e) {
					e.printStackTrace();
					f = -1;
				}
				
				if(f != -1){
					FaWebSelectConstantPanel.this.removeFromParent();
					setDisplay(false);
					calculate(f);
				}
			}
			else if(event.getSource().equals(cancel)){
				FaWebSelectConstantPanel.this.removeFromParent();
				setDisplay(false);
			}
		}
	};
	
	public void calculate(float constant) {
		if (mainPanel.getSelectedRow().size() == 1) {
			Calcul calcul = new Calcul();
			calcul.setOrientation(ForCalcul.ON_ROW);
			calcul.setFields(mainPanel.getSelectedRow());
			calcul.setOperator(operator);
			calcul.setConstant(constant);
			
			if(mainPanel.getDisplayPanel().isOverviewVisible()) {
				ForCalcul.distributiveCalcul(mainPanel, calcul, mainPanel.getDisplayPanel().getOverviewTab().getGridOverview());
				ForCalcul.distributiveCalcul(mainPanel, calcul, mainPanel.getGrid());
			}
			else {
				ForCalcul.distributiveCalcul(mainPanel, calcul, mainPanel.getGrid());
			}
			
			mainPanel.addCalcul(calcul);
			
			mainPanel.setSelectedRow(new ArrayList<String>());
			
		}
		else if (mainPanel.getSelectedCol().size() == 1) {
			Calcul calcul = new Calcul();
			calcul.setOrientation(ForCalcul.ON_COL);
			calcul.setFields(mainPanel.getSelectedCol());
			calcul.setOperator(operator);
			calcul.setConstant(constant);
			
			if(mainPanel.getDisplayPanel().isOverviewVisible()) {
				ForCalcul.distributiveCalcul(mainPanel, calcul, mainPanel.getDisplayPanel().getOverviewTab().getGridOverview());
				ForCalcul.distributiveCalcul(mainPanel, calcul, mainPanel.getGrid());
			}
			else {
				ForCalcul.distributiveCalcul(mainPanel, calcul, mainPanel.getGrid());
			}
			
			mainPanel.addCalcul(calcul);
			
			mainPanel.setSelectedCol(new ArrayList<String>());
		}
	
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public boolean isDisplay() {
		return isDisplay;
	}
}

