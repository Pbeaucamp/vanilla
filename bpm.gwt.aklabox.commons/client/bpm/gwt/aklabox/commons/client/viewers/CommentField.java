package bpm.gwt.aklabox.commons.client.viewers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.Comments.CommentStatus;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.DefaultResultDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class CommentField extends Composite {

	private static CommentFieldUiBinder uiBinder = GWT.create(CommentFieldUiBinder.class);

	interface CommentFieldUiBinder extends UiBinder<Widget, CommentField> {
	}

	@UiField
	TextArea textArea;
	@UiField
	HTMLPanel newCommentPanel;
	@UiField
	Label btnUserSort, btnDateSort, btnCommentSort,refDoc;
	@UiField
	Button saveComment;
	@UiField
	Button placeComment;


	private String statusSort = "ascending";

	private CommentStatus status;
	private List<Comments> comments;
	
	//for comment positionning
	private double xPos, yPos;
	
//	private boolean needValidation = false;
//	private Documents referenceDocument;
	private Documents doc;
	private User user;
	private int numPage;

	public CommentField(Documents doc, List<Comments> comments, User user, CommentStatus status, int numPage) {
		initWidget(uiBinder.createAndBindUi(this));
		this.doc = doc;
		this.status = status;
		this.comments = comments;
		this.user = user;
		this.numPage = numPage;

		loadComments();
	}

	public void loadComments() {
/*		AkladService.Connect.getService().getComments(doc.getId(), status, new AsyncCallback<List<Comments>>() {

			@Override
			public void onSuccess(List<Comments> result) {
				CommentField.this.comments = result;
				int value = 0;
				
				newCommentPanel.clear();
				for (Comments c : result) {
					newCommentPanel.add(new NewComment(CommentField.this, c, value, doc));
					value++;
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});*/
		//comments = new ArrayList<Comments>(comments);
		createList(comments);
	}

	private void saveComments() {
/*		Comments comment = new Comments();
		comment.setMessage(textArea.getText());
		comment.setDocId(doc.getId());
		comment.setCommentDate(new Date());
		comment.setUser(AkladMain.getInstance().getUser().getEmail());
		comment.setCommentStatus(status);
		
		comment.setPage(selectedPage);
		comment.setX(xPos);
		comment.setY(yPos);
		
		if(referenceDocument!=null){
			comment.setDocumentReferenceId(referenceDocument.getId());
		}
		
		if (needValidation) {
			comment.setAdminStatus(AdminStatus.NO_STATUS);
			new DefaultResultDialog(LabelConstants.lblCnst.YourCommentNeedToBeValidate(), "success").show();
		}

		AkladService.Connect.getService().saveComment(comment, AkladMain.getInstance().getUser().getUserType(), new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				loadComments();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});*/
		final Comments comment = new Comments();
		comment.setMessage(textArea.getText());
		comment.setDocId(doc.getId());
		comment.setCommentDate(new Date());
		comment.setUser(user.getEmail());
		comment.setCommentStatus(status);
		
		comment.setPage(numPage);
		comment.setX(xPos);
		comment.setY(yPos);
		
		/*AklaCommonService.Connect.getService().saveComment(comment, null, new GwtCallbackWrapper<Void>(null, false, false) {

			@Override
			public void onSuccess(Void result) {*/
				comments.add(comment);
//				comments.add(comment);
				createList(comments);
		/*	}
		}.getAsyncCallback());*/
		
		
	}

	@UiHandler("saveComment")
	void onSaveComment(ClickEvent e) {
		if (textArea.getText().isEmpty()) {
			new DefaultResultDialog(LabelsConstants.lblCnst.PleaseProvideYourComment(), "failure").show();
		}
		else {
			saveComments();
//			referenceDocument=null;
			refDoc.setText("");
			//newCommentPanel.clear();
			textArea.setValue("");
		}
	}

	private void resetSortedColumn(Label button) {
		btnUserSort.getElement().setAttribute("style", "font-weight: normal;");
		btnCommentSort.getElement().setAttribute("style", "font-weight: normal;");
		btnDateSort.getElement().setAttribute("style", "font-weight: normal;");

		if (button != btnUserSort) {
			btnUserSort.removeStyleName("ascending");
			btnUserSort.removeStyleName("descending");
		}

		if (button != btnCommentSort) {
			btnCommentSort.removeStyleName("ascending");
			btnCommentSort.removeStyleName("descending");
		}

		if (button != btnDateSort) {
			btnDateSort.removeStyleName("ascending");
			btnDateSort.removeStyleName("descending");
		}

		if (button != null) {
			if ((button.getStyleName().contains("ascending")) || (button.getStyleName().contains("descending"))) {
				button.removeStyleName("ascending");
				button.removeStyleName("descending");
				if (button.getStyleName().contains("ascending"))
					statusSort = "ascending";
				else if (button.getStyleName().contains("descending"))
					statusSort = "descending";
			}
			else {
				statusSort = "descending";
			}
			button.getElement().setAttribute("style", "font-weight: bold;");
			if (statusSort.equals("ascending")) {
				button.addStyleName("descending");
				statusSort = "descending";
			}
			else {
				button.addStyleName("ascending");
				statusSort = "ascending";
			}
		}
		else {
			btnDateSort.getElement().setAttribute("style", "font-weight: bold;");
			btnDateSort.addStyleName("ascending");
			statusSort = "descending";
		}
	}

	@UiHandler("btnUserSort")
	void onEmailSort(ClickEvent e) {
		resetSortedColumn(btnUserSort);
		Collections.sort(comments, new Comparator<Comments>() {
			@Override
			public int compare(Comments o1, Comments o2) {
				if (statusSort.equals("ascending")) {
					return o1.getUser().compareTo(o2.getUser());
				}
				else {
					return o2.getUser().compareTo(o1.getUser());
				}
			}
		});
		createList(comments);
	}

	@UiHandler("btnCommentSort")
	void onMessageSort(ClickEvent e) {
		resetSortedColumn(btnCommentSort);
		Collections.sort(comments, new Comparator<Comments>() {
			@Override
			public int compare(Comments o1, Comments o2) {
				if (statusSort.equals("ascending")) {
					return o1.getMessage().compareToIgnoreCase(o2.getMessage());
				}
				else {
					return o2.getMessage().compareToIgnoreCase(o1.getMessage());
				}
			}
		});
		createList(comments);
	}

	@UiHandler("btnDateSort")
	void onTimeSort(ClickEvent e) {
		resetSortedColumn(btnDateSort);
		Collections.sort(comments, new Comparator<Comments>() {
			@Override
			public int compare(Comments o1, Comments o2) {
				if (statusSort.equals("ascending")) {
					return o1.getCommentDate().compareTo(o2.getCommentDate());
				}
				else {
					return o2.getCommentDate().compareTo(o1.getCommentDate());
				}
			}
		});
		createList(comments);
	}

	private void createList(List<Comments> comments2) {
		int value = 0;
		newCommentPanel.clear();
		for (Comments c : comments2) {
			newCommentPanel.add(new NewComment(CommentField.this, c, value, doc));
			value++;
		}
	}

	@UiHandler("placeComment")
	void onPlaceComment(ClickEvent e) {
		final PlaceComment viewer = new PlaceComment(doc, new Comments());
		
		DefaultDialog dial = new DefaultDialog(LabelsConstants.lblCnst.ClickMe(), viewer, 800, 0, 10);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(viewer.isPlaced()){
					
					xPos = viewer.getxPos();
					yPos = viewer.getyPos();
				}
			}
		});
		dial.show();
		
	}

	public List<Comments> getComments() {
		return comments;
	}
}