package bpm.fmloader.client.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dialog.ErrorDialog;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.panel.GreyAbsolutePanel;
import bpm.fmloader.client.panel.WaitAbsolutePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MetricValuesPanel extends Composite {

	private static MetricValuesPanelUiBinder uiBinder = GWT.create(MetricValuesPanelUiBinder.class);

	@UiField
	HTMLPanel contentPanel, gridPanel;
	
	@UiField
	Button imgSave, imgCancelChange;
	
	private MetricDataPanel dataPanel;
	
	private Metric selectedMetric;
	
	interface MetricValuesPanelUiBinder extends UiBinder<Widget, MetricValuesPanel> {
	}

	public MetricValuesPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		imgSave.setTitle(Constantes.LBL.saveChanges());
		imgSave.addClickHandler(imgHandler);
		imgSave.getElement().getStyle().setDisplay(Display.NONE);
		
		imgCancelChange.setTitle(Constantes.LBL.cancelChanges());
		imgCancelChange.addClickHandler(imgHandler);
		imgCancelChange.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private ClickHandler imgHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(imgSave)) {
				
				final GreyAbsolutePanel greyPan = new GreyAbsolutePanel();
				final WaitAbsolutePanel waitPan = new WaitAbsolutePanel();
				
				waitPan.getElement().getStyle().setTop(contentPanel.getOffsetHeight() / 2 - 50, Unit.PX);
				waitPan.getElement().getStyle().setLeft(contentPanel.getOffsetWidth() / 2 - 100, Unit.PX);
				
				contentPanel.add(greyPan);
				contentPanel.add(waitPan);
				
				LoaderDataContainer values = dataPanel.getValues();
				
				Constantes.DATAS_SERVICES.updateValues(values, InfosUser.getInstance(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						ErrorDialog dial = new ErrorDialog("Error while updating values");
						dial.center();
						dial.show();
						
						contentPanel.remove(greyPan);
						contentPanel.remove(waitPan);
					}

					@Override
					public void onSuccess(Void result) {
						fillTable(InfosUser.getInstance().getSelectedDate(), selectedMetric);
						contentPanel.remove(greyPan);
						contentPanel.remove(waitPan);
					}
					
				});
				
			}
			else {
				fillTable(InfosUser.getInstance().getSelectedDate(), selectedMetric);
			}
		}
	};

	

	public void createTable(Date selectedDate) {
		
	}
	
	public void fillTable(Date selectedDate, Metric metric) {
		this.selectedMetric = metric;
		
		final GreyAbsolutePanel greyPan = new GreyAbsolutePanel();
		final WaitAbsolutePanel waitPan = new WaitAbsolutePanel();
		
		waitPan.getElement().getStyle().setTop(contentPanel.getOffsetHeight() / 2 - 50, Unit.PX);
		waitPan.getElement().getStyle().setLeft(contentPanel.getOffsetWidth() / 2 - 100, Unit.PX);
		
		contentPanel.add(greyPan);
		contentPanel.add(waitPan);
		
		InfosUser.getInstance().setSelectedDate(selectedDate);
		
		gridPanel.clear();
		
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(metric.getId());
		
		Constantes.DATAS_SERVICES.getValues(ids, selectedDate, null, true, new AsyncCallback<InfosUser>() {		
			@Override
			public void onSuccess(InfosUser result) {
				dataPanel = new MetricDataPanel(result.getValues(), true);
				
				String width = dataPanel.getWidth();
				
				dataPanel.getElement().getStyle().setProperty("minWidth", width);
				dataPanel.setWidth("100%");
				dataPanel.setHeight("100%");
//				dataPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
				gridPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
				
				gridPanel.add(dataPanel);
				contentPanel.remove(greyPan);
				contentPanel.remove(waitPan);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorDialog dial = new ErrorDialog("Error while getting values");
				dial.center();
				dial.show();
				
				contentPanel.remove(greyPan);
				contentPanel.remove(waitPan);
			}
		});
		
		
		int height = contentPanel.getOffsetHeight() - 60;
		
		gridPanel.setHeight(height +"px");
		gridPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
		gridPanel.getElement().getStyle().setLeft(0, Unit.PX);
		gridPanel.getElement().getStyle().setRight(0, Unit.PX);
		
		imgSave.getElement().getStyle().setDisplay(Display.INLINE);
		imgCancelChange.getElement().getStyle().setDisplay(Display.INLINE);
	}
	
	public Metric getSelectedMetric() {
		return selectedMetric;
	}

}
