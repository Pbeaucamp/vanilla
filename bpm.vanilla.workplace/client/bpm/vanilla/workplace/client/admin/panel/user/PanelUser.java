package bpm.vanilla.workplace.client.admin.panel.user;

import java.util.List;

import bpm.vanilla.workplace.client.admin.dialog.ConfirmDeleteDialog;
import bpm.vanilla.workplace.client.admin.dialog.ConfirmDeleteDialog.DeleteType;
import bpm.vanilla.workplace.client.admin.dialog.user.AddUserDialog;
import bpm.vanilla.workplace.client.images.Images;
import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PanelUser extends Composite implements ResizeHandler {

	private static PanelUserUiBinder uiBinder = GWT
			.create(PanelUserUiBinder.class);

	interface PanelUserUiBinder extends UiBinder<Widget, PanelUser> {
	}

	@UiField
	Image btnAddUser, btnDeleteUser;
	
	@UiField
	ListBox lstUser;
	
	@UiField
	SimplePanel panelInfos;

	private List<PlaceWebUser> users;
	private PlaceWebUser currentUser;
	
	public PanelUser() {
		initWidget(uiBinder.createAndBindUi(this));
		
		btnAddUser.setResource(Images.INSTANCE.add_user());
		btnDeleteUser.setResource(Images.INSTANCE.remove_user());
		
		int height = Window.getClientHeight() - 110;
		lstUser.setHeight(height + "px");
		
		Window.addResizeHandler(this);
		refreshUser();
		btnDeleteUser.setVisible(false);
	}
	
	@UiHandler("btnAddUser")
	public void onAddUserClick(ClickEvent event){
		AddUserDialog dial = new AddUserDialog();
		dial.setGlassEnabled(true);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshUser();
			}
		});
	}
	
	@UiHandler("btnDeleteUser")
	public void onDeleteUserClick(ClickEvent event){
		if(currentUser != null){
			ConfirmDeleteDialog dialDel = new ConfirmDeleteDialog(currentUser.getId(), null, null, 
					"Do you really want to delete this user ?", DeleteType.USER);
			dialDel.setGlassEnabled(true);
			dialDel.center();
			dialDel.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshUser();
				}
			});
		}
	}
	
	@UiHandler("lstUser")
	public void onUserSelection(ChangeEvent e){
		int userId = Integer.parseInt(lstUser.getValue(lstUser.getSelectedIndex()));
		for(PlaceWebUser user : users){
			if(user.getId() == userId){
				currentUser = user;
				btnDeleteUser.setVisible(true);
				loadInfos();
				break;
			}
		}
	}
	
	private void loadInfos(){
		PanelUserInformation panelUserInfos = new PanelUserInformation(currentUser, false);
		panelInfos.setWidget(panelUserInfos);
	}
	
	private void refreshUser(){
		AdminService.Connect.getInstance().getAllUser(new AsyncCallback<List<PlaceWebUser>>() {
			
			@Override
			public void onSuccess(List<PlaceWebUser> result) {
				lstUser.clear();
				
				users = result;
				if(result != null){
					for(PlaceWebUser user : result){
						lstUser.addItem(user.getName(), String.valueOf(user.getId()));
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void onResize(ResizeEvent event) {
		int height = event.getHeight() - 110;
		lstUser.setHeight(height + "px");
	}
}
