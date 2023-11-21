package bpm.gwt.commons.client.viewer.comments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.viewer.CommentRestitutionInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class CommentsRestitutionPanel extends Composite {

	private DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd \n HH:mm");

	private static ParametersPanelUiBinder uiBinder = GWT.create(ParametersPanelUiBinder.class);

	interface ParametersPanelUiBinder extends UiBinder<Widget, CommentsRestitutionPanel> {
	}

	interface MyStyle extends CssResource {
		String lblParam();

		String paramP();

		String captionParam();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, panelBtn;

	@UiField
	SimplePanel panelComment;

	@UiField
	Image btnAdd, btnEdit, btnDelete;

	private IWait waitPanel;

	private DataGrid<CommentValue> datagrid;
	private ListDataProvider<CommentValue> dataProvider;
	private ListHandler<CommentValue> sortHandler;

	private SingleSelectionModel<CommentValue> selectionModel;

	private CommentRestitutionInformations commentInfo;
	private User user;
	private List<VanillaGroupParameter> groupParameters;

	private NewCommentPanel currentNewCommentPanel;

	public CommentsRestitutionPanel(IWait waitPanel, CommentRestitutionInformations comment, User user, List<VanillaGroupParameter> groupParameters) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.commentInfo = comment;
		this.user = user;
		this.groupParameters = groupParameters;

		datagrid = buildGrid(comment.getComments());
		updateToolbar(false);
		updateUi(null, false);
	}

	private void loadComments(List<CommentValue> comments) {
		selectionModel.clear();
		dataProvider.flush();
		if (comments != null) {
			dataProvider.setList(comments);
		}
		else {
			dataProvider.setList(new ArrayList<CommentValue>());
		}
		sortHandler.setList(dataProvider.getList());

		commentInfo.setComments(comments);
		updateToolbar(false);
	}

	private DataGrid<CommentValue> buildGrid(List<CommentValue> comments) {
		Column<CommentValue, String> dateColumn = new Column<CommentValue, String>(new TextCell()) {

			@Override
			public String getValue(CommentValue object) {
				return sdf.format(object.getCreationDate());
			}
		};
		dateColumn.setSortable(true);
		dateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		Column<CommentValue, String> commentColumn = new Column<CommentValue, String>(new TextCell()) {

			@Override
			public String getValue(CommentValue object) {
				return object.getValue();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<CommentValue> dataGrid = new DataGrid<CommentValue>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(dateColumn, "Date");
		dataGrid.setColumnWidth(dateColumn, "90px");
		dataGrid.addColumn(commentColumn, "Comment");

		dataGrid.setEmptyTableWidget(new Label("No comments"));

		dataProvider = new ListDataProvider<CommentValue>(comments);
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<CommentValue>(comments);
		sortHandler.setComparator(dateColumn, new Comparator<CommentValue>() {

			@Override
			public int compare(CommentValue o1, CommentValue o2) {
				if (o1.getCreationDate() == null) {
					return -1;
				}
				else if (o2.getCreationDate() == null) {
					return 1;
				}

				return o2.getCreationDate().before(o1.getCreationDate()) ? -1 : o2.getCreationDate().after(o1.getCreationDate()) ? 1 : 0;
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		this.selectionModel = new SingleSelectionModel<CommentValue>();
		selectionModel.addSelectionChangeHandler(selectionChange);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	private void updateToolbar(boolean visible) {
		btnEdit.setVisible(visible);
		btnDelete.setVisible(visible);
	}

	private void updateUi(CommentValue comment, boolean newComment) {
		if (newComment) {
			currentNewCommentPanel = new NewCommentPanel(commentInfo, comment, user, groupParameters);
			panelComment.setWidget(currentNewCommentPanel);
		}
		else {
			panelComment.setWidget(datagrid);
		}

		panelToolbar.setVisible(!newComment);
		panelBtn.setVisible(newComment);
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		updateUi(null, true);
	}

	@UiHandler("btnEdit")
	public void onEditClick(ClickEvent event) {
		final CommentValue comment = selectionModel.getSelectedObject();
		if (comment != null) {
			updateUi(comment, true);
		}
	}

	@UiHandler("btnDelete")
	public void onDeleteClick(ClickEvent event) {
		final CommentValue comment = selectionModel.getSelectedObject();
		if (comment != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteConfirm(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						waitPanel.showWaitPart(true);

						comment.setStatus(CommentStatus.OLD);

						ReportingService.Connect.getInstance().addModifyOrDeleteComment(comment, new GwtCallbackWrapper<List<CommentValue>>(waitPanel, true) {

							@Override
							public void onSuccess(List<CommentValue> result) {
								loadComments(result);
							}
						}.getAsyncCallback());
					}
				}
			});
			dial.center();
		}
	}

	@UiHandler("btnBack")
	public void onBackClick(ClickEvent event) {
		updateUi(null, false);

		selectionModel.clear();
		updateToolbar(false);
	}

	@UiHandler("btnConfirm")
	public void onConfirmClick(ClickEvent event) {
		if (currentNewCommentPanel != null && currentNewCommentPanel.isValid()) {
			waitPanel.showWaitPart(true);

			CommentValue comment = currentNewCommentPanel.getCommentValue();
			ReportingService.Connect.getInstance().addModifyOrDeleteComment(comment, new GwtCallbackWrapper<List<CommentValue>>(waitPanel, true) {

				@Override
				public void onSuccess(List<CommentValue> result) {
					updateUi(null, false);

					loadComments(result);
				}
			}.getAsyncCallback());
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.CommentNotComplete());
			return;
		}
	}

	private Handler selectionChange = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			CommentValue comment = selectionModel.getSelectedObject();
			updateToolbar(comment != null);
		}
	};
}
