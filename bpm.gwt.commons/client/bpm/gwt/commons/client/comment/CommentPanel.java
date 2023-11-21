package bpm.gwt.commons.client.comment;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Comment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class CommentPanel extends PopupPanel {

	private static CommentPanelUiBinder uiBinder = GWT.create(CommentPanelUiBinder.class);

	interface CommentPanelUiBinder extends UiBinder<Widget, CommentPanel> {
	}

	interface MyStyle extends CssResource {
		String ovalThoughtBorder();
		String commentBtn();
	}

	@UiField
	HTMLPanel scrollPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
//	@UiField
//	ListBox fakeList;

	@UiField
	MyStyle style;

	private IWait waitPanel;
	
	private int objectId;
	private int type;
	private List<Group> groups;
	private Group selectedGroup;
	
	private List<Comment> comments;

	public CommentPanel(IWait waitPanel, int objectId, TypeCollaboration type, Group selectedGroup, List<Group> groups) {
		super(true);
		setWidget(uiBinder.createAndBindUi(this));
		setStyleName(style.ovalThoughtBorder());
		addStyleName(VanillaCSS.COMMENT_BORDER);

		this.waitPanel = waitPanel;
		this.objectId = objectId;
		if (type == TypeCollaboration.ITEM_NOTE) {
			this.type = Comment.ITEM;
		}
		else if (type == TypeCollaboration.DIRECTORY_NOTE) {
			this.type = Comment.DIRECTORY;
		}
		else if (type == TypeCollaboration.DOCUMENT_VERSION) {
			this.type = Comment.DOCUMENT_VERSION;
		}
		this.selectedGroup = selectedGroup;
		this.groups = groups;

		scrollPanel.getElement().getStyle().setPropertyPx("maxHeight", Window.getClientHeight() - 210);

		Window.addResizeHandler(resizeHandler);

		waitPanel.showWaitPart(true);
		
		loadComments();
		
//		fakeList.addItem("Thread Admin");
//		fakeList.addItem("Thread User");
	}

	private void loadComments() {
		CommonService.Connect.getInstance().getComments(objectId, type, new AsyncCallback<List<Comment>>() {

			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();

				MessageHelper.openMessageError(LabelsConstants.lblCnst.Error(), caught);
			}

			@Override
			public void onSuccess(List<Comment> result) {
				waitPanel.showWaitPart(false);

				loadComments(result);
			}
		});
	}

	private void loadComments(List<Comment> comments) {
		this.comments = comments;

		mainPanel.clear();

		if (comments != null) {
			int maxLevel = buildTreeComment(comments, -1);
			if (maxLevel == -1) {
				maxLevel = 1;
			}
			int left = Window.getClientWidth() - (360 + maxLevel * 20);
			this.getElement().getStyle().setLeft(left, Unit.PX);
		}

		HTML commentBtn = new HTML(LabelsConstants.lblCnst.AnyComment());
		commentBtn.getElement().setAttribute("role", "button");
		commentBtn.getElement().setAttribute("tabIndex", "0");
		commentBtn.addStyleName(style.commentBtn());
		commentBtn.addClickHandler(newCommentHandler);
		mainPanel.add(commentBtn);
	}
	
	private int buildTreeComment(List<Comment> comments, int level) {
		int maxLevel = level;
		level++;
		for(Comment com : comments) {
			mainPanel.add(new CommentWidget(waitPanel, this, com, selectedGroup.getName(), level));
			
			if(com.getChilds() != null && !com.getChilds().isEmpty()) {
				int newMaxLevel = buildTreeComment(com.getChilds(), level);
				if(newMaxLevel > maxLevel) {
					maxLevel = newMaxLevel;
				}
			}
		}
		return maxLevel;
	}
	public void showNewCommentPart(Integer parentId) {
		mainPanel.clear();
		mainPanel.add(new NewCommentWidget(waitPanel, this, objectId, type, parentId, groups));
	}

	public void showCommentPart(boolean update) {
		if(update) {
			loadComments();
		}
		else {
			loadComments(comments);
		}
	}

	public void editComment(Comment comment) {
		mainPanel.clear();
		mainPanel.add(new NewCommentWidget(waitPanel, this, comment, groups));
	}
	
	private ClickHandler newCommentHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			showNewCommentPart(null);
		}
	};

	private ResizeHandler resizeHandler = new ResizeHandler() {

		@Override
		public void onResize(ResizeEvent event) {
			int height = event.getHeight();
			scrollPanel.getElement().getStyle().setPropertyPx("maxHeight", height - 210);
		}
	};
	
	public void resize(int height){
		scrollPanel.getElement().getStyle().setPropertyPx("maxHeight", height - 140);
	}
}
