package bpm.gwt.commons.client.comment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.SecuredComment;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class NewCommentWidget extends Composite {

	private static NewCommentWidgetUiBinder uiBinder = GWT.create(NewCommentWidgetUiBinder.class);

	interface NewCommentWidgetUiBinder extends UiBinder<Widget, NewCommentWidget> {
	}

	interface MyStyle extends CssResource {
		String gridGroupCommentsPanel();
	}

	@UiField
	TextArea txtComment;

	@UiField
	SimplePanel panelGroup;

	@UiField
	Button btnNewCom, btnCancel;

	@UiField
	MyStyle style;

	private ListDataProvider<Group> dataProvider;
	private MultiSelectionModel<Group> selectionModel;

	private IWait waitPanel;
	private CommentPanel mainPanel;
	private Comment comment;
	private int objectId, type;
	private Integer parentId;
	private List<Group> groups;

	private boolean edit;

	public NewCommentWidget(IWait waitPanel, CommentPanel mainPanel, int objectId, int type, Integer parentId, List<Group> groups) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.mainPanel = mainPanel;
		this.objectId = objectId;
		this.type = type;
		this.parentId = parentId;
		this.groups = groups;
		this.edit = false;

		buildContent(groups);
	}

	public NewCommentWidget(IWait waitPanel, CommentPanel mainPanel, Comment comment, List<Group> groups) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.mainPanel = mainPanel;
		this.comment = comment;
		this.groups = groups;
		this.edit = true;

		buildContent(groups);

		txtComment.setText(comment.getComment());
		for (Group group : groups) {
			if (comment.getSecuredGroups() != null) {
				for (SecuredComment sec : comment.getSecuredGroups()) {
					if (sec.getGroupId() == group.getId()) {
						selectionModel.setSelected(group, true);
						break;
					}
				}
			}
		}
	}

	public void buildContent(List<Group> groups) {
		panelGroup.setWidget(createGridGroup());

		btnNewCom.setText(LabelsConstants.lblCnst.PostComment());
		btnCancel.setText(LabelsConstants.lblCnst.Cancel());

		if (groups != null) {
			dataProvider.setList(groups);
		}
		else {
			dataProvider.setList(new ArrayList<Group>());
		}
	}

	@UiHandler("btnNewCom")
	public void onClickNewComClick(ClickEvent event) {
		String com = txtComment.getText();
		if (!com.isEmpty()) {

			List<Integer> groupIds = new ArrayList<Integer>();
			if (groups != null) {
				for (Group gr : groups) {
					if (selectionModel.isSelected(gr)) {
						groupIds.add(gr.getId());
					}
				}
			}

			if (groupIds.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedSelectGroup());
				return;
			}

			waitPanel.showWaitPart(true);

			if (!edit) {
				Comment newCom = new Comment();
				newCom.setObjectId(objectId);
				newCom.setType(type);
				newCom.setParentId(parentId);
				newCom.setComment(com);

				CommonService.Connect.getInstance().addComment(newCom, groupIds, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						waitPanel.showWaitPart(false);

						caught.printStackTrace();

						MessageHelper.openMessageError(LabelsConstants.lblCnst.FailedSaveComment(), caught);
					}

					@Override
					public void onSuccess(Void result) {
						mainPanel.showCommentPart(true);
					}
				});
			}
			else {
				comment.setComment(com);

				CommonService.Connect.getInstance().editComment(comment, groupIds, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						waitPanel.showWaitPart(false);

						caught.printStackTrace();

						MessageHelper.openMessageError(LabelsConstants.lblCnst.FailedSaveComment(), caught);
					}

					@Override
					public void onSuccess(Void result) {
						mainPanel.showCommentPart(true);
					}
				});
			}
		}
	}

	@UiHandler("btnCancel")
	public void onCancelClick(ClickEvent event) {
		mainPanel.showCommentPart(false);
	}

	private SimplePanel createGridGroup() {
		CheckboxCell cell = new CheckboxCell();
		Column<Group, Boolean> modelNameColumn = new Column<Group, Boolean>(cell) {

			@Override
			public Boolean getValue(Group object) {
				return selectionModel.isSelected(object);
			}
		};

		TextCell txtCell = new TextCell();
		Column<Group, String> nameColumn = new Column<Group, String>(txtCell) {

			@Override
			public String getValue(Group object) {
				return object.getName();
			}
		};

		Header<Boolean> headerCheck = new Header<Boolean>(new CustomCheckboxCell()) {

			@Override
			public Boolean getValue() {
				return false;
			}
		};

		Header<String> headerName = new Header<String>(new TextCell()) {

			@Override
			public String getValue() {
				return LabelsConstants.lblCnst.Groups();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Group> dataGrid = new DataGrid<Group>(10000, resources);
		// dataGrid.addStyleName(CSS_DATA_GRID_BURST);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("130px");
		dataGrid.addColumn(modelNameColumn, headerCheck);
		dataGrid.setColumnWidth(modelNameColumn, "40px");
		dataGrid.addColumn(nameColumn, headerName);
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoGroup()));

		dataProvider = new ListDataProvider<Group>();
		dataProvider.addDataDisplay(dataGrid);

		// Add a selection model so we can select cells.
		selectionModel = new MultiSelectionModel<Group>();
		dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Group> createCheckboxManager());

		SimplePanel panelDataGrid = new SimplePanel();
		panelDataGrid.setWidget(dataGrid);
		panelDataGrid.addStyleName(style.gridGroupCommentsPanel());

		return panelDataGrid;
	}

	private class CustomCheckboxCell extends CheckboxCell {

		private boolean isSelected = false;

		public CustomCheckboxCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("click");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {

			isSelected = !isSelected;

			if (groups != null) {
				for (Group group : groups) {
					selectionModel.setSelected(group, isSelected);
				}
			}

			super.onBrowserEvent(context, parent, value, Document.get().createChangeEvent(), valueUpdater);
		}
	}

}
