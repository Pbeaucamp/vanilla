package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.core.beans.activities.MailActivity;
import bpm.vanillahub.core.beans.activities.attributes.Email;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.MailPanel;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class MailActivityProperties extends PropertiesPanel<Activity> implements IManager<ServerMail> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private MailActivity activity;

	private List<ServerMail> serverMails;
	private PropertiesListBox lstServerMails;
	private MailPanel mailPanel;

	public MailActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, MailActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
		this.serverMails = this.resourceManager.getServerMails();
		
		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if(serverMails != null) {
			int i = 0;
			for(ServerMail cible : serverMails) {
				items.add(new ListItem(cible.getName(), cible.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServerMails = addList(Labels.lblCnst.SelectServerMail(), items, WidgetWidth.PCT, changeServerMail, refreshHandler);
		lstServerMails.setSelectedIndex(selectedIndex);
		
		addCheckbox(Labels.lblCnst.JoinLog(), activity.isJoinLog(), joinLogChangeHandler);
		addCheckbox(Labels.lblCnst.SendMailOnlyIfError(), activity.sendOnlyIfError(), sendLogIfErrorChangeHandler);
		
		mailPanel = new MailPanel(activity, this.resourceManager.getUsers());
		addPanel(mailPanel);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
	}

	private ServerMail findServerMail(int cibleId) {
		if (serverMails != null) {
			for (ServerMail cible : serverMails) {
				if (cible.getId() == cibleId) {
					return cible;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeServerMail = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstServerMails.getValue(lstServerMails.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setResource(findServerMail(cibleId));
			}
		}
	};
	
	private ClickHandler refreshHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadServerMails(MailActivityProperties.this, MailActivityProperties.this);
		}
	};

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> joinLogChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setJoinLog(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> sendLogIfErrorChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setSendOnlyIfError(event.getValue());
		}
	};

	@Override
	public void loadResources(List<ServerMail> result) {
		this.serverMails = result;
		
		lstServerMails.clear();
		int selectedIndex = -1;
		if(serverMails != null) {
			int i = 0;
			for(ServerMail cible : serverMails) {
				lstServerMails.addItem(cible.getName(), String.valueOf(cible.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServerMails.setSelectedIndex(selectedIndex);
	}

	@Override
	public void loadResources() { }

	@Override
	public boolean isValid() {
		String emailText = mailPanel.getEmailText();
		
		List<User> users = mailPanel.getUsers();
		List<Email> emails = mailPanel.getEmails();
		
		return activity.getResourceId() > 0 && emailText != null && !emailText.isEmpty() && (!users.isEmpty() || !emails.isEmpty());
	}
	
	@Override
	public Activity buildItem() {
		String textEmail = mailPanel.getEmailText();
		
		List<User> users = mailPanel.getUsers();
		List<Email> emails = mailPanel.getEmails();
		
		activity.setMessage(textEmail);
		activity.setUsers(users);
		activity.setEmails(emails);
		
		return activity;
	}
}
