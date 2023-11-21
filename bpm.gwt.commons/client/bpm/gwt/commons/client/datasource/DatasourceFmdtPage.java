package bpm.gwt.commons.client.datasource;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.fmdt.FmdtModel;
import bpm.gwt.commons.shared.fmdt.FmdtPackage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceFmdtPage extends Composite implements IDatasourceObjectPage {

	private static DatasourceFmdtPageUiBinder uiBinder = GWT.create(DatasourceFmdtPageUiBinder.class);

	interface DatasourceFmdtPageUiBinder extends UiBinder<Widget, DatasourceFmdtPage> {
	}

	@UiField
	TextBox txtUser, txtPass, txtItem, txtUrl;

	@UiField
	ListBox lstGroup, lstRepository, lstModel, lstPackage;

	@UiField
	Button btnLogin, btnBrowseRep;

	@UiField
	CheckBox chkUrl;

	private List<Group> groups;
	private List<Repository> repositories;

	private RepositoryItem selectedItem;

	private List<FmdtModel> models;

	private Datasource datasource;
	private DatasourceWizard parent;

	private String currentUser = "";
	private String currentPassword = "";
	private String currentVanillaUrl = "";

	private Timer timerLoadModelPackages = null;

	public DatasourceFmdtPage(DatasourceWizard parent, Datasource datasourc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		txtItem.setEnabled(false);

		txtPass.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtUser.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtItem.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		lstGroup.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstRepository.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstModel.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstPackage.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnLogin.addStyleName(VanillaCSS.COMMONS_BUTTON);
		btnBrowseRep.addStyleName(VanillaCSS.COMMONS_BUTTON);
		txtUrl.addStyleName(VanillaCSS.COMMONS_TEXTBOX);

		lstModel.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String model = lstModel.getValue(lstModel.getSelectedIndex());
				for (FmdtModel m : models) {
					if (m.getName().equals(model)) {
						lstPackage.clear();
						for (FmdtPackage pack : m.getPackages()) {
							lstPackage.addItem(pack.getName(), pack.getName());
						}
					}
				}
			}
		});
		chkUrl.setValue(true);
		onUrlClick(null);

		this.datasource = datasourc;

		if (datasource != null && datasource.getObject() instanceof DatasourceFmdt) {
			chkUrl.setValue(((DatasourceFmdt) datasource.getObject()).isDefaultUrl());
			onUrlClick(null);
			txtUser.setText(((DatasourceFmdt) datasource.getObject()).getUser());
			txtPass.setText(((DatasourceFmdt) datasource.getObject()).getPassword());
//			if (((DatasourceFmdt) datasource.getObject()).isDefaultUrl()) {
//				txtUrl.setText("");
//			}
//			else {
				txtUrl.setText(((DatasourceFmdt) datasource.getObject()).getUrl());
//			}
			loadGroupAndRepository();
			timerLoadModelPackages = new Timer() {
				public void run() {
					for(int i = 0; i < lstGroup.getItemCount(); i++){
						if(lstGroup.getValue(i).equals(((DatasourceFmdt) datasource.getObject()).getGroupId()+"")){
							lstGroup.setSelectedIndex(i);
							break;
						}
					}
					for(int i = 0; i < lstRepository.getItemCount(); i++){
						if(lstRepository.getValue(i).equals(((DatasourceFmdt) datasource.getObject()).getRepositoryId()+"")){
							lstRepository.setSelectedIndex(i);
							break;
						}
					}
					
					Group group = null;
					Repository repo = null;
					for (Group g : groups) {
						if (g.getId().intValue() == Integer.parseInt(lstGroup.getValue(lstGroup.getSelectedIndex()))) {
							group = g;
							break;
						}
					}
					for (Repository g : repositories) {
						if (g.getId() == Integer.parseInt(lstRepository.getValue(lstRepository.getSelectedIndex()))) {
							repo = g;
							break;
						}
					}
					final Group fgroup = group;
					final Repository frepo = repo;
					DatasourceFmdtPage.this.parent.showWaitPart(true);
					LoginService.Connect.getInstance().initRepositoryConnection(group, repo, new GwtCallbackWrapper<Void>(DatasourceFmdtPage.this.parent, true) {

						@Override
						public void onSuccess(Void result) {
							loadMetadataItem(((DatasourceFmdt) datasource.getObject()).getItemId(), fgroup, frepo);
							timerLoadModelPackages = null;
						}
					}.getAsyncCallback());
					
					//loadModelPackages(((DatasourceFmdt)datasource.getObject()).getItemId());
					
				}
			};

		}
		this.parent.showWaitPart(true);
		LoginService.Connect.getInstance().getVanillaContext(new GwtCallbackWrapper<HashMap<String, String>>(this.parent, true) {

			@Override
			public void onSuccess(HashMap<String, String> result) {
				currentUser = result.get("login");
				currentPassword = result.get("password");
				currentVanillaUrl = result.get("url");
				if (datasource == null) {
					if (chkUrl.getValue()) {
						txtUrl.setText(currentVanillaUrl);
						txtPass.setText(currentPassword);
						txtUser.setText(currentUser);
					}
				}
			}
		}.getAsyncCallback());
	}

	private void loadMetadataItem(int itemId, Group group, Repository repo) {
		
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().getItemById(itemId, txtUrl.getText(), txtUser.getText(), txtPass.getText(), group, repo, new GwtCallbackWrapper<RepositoryItem>(parent, true) {

			@Override
			public void onSuccess(RepositoryItem result) {
				selectedItem = result;
				txtItem.setText(selectedItem.getName());
				
				loadModelPackages(((DatasourceFmdt) datasource.getObject()).getItemId());
			}
		}.getAsyncCallback());
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	@Override
	public IDatasourceObject getDatasourceObject() {

		DatasourceFmdt object = null;
		if (datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceFmdt) {
			object = (DatasourceFmdt) datasource.getObject();
		}
		else {
			object = new DatasourceFmdt();
		}

		object.setUser(txtUser.getText());
		object.setPassword(txtPass.getText());
		object.setUrl(txtUrl.getText());
		object.setDefaultUrl(chkUrl.getValue());
		if (selectedItem != null) {
			object.setItemId(selectedItem.getId());
		}
		object.setGroupId(Integer.parseInt(lstGroup.getValue(lstGroup.getSelectedIndex())));
		object.setRepositoryId(Integer.parseInt(lstRepository.getValue(lstRepository.getSelectedIndex())));
		object.setBusinessModel(lstModel.getValue(lstModel.getSelectedIndex()));
		object.setBusinessPackage(lstPackage.getValue(lstPackage.getSelectedIndex()));
		return object;
	}

	@UiHandler("btnLogin")
	public void onLogin(ClickEvent e) {
		loadGroupAndRepository();
	}

	private void loadGroupAndRepository() {
		lstGroup.clear();
		lstRepository.clear();
		if (chkUrl.getValue()) {

			parent.showWaitPart(true);
			LoginService.Connect.getInstance().getAvailableGroups(new GwtCallbackWrapper<List<Group>>(parent, true) {

				@Override
				public void onSuccess(List<Group> result) {
					groups = result;
					for (Group g : groups) {
						lstGroup.addItem(g.getName(), g.getId() + "");
					}
					parent.showWaitPart(true);
					LoginService.Connect.getInstance().getAvailableRepositories(new GwtCallbackWrapper<List<Repository>>(parent, true) {

						@Override
						public void onSuccess(List<Repository> result) {
							repositories = result;
							for (Repository g : repositories) {
								lstRepository.addItem(g.getName(), g.getId() + "");
							}
							if (timerLoadModelPackages != null)
								timerLoadModelPackages.run(); // lors de l'update du datasource
						}

					}.getAsyncCallback());
				}
			}.getAsyncCallback());

		}
		else {
			parent.showWaitPart(true);
			LoginService.Connect.getInstance().getAvailableGroups(txtUrl.getText(), txtUser.getText(), txtPass.getText(), new GwtCallbackWrapper<List<Group>>(parent, true) {

				@Override
				public void onSuccess(List<Group> result) {
					groups = result;
					for (Group g : groups) {
						lstGroup.addItem(g.getName(), g.getId() + "");
					}
					parent.showWaitPart(true);
					LoginService.Connect.getInstance().getAvailableRepositories(txtUrl.getText(), txtUser.getText(), txtPass.getText(), new GwtCallbackWrapper<List<Repository>>(parent, true) {

						@Override
						public void onSuccess(List<Repository> result) {
							repositories = result;

							for (Repository g : repositories) {
								lstRepository.addItem(g.getName(), g.getId() + "");
							}
							if (timerLoadModelPackages != null)
								timerLoadModelPackages.run();
						}

					}.getAsyncCallback());
				}

			}.getAsyncCallback());

		}

	}

	@UiHandler("btnBrowseRep")
	public void onBrowse(ClickEvent e) {
		Group group = null;
		Repository repo = null;
		for (Group g : groups) {
			if (g.getId().intValue() == Integer.parseInt(lstGroup.getValue(lstGroup.getSelectedIndex()))) {
				group = g;
				break;
			}
		}
		for (Repository g : repositories) {
			if (g.getId() == Integer.parseInt(lstRepository.getValue(lstRepository.getSelectedIndex()))) {
				repo = g;
				break;
			}
		}
		final Group fgroup = group;
		final Repository frepo = repo;
		parent.showWaitPart(true);
		LoginService.Connect.getInstance().initRepositoryConnection(group, repo, new GwtCallbackWrapper<Void>(parent, true) {

			@Override
			public void onSuccess(Void result) {
				final RepositoryDialog dial;
				if (chkUrl.getValue()) {
					dial = new RepositoryDialog(IRepositoryApi.FMDT_TYPE);
				}
				else {
					dial = new RepositoryDialog(txtUrl.getText(), txtUser.getText(), txtPass.getText(), fgroup, frepo, IRepositoryApi.FMDT_TYPE);
				}
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							selectedItem = dial.getSelectedItem();
							txtItem.setText(selectedItem.getName());

							loadModelPackages(selectedItem.getId());
						}
					}
				});
				dial.center();
			}
		}.getAsyncCallback());

	}

	private void loadModelPackages(int id) {
		if (chkUrl.getValue()) {
			parent.showWaitPart(true);
			FmdtServices.Connect.getInstance().getMetadataInfos(id, new AsyncCallback<List<FmdtModel>>() {

				@Override
				public void onSuccess(List<FmdtModel> result) {
					parent.showWaitPart(false);
					models = result;

					lstModel.clear();
					boolean first = true;
					int i = 0;
					for (FmdtModel model : result) {
						lstModel.addItem(model.getName(), model.getName());

						if (datasource != null && datasource.getObject() instanceof DatasourceFmdt) {
							first = false;
							if (model.getName().equals(((DatasourceFmdt) datasource.getObject()).getBusinessModel())) {
								first = true;
								lstModel.setSelectedIndex(i);
							}

						}

						if (first) {
							first = false;
							lstPackage.clear();
							int j = 0;
							for (FmdtPackage pack : model.getPackages()) {
								lstPackage.addItem(pack.getName(), pack.getName());
								if (datasource != null && datasource.getObject() instanceof DatasourceFmdt) {
									if (pack.getName().equals(((DatasourceFmdt) datasource.getObject()).getBusinessPackage())) {
										lstPackage.setSelectedIndex(j);
									}

								}
								j++;
							}
						}
						i++;
					}

				}

				@Override
				public void onFailure(Throwable caught) {
					ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					parent.showWaitPart(false);
				}
			});
		}
		else {
			Group group = null;
			Repository repo = null;
			for (Group g : groups) {
				if (g.getId().intValue() == Integer.parseInt(lstGroup.getValue(lstGroup.getSelectedIndex()))) {
					group = g;
					break;
				}
			}
			for (Repository g : repositories) {
				if (g.getId() == Integer.parseInt(lstRepository.getValue(lstRepository.getSelectedIndex()))) {
					repo = g;
					break;
				}
			}
			parent.showWaitPart(true);
			FmdtServices.Connect.getInstance().getMetadataInfos(id, txtUrl.getText(), txtUser.getText(), txtPass.getText(), group, repo, new AsyncCallback<List<FmdtModel>>() {

				@Override
				public void onSuccess(List<FmdtModel> result) {
					parent.showWaitPart(false);
					models = result;

					lstModel.clear();
					boolean first = true;
					int i = 0;
					for (FmdtModel model : result) {
						lstModel.addItem(model.getName(), model.getName());

						if (datasource != null && datasource.getObject() instanceof DatasourceFmdt) {
							first = false;
							if (model.getName().equals(((DatasourceFmdt) datasource.getObject()).getBusinessModel())) {
								first = true;
								lstModel.setSelectedIndex(i);
							}

						}

						if (first) {
							first = false;
							lstPackage.clear();
							int j = 0;
							for (FmdtPackage pack : model.getPackages()) {
								lstPackage.addItem(pack.getName(), pack.getName());
								if (datasource != null && datasource.getObject() instanceof DatasourceFmdt) {
									if (pack.getName().equals(((DatasourceFmdt) datasource.getObject()).getBusinessPackage())) {
										lstPackage.setSelectedIndex(j);
									}

								}
								j++;
							}
						}
						i++;
					}

				}

				@Override
				public void onFailure(Throwable caught) {
					ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					parent.showWaitPart(false);
				}
			});
		}

	}

	@UiHandler("chkUrl")
	public void onUrlClick(ClickEvent ev) {
		if (chkUrl.getValue()) {

			txtUrl.setText(currentVanillaUrl);
			txtPass.setText(currentPassword);
			txtUser.setText(currentUser);
			txtUrl.setEnabled(false);
			txtUser.setEnabled(false);
			txtPass.setEnabled(false);
		}
		else {
			txtUrl.setText("");
			txtPass.setText("");
			txtUser.setText("");
			txtUrl.setEnabled(true);
			txtUser.setEnabled(true);
			txtPass.setEnabled(true);
		}
	}
}
