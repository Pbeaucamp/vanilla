package bpm.freematrix.reborn.web.client.main.menu;

import bpm.freematrix.reborn.web.client.main.FreeMatrixMain;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FreeMatrixMenu extends Composite{

	private static FreeMatrixMenuUiBinder uiBinder = GWT
			.create(FreeMatrixMenuUiBinder.class);

	interface FreeMatrixMenuUiBinder extends UiBinder<Widget, FreeMatrixMenu> {
	}

	@UiField HTMLPanel panel;
	@UiField Button btnHome, btnAlert, btnCube, btnReport, btnDataViz;//btnStrat;//, btnSettings, btnSearch, btnWatchList, btnCollaboration;
	private FreeMatrixMain mainPanel;
	
	public FreeMatrixMenu(FreeMatrixMain freeMatrixMain) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = freeMatrixMain;
		setMenuActive(btnHome);
	}
	
	@UiHandler("btnHome")
	void onHome(ClickEvent e){
		setMenuActive(btnHome);
		mainPanel.showHome();
		mainPanel.showHideMenu();
	}

	@UiHandler("btnAlert")
	void onAlert(ClickEvent e){
		setMenuActive(btnAlert);
		mainPanel.showAlert();
		mainPanel.showHideMenu();
	}
	
	@UiHandler("btnDataViz")
	void onDataViz(ClickEvent e){
		setMenuActive(btnDataViz);
		mainPanel.showDataViz();
		mainPanel.showHideMenu();
	}
	
	@UiHandler("btnCube")
	void onCube(ClickEvent e){
		setMenuActive(btnCube);
		mainPanel.showCube();
		mainPanel.showHideMenu();
	}
	
	@UiHandler("btnReport")
	void onReport(ClickEvent e){
		setMenuActive(btnReport);
		mainPanel.showReport();
		mainPanel.showHideMenu();
	}
	
//	@UiHandler("btnStrat")
//	void onStrat(ClickEvent e){
//		setMenuActive(btnStrat);
//		mainPanel.showStrat();
//		mainPanel.showHideMenu();
//	}
	
//	@UiHandler("btnSettings")
//	void onSettings(ClickEvent e){
//		setMenuActive(btnSettings);
//	}
//	
//	@UiHandler("btnSearch")
//	void onSearch(ClickEvent e){
//		setMenuActive(btnSearch);
//	}
//	
//	@UiHandler("btnWatchList")
//	void onWatchList(ClickEvent e){
//		setMenuActive(btnWatchList);
//	}
//	
//	@UiHandler("btnCollaboration")
//	void onCollaboration(ClickEvent e){
//		setMenuActive(btnCollaboration);
//	}
	
	
	public void setMenuActive(Button menu){
		for(Widget w : panel){
			w.removeStyleName("iconsActive");
		}
		menu.addStyleName("iconsActive");
	}

	

}
