package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.Email;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class MailShareDialog extends AbstractDialogBox {

	// private static final String BIRT_RPT_DESIGN = "BIRT (RptDesign)";

	

	private String defaultEmailText_EN = "<p align=\"justify\" class=\"text\">" + "<br>" + "Hi {TARGET_USERNAME},<br><br>" + "Please find attached the report : {REPORT_NAME}.<br><br>" + "Regards,<br><br>" + "{CURRENT_USERNAME}<br>" + "</p>";
	private String defaultEmailText_FR = "<p align=\"justify\" class=\"text\">" + "<br>" + "Bonjour {TARGET_USERNAME},<br><br>" + "Ci-joint le rapport : {REPORT_NAME}.<br><br>" + "Cordialement,<br><br>" + "{CURRENT_USERNAME}<br>" + "</p>";

	private static MailShareDialogUiBinder uiBinder = GWT.create(MailShareDialogUiBinder.class);

	interface MailShareDialogUiBinder extends UiBinder<Widget, MailShareDialog> {
	}

	interface MyStyle extends CssResource {
		String selected();

		String disabled();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	@UiField
	HTMLPanel panelReportDescription, panelCheckboxs, panelDefineMail, panelRecipient, panelSeparator, panelReportOptions;

	// @UiField
	// HTMLPanel panelTemplate;

	@UiField
	TextHolderBox txtTitle, txtEmail, txtLeft, txtRight, txtTop, txtBottom;

	@UiField
	TextBox txtSeparator;

	@UiField
	TextAreaHolderBox txtDescription;

	@UiField
	Image btnPortrait, btnLandscape;

	@UiField
	ListBox lstFormat;

	// @UiField
	// ListBox lstTemplate;

	@UiField
	Label lblTips;

	@UiField
	CheckBox checkLandscape, checkExportChart, checkExportGed;

	private TypeExport typeExport;
	private TypeShare typeShare;

	private IShare share;
	private String emailText;

	private CustomDatagrid<Object> datagrid;
	private MultiSelectionModel<Object> multiSelectionModel;

	private String key;
	private String dashboardUrl;

	private List<Email> emails;
	private List<Group> availableGroups;
	private List<CheckBox> checkboxs = new ArrayList<CheckBox>();

	private String itemName;
	private boolean isPortrait = true;
	private DrillInformations drillInfo;

	public MailShareDialog(IShare share, String reportKey, LaunchReportInformations itemInfo, List<Group> availableGroups) {
		super(LabelsConstants.lblCnst.ShareMail(), false, true);
		this.share = share;
		this.key = reportKey;
		this.availableGroups = availableGroups;
		this.itemName = itemInfo.getItem().getName();
		this.typeExport = TypeExport.REPORT;

		buildContent();

		if (itemInfo.getOutputs() != null) {
			for (String format : itemInfo.getOutputs()) {
				lstFormat.addItem(format);
			}
		}
		checkLandscape.setVisible(false);
		panelReportDescription.setVisible(false);
		lblTips.setVisible(false);
		checkExportChart.setVisible(false);
		checkExportGed.setVisible(false);

		if (itemInfo.isGroupReport()) {
			this.typeExport = TypeExport.REPORT_GROUP;

			for (LaunchReportInformations report : itemInfo.getReports()) {
				panelCheckboxs.add(createCheckbox(report.getItem().getName(), report.getReportKey()));
			}
		}
		else {
			panelCheckboxs.setVisible(false);
		}
	}

	public MailShareDialog(IShare share, LaunchReportInformations itemInfo, String uuid, String dashboardUrl, List<String> folders, List<Group> availableGroups) {
		super(LabelsConstants.lblCnst.ShareMail(), false, true);
		this.share = share;
		this.key = uuid;
		this.dashboardUrl = dashboardUrl;
		this.availableGroups = availableGroups;
		this.itemName = itemInfo.getItem().getName();
		this.typeExport = TypeExport.DASHBOARD;

		buildContent();

		for (int i = 0; i < CommonConstants.FORMAT_VALUE_FD.length; i++) {
			lstFormat.addItem(CommonConstants.FORMAT_DISPLAY_FD[i], CommonConstants.FORMAT_VALUE_FD[i]);
		}

		for (String folder : folders) {
			panelCheckboxs.add(createCheckbox(folder, folder));
		}
		panelReportDescription.setVisible(false);
		checkExportChart.setVisible(false);
		checkExportGed.setVisible(false);
	}

	public MailShareDialog(IShare share, String itemName, List<Group> availableGroups, TypeShare typeShare, TypeExport typeExport, DrillInformations drillInfo) {
		super(typeShare == TypeShare.EXPORT ? LabelsConstants.lblCnst.Export() : LabelsConstants.lblCnst.ShareMail(), false, true);
		this.share = share;
		this.availableGroups = availableGroups;
		this.itemName = itemName;
		this.typeShare = typeShare;
		this.typeExport = typeExport;
		this.drillInfo = drillInfo;

		buildContent();

		checkLandscape.setVisible(false);
		panelCheckboxs.setVisible(false);
		lblTips.setVisible(false);

		if (typeExport == TypeExport.CUBE) {
			for (int i = 0; i < CommonConstants.FORMAT_VALUE_CUBE.length; i++) {
				lstFormat.addItem(CommonConstants.FORMAT_DISPLAY_CUBE[i], CommonConstants.FORMAT_VALUE_CUBE[i]);
			}
			// lstFormat.addItem(BIRT_RPT_DESIGN, BIRT_RPT_DESIGN);

			panelSeparator.setVisible(false);
		}
		else if (typeExport == TypeExport.DRILLTHROUGH) {
			for (int i = 0; i < CommonConstants.FORMAT_VALUE_DRILLTHROUGH.length; i++) {
				lstFormat.addItem(CommonConstants.FORMAT_DISPLAY_DRILLTHROUGH[i], CommonConstants.FORMAT_VALUE_DRILLTHROUGH[i]);
			}

			panelReportOptions.setVisible(false);
			txtSeparator.setEnabled(false);
			txtSeparator.addStyleName(style.disabled());
			checkExportChart.setVisible(false);
			checkExportGed.setVisible(false);
		}

		if (typeShare == TypeShare.EXPORT) {
			panelDefineMail.setVisible(false);
			panelRecipient.setVisible(false);
		}
		else {
			checkExportGed.setVisible(false);
		}
	}

	public MailShareDialog(IShare share, String itemName, List<Group> availableGroups) {
		super(LabelsConstants.lblCnst.ShareMail(), false, true);
		this.share = share;
		this.availableGroups = availableGroups;
		this.itemName = itemName;
		this.typeExport = TypeExport.MARKDOWN;

		buildContent();

		checkLandscape.setVisible(false);
		panelReportDescription.setVisible(false);
		panelCheckboxs.setVisible(false);
		lblTips.setVisible(false);

		lstFormat.addItem(CommonConstants.FORMAT_HTML_NAME, CommonConstants.FORMAT_HTML);
		lstFormat.addItem(CommonConstants.FORMAT_PDF_NAME, CommonConstants.FORMAT_PDF);
		lstFormat.addItem(CommonConstants.FORMAT_DOCX_NAME, CommonConstants.FORMAT_DOCX);

		panelReportOptions.setVisible(false);

		checkExportChart.setVisible(false);
		checkExportGed.setVisible(false);

	}

	private Widget createCheckbox(String folder, String value) {
		CheckBox checkbox = new CheckBox(folder);
		checkbox.setName(value);
		checkbox.setValue(true);

		checkboxs.add(checkbox);

		SimplePanel panel = new SimplePanel();
		panel.add(checkbox);
		return panel;
	}

	private void buildContent() {
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		multiSelectionModel = new MultiSelectionModel<Object>();
		datagrid = new CustomDatagrid<Object>(buildList(null), multiSelectionModel, 200, LabelsConstants.lblCnst.NoGroupAvailable(), LabelsConstants.lblCnst.Groups());
		panelGrid.setWidget(datagrid);

		String defaultText = defaultEmailText_EN;
		if (typeExport == TypeExport.DASHBOARD) {
			defaultText = defaultText.replaceAll("report", "dashboard");
		}
		else if (typeExport == TypeExport.MARKDOWN) {
			defaultText = defaultText.replaceAll("report", "markdown document");
		}

		try {
			if (LocaleInfo.getCurrentLocale().getLocaleName().equalsIgnoreCase("fr")) {
				defaultText = defaultEmailText_FR;
				if (typeExport == TypeExport.DASHBOARD) {
					defaultText = defaultText.replaceAll("rapport", "tableau de bord");
				}
				else if (typeExport == TypeExport.MARKDOWN) {
					defaultText = defaultText.replaceAll("rapport", "document markdown");
				}
			}
		} catch (Exception e) {
		}

		setEmailText(defaultText);

		// panelTemplate.setVisible(false);
	}

	public void setEmailText(String html) {
		this.emailText = html;
	}

	private void loadItems(Email newEmail) {
		List<Object> items = buildList(newEmail);
		List<Object> selectedItems = getSelectedObjects(items, newEmail);
		datagrid.loadItems(items, selectedItems);
	}

	private List<Object> buildList(Email newEmail) {
		List<Object> objects = new ArrayList<Object>();
		if (availableGroups != null) {
			for (Group grp : availableGroups) {
				objects.add(grp);
			}
		}

		if (emails == null) {
			emails = new ArrayList<Email>();
		}
		if (newEmail != null) {
			emails.add(newEmail);
		}
		for (Email email : emails) {
			objects.add(email);
		}

		return objects;
	}

	private List<Object> getSelectedObjects(List<Object> items, Email newEmail) {
		List<Object> selectedItems = new ArrayList<Object>();
		for (Object item : items) {
			if (multiSelectionModel.isSelected(item)) {
				selectedItems.add(item);
			}
		}
		selectedItems.add(newEmail);
		return selectedItems;
	}

	@UiHandler("btnDefineEmail")
	public void onDefineEmailClick(ClickEvent event) {
		TextEmailDialog dial = new TextEmailDialog(this, emailText);
		dial.center();
	}

	@UiHandler("btnConfirmAdd")
	public void onConfirmAddClick(ClickEvent event) {
		String emails = txtEmail.getText();
		List<String> listEmail = splitMails(emails);

		boolean allValid = true;
		for (String email : listEmail) {
			if (checkEmail(email)) {
				txtEmail.setText("");

				loadItems(new Email(email));
			}
			else {
				allValid = false;
			}
		}

		if (!allValid) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.SomeEmailsNotValid());
		}
	}

	private List<String> splitMails(String emails) {
		String[] emailArray = emails.split(",");
		if (emailArray != null && emailArray.length != 0) {
			return Arrays.asList(emailArray);
		}
		return new ArrayList<String>();
	}

	private boolean checkEmail(String email) {
		if (email == null)
			return false;

		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		return email.matches(emailPattern);
	}

	@UiHandler("lstFormat")
	public void onChangeFormat(ChangeEvent event) {
		String selectedFormat = lstFormat.getValue(lstFormat.getSelectedIndex());
		if (typeExport == TypeExport.DASHBOARD) {
			if (selectedFormat.equalsIgnoreCase(CommonConstants.FORMAT_PDF_NAME)) {
				panelCheckboxs.setVisible(true);
			}
			else {
				panelCheckboxs.setVisible(false);
			}
		}
		// else if(typeExport == TypeExport.CUBE) {
		// if(selectedFormat.equals(BIRT_RPT_DESIGN)) {
		// panelTemplate.setVisible(true);
		//
		// lstTemplate.addItem("Template PM 1");
		// lstTemplate.addItem("Template PM 2");
		// checkExportChart.setEnabled(false);
		// }
		// }
		else if (typeExport == TypeExport.DRILLTHROUGH) {
			if (selectedFormat.equalsIgnoreCase(CommonConstants.FORMAT_CSV)) {
				txtSeparator.setText(";");
				txtSeparator.setEnabled(true);
				txtSeparator.removeStyleName(style.disabled());
			}
			else {
				if (selectedFormat.equalsIgnoreCase(CommonConstants.FORMAT_WEKA)) {
					txtSeparator.setText(",");
				}
				else {
					txtSeparator.setText(";");
				}
				txtSeparator.setEnabled(false);
				txtSeparator.addStyleName(style.disabled());
			}
		}
	}

	@UiHandler("btnPortrait")
	public void onPortraitClick(ClickEvent event) {
		this.isPortrait = true;

		btnLandscape.removeStyleName(style.selected());
		btnPortrait.addStyleName(style.selected());
	}

	@UiHandler("btnLandscape")
	public void onLandscapeClick(ClickEvent event) {
		this.isPortrait = false;

		btnPortrait.removeStyleName(style.selected());
		btnLandscape.addStyleName(style.selected());
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			MailShareDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			List<Group> selectedGroups = new ArrayList<Group>();
			List<Email> selectedEmails = new ArrayList<Email>();

			if (availableGroups != null) {
				for (Group group : availableGroups) {
					if (multiSelectionModel.isSelected(group)) {
						selectedGroups.add(group);
					}
				}
			}

			if (emails != null) {
				for (Email email : emails) {
					if (multiSelectionModel.isSelected(email)) {
						selectedEmails.add(email);
					}
				}
			}

			if (typeShare != TypeShare.EXPORT && selectedGroups.isEmpty() && selectedEmails.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseARecipient());
				return;
			}

			String format = lstFormat.getValue(lstFormat.getSelectedIndex());
			if (format.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseFormat());
				return;
			}

			MailShareDialog.this.hide();

			if (typeExport == TypeExport.DASHBOARD || typeExport == TypeExport.REPORT || typeExport == TypeExport.REPORT_GROUP) {
				HashMap<String, String> selectedFolders = new HashMap<String, String>();
				if (typeExport == TypeExport.DASHBOARD || typeExport == TypeExport.REPORT_GROUP) {
					for (CheckBox checkbox : checkboxs) {
						if (checkbox.getValue()) {
							selectedFolders.put(checkbox.getText(), checkbox.getName());
						}
					}
				}
				
				boolean isLandscape = checkLandscape.getValue();

				InfoShareMail infoShare = new InfoShareMail(typeExport, key, itemName, format, selectedGroups, selectedEmails, emailText, isLandscape);
				infoShare.setDashboardUrl(dashboardUrl);
				infoShare.setSelectedFolders(selectedFolders);

				share.share(infoShare);
			}
			else if (typeExport == TypeExport.CUBE || typeExport == TypeExport.DRILLTHROUGH) {
				String title = txtTitle.getText();
				String description = txtDescription.getText();

				String left = txtLeft.getText();
				String right = txtRight.getText();
				String top = txtTop.getText();
				String bottom = txtBottom.getText();

				String separator = txtSeparator.getText();
				if (separator.isEmpty()) {
					separator = ";";
				}

				boolean exportChart = checkExportChart.getValue();

				InfoShareCube infoShare = new InfoShareCube(typeShare, typeExport, title, description, itemName, format, separator, isPortrait, selectedGroups, selectedEmails, emailText, exportChart);
				infoShare.setMargins(left, right, top, bottom);
				infoShare.setDrillInformations(drillInfo);
				if(checkExportGed.getValue()) {
					infoShare.setTypeShare(TypeShare.GED);
				}
				
				share.share(infoShare);
				
			}
			else if (typeExport == TypeExport.MARKDOWN) {
				InfoShare infoShare = new InfoShareMail(typeExport, null, itemName, format, selectedGroups, selectedEmails, emailText, false);

				share.share(infoShare);
			}
		}
	};
}
