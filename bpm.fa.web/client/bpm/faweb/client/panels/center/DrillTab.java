package bpm.faweb.client.panels.center;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.DrillDTO;
import bpm.faweb.shared.DrillParameterDTO;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DrillTab extends Tab {

	private static ChartTabUiBinder uiBinder = GWT.create(ChartTabUiBinder.class);

	interface ChartTabUiBinder extends UiBinder<Widget, DrillTab> {
	}

	interface MyStyle extends CssResource {
		String lblNoDrill();
		String lstParam();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, paramPanelParent, paramPanel, reportPanel;

	@UiField
	Frame reportFrame;

	@UiField
	ListBoxWithButton<DrillDTO> lstDrill;

	private MainPanel mainPanel;
	private List<DrillDTO> drills;
	
	private String itemUrl;

	public DrillTab(TabManager tabManager, MainPanel mainPanel) {
		super(tabManager, FreeAnalysisWeb.LBL.Drill(), true);
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.drills = mainPanel.getInfosReport().getDrills();

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);

		loadDrills();

		NativeEvent event = Document.get().createChangeEvent();
		ChangeEvent.fireNativeEvent(event, lstDrill);
	}

	private void loadDrills() {
		if (drills != null && !drills.isEmpty()) {
			lstDrill.setList(drills, true);
		}
		else {
			Label lbl = new Label(FreeAnalysisWeb.LBL.NoDrillAvailable());
			lbl.addStyleName(style.lblNoDrill());
			
			paramPanel.clear();
			paramPanel.add(lbl);
		}
	}
	
	@UiHandler("lstDrill")
	public void onDrillChange(ChangeEvent event) {
		DrillDTO drill = lstDrill.getSelectedObject();
		fillParameterTable(drill);
	}

	private void fillParameterTable(final DrillDTO drill) {
		paramPanel.clear();
		if (drill.getType() == DrillDTO.REPORT && drill.getParameters() != null && drill.getParameters().size() > 0) {
			FaWebService.Connect.getInstance().getPossibleValuesForParameter(mainPanel.getKeySession(), drill.getParameters(), new GwtCallbackWrapper<List<DrillParameterDTO>>(this, true, true) {
				@Override
				public void onSuccess(List<DrillParameterDTO> result) {
					drill.setParameters(result);
					
					loadParameters(result);
				}
			}.getAsyncCallback());
		}

	}
	
	private void loadParameters(List<DrillParameterDTO> parameters) {
		paramPanel.clear();
		
		if (parameters != null && !parameters.isEmpty()) {
			paramPanelParent.setVisible(true);
			
			for (final DrillParameterDTO param : parameters) {
				final ListBoxWithButton<String> list = new ListBoxWithButton<String>();
				list.setLabel(param.getName());
				list.setList(param.getPossibleValues(), true);
				list.addStyleName(style.lstParam());
				list.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String value = list.getSelectedObject();
						String[] parts = value.split("\\[");
						param.setValue(parts[parts.length - 1].replace("]", ""));
					}
				});
				
//				lstParams.add(list);
				paramPanel.add(list);
			}
		}
		else {
			paramPanelParent.setVisible(false);
		}
	}
	
	@UiHandler("btnLaunch")
	public void onLaunch(ClickEvent event) {
		DrillDTO drill = lstDrill.getSelectedObject();
		FaWebService.Connect.getInstance().runDrill(mainPanel.getKeySession(), drill, new GwtCallbackWrapper<String>(this, true, true) {

			@Override
			public void onSuccess(String result) {
				itemUrl = result;
				reportFrame.setUrl(result);
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnOpenInNewTab")
	public void onOpenInNewTab(ClickEvent e) {
		if (itemUrl != null && !itemUrl.isEmpty()) {
			openNewTab(itemUrl);
		}
	}
	
	public static native void openNewTab(String url)/*-{
		$wnd.open(url);
	}-*/;
}
