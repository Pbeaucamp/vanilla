package bpm.vanillahub.web.client.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.beans.activities.MailActivity;
import bpm.vanillahub.core.beans.activities.attributes.Email;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.TextEmailDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class MailPanel extends Composite {

	private static MailPanelUiBinder uiBinder = GWT.create(MailPanelUiBinder.class);
	
	interface MailPanelUiBinder extends UiBinder<Widget, MailPanel> {
	}

	@UiField
	SimplePanel panelGrid;

	@UiField
	HTMLPanel panelDefineMail, panelRecipient;

	@UiField
	TextHolderBox txtEmail;

	private List<User> users;
	private String emailText;

	private CustomDatagrid<Object> datagrid;
	private MultiSelectionModel<Object> multiSelectionModel;

	private List<Email> emails;

	public MailPanel(MailActivity activity, List<User> users) {
		initWidget(uiBinder.createAndBindUi(this));
		this.users = users;
		this.emails = activity.getEmails();

		multiSelectionModel = new MultiSelectionModel<Object>();
		datagrid = new CustomDatagrid<Object>(new ArrayList<Object>(), multiSelectionModel, 200, Labels.lblCnst.NoUserAvailable(), Labels.lblCnst.Users());
		panelGrid.setWidget(datagrid);

		if(activity.getMessage() != null && !activity.getMessage().isEmpty()) {
			setEmailText(activity.getMessage());
		}
		else {
			setEmailText(buildEmail());
		}

		List<Object> items = buildList(null);
		List<Object> selectedItems = getSelectedObjects(activity);
		datagrid.loadItems(items, selectedItems);
	}
	
	private String buildEmail() {
		StringBuffer buf = new StringBuffer();
		buf.append("<p>");
		buf.append("<br>");
		buf.append("Bonjour " + Constants.MAIL_TARGET_USERNAME + ",<br><br>");
		buf.append("Ci-joint les logs du workflow : " + Constants.MAIL_WORKFLOW_NAME + ".<br><br>");
		buf.append("Cordialement,<br><br>");
		buf.append(Constants.MAIL_CURRENT_USERNAME + "<br><br><br>");
		
		buf.append("<ul>");
		buf.append("<li> Workflow : " + Constants.MAIL_WORKFLOW_NAME + " </li>");
		buf.append("<li> Début : " + Constants.MAIL_START_DATE + " </li>");
		buf.append("<li> Fin : " + Constants.MAIL_END_DATE + " </li>");
		buf.append("<li> Erreurs : " + Constants.MAIL_HAS_ERRORS + " </li>");
		buf.append("</ul>");
		buf.append("</p>");
		return buf.toString();
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
		if (users != null) {
			for (User user : users) {
				objects.add(user);
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

	private List<Object> getSelectedObjects(MailActivity activity) {
		List<Object> selectedItems = new ArrayList<Object>();
		if (activity.getUsers() != null) {
			for (User activityUser : activity.getUsers()) {
				for (User user : users) {
					if (activityUser.getId() == user.getId()) {
						selectedItems.add(user);
					}
				}
			}
		}

		if (activity.getEmails() != null) {
			for (Email email : activity.getEmails()) {
				selectedItems.add(email);
			}
		}

		return selectedItems;
	}

	@UiHandler("btnDefineEmail")
	public void onDefineEmailClick(ClickEvent event) {
		TextEmailDialog dial = new TextEmailDialog(this, emailText);
		dial.center();
	}

	@UiHandler("btnConfirmAdd")
	public void onConfirmAddClick(ClickEvent event) {
		String email = txtEmail.getText();
		if (checkEmail(email)) {
			txtEmail.setText("");

			loadItems(new Email(email));
		}
		else {
			MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Error(), Labels.lblCnst.EmailNotValid());
		}
	}

	private boolean checkEmail(String email) {
		if (email == null)
			return false;

		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		return email.matches(emailPattern);
	}

	public String getEmailText() {
		return emailText;
	}

	public List<Email> getEmails() {
		List<Email> selectedEmails = new ArrayList<Email>();
		if (emails != null) {
			for (Email email : emails) {
				if (multiSelectionModel.isSelected(email)) {
					selectedEmails.add(email);
				}
			}
		}
		return selectedEmails;
	}

	public List<User> getUsers() {
		List<User> selectedUsers = new ArrayList<User>();
		if (users != null) {
			for (User user : users) {
				if (multiSelectionModel.isSelected(user)) {
					selectedUsers.add(user);
				}
			}
		}
		return selectedUsers;
	}
}
