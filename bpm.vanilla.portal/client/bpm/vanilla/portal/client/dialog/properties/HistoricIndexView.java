package bpm.vanilla.portal.client.dialog.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.portal.client.services.SecurityService;
import bpm.vanilla.portal.client.services.SecurityService.TypeGroupRights;
import bpm.vanilla.portal.client.services.SecurityService.TypeItemGed;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class HistoricIndexView extends Composite {

	private static HistoricIndexUiBinder uiBinder = GWT.create(HistoricIndexUiBinder.class);

	interface HistoricIndexUiBinder extends UiBinder<Widget, HistoricIndexView> {
	}

	@UiField
	CaptionPanel panelDocManager, panelHistoric;
	
	@UiField
	CheckBox checkAvailableGed, checkRealtime, checkCreateEntry;

	@UiField
	Button btnLoadGroups;

	private MultiSelectionModel<Group> selectionHistorics = new MultiSelectionModel<Group>();

	private IWait waitPanel;
	private PortailRepositoryItem item;

	private List<Group> allGroups, historicGroups;

	public HistoricIndexView(IWait waitPanel, PortailRepositoryItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.item = item;

		panelDocManager.setCaptionText(ToolsGWT.lblCnst.DocumentManagerOptions());
		panelDocManager.setVisible(false);

		panelHistoric.setCaptionText(ToolsGWT.lblCnst.OptionsHistoric());
		panelHistoric.setVisible(false);
		
		checkAvailableGed.setText(ToolsGWT.lblCnst.IsAvailableInDocumentManager());
		checkAvailableGed.setValue(item.getItem().isAvailableGed());
		
		checkRealtime.setText(ToolsGWT.lblCnst.RealtimeIndexation());
		checkRealtime.setValue(item.getItem().isRealtimeGed());
		
		checkCreateEntry.setText(ToolsGWT.lblCnst.CreateEntry());
		checkCreateEntry.setValue(item.getItem().isCreateEntry());
		
		btnLoadGroups.setText(ToolsGWT.lblCnst.LoadGroups());
		
		updateCheckbox();
	}

	@UiHandler("btnLoadGroups")
	public void onLoadGroupClick(ClickEvent event) {
		loadGroups();
	}
	
	@UiHandler("checkAvailableGed")
	public void onCheckAvailableGed(final ValueChangeEvent<Boolean> event) {
		waitPanel.showWaitPart(true);
		
		SecurityService.Connect.getInstance().updateItemGed(item.getId(), event.getValue(), TypeItemGed.GED_AVAILABLE, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, "Unable to update the Item's Document Manager informations");
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(Void result) {
				item.getItem().setAvailableGed(event.getValue());
				
				if(!event.getValue()) {
					item.getItem().setRealtimeGed(false);
					checkRealtime.setValue(false);
					
					item.getItem().setCreateEntry(false);
					checkCreateEntry.setValue(false);
				}
				
				updateCheckbox();
				waitPanel.showWaitPart(false);
			}
		});
	}
	
	@UiHandler("checkRealtime")
	public void onCheckRealtime(final ValueChangeEvent<Boolean> event) {
		waitPanel.showWaitPart(true);
		
		SecurityService.Connect.getInstance().updateItemGed(item.getId(), event.getValue(), TypeItemGed.REALTIME, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, "Unable to update the Item's Document Manager informations");
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(Void result) {
				item.getItem().setRealtimeGed(event.getValue());
				
				if(!event.getValue()) {
					item.getItem().setCreateEntry(false);
					checkCreateEntry.setValue(false);
				}
				
				updateCheckbox();
				waitPanel.showWaitPart(false);
			}
		});
	}
	
	@UiHandler("checkCreateEntry")
	public void onCheckCreateEntry(final ValueChangeEvent<Boolean> event) {
		waitPanel.showWaitPart(true);
		
		SecurityService.Connect.getInstance().updateItemGed(item.getId(), event.getValue(), TypeItemGed.CREATE_ENTRY, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, "Unable to update the Item's Document Manager informations");
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(Void result) {
				item.getItem().setCreateEntry(event.getValue());
				updateCheckbox();
				waitPanel.showWaitPart(false);
			}
		});
	}
	
	private void updateCheckbox() {
		if(!checkAvailableGed.getValue()) {
			checkRealtime.setEnabled(false);
			checkCreateEntry.setEnabled(false);
		}
		else {
			checkRealtime.setEnabled(true);
			if(!checkRealtime.getValue()) {
				checkCreateEntry.setEnabled(false);
			}
			else {
				checkCreateEntry.setEnabled(true);
			}
		}
	}
	
	private void loadGroups() {
		waitPanel.showWaitPart(true);

		panelDocManager.setVisible(true);
		panelHistoric.setVisible(true);
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
				loadHistoricGroups(true);
			}
		});
	}

	private void loadHistoricGroups(final boolean keepGoing) {
		ReportingService.Connect.getInstance().getAvailableGroupsForHisto(item.getId(), new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableGetDisplayGroups());
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Group> result) {
				historicGroups = result != null ? result : new ArrayList<Group>();
				if (keepGoing) {
					setGroupsToPanel();
				}
				else {
					setAlreadySelectGroup(selectionHistorics, historicGroups);
				}
				waitPanel.showWaitPart(false);
			}
		});
	}

	private void setGroupsToPanel() {

		panelHistoric.add(new CustomDatagrid<Group>(allGroups, selectionHistorics, 150, LabelsConstants.lblCnst.NoGroup()));
		setAlreadySelectGroup(selectionHistorics, historicGroups);
		selectionHistorics.addSelectionChangeHandler(historicHandler);

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

	private SelectionChangeEvent.Handler historicHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			waitPanel.showWaitPart(true);

			Helper groups = getGroupChanges(selectionHistorics, historicGroups);

			SecurityService.Connect.getInstance().updateGroupRights(item.getId(), groups.getToAdd(), groups.getToRemove(), false, TypeGroupRights.HISTORIC, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableUpdateGroupRight());
					waitPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(Void result) {
					loadHistoricGroups(false);
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
