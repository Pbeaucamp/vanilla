package bpm.vanilla.portal.client.dialog.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.portal.client.services.SecurityService;
import bpm.vanilla.portal.client.services.SecurityService.TypeGroupRights;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class ItemRightView extends Composite {

	private static ItemRightViewUiBinder uiBinder = GWT.create(ItemRightViewUiBinder.class);

	interface ItemRightViewUiBinder extends UiBinder<Widget, ItemRightView> {
	}

	@UiField
	CaptionPanel panelAvailable, panelExecutable, panelCommentable, panelSecurity;

	@UiField
	Button btnLoadGroups;

	private MultiSelectionModel<Group> selectionAvailable = new MultiSelectionModel<Group>();
	private MultiSelectionModel<Group> selectionExecutable = new MultiSelectionModel<Group>();
	private MultiSelectionModel<Group> selectionCommentable = new MultiSelectionModel<Group>();
	private MultiSelectionModel<Group> selectionSecurity = new MultiSelectionModel<Group>();

	private IWait waitPanel;
	private int itemId;
	private int type;
	private boolean isDirectory;

	private List<Group> allGroups, availableGroups, executableGroups, commentableGroups, securityGroups;

	public ItemRightView(IWait waitPanel, int itemId, int type, boolean isDirectory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.itemId = itemId;
		this.type = type;
		this.isDirectory = isDirectory;

		panelAvailable.setCaptionText(ToolsGWT.lblCnst.AvailableForGroups());
		panelAvailable.setVisible(true);

		panelExecutable.setCaptionText(ToolsGWT.lblCnst.ExecutableForGroups());
		panelExecutable.setVisible(true);

		panelCommentable.setCaptionText(ToolsGWT.lblCnst.CommentableForGroups());
		panelCommentable.setVisible(true);

		panelSecurity.setCaptionText(ToolsGWT.lblCnst.ProjectionForGroups());
		panelSecurity.setVisible(true);

		btnLoadGroups.setText(ToolsGWT.lblCnst.LoadGroups());
		panelAvailable.setVisible(false);
		panelExecutable.setVisible(false);
		panelCommentable.setVisible(false);
		panelSecurity.setVisible(false);
	}

	@UiHandler("btnLoadGroups")
	public void onLoadGroupClick(ClickEvent event) {
		loadGroups();
	}

	private void loadGroups() {
		waitPanel.showWaitPart(true);

		panelAvailable.setVisible(true);
		panelExecutable.setVisible(true);
		panelCommentable.setVisible(true);
		panelSecurity.setVisible(true);
		btnLoadGroups.setVisible(false);

		SecurityService.Connect.getInstance().getAllGroups(new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableGetAllGroups());
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				allGroups = result != null ? result : new ArrayList<Group>();
				loadAvailableGroups(true);
			}
		});
	}

	private void loadAvailableGroups(final boolean keepGoing) {
		SecurityService.Connect.getInstance().getAvailableGroupsForDisplay(itemId, isDirectory, new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableGetDisplayGroups());
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				availableGroups = result != null ? result : new ArrayList<Group>();
				if (keepGoing) {
					loadExecutableGroups(true);
				}
				else {
					setAlreadySelectGroup(selectionAvailable, availableGroups);
					waitPanel.showWaitPart(false);
				}
			}
		});
	}

	private void loadExecutableGroups(final boolean keepGoing) {
		ReportingService.Connect.getInstance().getAvailableGroupsForRun(itemId, isDirectory, new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableGetExecutableGroups());
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				executableGroups = result != null ? result : new ArrayList<Group>();
				if (keepGoing) {
					loadCommentableGroups(true);
				}
				else {
					setAlreadySelectGroup(selectionExecutable, executableGroups);
					waitPanel.showWaitPart(false);
				}
			}
		});
	}

	private void loadCommentableGroups(final boolean keepGoing) {
		SecurityService.Connect.getInstance().getCommentableGroups(itemId, isDirectory, new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableGetCommentableGroups());
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				commentableGroups = result != null ? result : new ArrayList<Group>();
				if (keepGoing) {
					loadProjectionGroups(keepGoing);
				}
				else {
					setAlreadySelectGroup(selectionCommentable, commentableGroups);
					waitPanel.showWaitPart(false);
				}
			}
		});
	}

	private void loadProjectionGroups(final boolean keepGoing) {
		if (type == IRepositoryApi.FASD_TYPE) {
			SecurityService.Connect.getInstance().getGroupProjections(itemId, new AsyncCallback<List<Group>>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableGetProjectionGroups());
					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(List<Group> result) {
					securityGroups = result != null ? result : new ArrayList<Group>();
					if (keepGoing) {
						setGroupsToPanel(true);
					}
					else {
						setAlreadySelectGroup(selectionSecurity, securityGroups);
						waitPanel.showWaitPart(false);
					}
				}
			});
		}
		else {
			securityGroups = new ArrayList<Group>();
			if (keepGoing) {
				setGroupsToPanel(false);
			}
			else {
				setAlreadySelectGroup(selectionSecurity, securityGroups);
				waitPanel.showWaitPart(false);
			}
		}
	}

	private void setGroupsToPanel(boolean withProjection) {

		panelAvailable.add(new CustomDatagrid<Group>(allGroups, selectionAvailable, 150, LabelsConstants.lblCnst.NoGroup()));
		setAlreadySelectGroup(selectionAvailable, availableGroups);
		selectionAvailable.addSelectionChangeHandler(availableHandler);

		if(!isDirectory) {
			panelExecutable.add(new CustomDatagrid<Group>(allGroups, selectionExecutable, 150, LabelsConstants.lblCnst.NoGroup()));
			setAlreadySelectGroup(selectionExecutable, executableGroups);
			selectionExecutable.addSelectionChangeHandler(executableHandler);
		}
		else {
			panelExecutable.setVisible(false);
		}
			
		panelCommentable.add(new CustomDatagrid<Group>(allGroups, selectionCommentable, 150, LabelsConstants.lblCnst.NoGroup()));
		setAlreadySelectGroup(selectionCommentable, commentableGroups);
		selectionCommentable.addSelectionChangeHandler(commentableHandler);

		if(withProjection) {
			panelSecurity.add(new CustomDatagrid<Group>(allGroups, selectionSecurity, 150, LabelsConstants.lblCnst.NoGroup()));
			setAlreadySelectGroup(selectionSecurity, securityGroups);
			selectionSecurity.addSelectionChangeHandler(projectionHandler);
		}
		else {
			panelSecurity.setVisible(false);
		}

		waitPanel.showWaitPart(false);
	}

	private void setAlreadySelectGroup(MultiSelectionModel<Group> selectionModel, List<Group> groups) {
		if (groups != null && allGroups != null) {
			for (Group gr : groups) {
				for (Group allGr : allGroups) {
					if (gr.getId().equals(allGr.getId())) {
						selectionModel.setSelected(allGr, true);
						break;
					}
				}
			}
		}
	}

	private SelectionChangeEvent.Handler availableHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			waitPanel.showWaitPart(true);

			Helper groups = getGroupChanges(selectionAvailable, availableGroups);

			SecurityService.Connect.getInstance().updateGroupRights(itemId, groups.getToAdd(), groups.getToRemove(), isDirectory, TypeGroupRights.DISPLAY, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableUpdateGroupRight());
					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(Void result) {
					loadAvailableGroups(false);
				}
			});
		}
	};

	private SelectionChangeEvent.Handler executableHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			waitPanel.showWaitPart(true);

			Helper groups = getGroupChanges(selectionExecutable, executableGroups);

			SecurityService.Connect.getInstance().updateGroupRights(itemId, groups.getToAdd(), groups.getToRemove(), isDirectory, TypeGroupRights.EXECUTE, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableUpdateGroupRight());
					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(Void result) {
					loadExecutableGroups(false);
				}
			});
		}
	};

	private SelectionChangeEvent.Handler commentableHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			waitPanel.showWaitPart(true);

			Helper groups = getGroupChanges(selectionCommentable, commentableGroups);

			SecurityService.Connect.getInstance().updateGroupRights(itemId, groups.getToAdd(), groups.getToRemove(), isDirectory, TypeGroupRights.COMMENT, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableUpdateGroupRight());
					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(Void result) {
					loadCommentableGroups(false);
				}
			});
		}
	};

	private SelectionChangeEvent.Handler projectionHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			waitPanel.showWaitPart(true);

			Helper groups = getGroupChanges(selectionSecurity, securityGroups);

			SecurityService.Connect.getInstance().updateGroupRights(itemId, groups.getToAdd(), groups.getToRemove(), isDirectory, TypeGroupRights.PROJECTION, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableUpdateGroupRight());
					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(Void result) {
					loadProjectionGroups(false);
				}
			});
		}
	};

	private Helper getGroupChanges(MultiSelectionModel<Group> selectionModel, List<Group> toCheck) {
		List<Group> toAdd = new ArrayList<Group>();
		List<Group> toRemove = new ArrayList<Group>();
		if (allGroups != null) {
			for (Group gr : allGroups) {
				if (selectionModel.isSelected(gr)) {
					boolean found = false;
					for (Group selGr : toCheck) {
						if (selGr.getId().equals(gr.getId())) {
							found = true;
							break;
						}
					}

					if (!found) {
						toAdd.add(gr);
					}
				}
				else {
					boolean found = false;
					for (Group selGr : toCheck) {
						if (selGr.getId().equals(gr.getId())) {
							found = true;
							break;
						}
					}

					if (found) {
						toRemove.add(gr);
					}
				}
			}
		}

		return new Helper(toAdd, toRemove);
	}

	private class Helper {
		private List<Group> toAdd;
		private List<Group> toRemove;

		private Helper(List<Group> toAdd, List<Group> toRemove) {
			this.toAdd = toAdd;
			this.toRemove = toRemove;
		}

		private List<Group> getToAdd() {
			return toAdd;
		}

		private List<Group> getToRemove() {
			return toRemove;
		}
	}
}
