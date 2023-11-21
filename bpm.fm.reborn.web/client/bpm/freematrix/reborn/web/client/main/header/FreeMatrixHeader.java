package bpm.freematrix.reborn.web.client.main.header;

import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.freematrix.reborn.web.client.main.FreeMatrixMain;
import bpm.gwt.commons.client.free.metrics.FMFilterPanel;
import bpm.gwt.commons.client.free.metrics.IFilterChangeHandler;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FreeMatrixHeader extends Composite implements IFilterChangeHandler {
	
	private static FreeMatrixHeader instance;

	private static FreeMatrixHeaderUiBinder uiBinder = GWT
			.create(FreeMatrixHeaderUiBinder.class);

	interface FreeMatrixHeaderUiBinder extends
			UiBinder<Widget, FreeMatrixHeader> {
	}

	@UiField HTMLPanel panelFilter;
	
	private FreeMatrixMain freeMatrixMain;
	private boolean isMenuHidden = false;
	
	private FMFilterPanel filterPanel;

	public FreeMatrixHeader(FreeMatrixMain freeMatrixMain, Integer themeId, Integer groupId, boolean loadAtStartup) {
		initWidget(uiBinder.createAndBindUi(this));
		this.freeMatrixMain = freeMatrixMain;
		instance = this;
		
		filterPanel = new FMFilterPanel(this, loadAtStartup, themeId, groupId);
		panelFilter.add(filterPanel);
	}
	
	public static FreeMatrixHeader getInstance() {
		return instance;
	}


	@UiHandler("btnMenu")
	public void onClick(ClickEvent e) {
		hideShow();
	}
	
	public void hideShow() {
		if(isMenuHidden){
			freeMatrixMain.menuPanel.setWidth("0%");
//			freeMatrixMain.contentPanel.getElement().getStyle().setLeft(0, Unit.PX);
			isMenuHidden = false;
		}else{
			freeMatrixMain.menuPanel.setWidth("5%");
//			freeMatrixMain.contentPanel.getElement().getStyle().setLeft(5, Unit.PCT);
			isMenuHidden = true;
		}
	}
	
	@UiHandler("btnLogout")
	public void onLogout(ClickEvent e){
		freeMatrixMain.getMainPanel().onLogout();
	}

	@Override
	public void selectionChanged(Group group, Observatory obs, Theme theme) {
		freeMatrixMain.filterChange(group, obs, theme);
		
	}
	
	public int getSelectedGroup() {
		return filterPanel.getSelectedGroup().getId();
	}
	
	public int getSelectedObs() {
		Observatory obs = filterPanel.getSelectedObservatory();
		if(obs != null) {
			return obs.getId();
		}
		return -1; 
	}
	
	public int getSelectedTheme() {
		Theme obs = filterPanel.getSelectedTheme();
		if(obs != null) {
			return obs.getId();
		}
		return -1; 
	}
	
	public Theme getSelectedThemeObject() {
		return filterPanel.getSelectedTheme();
	}
	
	public Observatory getSelectedObsObject() {
		return filterPanel.getSelectedObservatory();
	}
	
	public Group getSelectedGroupObject() {
		return filterPanel.getSelectedGroup();
	}

	public List<Observatory> getObservatoriesForSelection() {
		return filterPanel.getObservatories();
	}
	
	public boolean isAllowed(Metric metric) {
		
		if(FreeMatrixHeader.getInstance().getSelectedThemeObject() != null) {
			for(Metric m : FreeMatrixHeader.getInstance().getSelectedThemeObject().getMetrics()) {
				if(m.getId() == metric.getId()) {
					return true;
				}
			}
		}
		else {
			if(FreeMatrixHeader.getInstance().getSelectedObsObject() != null) {
				for(Theme th : FreeMatrixHeader.getInstance().getSelectedObsObject().getThemes()) {
					for(Metric m : th.getMetrics()) {
						if(m.getId() == metric.getId()) {
							return true;
						}
					}
				}
			}
			else {
				for(Observatory obs : FreeMatrixHeader.getInstance().getObservatoriesForSelection()) {
					for(Theme th : obs.getThemes()) {
						for(Metric m : th.getMetrics()) {
							if(m.getId() == metric.getId()) {
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
}
