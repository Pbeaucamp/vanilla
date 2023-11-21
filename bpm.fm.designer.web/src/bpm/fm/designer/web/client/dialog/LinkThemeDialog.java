package bpm.fm.designer.web.client.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.panel.MainPanel;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.fm.designer.web.client.utils.ThemeDatagrid;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class LinkThemeDialog extends AbstractDialogBox {

	private static LinkThemeDialogUiBinder uiBinder = GWT.create(LinkThemeDialogUiBinder.class);

	interface LinkThemeDialogUiBinder extends UiBinder<Widget, LinkThemeDialog> {
	}

	interface MyStyle extends CssResource {
		String grid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, gridPanel;
	
	private MultiSelectionModel<Theme> selectionModel = new MultiSelectionModel<Theme>();

	private Object axeMetric;

	private List<Theme> themes;
	
	public LinkThemeDialog(Object axeMetric) {
		super(Messages.lbl.linkAxisMetricTheme(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.axeMetric = axeMetric;
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		loadThemes();
	}

	private void loadThemes() {
		
		int obsId = MainPanel.getInstance().getSelectedObservatory();
		
		for(Observatory obs : ClientSession.getInstance().getObservatories()) {
			if(obsId == -1) {
				if(themes == null) {
					themes = new ArrayList<Theme>();
				}
				themes.addAll(obs.getThemes());
			}
			else if(obs.getId() == obsId) {
				themes = obs.getThemes();
				
				break;
			}
		}
		
		Collections.sort(themes, new Comparator<Theme>() {

			@Override
			public int compare(Theme o1, Theme o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		for(Theme th : themes) {
			if(axeMetric instanceof Axis) {
				if(th.getAxis().contains(axeMetric)) {
					selectionModel.setSelected(th, true);
				}
			}
			else {
				if(th.getMetrics().contains(axeMetric)) {
					selectionModel.setSelected(th, true);
				}
			}
		}
		
		createGrid();
	}

	private void createGrid() {
		gridPanel.clear();
		
		ThemeDatagrid dg = new ThemeDatagrid(themes, selectionModel, 300);
		gridPanel.add(dg);
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			List<Theme> themes = new ArrayList<Theme>(selectionModel.getSelectedSet());
			
			if(themes == null || themes.isEmpty()) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.NoThemeLinked(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.NoThemeLinkedMessage(), false);
				dial.center();
			}
			
			else if(axeMetric instanceof Axis) {
			
				MetricService.Connection.getInstance().updateThemeForAxis(themes, (Axis)axeMetric, new AsyncCallback<Void>() {			
					@Override
					public void onSuccess(Void result) {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.successSaveAxisTheme(), false);
						dial.center();
						LinkThemeDialog.this.hide();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			else {
				MetricService.Connection.getInstance().updateThemeForMetric(themes, (Metric)axeMetric, new AsyncCallback<Void>() {			
					@Override
					public void onSuccess(Void result) {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.successSaveMetricTheme(), false);
						dial.center();
						LinkThemeDialog.this.hide();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			LinkThemeDialog.this.hide();
		}
	};
}
