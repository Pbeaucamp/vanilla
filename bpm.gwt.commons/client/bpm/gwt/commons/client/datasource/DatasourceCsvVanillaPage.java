package bpm.gwt.commons.client.datasource;

import java.util.List;

import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceCsvVanilla;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceCsvVanillaPage extends Composite implements IDatasourceObjectPage {

	private static DatasourceCsvVanillaPageUiBinder uiBinder = GWT.create(DatasourceCsvVanillaPageUiBinder.class);

	interface DatasourceCsvVanillaPageUiBinder extends UiBinder<Widget, DatasourceCsvVanillaPage> {
	}
	
	@UiField
	TextBox txtUser, txtPass, txtItem, txtSeparator;
	
	@UiField
	ListBox lstGroup, lstRepository;
	
	@UiField
	Button btnLogin, btnBrowseRep;
	
	private List<Group> groups;
	private List<Repository> repositories;
	
	private RepositoryItem selectedItem;

	private Datasource datasource;
	private DatasourceWizard parent;

	public DatasourceCsvVanillaPage(DatasourceWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		txtSeparator.setText(";");
		
		txtItem.setEnabled(false);
		
		txtPass.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtUser.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtItem.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		txtSeparator.addStyleName(VanillaCSS.COMMONS_TEXTBOX);
		lstGroup.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstRepository.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnLogin.addStyleName(VanillaCSS.COMMONS_BUTTON);
		btnBrowseRep.addStyleName(VanillaCSS.COMMONS_BUTTON);
		
		this.datasource = datasource;
		if(datasource != null && datasource.getObject() instanceof DatasourceCsvVanilla) {
			txtUser.setText(((DatasourceCsvVanilla)datasource.getObject()).getUser());
			txtPass.setText(((DatasourceCsvVanilla)datasource.getObject()).getPassword());
			txtSeparator.setText(((DatasourceCsvVanilla)datasource.getObject()).getSeparator());
			loadGroupAndRepository();
			
		}
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
		DatasourceCsvVanilla csvVanilla = new DatasourceCsvVanilla();
		
		if(datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceCsvVanilla) {
			csvVanilla = (DatasourceCsvVanilla) datasource.getObject();
		}
		
		csvVanilla.setUser(txtUser.getText());
		csvVanilla.setPassword(txtPass.getText());
		if(selectedItem != null) {
			csvVanilla.setItemId(selectedItem.getId());
		}
		csvVanilla.setGroupId(Integer.parseInt(lstGroup.getValue(lstGroup.getSelectedIndex())));
		csvVanilla.setRepositoryId(Integer.parseInt(lstRepository.getValue(lstRepository.getSelectedIndex())));
		
		csvVanilla.setSeparator(txtSeparator.getText());
		
		return csvVanilla;
	}
	
	@UiHandler("btnLogin")
	public void onLogin(ClickEvent e) {
		loadGroupAndRepository();
	}
	
	private void loadGroupAndRepository() {
		parent.showWaitPart(true);
		LoginService.Connect.getInstance().getAvailableGroups(new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				parent.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				parent.showWaitPart(false);
				groups = result;
				
				for(Group g : groups) {
					lstGroup.addItem(g.getName(), g.getId()+"");
				}
				
			}
			
		});
		LoginService.Connect.getInstance().getAvailableRepositories(new AsyncCallback<List<Repository>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				parent.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Repository> result) {
				parent.showWaitPart(false);
				repositories = result;
				
				for(Repository g : repositories) {
					lstRepository.addItem(g.getName(), g.getId()+"");
				}
			}
			
		});
	}
	
	@UiHandler("btnBrowseRep")
	public void onBrowse(ClickEvent e) {
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
		LoginService.Connect.getInstance().initRepositoryConnection(group, repo, new GwtCallbackWrapper<Void>(parent, true) {

			@Override
			public void onSuccess(Void result) {
				parent.showWaitPart(false);
				
				final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.EXTERNAL_DOCUMENT);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dial.isConfirm()) {
							selectedItem = dial.getSelectedItem();
							txtItem.setText(selectedItem.getName());
						}
					}
				});
				dial.center();
			}
		}.getAsyncCallback());
	}

}
