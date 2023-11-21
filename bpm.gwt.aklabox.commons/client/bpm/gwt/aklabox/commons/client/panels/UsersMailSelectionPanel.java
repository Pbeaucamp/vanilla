package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class UsersMailSelectionPanel extends Composite {

	private static UsersMailSelectionPanelUiBinder uiBinder = GWT.create(UsersMailSelectionPanelUiBinder.class);

	interface UsersMailSelectionPanelUiBinder extends UiBinder<Widget, UsersMailSelectionPanel> {
	}

	@UiField(provided = true)
	SuggestBox txtMail;

	@UiField
	LayoutPanel panelGridUsers;

	private MultiWordSuggestOracle suggestUsers = new MultiWordSuggestOracle();
	
	private DataGrid<User> gridUsers;
	private MultiSelectionModel<User> selectionModelUser;
	private ListDataProvider<User> dataProvider;
	
	private List<User> allUsers;
	private List<Group> allGroups;
	private HashMap<User, List<TypeUser>> enterpriseUsers;

	private List<User> users;

	public UsersMailSelectionPanel(List<User> allUsers, List<Group> allGroups, HashMap<User, List<TypeUser>> enterpriseUsers, boolean loadUsersInGrid) {
		txtMail = new SuggestBox(suggestUsers);
		initWidget(uiBinder.createAndBindUi(this));
		this.allUsers = allUsers;
		this.allGroups = allGroups;
		this.enterpriseUsers = enterpriseUsers;
		this.users = loadUsersInGrid ? (enterpriseUsers != null ? new ArrayList<User>(enterpriseUsers.keySet()) : allUsers) : null;
		
		txtMail.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.Recepients());

		gridUsers = buildGridUsers(enterpriseUsers != null);
		panelGridUsers.add(gridUsers);
		
		loadSuggests();
		loadUsersGrid();
	}

	public UsersMailSelectionPanel(List<User> allUsers, List<Group> allGroups, HashMap<User, List<TypeUser>> enterpriseUsers) {
		this(allUsers, allGroups, enterpriseUsers, true);
	}
	
	private void loadSuggests() {
		suggestUsers.clear();
		if (allUsers != null) {
			for (User user : allUsers) {
				suggestUsers.add(user.getEmail());
			}
		}

		if (allGroups != null) {
			for (Group group : allGroups) {
				if (group.getGroupMail() != null && !group.getGroupMail().isEmpty()) {
					suggestUsers.add(group.getGroupMail());
				}
			}
		}
	}
	
	public void loadUsersGrid() {
		if (users == null) {
			this.users = new ArrayList<>();
		}

		dataProvider.setList(users);
		dataProvider.refresh();
	}

	private DataGrid<User> buildGridUsers(boolean showRole) {

		Column<User, Boolean> checkColumn = new Column<User, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(User object) {
				return selectionModelUser.isSelected(object);
			}
		};

		DataGrid<User> dataGridUser = new DataGrid<User>(99999);
		dataGridUser.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		dataGridUser.setColumnWidth(checkColumn, 40, Unit.PX);

		TextColumn<User> name = new TextColumn<User>() {
			@Override
			public String getValue(User e) {
				return e != null ? e.getFirstName() + " " + e.getLastName() : "";
			}
		};
		dataGridUser.setColumnWidth(name, 100, Unit.PX);
		dataGridUser.addColumn(name, LabelsConstants.lblCnst.Name());
		name.setSortable(true);

		TextColumn<User> email = new TextColumn<User>() {
			@Override
			public String getValue(User e) {
				return e.getEmail();
			}
		};
		dataGridUser.setColumnWidth(email, 100, Unit.PX);
		dataGridUser.addColumn(email, LabelsConstants.lblCnst.Email());
		email.setSortable(true);

		if (showRole) {
			TextColumn<User> role = new TextColumn<User>() {
				@Override
				public String getValue(User e) {
					List<TypeUser> typeUsers = enterpriseUsers.get(e);
					if (typeUsers == null || typeUsers.isEmpty()) {
						return LabelsConstants.lblCnst.NoSpecificRole();
					}

					boolean first = true;
					StringBuffer role = new StringBuffer();
					for (TypeUser typeUser : typeUsers) {
						if (!first) {
							role.append(", ");
						}
						first = false;

						switch (typeUser) {
						case ADMIN:
							role.append(LabelsConstants.lblCnst.Admin());
							break;
						case MAILER:
							role.append(LabelsConstants.lblCnst.Mailer());
							break;
						case READER:
							role.append(LabelsConstants.lblCnst.Reader());
							break;
						case VALIDATOR:
							role.append(LabelsConstants.lblCnst.Validator());
							break;

						default:
							break;
						}
					}

					return role.toString();
				}
			};
			dataGridUser.setColumnWidth(role, 100, Unit.PX);
			dataGridUser.addColumn(role, LabelsConstants.lblCnst.Role());
			role.setSortable(true);
		}

		selectionModelUser = new MultiSelectionModel<User>();

		dataGridUser.setAutoHeaderRefreshDisabled(true);
		dataGridUser.setAutoFooterRefreshDisabled(true);
		dataGridUser.setSize("100%", "200px");
		dataGridUser.setSelectionModel(selectionModelUser, DefaultSelectionEventManager.<User> createCheckboxManager());

		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(dataGridUser);

		return dataGridUser;
	}
	
	@UiHandler("btnConfirmAdd")
	public void onAddUser(ClickEvent e) {
		String email = txtMail.getText();
		addUser(email);
	}
	
	public void addUser(String email) {
		if (email.isEmpty()) {
			new DefaultResultDialog(LabelsConstants.lblCnst.NeedToEnterEmail(), "failure").show();
			return;
		}

		User user = findUser(email);

		users.add(user);
		loadUsersGrid();

		selectionModelUser.setSelected(user, true);

		txtMail.setText("");
	}
	
	private User findUser(String email) {
		if (allUsers != null) {
			for (User user : allUsers) {
				if (user.getEmail().equals(email)) {
					return user;
				}
			}
		}

		//We don't found the user
		User user = new User();
		user.setEmail(email);
		return user;
	}

	public boolean hasUsersSelected() {
		List<String> emails = getSelectedMails();
		return emails != null && !emails.isEmpty();
	}
	
	public List<String> getSelectedMails() {
		List<String> emails = new ArrayList<String>();
		if (users != null) {
			for (User user : users) {
				if (selectionModelUser.isSelected(user)) {
					emails.add(user.getEmail());
				}
			}
		}
		return emails;
	}

	public void setText(String mail) {
		txtMail.setText(mail);
	}

	public void onResize() {
		gridUsers.onResize();
	}
}
