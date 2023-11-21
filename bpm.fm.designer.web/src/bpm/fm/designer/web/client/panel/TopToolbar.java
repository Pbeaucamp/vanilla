package bpm.fm.designer.web.client.panel;

import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.FMDesignerWeb;
import bpm.fm.designer.web.client.dialog.SecurityDialog;
import bpm.gwt.commons.client.datasource.DatasourceDialog;
import bpm.gwt.commons.client.free.metrics.FMFilterPanel;
import bpm.gwt.commons.client.free.metrics.IFilterChangeHandler;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TopToolbar extends Composite implements IFilterChangeHandler {

	private static TopToolbarUiBinder uiBinder = GWT.create(TopToolbarUiBinder.class);

	interface TopToolbarUiBinder extends UiBinder<Widget, TopToolbar> {
	}

	interface MyStyle extends CssResource {
		String btnToolbar();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Image imgHome, imgDatasource, imgSecurity;
	
	@UiField
	HTMLPanel panelFilter;
	
	private FMFilterPanel filterPanel;
	
	public TopToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		filterPanel = new FMFilterPanel(this);
		
		panelFilter.add(filterPanel);
	}

	@UiHandler("imgDatasource")
	public void onDatasourceClick(ClickEvent event) {
		DatasourceDialog dial = new DatasourceDialog();
		dial.center();
	}
	
	@UiHandler("imgSecurity")
	public void onSecurityClick(ClickEvent event) {
		SecurityDialog dial = new SecurityDialog();
		dial.center();
	}
	
	@UiHandler("imgHome")
	public void onHome(ClickEvent event) {
		FMDesignerWeb.getInstance().onLogout();
	}

	public int getSelectedObservatory() {
		try {
			return filterPanel.getSelectedObservatory().getId();
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public void selectionChanged(Group group, Observatory obs, Theme theme) {
		int id = -1;
		if(theme != null) {
			id = theme.getId();
		}
		MainPanel.getInstance().reloadMetricAxes(id);
		
	}

	public void loadObservatories() {
		try {
			filterPanel.initLists(filterPanel.getSelectedTheme().getId(), filterPanel.getSelectedGroup().getId());
		} catch(Exception e) {
			filterPanel.initLists(null, null);
		}
	}
}
