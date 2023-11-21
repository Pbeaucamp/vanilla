package bpm.vanillahub.web.client.dialogs;

import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.web.client.properties.resources.UserResourceProperties;
import bpm.vanillahub.web.client.services.ResourcesService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class UserDialog extends AbstractDialogBox implements NameChecker {

	private static UserDialogUiBinder uiBinder = GWT.create(UserDialogUiBinder.class);

	interface UserDialogUiBinder extends UiBinder<Widget, UserDialog> {
	}

	private IManager<User> manager;

	private PropertiesPanel<User> propsPanel;
	private List<User> users;

	private boolean edit;

	public UserDialog(IManager<User> manager, IResourceManager resourceManager, List<User> users, User user) {
		super(user == null ? LabelsCommon.lblCnst.Add() : LabelsCommon.lblCnst.Edit(), false, true);
		this.manager = manager;
		this.users = users;
		this.edit = user != null;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.OK(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		propsPanel = new UserResourceProperties(this, resourceManager, user);
				
		if(propsPanel != null) {
			setWidget(propsPanel);
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if(!propsPanel.isValid()) {
				return;
			}
			
			User user = propsPanel.buildItem();
			manageResource(user, edit);
		}
	};

	private void manageResource(User user, boolean edit) {
		showWaitPart(true);

		ResourcesService.Connect.getInstance().manageUser(user, edit, new GwtCallbackWrapper<User>(this, true) {

			@Override
			public void onSuccess(User result) {
				hide();

				manager.loadResources();
			}
		}.getAsyncCallback());
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	@Override
	public boolean checkIfNameTaken(int id, String value) {
		if (users != null) {
			for(User user : users) {
				if(!(id > 0 && user.getId() == id) && user.getName().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
}
