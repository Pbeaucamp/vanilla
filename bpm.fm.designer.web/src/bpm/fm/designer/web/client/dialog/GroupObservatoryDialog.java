package bpm.fm.designer.web.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Observatory;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.ConnectionServices;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class GroupObservatoryDialog extends AbstractDialogBox {

	private static GroupObservatoryDialogUiBinder uiBinder = GWT.create(GroupObservatoryDialogUiBinder.class);

	interface GroupObservatoryDialogUiBinder extends UiBinder<Widget, GroupObservatoryDialog> {
	}
	
	interface MyStyle extends CssResource {
		String grid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, gridPanel;

	private MultiSelectionModel<Group> selectionModel = new MultiSelectionModel<Group>();
	
	private List<Group> groups;
	private Observatory observatory;

	public GroupObservatoryDialog(Observatory observatory) {
		super(Messages.lbl.linkGroupObs(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.observatory = observatory;
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		loadGroups();
	}
	
	private void loadGroups() {
		ConnectionServices.Connection.getInstance().getAllGroups(new AsyncCallback<List<Group>>() {		
			@Override
			public void onSuccess(List<Group> result) {
				groups = result;		
				
				for(Group g : observatory.getGroups()) {
					selectionModel.setSelected(result.get(result.indexOf(g)), true);
				}
				
				createGrid();
				
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
		
	}

	private void createGrid() {
		
		gridPanel.clear();
		
		CustomDatagrid<Group> dg = new CustomDatagrid<Group>(groups, selectionModel, 300, "No group available");
		gridPanel.add(dg);
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			List<Group> groups = new ArrayList<Group>(selectionModel.getSelectedSet());
			
			MetricService.Connection.getInstance().updateGroupForObservatory(groups, observatory, new AsyncCallback<Void>() {			
				@Override
				public void onSuccess(Void result) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.successSaveGroupTheme(), false);
					dial.center();
					GroupObservatoryDialog.this.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
			
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			GroupObservatoryDialog.this.hide();
		}
	};

}
