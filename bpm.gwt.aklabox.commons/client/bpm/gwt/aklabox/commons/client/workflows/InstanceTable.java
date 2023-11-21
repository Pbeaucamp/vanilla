package bpm.gwt.aklabox.commons.client.workflows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.observerpattern.Observer;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.AklaTextBox;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

@SuppressWarnings("deprecation")
public class InstanceTable extends CompositeWaitPanel implements Observer{

	private static InstanceTableUiBinder uiBinder = GWT.create(InstanceTableUiBinder.class);

	interface InstanceTableUiBinder extends UiBinder<Widget, InstanceTable> {
	}

	@UiField
	HTMLPanel panel, sortPanel, tableContentPanel, filterPanel, filterWidgetPanel, datePanel,titleInstanceTable, actionPanel, actionWidgetPanel;
	@UiField
	Label filterInstance, filterStatus, filterVersionNumber, filterUser, filterInstanceDate, lblDateFrom, lblDateTo;

	@UiField
	AklaTextBox txtFilter;
	@UiField
	ListBox lstFilter, lstFilterStatus, lstDelegation, lstAction, lstFilterType;
	@UiField
	CheckBox cbAll;

	private List<Instance> listInstance = new ArrayList<Instance>();
	private boolean isAscending = true;
	private Date fromDate = new Date();
	private Date toDate = new Date();
	private DatePicker fromDatePicker = new DatePicker();
	private DatePicker toDatePicker = new DatePicker();
	private PopupPanel pop = new PopupPanel();
	private ConsoleWindow console;
	
	private List<InstanceTableItem> allRows = new ArrayList<>();
	private User user;

	public InstanceTable(User user) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.user = user;

//		UserMain.getInstance().registerObserver(this);
//		setColor();
		
		filterWidgetPanel.clear();
		filterWidgetPanel.add(txtFilter);

		console = new ConsoleWindow(this);
		panel.add(console);

		fromDatePicker.setCurrentMonth(fromDate);
		toDatePicker.setCurrentMonth(toDate);

		lblDateFrom.setText(DateTimeFormat.getMediumDateFormat().format(fromDate));
		lblDateTo.setText(DateTimeFormat.getMediumDateFormat().format(toDate));
		
		lstFilter.addItem(LabelsConstants.lblCnst.Instance());
		lstFilter.addItem(LabelsConstants.lblCnst.Status());
		lstFilter.addItem(LabelsConstants.lblCnst.VersionNumber());
		lstFilter.addItem(LabelsConstants.lblCnst.Creator());
		lstFilter.addItem(LabelsConstants.lblCnst.CreationDate());
		lstFilter.addItem(LabelsConstants.lblCnst.CurrentAssignedUser());
		lstFilter.addItem(LabelsConstants.lblCnst.Type());
		
		lstFilterStatus.addItem(LabelsConstants.lblCnst.Running());
		lstFilterStatus.addItem(LabelsConstants.lblCnst.Stopped());
		lstFilterStatus.addItem(LabelsConstants.lblCnst.OnStandBy());
		lstFilterStatus.addItem(LabelsConstants.lblCnst.Finished());
		
		lstAction.addItem(LabelsConstants.lblCnst.Start());
		lstAction.addItem(LabelsConstants.lblCnst.Restart());
		lstAction.addItem(LabelsConstants.lblCnst.StandBy());
		lstAction.addItem(LabelsConstants.lblCnst.Stop());
		lstAction.addItem(LabelsConstants.lblCnst.Delegate());
		
		lstFilterType.addItem(LabelsConstants.lblCnst.Simple(), Type.SIMPLE.name());
		lstFilterType.addItem(LabelsConstants.lblCnst.Master(), Type.MASTER.name());
		lstFilterType.addItem(LabelsConstants.lblCnst.OrbeonForm(), Type.ORBEON.name());
		
		actionWidgetPanel.setVisible(false);

		onRegisterDatePickerHandler();
		getAllInstance();
		
		getAllUsers();
		
		if(!user.getSuperUser()) actionPanel.setVisible(false);
	}

	private void onRegisterDatePickerHandler() {
		fromDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				fromDate = event.getValue();
				lblDateFrom.setText(DateTimeFormat.getMediumDateFormat().format(fromDate));
				pop.clear();
				pop.hide();
			}
		});
		toDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				toDate = event.getValue();
				lblDateTo.setText(DateTimeFormat.getMediumDateFormat().format(toDate));
				pop.clear();
				pop.hide();
			}
		});
	}

	private void getAllInstance() {
		showWaitPart(true);
		AklaCommonService.Connect.getService().getAllInstanceByUser(new AsyncCallback<List<Instance>>() {

			@Override
			public void onSuccess(List<Instance> result) {
				showWaitPart(false);
				buildInstanceTable(result, filterInstance);
				
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				showWaitPart(false);
			}
		});
	}

	private void buildInstanceTable(List<Instance> listInstance, Label lblSort) {
		this.listInstance = listInstance;
		tableContentPanel.clear();
		allRows.clear();
		for (Widget w : sortPanel) {
			w.removeStyleName("asc");
			w.removeStyleName("desc");
		}
		if (isAscending) {
			lblSort.addStyleName("asc");
			lblSort.removeStyleName("desc");
			isAscending = false;
		}
		else {
			lblSort.addStyleName("desc");
			lblSort.removeStyleName("asc");
			isAscending = true;
		}
		for (Instance instance : listInstance) {
			InstanceTableItem it = new InstanceTableItem(instance, this);
			tableContentPanel.add(it);
			allRows.add(it);
		}
	}

	private void sortList(final int i, Label lblSort) {
		Collections.sort(listInstance, new Comparator<Instance>() {

			@Override
			public int compare(Instance o1, Instance o2) {
				switch (i) {
				case 0:
					if (isAscending) {
						return o1.getInstanceName().compareTo(o2.getInstanceName());
					}
					else {
						return o2.getInstanceName().compareTo(o1.getInstanceName());
					}
				case 1:
					if (isAscending) {
						return o1.getInstanceStatus().compareTo(o2.getInstanceStatus());
					}
					else {
						return o2.getInstanceStatus().compareTo(o1.getInstanceStatus());
					}
				case 2:
					if (isAscending) {
						return o1.getVersionNumber() - o2.getVersionNumber();
					}
					else {
						return o2.getVersionNumber() - o1.getVersionNumber();
					}
				case 3:
					if (isAscending) {
						return o1.getUserEmail().compareTo(o2.getUserEmail());
					}
					else {
						return o2.getUserEmail().compareTo(o1.getUserEmail());
					}
				case 4:
					if (isAscending) {
						return o1.getInstanceDate().compareTo(o2.getInstanceDate());
					}
					else {
						return o2.getInstanceDate().compareTo(o1.getInstanceDate());
					}
				default:
					return 0;
				}
			}

		});
		buildInstanceTable(listInstance, lblSort);
	}

	@UiHandler("filterInstance")
	void onFilterName(ClickEvent e) {
		sortList(0, filterInstance);
	}

	@UiHandler("filterStatus")
	void onFilterStatus(ClickEvent e) {
		sortList(1, filterStatus);
	}

	@UiHandler("filterVersionNumber")
	void onFilterVersionNumber(ClickEvent e) {
		sortList(2, filterVersionNumber);
	}

	@UiHandler("filterUser")
	void onFilterUser(ClickEvent e) {
		sortList(3, filterUser);
	}

	@UiHandler("filterInstanceDate")
	void onFilterInstanceDate(ClickEvent e) {
		sortList(4, filterInstanceDate);
	}

	@UiHandler("lstFilter")
	void onChangeFilter(ChangeEvent e) {
		filterWidgetPanel.clear();
		txtFilter.setText("");
		switch (lstFilter.getSelectedIndex()) {
		case 0:
			filterWidgetPanel.add(txtFilter);
			break;
		case 1:
			filterWidgetPanel.add(lstFilterStatus);
			break;
		case 2:
			filterWidgetPanel.add(txtFilter);
			break;
		case 3:
			filterWidgetPanel.add(txtFilter);
			break;
		case 4:
			filterWidgetPanel.add(datePanel);
			break;
		case 5:
			filterWidgetPanel.add(txtFilter);
			break;
		case 6:
			filterWidgetPanel.add(lstFilterType);
			break;
		default:
			break;
		}
	}

	@UiHandler("btnFilter")
	void onFilter(ClickEvent e) {
		List<Instance> filteredInstance = new ArrayList<Instance>();
		for (Instance i : listInstance) {
			switch (lstFilter.getSelectedIndex()) {
			case 0:
				if (i.getInstanceName().toLowerCase().contains(txtFilter.getText().toLowerCase())) {
					filteredInstance.add(i);
				}
				break;
			case 1:
				switch (lstFilterStatus.getSelectedIndex()) {
				case 0:
					if (i.getInstanceStatus().contains(lstFilterStatus.getItemText(0))) {
						filteredInstance.add(i);
					}
					break;
				case 1:
					if (i.getInstanceStatus().contains(lstFilterStatus.getItemText(1))) {
						filteredInstance.add(i);
					}
					break;
				case 2:
					if (i.getInstanceStatus().contains(lstFilterStatus.getItemText(2))) {
						filteredInstance.add(i);
					}
					break;

				default:
					break;
				}
			case 2:
				if (String.valueOf(i.getVersionNumber()).equals(txtFilter.getText())) {
					filteredInstance.add(i);
				}
				break;
			case 3:
				if (i.getUserEmail().toLowerCase().contains(txtFilter.getText().toLowerCase())) {
					filteredInstance.add(i);
				}
				break;
			case 4:
				if (i.getInstanceDate().after(fromDate) && i.getInstanceDate().before(toDate)) {
					filteredInstance.add(i);
				}
				break;
			case 5:
				if (getInsanceTableItem(i).getAssignedUser().toLowerCase().contains(txtFilter.getText().toLowerCase())) {
					filteredInstance.add(i);
				}
				break;
			case 6:
				if (i.getWorkflow().getTypeWorkflow() == Type.valueOf(lstFilterType.getValue(lstFilterType.getSelectedIndex()))) {
					filteredInstance.add(i);
				}
				break;
			default:
				break;
			}
		}
		tableContentPanel.clear();
		for (Instance instance : filteredInstance) {
			tableContentPanel.add(new InstanceTableItem(instance, this));
		}

	}

	@UiHandler("btnDateFrom")
	void onSetFromDate(ClickEvent e) {
		pop.add(fromDatePicker);
		pop.setPopupPosition(e.getClientX(), e.getClientY());
		pop.setAutoHideEnabled(true);
		pop.addPopupListener(new PopupListener() {

			@Override
			public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
				pop.clear();
			}
		});
		pop.show();
	}

	@UiHandler("btnDateTo")
	void onSetToDate(ClickEvent e) {
		pop.add(toDatePicker);
		pop.setPopupPosition(e.getClientX(), e.getClientY());
		pop.setAutoHideEnabled(true);
		pop.addPopupListener(new PopupListener() {

			@Override
			public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
				pop.clear();
			}
		});
		pop.show();
	}

	public ConsoleWindow getConsole() {
		return console;
	}

	public void setConsole(ConsoleWindow console) {
		this.console = console;
	}

//	public void setColor(){
//		ThemeColorManager.setTitleColor(UserMain.getInstance().getUser().getSelectedTheme(), titleInstanceTable);	
//	}

	@Override
	public void notifyObserver() {
//		setColor();
	}
	
	@UiHandler("cbAll")
	public void onClickAll(ClickEvent e){
		for(Widget w : tableContentPanel){
			if(w instanceof InstanceTableItem){
				((InstanceTableItem)w).setSelected(cbAll.getValue());
			}
		}
	}

	public void manageDelegation() {
		// TODO Nothing yet
		//lstDelegation.clear();
		
	}
	
	@UiHandler("btnAct")
	void onClickAction(ClickEvent e) {
		for(Widget w : tableContentPanel){
			if(w instanceof InstanceTableItem){
				InstanceTableItem item = ((InstanceTableItem)w);
				if(item.isSelected()){
					switch (lstAction.getSelectedIndex()) {
					case 0:
						item.onStartInstance(null);
						break;
					case 1:
						item.onRestartInstance(null);
						break;
					case 2:
						item.onStandByInstance(null);
						break;
					case 3:
						item.onStopInstance(null);
						break;
					case 4:
						item.delegateInstance(lstDelegation.getValue(lstDelegation.getSelectedIndex()));
						break;
					default:
						break;
					}
				}
				
			}
		}
		
		
	}
	
	@UiHandler("lstAction")
	void onChangeAction(ChangeEvent e) {
		switch (lstAction.getSelectedIndex()) {
		case 0:
		case 1:
		case 2:
		case 3:
			actionWidgetPanel.setVisible(false);
			break;
		case 4:
			actionWidgetPanel.setVisible(true);
			break;
		default:
			break;
		}
	}
	
	private void getAllUsers() {
		WaitDialog.showWaitPart(true);
		AklaCommonService.Connect.getService().getAllUsers(new AsyncCallback<List<User>>() {

			@Override
			public void onSuccess(List<User> result) {
				WaitDialog.showWaitPart(false);
				Collections.sort(result, new Comparator<User>() {

					@Override
					public int compare(User o1, User o2) {
						return o1.getEmail().compareToIgnoreCase(o2.getEmail());
					}
				});
				for(User u : result){
					lstDelegation.addItem(u.getEmail(), u.getUserId()+"");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				new DefaultResultDialog(caught.getMessage(), "failure").show();
				WaitDialog.showWaitPart(false);
			}
		});
	}
	
	private InstanceTableItem getInsanceTableItem(Instance instance){
		for(InstanceTableItem item : allRows){
			if(item.getInstance().getId() == instance.getId()){
				return item;
			}
		}
		return null;
	}

	public User getUser() {
		return user;
	}
	
	
}
