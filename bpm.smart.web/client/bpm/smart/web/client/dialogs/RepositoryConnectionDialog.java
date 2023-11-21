package bpm.smart.web.client.dialogs;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.RepositoryDirectoryDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.smart.core.model.RScript.ScriptType;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.MarkdownType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryConnectionDialog extends AbstractDialogBox {
	private static RepositoryConnectionDialogUiBinder uiBinder = GWT.create(RepositoryConnectionDialogUiBinder.class);

	interface RepositoryConnectionDialogUiBinder extends UiBinder<Widget, RepositoryConnectionDialog> {
	}


	@UiField
	TextBox txtUser, txtPass, txtUrl;
	
	@UiField
	ListBox lstGroup, lstRepository;
	
	@UiField
	Button btnLogin;
	
	@UiField
	CheckBox chkUrl;
	

	private int type;
	private int subType;
	private MarkdownType object;
	private List<Group> groups;
	private List<Repository> repositories;
	
	private String currentUser ="";
	private String currentPassword ="";
	private String currentVanillaUrl ="";

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	public RepositoryConnectionDialog(int type, MarkdownType object) {
		super(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Connection(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Connection(), okHandler, lblCnst.Cancel(), cancelHandler);
		this.type = type;
		this.object = object;
		if(object.getType().equals(ScriptType.MARKDOWN.name())){
			this.subType = IRepositoryApi.MARKDOWN_SUBTYPE;
		} else if (object.getType().equals(ScriptType.R.name())){
			this.subType = IRepositoryApi.R_SUBTYPE;
		}
		
		txtPass.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtUser.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		lstGroup.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstRepository.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnLogin.addStyleName(VanillaCSS.COMMONS_BUTTON);
		txtUrl.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		
		chkUrl.setValue(true);
		onUrlClick(null);
		
		showWaitPart(true);
		LoginService.Connect.getInstance().getVanillaContext(new GwtCallbackWrapper<HashMap<String, String>>(this, true) {

			@Override
			public void onSuccess(HashMap<String, String> result) {
				currentUser = result.get("login");
				currentPassword = result.get("password");
				currentVanillaUrl = result.get("url");
					if(chkUrl.getValue()){
						txtUrl.setText(currentVanillaUrl);
						txtPass.setText(currentPassword);
						txtUser.setText(currentUser);
					}
				
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnLogin")
	public void onLogin(ClickEvent e) {
		loadGroupAndRepository();
	}
	
	private void loadGroupAndRepository() {
		lstGroup.clear();lstRepository.clear();
		if(chkUrl.getValue()){
			
			showWaitPart(true);
			LoginService.Connect.getInstance().getAvailableGroups(new GwtCallbackWrapper<List<Group>>(this, true) {

				@Override
				public void onSuccess(List<Group> result) {
					groups = result;
					for(Group g : groups) {
						lstGroup.addItem(g.getName(), g.getId()+"");
					}
					showWaitPart(true);
					LoginService.Connect.getInstance().getAvailableRepositories(new GwtCallbackWrapper<List<Repository>>(RepositoryConnectionDialog.this, true) {

						@Override
						public void onSuccess(List<Repository> result) {
							repositories = result;
							for(Repository g : repositories) {
								lstRepository.addItem(g.getName(), g.getId()+"");
							}
						}
						
					}.getAsyncCallback());
				}
			}.getAsyncCallback());
			
		} else {
			showWaitPart(true);
			LoginService.Connect.getInstance().getAvailableGroups(txtUrl.getText(), txtUser.getText(), txtPass.getText(), new GwtCallbackWrapper<List<Group>>(this, true) {

				@Override
				public void onSuccess(List<Group> result) {
					groups = result;
					for(Group g : groups) {
						lstGroup.addItem(g.getName(), g.getId()+"");
					}
					showWaitPart(true);
					LoginService.Connect.getInstance().getAvailableRepositories(txtUrl.getText(), txtUser.getText(), txtPass.getText(), new GwtCallbackWrapper<List<Repository>>(RepositoryConnectionDialog.this, true) {

						@Override
						public void onSuccess(List<Repository> result) {
							repositories = result;
							
							for(Repository g : repositories) {
								lstRepository.addItem(g.getName(), g.getId()+"");
							}
						}
						
					}.getAsyncCallback());
				}
				
			}.getAsyncCallback());
			
		}
		
	}

	
	@UiHandler("chkUrl")
	public void onUrlClick(ClickEvent ev){
		if(chkUrl.getValue()){
			
			txtUrl.setText(currentVanillaUrl);
			txtPass.setText(currentPassword);
			txtUser.setText(currentUser);
			txtUrl.setEnabled(false);
			txtUser.setEnabled(false);
			txtPass.setEnabled(false);
		} else {
			txtUrl.setText("");
			txtPass.setText("");
			txtUser.setText("");
			txtUrl.setEnabled(true);
			txtUser.setEnabled(true);
			txtPass.setEnabled(true);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Group group = null;
			Repository repo = null;
			for(Group g : groups) {
				if(g.getId().intValue() == Integer.parseInt(lstGroup.getValue(lstGroup.getSelectedIndex()))) {
					group = g;
					break;
				}
			}
			for(Repository g : repositories) {
				if(g.getId() == Integer.parseInt(lstRepository.getValue(lstRepository.getSelectedIndex()))) {
					repo = g;
					break;
				}
			}
			final Group fgroup = group;
			final Repository frepo = repo;
			showWaitPart(true);
			LoginService.Connect.getInstance().initRepositoryConnection(group, repo, new GwtCallbackWrapper<Void>(RepositoryConnectionDialog.this, true) {

				@Override
				public void onSuccess(Void result) {
					final RepositoryDirectoryDialog dial;
					if(chkUrl.getValue()){
						dial = new RepositoryDirectoryDialog(type, subType, groups, object);
					} else {
						dial = new RepositoryDirectoryDialog(txtUrl.getText(), txtUser.getText(), txtPass.getText(), fgroup, frepo, type, subType, groups, object);
					}
					dial.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if(dial.getConfirm()) {
								hide();
							}
						}
					});
					dial.center();
				}
			}.getAsyncCallback());	
		
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			RepositoryConnectionDialog.this.hide();
		}
	};

}
