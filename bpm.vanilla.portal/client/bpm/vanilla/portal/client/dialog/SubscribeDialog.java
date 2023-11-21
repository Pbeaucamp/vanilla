package bpm.vanilla.portal.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository.ObjectEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.wizard.AddAlertWizard;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class SubscribeDialog extends AbstractDialogBox {

	private static SubscribeDialogUiBinder uiBinder = GWT.create(SubscribeDialogUiBinder.class);
	
	interface MyStyle extends CssResource {
		String grid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, gridPanel;
	
	@UiField
	Image imgAdd, imgRefresh;
	
	private DataGrid<Alert> dataGrid;
	private ListDataProvider<Alert> dataProvider;

	private SingleSelectionModel<Alert> selectionModel;
	
	private PortailRepositoryItem item;
	
	private List<Alert> createdByUser = new ArrayList<Alert>();

	interface SubscribeDialogUiBinder extends UiBinder<Widget, SubscribeDialog> {
	}

	public SubscribeDialog(PortailRepositoryItem item) {
		super(ToolsGWT.lblCnst.SubscribeToItem(), false, true);
		this.item = item;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		createGrid();
		
		gridPanel.add(dataGrid);
		
		refresh();
	}
	
	private void createGrid() {
		dataGrid = new DataGrid<Alert>(15);
		dataGrid.addStyleName(style.grid());
		
		TextCell cell = new TextCell();
		final Column<Alert, String> nameColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		
		final Column<Alert, String> descColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				return object.getDescription();
			}
		};
		descColumn.setSortable(true);
	    
		
		final Column<Alert, String> typeEventColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				return object.getEventObject().getSubtypeEventLabel();
			}
		};
		typeEventColumn.setSortable(true);
	    
		final Column<Alert, String> stateColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				if(object.getState() == 1){
					return ToolsGWT.lblCnst.Active();
				}
				else {
					return ToolsGWT.lblCnst.Inactive();
				}
			}
		};
		stateColumn.setSortable(true);
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.AlertName());
		dataGrid.addColumn(descColumn, ToolsGWT.lblCnst.Description());
		dataGrid.addColumn(typeEventColumn, ToolsGWT.lblCnst.TypeEvent());
		dataGrid.addColumn(stateColumn, ToolsGWT.lblCnst.State());
		
		dataGrid.setColumnWidth(nameColumn, 25, Unit.PCT);
		dataGrid.setColumnWidth(descColumn, 35, Unit.PCT);
		dataGrid.setColumnWidth(typeEventColumn, 20, Unit.PCT);
		dataGrid.setColumnWidth(stateColumn, 20, Unit.PCT);
		
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoData()));
		
		dataProvider = new ListDataProvider<Alert>();
		dataProvider.addDataDisplay(dataGrid);
	    
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    
	    selectionModel = new SingleSelectionModel<Alert>();
	    dataGrid.setSelectionModel(selectionModel);
		
		
	}
	
	private void refresh() {
		showWaitPart(true);
		AdminService.Connect.getInstance().getAlertsByTypeAndDirectoryItemId(TypeEvent.OBJECT_TYPE, item.getId(), biPortal.get().getInfoUser().getRepository().getId(), new AsyncCallback<List<Alert>>() {	
			@Override
			public void onSuccess(List<Alert> result) {
				showWaitPart(false);
				List<Alert> mailAlerts = new ArrayList<Alert>();
				for(Alert al : result){
					if(al.getTypeAction().equals(TypeAction.MAIL)){
						mailAlerts.add(al);
					}
				}
				dataProvider.setList(mailAlerts);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedGetAlerts());
			}
		});
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Alert selectedAlert = selectionModel.getSelectedObject();
			if(selectedAlert != null){
				if(justCreated(selectedAlert)){
					SubscribeDialog.this.hide();
				} else {
					if(isAlreadySubscribed(((ActionMail)selectedAlert.getAction().getActionObject()).getSubscribers())){
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), ToolsGWT.lblCnst.AlreadySubscribe());
					} else {
						showWaitPart(true);
						AdminService.Connect.getInstance().addSubscriber(selectedAlert, new AsyncCallback<Void>() {	
							@Override
							public void onSuccess(Void result) {
								showWaitPart(false);
								MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Success(), ToolsGWT.lblCnst.SubscribeSuccess());
								SubscribeDialog.this.hide();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								showWaitPart(false);
								caught.printStackTrace();
								ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.SubscribeFailure());
							}
						});
					}
				}		
			} else {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.PleaseSelectAnAlert());
			}
			
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			SubscribeDialog.this.hide();
		}
	};
	
	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		Alert alert = new Alert();
		alert.setTypeEvent(TypeEvent.OBJECT_TYPE);
		alert.setTypeAction(TypeAction.MAIL);
		
		ActionMail mail = new ActionMail();
		mail.addSubscriber(new Subscriber(biPortal.get().getInfoUser().getUser()));
		Action action = new Action();
		action.setActionType(TypeAction.MAIL);
		action.setActionObject(mail);
		
		AlertRepository rep = new AlertRepository();
		if(item.getType() == IRepositoryApi.CUST_TYPE){
			rep.setSubtypeEvent(ObjectEvent.BIRT);
		} else if(item.getType() == IRepositoryApi.FWR_TYPE){
			rep.setSubtypeEvent(ObjectEvent.FWR);
		} else if(item.getType() == IRepositoryApi.GTW_TYPE){
			rep.setSubtypeEvent(ObjectEvent.GTW);
		} else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.UnexpectedType());
			return;
		}
		rep.setDirectoryItemId(item.getId());
		rep.setRepositoryId(biPortal.get().getInfoUser().getRepository().getId());
		
		alert.setEventObject(rep);
		
		final AddAlertWizard dial = new AddAlertWizard(alert);
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(isAlreadySubscribed(((ActionMail)dial.getSelectedAlert().getAction().getActionObject()).getSubscribers())){
					createdByUser.add(dial.getSelectedAlert());
				}
				
				refresh();
			}
		});
		
		dial.center();
	}
	
	@UiHandler("imgRefresh")
	public void onRefresh(ClickEvent e) {
		refresh();
	}
	
	private boolean isAlreadySubscribed(List<Subscriber> subscribers) {
		for(Subscriber sub : subscribers){
			if(sub.getUserId() == biPortal.get().getInfoUser().getUser().getId()){
				return true;
			}
		}
		return false;
	}
	
	private boolean justCreated(Alert alert) {
		for(Alert al : createdByUser){
			if(al.getName().equals(alert.getName())){
				return true;
			}
		}
		return false;
	}

}
