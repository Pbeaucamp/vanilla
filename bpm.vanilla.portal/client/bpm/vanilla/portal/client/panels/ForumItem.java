package bpm.vanilla.portal.client.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.center.ForumPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class ForumItem extends Composite {

	private static ForumItemUiBinder uiBinder = GWT.create(ForumItemUiBinder.class);

	interface ForumItemUiBinder extends UiBinder<Widget, ForumItem> {
	}
	
	interface MyStyle extends CssResource {
		String gridGroupCommentsPanel();
		String imgItem();
		String popup();
	}
	
	@UiField
	HTMLPanel panelContent, panelLeft, panelRight, panelComments;
	
	@UiField
	SimplePanel panelGroup;
	
	@UiField
	Image btnExpend, btnCollapse, imgItem, btnLaunch;
	
	@UiField
	Label lblName, lblDate;
	
	@UiField
	Button btnNewCom;
	
	@UiField
	TextArea txtComment;
	
	@UiField
	MyStyle style;
	
	private ForumPanel parent;
	private RepositoryItem item;
	private List<Comment> comments;
	
	private ListDataProvider<Group> dataProvider;
	private MultiSelectionModel<Group> selectionModel;
	private List<Group> groups;
	private DataGrid<Group> groupGrid;
	
	public ForumItem(ForumPanel parent, RepositoryItem item, List<Comment> comments) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.parent = parent;
		this.item = item; 
		this.comments = comments;
		
		initItem();
		this.groups = biPortal.get().getInfoUser().getAvailableGroups();
		buildContentSendCom(this.groups);
	}
	
	@Override
	public void onLoad(){
		Timer res = new Timer() {
			@Override
			public void run() {
				onResize();
			}
		};
		res.schedule(50);
	}

	private void initItem() {
		lblName.setText(item.getName());
		
		if(comments.size() >0){
			onExpendClick(null);
		} else {
			onCollapseClick(null);
		}
		
		btnLaunch.setVisible(true);
		DateTimeFormat fmt = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
		lblDate.setText(fmt.format(item.getDateModification()));
		
		//comments
		for(Comment comment: comments){
			addCommentItem(new CommentItem(this, comment, 0));
		}
		
		imgItem.setResource(ToolsGWT.getImageForObject(new PortailRepositoryItem(item, parent.getTypeNames().get(item.getType())), true));
		imgItem.addStyleName(style.imgItem());
	}
	
	
	
	public void onResize() {
		if(panelLeft.getOffsetHeight() > panelRight.getOffsetHeight()){
			panelContent.setHeight(panelLeft.getOffsetHeight() + "px");
		} else {
			panelContent.setHeight(panelRight.getOffsetHeight()+10 + "px");
			panelLeft.setHeight(panelRight.getOffsetHeight()+10 + "px");
		}
	}

	@UiHandler("btnExpend")
	public void onExpendClick(ClickEvent event) {
		panelContent.setVisible(true);
		btnExpend.setVisible(false);
		btnCollapse.setVisible(true);
		onResize();
		if(groupGrid != null) groupGrid.redraw();
	}
	
	@UiHandler("btnCollapse")
	public void onCollapseClick(ClickEvent event) {
		panelContent.setVisible(false);
		btnExpend.setVisible(true);
		btnCollapse.setVisible(false);
		onResize();
	}
	
	public void addCommentItem(CommentItem commentItem){
		panelComments.add(commentItem);
	}
	
	public void buildContentSendCom(List<Group> groups) {
		panelGroup.setWidget(createGridGroup());

		btnNewCom.setText(LabelsConstants.lblCnst.PostComment());

		if (groups != null) {
			dataProvider.setList(groups);
		}
		else {
			dataProvider.setList(new ArrayList<Group>());
		}
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
		this.groupGrid = dataGrid;
		
		return panelDataGrid;
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

			parent.showWaitPart(true);

			final Comment newCom = new Comment();
			newCom.setObjectId(item.getId());
			newCom.setType(Comment.ITEM);
			//newCom.setParentId(parentId);
			newCom.setComment(com);
			newCom.setCreationDate(new Date());
			newCom.setUser(biPortal.get().getInfoUser().getUser());

			CommonService.Connect.getInstance().addComment(newCom, groupIds, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					parent.showWaitPart(false);

					caught.printStackTrace();

					MessageHelper.openMessageError(LabelsConstants.lblCnst.FailedSaveComment(), caught);
				}

				@Override
				public void onSuccess(Void result) {
					addCommentItem(new CommentItem(ForumItem.this, newCom, 0));
					parent.showWaitPart(false);
				}
			});
		}
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
	
	@UiHandler("btnLaunch")
	public void onLaunchClick(ClickEvent event) {
//		VanillaViewer vanillaViewer = new VanillaViewer(parent.getMainPanel(), biPortal.get().getInfoUser().getGroup(), biPortal.get().getInfoUser().getAvailableGroups());
//		SimplePanel wid = new SimplePanel(vanillaViewer.createViewer(
//				new PortailRepositoryItem(item, parent.getTypeNames().get(item.getType())), biPortal.get().getInfoUser(), false)); 
//		PopupPanel dial = new PopupPanel();
//		dial.add(wid);
//		dial.setStyleName(style.popup());
//		dial.center();
		parent.getMainPanel().openViewer(new PortailRepositoryItem(item, parent.getTypeNames().get(item.getType())));
	}

	public RepositoryItem getItem() {
		return item;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	
}
