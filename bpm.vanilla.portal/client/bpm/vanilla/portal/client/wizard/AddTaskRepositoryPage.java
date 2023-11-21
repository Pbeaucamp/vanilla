package bpm.vanilla.portal.client.wizard;

import bpm.gwt.commons.client.tree.RepositoryTree;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AddTaskRepositoryPage extends Composite implements IGwtPage {

	private static AddTaskRepositoryPageUiBinder uiBinder = GWT.create(AddTaskRepositoryPageUiBinder.class);

	interface AddTaskRepositoryPageUiBinder extends UiBinder<Widget, AddTaskRepositoryPage> {
	}

	@UiField
	SimplePanel panelRepository;

	@UiField
	Label lblFilter, lblFormat;

	@UiField
	ListBox lstFilter, lstFormat;

	private IGwtWizard parent;
	private int index;

	private RepositoryTree repositoryTree;
	private PortailRepositoryItem item;

	private boolean first;

	public AddTaskRepositoryPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.first = true;

		lblFilter.setText(ToolsGWT.lblCnst.Filters());
		loadFilters();

		repositoryTree = new RepositoryTree(repositoryHandler);
		panelRepository.setWidget(repositoryTree);

		lblFormat.setText(ToolsGWT.lblCnst.Format());
		loadFormats();
		lstFormat.setEnabled(false);
	}

	public AddTaskRepositoryPage(IGwtWizard parent, int index, PortailRepositoryItem item) {
		this(parent, index);
		this.item = item;

		for (int i = 1; i < lstFilter.getItemCount(); i++) {
			int type = Integer.parseInt(lstFilter.getValue(i));
			if (type == item.getType()) {
				lstFilter.setSelectedIndex(i);
				break;
			}
		}
		repositoryTree.loadTree(item.getType(), item.getId());

	}

	private void loadFilters() {
		lstFilter.addItem(ToolsGWT.lblCnst.SelectAType(), String.valueOf(-1));
		lstFilter.addItem(ToolsGWT.lblCnst.Gateway(), String.valueOf(IRepositoryApi.GTW_TYPE));
		lstFilter.addItem(ToolsGWT.lblCnst.Workflow(), String.valueOf(IRepositoryApi.BIW_TYPE));
		lstFilter.addItem(ToolsGWT.lblCnst.Fwr(), String.valueOf(IRepositoryApi.FWR_TYPE));
		lstFilter.addItem(ToolsGWT.lblCnst.BirtReport(), String.valueOf(IRepositoryApi.CUST_TYPE));
	}

	private void loadFormats() {
		for (int i = 0; i < CommonConstants.FORMAT_DISPLAY.length; i++) {
			lstFormat.addItem(CommonConstants.FORMAT_DISPLAY[i], CommonConstants.FORMAT_VALUE[i]);
		}

		lstFormat.setSelectedIndex(0);
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return repositoryTree.getSelectedValue() != null || item != null;
	}

	public RepositoryItem getSelectedItem() {
		return repositoryTree.getSelectedValue() != null ? repositoryTree.getSelectedValue() : item.getItem();
	}

	@UiHandler("lstFilter")
	public void onFilterChange(ChangeEvent event) {
		int typeRepository = Integer.parseInt(lstFilter.getValue(lstFilter.getSelectedIndex()));
		if (typeRepository != -1) {
			if (first) {
				lstFilter.removeItem(0);
				first = false;
			}
			repositoryTree.loadTree(typeRepository);
		}
	}

	private SelectionHandler<TreeItem> repositoryHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			if (repositoryTree.getSelectedValue() != null && (((RepositoryItem) repositoryTree.getSelectedValue()).getType() == IRepositoryApi.FWR_TYPE || ((RepositoryItem) repositoryTree.getSelectedValue()).getType() == IRepositoryApi.CUST_TYPE)) {
				lstFormat.setEnabled(true);
			}
			else {
				lstFormat.setEnabled(false);
			}
			parent.updateBtn();
		}
	};

	public String getFormat() {
		return lstFormat.getValue(lstFormat.getSelectedIndex());
	}
}
