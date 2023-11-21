package bpm.freematrix.reborn.web.client.main;

import java.util.Date;

import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.freematrix.reborn.web.client.Bpm_freematrix_reborn_web;
import bpm.freematrix.reborn.web.client.ClientSession;
import bpm.freematrix.reborn.web.client.main.alert.AlertPanel;
import bpm.freematrix.reborn.web.client.main.cube.CubeView;
import bpm.freematrix.reborn.web.client.main.cube.ReportView;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.main.home.HomeView;
import bpm.freematrix.reborn.web.client.main.home.charts.StrategyChart;
import bpm.freematrix.reborn.web.client.main.menu.FreeMatrixMenu;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FreeMatrixMain extends Composite {

	private static FreeMatrixMainUiBinder uiBinder = GWT.create(FreeMatrixMainUiBinder.class);

	interface FreeMatrixMainUiBinder extends UiBinder<Widget, FreeMatrixMain> {
	}

	interface MyStyle extends CssResource {
		String fullScreen();
	}

	@UiField
	MyStyle style;

	@UiField
	public HTMLPanel headerPanel, menuPanel, contentPanel, middlePanel;

	private Bpm_freematrix_reborn_web mainPanel;

	private HomeView homeView;
	private AlertPanel alertPanel;

	private FreeMatrixHeader header;

	public FreeMatrixMain(Bpm_freematrix_reborn_web mainPanel, Integer themeId, Integer groupId, Date selectedDate, boolean viewer) {
		initWidget(uiBinder.createAndBindUi(this));

		boolean loadAtStartup = true;
		if (selectedDate != null) {
			loadAtStartup = true;
			ClientSession.getInstance().setDefaultDate(selectedDate);
		}

		
		header = new FreeMatrixHeader(this, themeId, groupId, loadAtStartup);
		headerPanel.add(header);

		menuPanel.add(new FreeMatrixMenu(this));

		homeView = new HomeView(this);
		contentPanel.add(homeView);
		
		headerPanel.setVisible(!viewer);
		if (viewer) {
			middlePanel.addStyleName(style.fullScreen());
		}

		this.mainPanel = mainPanel;
	}

	public void showHideMenu() {
		header.hideShow();
	}

	public void showHome() {
		contentPanel.clear();
		contentPanel.add(homeView);
	}

	public void showAlert() {
		contentPanel.clear();
		if (alertPanel == null) {
			alertPanel = new AlertPanel();
		}
		contentPanel.add(alertPanel);
	}

	public Bpm_freematrix_reborn_web getMainPanel() {
		return mainPanel;
	}

	public void showStrat() {
		contentPanel.clear();
		StrategyChart strat = new StrategyChart(ClientSession.getInstance().getObservatories());
		contentPanel.add(strat);
	}

	public void showCube() {
		contentPanel.clear();
		contentPanel.add(new CubeView());
	}

	public void showReport() {
		contentPanel.clear();
		contentPanel.add(new ReportView());
	}

	public void filterChange(Group group, Observatory obs, Theme theme) {
		Widget w = contentPanel.getWidget(0);
		if (w instanceof HomeView) {
			homeView.filterChange(group, obs, theme);
		}
		else if (w instanceof AlertPanel) {
			alertPanel.filterChange(group, obs, theme);
		}
		else if (w instanceof CubeView) {

		}

	}

	public void showDataViz() {
		contentPanel.clear();
		contentPanel.add(new DataVizPanel(this));
	}

}
