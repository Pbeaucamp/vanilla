package bpm.gwt.commons.client.comment;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.repository.Comment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class CommentWidget extends Composite {

	private static CommentWidgetUiBinder uiBinder = GWT.create(CommentWidgetUiBinder.class);

	interface CommentWidgetUiBinder extends UiBinder<Widget, CommentWidget> {
	}
	
	interface MyStyle extends CssResource {
		String imgDeleteComment();
		String imgResponse();
		String imgEdit();
	}
	
	@UiField
	Image imgResponse, imgEdit, imgDel;
	
	@UiField
	HTML htmlCom, htmlDateGroup;

	@UiField
	MyStyle style;
	
	private IWait waitPanel;
	private CommentPanel mainPanel;
	private Comment comment;
	
	public CommentWidget(IWait waitPanel, CommentPanel mainPanel, Comment comment, String groupName, int level) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.mainPanel = mainPanel;
		this.comment = comment;

		imgResponse.setResource(CommonImages.INSTANCE.comment_response());
		imgResponse.addStyleName(style.imgResponse());
		
		imgEdit.setResource(CommonImages.INSTANCE.edit());
		imgEdit.addStyleName(style.imgEdit());
		
		imgDel.setResource(CommonImages.INSTANCE.close());
		imgDel.addStyleName(style.imgDeleteComment());

		DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");
		
		htmlCom.setHTML(comment.getComment());
		htmlDateGroup.setHTML(groupName + " - " + sdf.format(comment.getBeginDate()));
		
		this.getElement().getStyle().setMarginLeft(20 * level, Unit.PX);
	}
	
	@UiHandler("imgResponse")
	public void onResponseClick(ClickEvent event) {
		mainPanel.showNewCommentPart(comment.getId());
	}
	
	@UiHandler("imgEdit")
	public void onEditClick(ClickEvent event) {
		mainPanel.editComment(comment);
	}

	@UiHandler("imgDel")
	public void onHoverHandler(MouseOverEvent event) {
		imgDel.setResource(CommonImages.INSTANCE.closeTabHover());
	}

	@UiHandler("imgDel")
	public void onOutHandler(MouseOutEvent event) {
		imgDel.setResource(CommonImages.INSTANCE.close());
	}
	
	@UiHandler("imgDel")
	public void onDeleteHandler(ClickEvent event) {
		waitPanel.showWaitPart(true);
		CommonService.Connect.getInstance().deleteComment(comment, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				MessageHelper.openMessageError(LabelsConstants.lblCnst.Error(), caught);
				mainPanel.showCommentPart(true);
				
				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(Void result) {
				mainPanel.showCommentPart(true);
				
				waitPanel.showWaitPart(false);
			}
		});
	}
}
