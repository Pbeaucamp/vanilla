package bpm.gwt.aklabox.commons.client.viewers;

import java.util.Date;

import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class NewComment extends Composite {

	private static NewCommentUiBinder uiBinder = GWT.create(NewCommentUiBinder.class);

	interface NewCommentUiBinder extends UiBinder<Widget, NewComment> {
	}

	@UiField
	Label lblDate, lblEmail, lblComment, lblIsPlaced;
	@UiField
	HTMLPanel commentField, editField;
	@UiField
	Button edit, saveEdit, remove,openRefDoc;
	@UiField
	Image imgProfilePic;
	
	private CommentField parent;

	private Comments comment;
	
	private TextArea txtEditField = new TextArea();

	public NewComment(CommentField parent, Comments comment, int colorValue, final IObject doc) {
		initWidget(uiBinder.createAndBindUi(this));
		this.comment = comment;
		this.parent = parent;

		getUserInfo();
		saveEdit.setVisible(false);


//		if (!Cookies.getCookie(AklaboxConstant.COOKIE_EMAIL).equals(comment.getUser())) {
//			AkladService.Connect.getService().getUserInfo(Cookies.getCookie(AklaboxConstant.COOKIE_EMAIL), new AsyncCallback<User>() {
//
//				@Override
//				public void onSuccess(User result) {
//					if (result.getUserId() != doc.getUserId()) {
//						edit.removeFromParent();
//						remove.removeFromParent();
//					}
//				}
//
//				@Override
//				public void onFailure(Throwable caught) {
//				}
//			});
//		}

		if (colorValue % 2 == 0)
			commentField.getElement().setAttribute("style", "background: aliceblue;");
		else
			commentField.getElement().setAttribute("style", "background: #fff");
		lblEmail.setText(comment.getUser());
		lblComment.setText(comment.getMessage());
/*		if(comment.getPage() != -1){
			lblIsPlaced.setText(LabelConstants.lblCnst.IsPositioned());
		} else {
			lblIsPlaced.setText(LabelConstants.lblCnst.IsNoPositioned());
		}*/
		lblIsPlaced.setText(comment.getX() + ", " + comment.getY());
		if(comment.getDocumentReferenceId()!=-1){
			openRefDoc.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			AklaCommonService.Connect.getService().getDocInfo(comment.getDocumentReferenceId(), new AsyncCallback<Documents>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Documents result) {
					openRefDoc.setTitle(result.getName());
				}
			});
			
		}
	}

	private void getUserInfo() {
		AklaCommonService.Connect.getService().getUserInfo(comment.getUser(), new AsyncCallback<User>() {

			@Override
			public void onSuccess(User result) {
				lblDate.setText(String.valueOf(DateTimeFormat.getFormat("MMMM dd, yyyy HH:MM").format(comment.getCommentDate())));

				if (result.getUserId() != 0) {
					imgProfilePic.setUrl(PathHelper.getRightPath(result.getProfilePic()));
				}
				else {
					imgProfilePic.setUrl("webapps/aklabox_files/images/ic_profile_pic.png");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				//new SessionController().checkSession(caught);
			}
		});
	}

	private void removeComment() {
		AklaCommonService.Connect.getService().removeComment(comment, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				//new SessionController().checkSession(caught);
			}

			@Override
			public void onSuccess(Void result) {
				parent.loadComments();
			}
		});
	}

	private void editComment() {
		txtEditField.setWidth("100%");
		txtEditField.setText(lblComment.getText());
		editField.add(txtEditField);
		lblComment.removeFromParent();
	}

	private void updateComment() {
		Comments comment = new Comments();
		comment.setMessage(txtEditField.getText());
		comment.setCommentDate(new Date());
		comment.setCommentId(this.comment.getCommentId());
		AklaCommonService.Connect.getService().updateComment(comment, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				//new SessionController().checkSession(caught);
			}

			@Override
			public void onSuccess(Void result) {
				parent.loadComments();
			}
		});
	}

	@UiHandler("remove")
	void onRemove(ClickEvent e) {
		removeComment();
	}

	@UiHandler("edit")
	void onEdit(ClickEvent e) {
		editComment();
		edit.removeFromParent();
		saveEdit.setVisible(true);
		saveEdit.getElement().setAttribute("title", LabelsConstants.lblCnst.Save());
	}

	@UiHandler("saveEdit")
	void onSaveEdit(ClickEvent e) {
		updateComment();
	}
	
	
}
