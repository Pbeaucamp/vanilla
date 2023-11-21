package bpm.vanilla.workplace.client.admin.panel.project;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.workplace.client.admin.dialog.AddUsersDialog;
import bpm.vanilla.workplace.client.admin.dialog.ConfirmDeleteDialog;
import bpm.vanilla.workplace.client.admin.dialog.DialogInformation;
import bpm.vanilla.workplace.client.admin.dialog.ConfirmDeleteDialog.DeleteType;
import bpm.vanilla.workplace.client.admin.dialog.project.CreatePackageDialogBox;
import bpm.vanilla.workplace.client.admin.dialog.project.CreateProjectDialogBox;
import bpm.vanilla.workplace.client.admin.panel.project.tree.PackageClickHandler;
import bpm.vanilla.workplace.client.admin.panel.project.tree.ProjectClickHandler;
import bpm.vanilla.workplace.client.images.Images;
import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class PanelProject extends Composite {

	private static PanelProjectUiBinder uiBinder = GWT
			.create(PanelProjectUiBinder.class);

	interface PanelProjectUiBinder extends UiBinder<Widget, PanelProject> {
	}
	
	interface MyStyle extends CssResource {
		String btnTop();
		String btnAddUser();
		String btnRemoveUser();
	}

	@UiField
	SimplePanel panelProject;
	
	@UiField
	HTMLPanel panelUsers;
	
	@UiField
	Image btnAddProject, btnDeleteProject, btnAddPackage, btnDeletePackage, btnAddUsers, btnRemoveUser;
	
	@UiField
	MyStyle style;
	
	@UiField
	ListBox lstUsers;
	
	private List<PlaceWebProject> projects;
	private PlaceWebProject currentProject;
	private PlaceWebPackage currentPackage;
	private PlaceWebUser currentUser;
	
	private int userId;
	
	private Tree treeProject;
	
	private List<PlaceWebUser> users;
	
	public PanelProject(PlaceWebUser user) {
		initWidget(uiBinder.createAndBindUi(this));
		this.userId = user.getId();
		
		btnAddProject.setResource(Images.INSTANCE.add_project());
		btnAddProject.addStyleName(style.btnTop());
		
		btnDeleteProject.setResource(Images.INSTANCE.delete_project());
		btnDeleteProject.addStyleName(style.btnTop());
		btnDeleteProject.setVisible(false);
		
		btnAddPackage.setResource(Images.INSTANCE.add_packages());
		btnAddPackage.addStyleName(style.btnTop());
		btnAddPackage.setVisible(false);
		
		btnDeletePackage.setResource(Images.INSTANCE.delete_packages());
		btnDeletePackage.addStyleName(style.btnTop());
		btnDeletePackage.setVisible(false);
		
		treeProject = new Tree();
		panelProject.setWidget(treeProject);
		
		panelUsers.setVisible(false);
		
		btnAddUsers.setResource(Images.INSTANCE.add_user());
		btnAddUsers.addStyleName(style.btnAddUser());
		
		btnRemoveUser.setResource(Images.INSTANCE.remove_user());
		btnRemoveUser.addStyleName(style.btnRemoveUser());
		btnRemoveUser.setVisible(false);
		
		refreshTree();
	}
	
	public void refreshTree() {
		AdminService.Connect.getInstance().getProjects(new AsyncCallback<List<PlaceWebProject>>() {
			
			@Override
			public void onSuccess(List<PlaceWebProject> result) {
				if(result != null){
					projects = result;
					
					buildTree();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}
	
	private void buildTree(){
		treeProject.clear();
		
		for(PlaceWebProject proj : projects){
			HTML htmlProject = new HTML(new Image(Images.INSTANCE.project()) + " " + proj.getName());
			htmlProject.addClickHandler(new ProjectClickHandler(this, proj));
			
			TreeItem item = new TreeItem(htmlProject);
			for(PlaceWebPackage pack : proj.getPackages()){
				HTML htmlPackage = new HTML(new Image(Images.INSTANCE.packages()) + " " + pack.getName());
				htmlPackage.addClickHandler(new PackageClickHandler(this, pack));
				
				TreeItem itemPack = new TreeItem(htmlPackage);
				item.addItem(itemPack);
			}
			treeProject.addItem(item);
		}
	}
	
	public void setCurrentProject(PlaceWebProject project){
		this.currentProject = project;
		
		btnDeletePackage.setVisible(false);
		
		btnDeleteProject.setVisible(true);
		btnAddPackage.setVisible(true);
		
		panelUsers.setVisible(false);
	}
	
	public void setCurrentPackage(PlaceWebPackage currentPackage){
		this.currentPackage = currentPackage;
		
		btnDeletePackage.setVisible(true);
		
		btnDeleteProject.setVisible(false);
		btnAddPackage.setVisible(false);

		panelUsers.setVisible(true);
		btnRemoveUser.setVisible(false);
		
		refreshUsers();
	}
	
	private void refreshUsers(){
		AdminService.Connect.getInstance().getUsersAvailable(currentPackage.getId(), new AsyncCallback<List<PlaceWebUser>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<PlaceWebUser> result) {
				users = result;
				if(result != null){
					loadUsers();
				}
			}
		});
	}
	
	private void loadUsers(){
		lstUsers.clear();
		
		for(PlaceWebUser user : users){
			lstUsers.addItem(user.getName(), String.valueOf(user.getId()));
		}
	}
	
	@UiHandler("lstUsers")
	public void onUserSelection(ChangeEvent e){
		int userId = Integer.parseInt(lstUsers.getValue(lstUsers.getSelectedIndex()));
		for(PlaceWebUser user : users){
			if(user.getId() == userId){
				currentUser = user;
				btnRemoveUser.setVisible(true);
				break;
			}
		}
	}
	
	@UiHandler("btnRemoveUser")
	public void onRemovePackClick(ClickEvent e){
		if(currentPackage != null && currentUser != null){
			int userId = currentUser.getId();
			int packageId = currentPackage.getId();
			AdminService.Connect.getInstance().deleteLinkPackageForUser(userId, packageId, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					
					String infos = caught.getMessage();
					
					DialogInformation dialInfo = new DialogInformation(infos);
					dialInfo.setGlassEnabled(true);
					dialInfo.center();
					
					refreshUsers();
				}

				@Override
				public void onSuccess(Void result) {
					String infos = "Package deleted for this user.";
					
					DialogInformation dialInfo = new DialogInformation(infos);
					dialInfo.setGlassEnabled(true);
					dialInfo.center();
					
					refreshUsers();
				}
			});
		}
	}
	
	@UiHandler("btnAddUsers")
	public void onAddUsersClick(ClickEvent e) {
		AdminService.Connect.getInstance().getAllUser(new AsyncCallback<List<PlaceWebUser>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<PlaceWebUser> result) {
				if(result != null){
					if(users != null){
						List<PlaceWebUser> usersCanBeAdd = new ArrayList<PlaceWebUser>();
						for(PlaceWebUser user : result){
							boolean found = false;
							for(PlaceWebUser userTmp : users){
								if(user.getId() == userTmp.getId()){
									found = true;
									break;
								}
							}
							
							if(!found){
								usersCanBeAdd.add(user);
							}
						}
						
						AddUsersDialog dialAdd = new AddUsersDialog(currentPackage.getId(), usersCanBeAdd);
						dialAdd.setGlassEnabled(true);
						dialAdd.center();
						dialAdd.addCloseHandler(new CloseHandler<PopupPanel>() {

							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								refreshUsers();
							}
						});
					}
					else {
						AddUsersDialog dialAdd = new AddUsersDialog(currentPackage.getId(), result);
						dialAdd.setGlassEnabled(true);
						dialAdd.center();
						dialAdd.addCloseHandler(new CloseHandler<PopupPanel>() {

							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								refreshUsers();
							}
						});
					}
				}
			}
		});
	}
	
	@UiHandler("btnAddProject")
	public void onAddProjectClick(ClickEvent e){
		CreateProjectDialogBox dial = new CreateProjectDialogBox(userId);
		dial.setGlassEnabled(true);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshTree();
			}
		});
	}
	
	@UiHandler("btnDeleteProject")
	public void onDeleteProjectClick(ClickEvent event){
		if(currentProject != null){
			ConfirmDeleteDialog dial = new ConfirmDeleteDialog(userId, currentProject.getId(), null, 
					"Do you really want to delete this project?", DeleteType.PROJECT);
			dial.setGlassEnabled(true);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshTree();
				}
			});
		}
	}
	
	@UiHandler("btnAddPackage")
	public void onAddPackageClick(ClickEvent event){
		if(currentProject != null){
			CreatePackageDialogBox dial = new CreatePackageDialogBox(currentProject.getId(), userId);
			dial.setGlassEnabled(true);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
	
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshTree();
				}
			});
		}
	}
	
	@UiHandler("btnDeletePackage")
	public void onDeletePackageClick(ClickEvent event){
		if(currentPackage != null){
			ConfirmDeleteDialog dial = new ConfirmDeleteDialog(userId, currentProject.getId(), 
					currentPackage.getId(), "Do you really want to delete this package?", 
					DeleteType.PACKAGE);
			dial.setGlassEnabled(true);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshTree();
				}
			});
		}
	}
}
