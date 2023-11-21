package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.Group;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomDatagrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class GroupsDialog extends AbstractDialogBox {

	private static GroupsDialogUiBinder uiBinder = GWT.create(GroupsDialogUiBinder.class);

	interface GroupsDialogUiBinder extends UiBinder<Widget, GroupsDialog> {
	}
	
	@UiField
	SimplePanel panelContent;
	
	private MultiSelectionModel<Group> selectionModel;
	
	private boolean confirm = false;

	public GroupsDialog(String title, String header, List<Group> groups, List<Group> selectedGroups) {
		super(title, false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		selectionModel = new MultiSelectionModel<>();
		CustomDatagrid<Group> gridGroups = new CustomDatagrid<>(groups, selectionModel, 250, LabelsConstants.lblCnst.NoGroups(), header);
		gridGroups.loadItems(groups, selectedGroups);
		
		panelContent.setWidget(gridGroups);
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	public List<Group> getSelectedGroups() {
		List<Group> groups = new ArrayList<>();
		groups.addAll(selectionModel.getSelectedSet());
		return groups;
	}
	
	public boolean isConfirm() {
		return confirm;
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			hide();
		}
	};

	@Override
	public int getThemeColor() {
		return 0;
	}
}
