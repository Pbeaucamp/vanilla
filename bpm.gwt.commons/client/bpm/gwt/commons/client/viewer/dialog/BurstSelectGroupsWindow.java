package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.text.RichTextToolbar;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class BurstSelectGroupsWindow extends AbstractDialogBox {
	private static final String CSS_MAIN_PANEL_BURST = "mainPanelBurst";
	private static final String CSS_PANEL_BURST = "panelBtnRadioBurst";
	private static final String CSS_BTN_NO_MAIL = "btnNoMailBurst";
	
	private static BurstSelectGroupsWindowUiBinder uiBinder = GWT.create(BurstSelectGroupsWindowUiBinder.class);

	interface BurstSelectGroupsWindowUiBinder extends UiBinder<Widget, BurstSelectGroupsWindow> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	private IWait waitPanel;
	private LaunchReportInformations itemInfo;
	
	private List<Group> groupsForBurst;
	private List<Group> groupsForHisto;
	private List<Group> selectedGroups = new ArrayList<Group>();

	private RichTextArea txtRichArea;
	
	private MultiSelectionModel<Group> selectionModel = new MultiSelectionModel<Group>();;
	
	private boolean addToGed = false;
	private HorizontalPanel panelGedName;
	private TextBox txtGedName;
	
	private boolean sendMail = false;
	private HTMLPanel panelWriteMail;
	
	private String defaultEmailText_EN = "<p align=\"justify\" class=\"text\">" +
		"<br>" +
		"Hi {TARGET_USERNAME},<br><br>" +
		"Please find attached the burst of the report : {REPORT_NAME} " +
		"generated for the group {TARGET_GROUP}.<br><br>" +
		"Regards,<br><br>" +
		"{CURRENT_USERNAME}<br>" +
		"</p>";
	
	private String defaultEmailText_FR = "<p align=\"justify\" class=\"text\">" +
		"<br>" +
		"Bonjour {TARGET_USERNAME},<br><br>" +
		"Ci-joint le burst du rapport : {REPORT_NAME} " +
		" pour le groupe {TARGET_GROUP}.<br><br>" +
		"Cordialement,<br><br>" +
		"{CURRENT_USERNAME}<br>" +
		"</p>";
	
	public BurstSelectGroupsWindow(IWait waitPanel, LaunchReportInformations itemInfo, List<Group> groupsForBurst, List<Group> groupsForHisto) {
		super(LabelsConstants.lblCnst.BurstReport(), false, true);
		
		this.waitPanel = waitPanel;
		this.groupsForBurst = groupsForBurst;
		this.itemInfo = itemInfo;
		
		if(groupsForHisto != null){
			this.groupsForHisto = groupsForHisto;
		}
		else {
			this.groupsForHisto = new ArrayList<Group>();
		}

		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		RadioButton btnYesMail = new RadioButton("mail", LabelsConstants.lblCnst.Yes());
		btnYesMail.addValueChangeHandler(yesMailHandler);
		
		RadioButton btnNoMail = new RadioButton("mail", LabelsConstants.lblCnst.No());
		btnNoMail.addValueChangeHandler(noMailHandler);
		btnNoMail.addStyleName(CSS_BTN_NO_MAIL);
		btnNoMail.setValue(true);
		
		HorizontalPanel panelYesNoMail = new HorizontalPanel();
		panelYesNoMail.addStyleName(CSS_PANEL_BURST);
		panelYesNoMail.add(btnYesMail);
		panelYesNoMail.add(btnNoMail);
		
		panelWriteMail = createFormPanel();
		panelWriteMail.setVisible(false);
		
		HTMLPanel panelMail = new HTMLPanel("");
		panelMail.add(panelYesNoMail);
		panelMail.add(panelWriteMail);
		
		CaptionPanel captionPanelMail = new CaptionPanel(LabelsConstants.lblCnst.SendByMail());
		captionPanelMail.add(panelMail);
		
		HTMLPanel mainPanel = new HTMLPanel("");
		mainPanel.addStyleName(CSS_MAIN_PANEL_BURST);
		mainPanel.add(new CustomDatagrid<Group>(groupsForBurst, selectionModel, 200, LabelsConstants.lblCnst.NoGroup()));
		mainPanel.add(createGedPanel());
		mainPanel.add(captionPanelMail);
		
		contentPanel.add(mainPanel);
	}
	
	private CaptionPanel createGedPanel() {
		RadioButton btnYesGed = new RadioButton("ged", LabelsConstants.lblCnst.Yes());
		btnYesGed.addValueChangeHandler(yesGedHandler);
		
		RadioButton btnNoGed = new RadioButton("ged", LabelsConstants.lblCnst.No());
		btnNoGed.addValueChangeHandler(noGedHandler);
		btnNoGed.addStyleName(CSS_BTN_NO_MAIL);
		btnNoGed.setValue(true);
		
		HorizontalPanel panelYesNoGed = new HorizontalPanel();
		panelYesNoGed.addStyleName(CSS_PANEL_BURST);
		panelYesNoGed.add(btnYesGed);
		panelYesNoGed.add(btnNoGed);
		
		Label lblGedName = new Label(LabelsConstants.lblCnst.Name());
		
		txtGedName = new TextBox();
		txtGedName.addStyleName(CSS_BTN_NO_MAIL);
		
		panelGedName = new HorizontalPanel();
		panelGedName.add(lblGedName);
		panelGedName.add(txtGedName);
		panelGedName.setVisible(false);
		
		HTMLPanel panelGed = new HTMLPanel("");
		panelGed.add(panelYesNoGed);
		panelGed.add(panelGedName);
		
		CaptionPanel mainGedPanel = new CaptionPanel(LabelsConstants.lblCnst.AddToGed());
		mainGedPanel.add(panelGed);
		return mainGedPanel;
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			BurstSelectGroupsWindow.this.hide();
		}
	};
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			selectedGroups.clear();
			
			boolean showWarningMessage = false;
			StringBuilder warningMessage = new StringBuilder(LabelsConstants.lblCnst.HistoricNotAvailable() + ": ");
			
			if(groupsForBurst != null){
				for(Group group : groupsForBurst){
					if(selectionModel.isSelected(group)){
						selectedGroups.add(group);
						
						boolean found = false;
						for (Group gr : groupsForHisto){
							if(group.getId() == gr.getId()){
								found = true;
								break;
							}
						}
						
						if(!found){
							showWarningMessage = true;
							warningMessage.append("\n - ");
							warningMessage.append(group.getName());
						}
					}
				}
			}
			
			warningMessage.append(".\n" + LabelsConstants.lblCnst.RunAnyway());
			
			if (selectedGroups.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseAGroup());
				return;
			}
			
			if(addToGed && txtGedName.getText().isEmpty()){
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedHistoName());
				return;
			}
			
			if(addToGed && showWarningMessage){
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), warningMessage.toString(), false);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						startBurst();
					}
				});
				dial.center();
			}
			else {
				startBurst();
			}
		}
	};
	
	private HTMLPanel createFormPanel() {
		txtRichArea = new RichTextArea();
		txtRichArea.setWidth("100%");
		
		RichTextToolbar toolbar = new RichTextToolbar(txtRichArea);
		toolbar.setWidth("100%");
		
		try {
			if (LocaleInfo.getCurrentLocale().getLocaleName().equalsIgnoreCase("fr")) {
				txtRichArea.setHTML(defaultEmailText_FR);
			}
			else {
				txtRichArea.setHTML(defaultEmailText_EN);
			}
		} catch (Exception e) {
			txtRichArea.setHTML(defaultEmailText_EN);
		}

		HTMLPanel panelEmail = new HTMLPanel("");
		panelEmail.add(toolbar);
		panelEmail.add(txtRichArea);
		return panelEmail;
	}

	private void startBurst() {
		String htmlText = txtRichArea.getHTML();
		
		String gedName = txtGedName.getText();
		
		BurstSelectGroupsWindow.this.hide();
		
		waitPanel.showWaitPart(true);
		
		ReportingService.Connect.getInstance().burstReport(itemInfo, 
				selectedGroups, 
				htmlText, 
				sendMail, 
				addToGed, 
				gedName, 
				new AsyncCallback<String>() {
			
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}
			
			@Override
			public void onSuccess(String arg0) {
				waitPanel.showWaitPart(false);

				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), arg0);
			}
		});
	}
	
	private ValueChangeHandler<Boolean> yesMailHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				sendMail = true;
				panelWriteMail.setVisible(true);
			}
		}
	};
	
	private ValueChangeHandler<Boolean> noMailHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				sendMail = false;
				panelWriteMail.setVisible(false);
			}
		}
	};
	
	private ValueChangeHandler<Boolean> yesGedHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				addToGed = true;
				panelGedName.setVisible(true);
			}
		}
	};
	
	private ValueChangeHandler<Boolean> noGedHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				addToGed = false;
				panelGedName.setVisible(false);
			}
		}
	};
}
