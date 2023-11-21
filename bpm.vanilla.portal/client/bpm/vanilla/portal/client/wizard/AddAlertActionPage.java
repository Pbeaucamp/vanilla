package bpm.vanilla.portal.client.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.utils.alert.AlertParameterItem;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;
import bpm.vanilla.platform.core.beans.alerts.ActionGateway;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.ActionWorkflow;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.utils.ToolsGWT;

public class AddAlertActionPage extends Composite implements IGwtPage {
	private static AddTaskDefinitionPageUiBinder uiBinder = GWT.create(AddTaskDefinitionPageUiBinder.class);

	interface AddTaskDefinitionPageUiBinder extends UiBinder<Widget, AddAlertActionPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	ListBox lstActions, lstState;

	@UiField
	LabelTextBox txtSubject, txtItem;
	
	@UiField
	LabelTextArea txtContent;
	
	@UiField
	HTMLPanel panelMail, panelRep, panelParameters;
	
	@UiField
	SimplePanel gridSubscribers;
	
	@UiField
	Button btnBrowseRep;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;
	private Alert currentAlert;
	private RepositoryItem item;
	
	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;
	private MultiSelectionModel<User> selectionModel;
	
	private List<User> selectedUsers = new ArrayList<User>();

	public AddAlertActionPage(IGwtWizard parent, int index, Alert currentAlert) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.currentAlert = currentAlert;
		
		lstActions.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		lstState.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnBrowseRep.addStyleName(VanillaCSS.COMMONS_BUTTON);
		
		lstState.addItem(ToolsGWT.lblCnst.Inactive());
		lstState.addItem(ToolsGWT.lblCnst.Active());
		
		initActions();
		initMail();
		initPanelRepository();
		
		txtSubject.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				AddAlertActionPage.this.parent.updateBtn();
			}
		}, KeyUpEvent.getType());
		txtContent.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				AddAlertActionPage.this.parent.updateBtn();
			}
		}, KeyUpEvent.getType());
	}
	
	private void initActions() {
		for(TypeAction act : TypeAction.values()){
			lstActions.addItem(act.getLabel(), act.toString());
		}
		
		if(currentAlert.getAction() != null && currentAlert.getAction().getActionType() != null){
			for(int i=0; i<lstActions.getItemCount(); i++){
				if(lstActions.getValue(i).equals(currentAlert.getAction().getActionType().toString())){
					lstActions.setSelectedIndex(i);
					
				}
			}
		}
		onActionChange(null);
		lstState.setSelectedIndex(currentAlert.getState()); // 0 inactive ou non defini, 1 active
	}
	
	private void initMail() {
		gridSubscribers.add(UserGridData());
		CommonService.Connect.getInstance().getUsers(new GwtCallbackWrapper<List<User>>(parent, true) {
			@Override
			public void onSuccess(List<User> result) {
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				
				if(currentAlert.getAction() != null && currentAlert.getAction().getActionType() != null && 
						currentAlert.getAction().getActionType().equals(TypeAction.MAIL)){
					ActionMail actmail = (ActionMail) currentAlert.getAction().getActionObject();
					txtSubject.setText(actmail.getSubject());
					txtContent.setText(actmail.getContent());
					
					for(Subscriber sub : actmail.getSubscribers()){
						for(User user : result){
							if(sub.getUserId() == user.getId()){
								selectionModel.setSelected(user, true);
							}
						}
					}
				}
				
			}
		}.getAsyncCallback());
	}
	
	private void initPanelRepository() {
		txtItem.setEnabled(false);
		if(currentAlert.getAction() != null && currentAlert.getAction().getActionType() != null && 
				!currentAlert.getAction().getActionType().equals(TypeAction.MAIL)){
			int itemId = 0;
			final String typeName;
			if(currentAlert.getAction().getActionType().equals(TypeAction.GATEWAY)){
				itemId = ((ActionGateway) currentAlert.getAction().getActionObject()).getDirectoryItemId();
				typeName = IRepositoryApi.BIG;
			} else if (currentAlert.getAction().getActionType().equals(TypeAction.WORKFLOW)){
				itemId = ((ActionWorkflow) currentAlert.getAction().getActionObject()).getDirectoryItemId();
				typeName = IRepositoryApi.BIW;
			} else {
				typeName = "";
			}
		
			CommonService.Connect.getInstance().getItemById(itemId, new GwtCallbackWrapper<RepositoryItem>(parent, true) {
				@Override
				public void onSuccess(RepositoryItem result) {
					item = result;
					txtItem.setText(item.getName());
					
					showItemParameters(item, typeName);
					parent.updateBtn();
				}
			}.getAsyncCallback());
		} else {
			item = null;
			txtItem.setText("");
			parent.updateBtn();
		}
	}

	private DataGrid<User> UserGridData() {

		TextCell cell = new TextCell();
		Column<User, Boolean> checkboxColumn = new Column<User, Boolean>(
				new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(User object) {
				return selectionModel.isSelected(object);
			}
		};

		Column<User, String> nameColumn = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		
		checkboxColumn.setFieldUpdater(new FieldUpdater<User, Boolean>() {
			
			@Override
			public void update(int index, User object, Boolean value) {
				
				selectionModel.setSelected(object, value);	
			}
		});

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<User> dataGrid = new DataGrid<User>(99);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(checkboxColumn, "");
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.Users());
		dataGrid.setColumnWidth(checkboxColumn, 15.0, Unit.PCT);
		dataGrid.setColumnWidth(nameColumn, 85.0, Unit.PCT);

		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<User>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<User>(new ArrayList<User>());
		sortHandler.setComparator(nameColumn, new Comparator<User>() {

			@Override
			public int compare(User m1, User m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new MultiSelectionModel<User>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);
		

		return dataGrid;
	}
	
	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			selectedUsers.clear();
			selectedUsers.addAll(selectionModel.getSelectedSet());
			parent.updateBtn();
		}
	};

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		if(lstActions.getValue(lstActions.getSelectedIndex()).equals(TypeAction.MAIL.toString())){
			return !txtContent.getText().equals("") && !txtSubject.getText().equals("") && getSubscribers().size() > 0;
		} else {
			return !txtItem.getText().equals("");
		}
		
	}
	
//	@UiHandler("txtItem")
//	public void onNameChange(KeyUpEvent event) {
//		parent.updateBtn();
//	}
	
	@UiHandler("btnBrowseRep")
	public void onBrowse(ClickEvent e) {
		int repositoryType = 0;
		switch(getSelectedActionType()){
		case GATEWAY:
			repositoryType = IRepositoryApi.GTW_TYPE;
			break;
		case MAIL:
			return;
		case WORKFLOW:
			repositoryType = IRepositoryApi.BIW_TYPE;
			break;
		default:
			repositoryType = IRepositoryApi.BIW_TYPE;
			break;
		
		
		}
		
		final RepositoryDialog dial = new RepositoryDialog(repositoryType);
		
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					String typeName;
					if(lstActions.getValue(lstActions.getSelectedIndex()).equals(TypeAction.GATEWAY.toString())){
						typeName = IRepositoryApi.BIG;
					} else if (lstActions.getValue(lstActions.getSelectedIndex()).equals(TypeAction.WORKFLOW.toString())){
						typeName = IRepositoryApi.BIW;
					} else {
						typeName = "";
					}
					
					item = dial.getSelectedItem();
					txtItem.setText(item.getName());
					
					showItemParameters(item, typeName);
					parent.updateBtn();
				}
			}
		});
		dial.center();
	

	}
	
	@UiHandler("lstActions")
	public void onActionChange(ChangeEvent event) {
		if(lstActions.getValue(lstActions.getSelectedIndex()).equals(TypeAction.MAIL.toString())){
			panelMail.setVisible(true);
			panelRep.setVisible(false);
		} else {
			panelRep.setVisible(true);
			panelMail.setVisible(false);
		}
		parent.updateBtn();
	}
	
	private void showItemParameters(RepositoryItem item, String typeName){
		ReportingService.Connect.getInstance().getLaunchReportInformations(new PortailRepositoryItem(item, typeName), biPortal.get().getInfoUser().getGroup(), new GwtCallbackWrapper<LaunchReportInformations>(parent, true) {
			@Override
			public void onSuccess(LaunchReportInformations result) {
				panelParameters.clear();
				for(VanillaGroupParameter paramGroup : result.getGroupParameters()){
					for(VanillaParameter param : paramGroup.getParameters()){
						AlertParameterItem api = new AlertParameterItem(param);
						if(currentAlert.getAction() != null && currentAlert.getAction().getActionType() != null){
							if(currentAlert.getAction().getActionType().equals(TypeAction.GATEWAY)){
								api.setValue(((ActionGateway) currentAlert.getAction().getActionObject()).getParameters().get(param));
							} else if (currentAlert.getAction().getActionType().equals(TypeAction.WORKFLOW)){
								api.setValue(((ActionWorkflow) currentAlert.getAction().getActionObject()).getParameters().get(param));
							}
						}
						panelParameters.add(api);
					}
					
				}
			}
		}.getAsyncCallback());
	}

	public int getState() {
		return lstState.getSelectedIndex();
	}
	
	public Action getAction() {
		
		Action action;
		if(currentAlert.getAction() != null){
			action = currentAlert.getAction();
		} else {
			action = new Action();
		}
		action.setActionType(getSelectedActionType());
		
		switch(getSelectedActionType()){
		case GATEWAY:
			ActionGateway gtw = new ActionGateway();
			gtw.setDirectoryItemId(item.getId());
			gtw.setRepositoryId(biPortal.get().getInfoUser().getRepository().getId());
			gtw.setParameters(getParameters());
			action.setActionObject(gtw);
			break;
		case MAIL:
			ActionMail mail = new ActionMail();
			mail.setSubject(txtSubject.getText());
			mail.setContent(txtContent.getText());
			mail.setSubscribers(getSubscribers());
			action.setActionObject(mail);
			break;
		case WORKFLOW:
			ActionWorkflow biw = new ActionWorkflow();
			biw.setDirectoryItemId(item.getId());
			biw.setRepositoryId(biPortal.get().getInfoUser().getRepository().getId());
			biw.setParameters(getParameters());
			action.setActionObject(biw);
			break;
		default:
			break;
		}
		
		return action;
	}
	
	public List<Subscriber> getSubscribers() {
		List<Subscriber> subs = new ArrayList<Subscriber>();
		for(User user: selectionModel.getSelectedSet()){
			Subscriber sub = new Subscriber();
			sub.setUserId(user.getId());
			sub.setUserMail(user.getBusinessMail());
			sub.setUserName(user.getName());
			subs.add(sub);
		}
		
		return subs;
	}
	
	public Map<VanillaParameter, String> getParameters() {
		Map<VanillaParameter, String> parameters = new HashMap<VanillaParameter, String>();
		for(int i=0; i<panelParameters.getWidgetCount(); i++){
			if(panelParameters.getWidget(i) instanceof AlertParameterItem){
				AlertParameterItem api = (AlertParameterItem) panelParameters.getWidget(i);
				parameters.put(api.getParameter(), api.getValue());
			}
		}
		
		return parameters;
	}
	
	public TypeAction getSelectedActionType() {
		for(TypeAction act : TypeAction.values()){
			if(lstActions.getValue(lstActions.getSelectedIndex()).equals(act.toString())){
				return act;
			}
		}
		return null;
	}
}
