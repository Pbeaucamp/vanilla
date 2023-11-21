//package bpm.fwr.client.panels;
//
//import bpm.fwr.client.Bpm_fwr;
//import bpm.fwr.client.WysiwygPanel;
//import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
//import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
//
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.dom.client.Style.Unit;
//import com.google.gwt.event.logical.shared.ResizeEvent;
//import com.google.gwt.event.logical.shared.ResizeHandler;
//import com.google.gwt.event.logical.shared.SelectionEvent;
//import com.google.gwt.event.logical.shared.SelectionHandler;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.AbsolutePanel;
//import com.google.gwt.user.client.ui.Composite;
//import com.google.gwt.user.client.ui.SimplePanel;
//import com.google.gwt.user.client.ui.Widget;
//
///**
// * 
// * Don't forget to set the parent because if it is null, it doesn't work
// * 
// * @author patrickbeaucamp
// *
// */
//public class TabReportPanel extends Composite {
//
//	private static TabReportPanelUiBinder uiBinder = GWT.create(TabReportPanelUiBinder.class);
//
//	interface TabReportPanelUiBinder extends
//			UiBinder<Widget, TabReportPanel> {
//	}
//	
//	@UiField
//	SimplePanel contentPanel;
//	
//	private ReportPanel reportPanel;
//	private CustomTabLayoutPanel tabPanel;
//	private AbsolutePanel previewPanel;
//	private WysiwygPanel wysiwygPanelParent;
//	
//	private GreyAbsolutePanel greyPanel;
//	private WaitAbsolutePanel waitPanel;
//	
//	private boolean isOnPreview = false;
//	
//	public TabReportPanel(ReportPanel reportPanel, WysiwygPanel wysiwygPanelParent) {
//		GWT.log("Create Tab Panel");
//		initWidget(uiBinder.createAndBindUi(this));
//		this.reportPanel = reportPanel;
//		this.wysiwygPanelParent = wysiwygPanelParent;
//		
//		tabPanel = new CustomTabLayoutPanel(20, Unit.PX, true);
//		int height = Window.getClientHeight();
//		tabPanel.setHeight(String.valueOf(height - 105) + "px");
//		contentPanel.setWidget(tabPanel);
//
//		SimplePanel reportContentPanel = new SimplePanel();
//		reportContentPanel.setWidget(reportPanel);
//	    tabPanel.add(reportContentPanel, Bpm_fwr.LBLW.TabReport());
//	    
//	    previewPanel = new AbsolutePanel();
//	    previewPanel.addStyleName("previewPanel");
//	    tabPanel.add(previewPanel, Bpm_fwr.LBLW.TabPreview());
//
//	    // Return the content
//	    tabPanel.selectTab(0);
//	    tabPanel.ensureDebugId("cwTabPanel");
//	    tabPanel.addSelectionHandler(tabSelectionHandler);
//
//	    Window.addResizeHandler(tabResizeHandler);
//	}
//	
//	private SelectionHandler<Integer> tabSelectionHandler = new SelectionHandler<Integer>() {
//
//		@Override
//		public void onSelection(SelectionEvent<Integer> event) {
//			if(event.getSelectedItem() == 0){
//				setOnPreview(false);
//				
//				wysiwygPanelParent.setBinVisibility(true);
//			}
//			else {
//				setOnPreview(true);
//				
//				wysiwygPanelParent.setBinVisibility(false);
//				wysiwygPanelParent.loadPreview();
//			}
//		}
//	};
//	
//	private ResizeHandler tabResizeHandler = new ResizeHandler() {
//		
//		@Override
//		public void onResize(ResizeEvent event) {
//			int height = event.getHeight();
//			tabPanel.setHeight(height - 105 + "px");
//		}
//	};
//	
//	public void selectTab(int index){
//		tabPanel.selectTab(index);
//	}
//
//	public AbsolutePanel getPreviewPanel() {
//		return previewPanel;
//	}
//
//	public void setPreviewPanel(AbsolutePanel previewPanel) {
//		this.previewPanel = previewPanel;
//	}
//
//	public void setReportPanel(ReportPanel reportPanel) {
//		this.reportPanel = reportPanel;
//	}
//
//	public ReportPanel getReportPanel() {
//		return reportPanel;
//	}
//	
//	public void showWaitPreview(int width, int height){
//		greyPanel = new GreyAbsolutePanel();
//		waitPanel = new WaitAbsolutePanel();
//		
//		previewPanel.add(greyPanel);
//		previewPanel.add(waitPanel);
//		previewPanel.setWidgetPosition(waitPanel, (width/2) - 80, (height/2) - 30);
//	}
//	
//	public void hideWaitPreview(){
//		previewPanel.remove(greyPanel);
//		previewPanel.remove(waitPanel);
//	}
//
//	private void setOnPreview(boolean isOnPreview) {
//		this.isOnPreview = isOnPreview;
//	}
//
//	public boolean isOnPreview() {
//		return isOnPreview;
//	}
//}
