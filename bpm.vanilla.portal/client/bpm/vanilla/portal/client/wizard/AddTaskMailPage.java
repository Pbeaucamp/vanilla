package bpm.vanilla.portal.client.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class AddTaskMailPage extends Composite implements IGwtPage {
	
	private static AddTaskMailPageUiBinder uiBinder = GWT.create(AddTaskMailPageUiBinder.class);

	interface AddTaskMailPageUiBinder extends UiBinder<Widget, AddTaskMailPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	LabelTextBox txtSubject;
	
	@UiField
	LabelTextArea txtContent;
	
	@UiField
	SimplePanel gridSubscribers;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;
	
	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;
	private MultiSelectionModel<User> selectionModel;
	
	private List<User> selectedUsers = new ArrayList<User>();

	public AddTaskMailPage(IGwtWizard parent, int index, JobDetail details) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;

		gridSubscribers.add(UserGridData());
		initMail(details);
		
		txtSubject.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				AddTaskMailPage.this.parent.updateBtn();
			}
		}, KeyUpEvent.getType());
		txtContent.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				AddTaskMailPage.this.parent.updateBtn();
			}
		}, KeyUpEvent.getType());
	}
	
	private void initMail(final JobDetail details) {
		CommonService.Connect.getInstance().getUsers(new GwtCallbackWrapper<List<User>>(parent, false) {
			@Override
			public void onSuccess(List<User> result) {
				parent.showWaitPart(false);
				
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				
				txtSubject.setText(details != null ? details.getSubject() : null);
				txtContent.setText(details != null ? details.getContent() : null);
				
				List<User> users = dataProvider.getList();
				if (users != null && !users.isEmpty()) {
					for(Integer userId : details.getSubscribers()){
						for(User user : users){
							if (userId == user.getId()){
								selectionModel.setSelected(user, true);
							}
						}
					}
				}
			}
		}.getAsyncCallback());
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
		return true;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return !txtContent.getText().equals("") && !txtSubject.getText().equals("") && getSubscribers().size() > 0;
	}
	
	public ActionMail getAction() {
		ActionMail mail = new ActionMail();
		mail.setSubject(txtSubject.getText());
		mail.setContent(txtContent.getText());
		return mail;
	}
	
	public List<Integer> getSubscribers() {
		List<Integer> subs = new ArrayList<Integer>();
		for(User user: selectionModel.getSelectedSet()){
			subs.add(user.getId());
		}
		
		return subs;
	}
}
