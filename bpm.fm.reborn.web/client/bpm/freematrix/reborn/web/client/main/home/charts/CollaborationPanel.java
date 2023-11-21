package bpm.freematrix.reborn.web.client.main.home.charts;

import java.util.Date;
import java.util.List;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Comment;
import bpm.fm.api.model.CommentAlert;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.ClientSession;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class CollaborationPanel extends Composite {

	private static CollaborationPanelUiBinder uiBinder = GWT.create(CollaborationPanelUiBinder.class);

	interface CollaborationPanelUiBinder extends UiBinder<Widget, CollaborationPanel> {
	}
	
	@UiField
	TextArea txtComment;
	
	@UiField
	HTMLPanel commentPanel;
	
//	@UiField
//	ListBox lstObs, lstTh, lstKpi, lstType;
	
	private MetricValue value;

	private AlertRaised alert;
	
	private boolean isAlert = false;
	
	private DataGrid<Comment> datagrid;
	private ListDataProvider<Comment> dataprovider;
	
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	public CollaborationPanel(MetricValue metricValue) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.value = metricValue;
		DataGrid.Resources resources = new CustomResources();
		
		datagrid = new DataGrid<Comment>(30, resources);
		datagrid.setSize("100%", "100%");

		dataprovider = new ListDataProvider<Comment>();
		dataprovider.addDataDisplay(datagrid);
		
		TextCell cell = new TextCell();
		Column<Comment, String> colDate = new Column<Comment, String>(cell) {
			@Override
			public String getValue(Comment object) {
				return dateFormat.format(object.getValueDate());
			}
		};
		Column<Comment, String> colUser = new Column<Comment, String>(cell) {
			@Override
			public String getValue(Comment object) {
				return object.getUser().getName();
			}
		};
		Column<Comment, String> colComment = new Column<Comment, String>(cell) {
			@Override
			public String getValue(Comment object) {
				return object.getComment();
			}
		};
		
		datagrid.addColumn(colDate, "Date");
		datagrid.addColumn(colUser, "Utilisateur");
		datagrid.addColumn(colComment, "Commentaire");
		
		commentPanel.add(datagrid);
//		lstObs.addItem("Customers");
//		lstTh.addItem("Satisfy customer needs");
//		lstKpi.addItem("Noise pollution");
//		lstType.addItem("Issue");
		
		refresh();
		
	}

	public CollaborationPanel(AlertRaised selectedObject) {
		initWidget(uiBinder.createAndBindUi(this));
		this.alert = selectedObject;
		isAlert = true;
		
		refresh();
	}

	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		if(txtComment.getText() != null && !txtComment.getText().isEmpty()) {
			if(isAlert) {
				
				CommentAlert comment = new CommentAlert();
				comment.setComment(txtComment.getText());
				comment.setDate(new Date());
				comment.setRaisedId(alert.getId());
				comment.setResolutionComment(false);
				
				MetricService.Connection.getInstance().addAlertComment(comment, alert, false, ClientSession.getInstance().getLogin(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Void result) {
						refresh();
						
					}
				});
				
			}
			else {
				Comment comment = new Comment();
				comment.setComment(txtComment.getText());
				comment.setMetricId(value.getMetric().getId());
				comment.setValueDate(value.getDate());
				MetricService.Connection.getInstance().addComment(comment, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						refresh();
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
	}
	
	public void refresh() {
//		commentPanel.clear();
		if(isAlert) {
			MetricService.Connection.getInstance().getAlertComment(alert.getId(), new AsyncCallback<List<CommentAlert>>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(List<CommentAlert> result) {
					for(CommentAlert com : result) {
						CommentPanel panel = new CommentPanel(com);
						commentPanel.add(panel);
					}
				}
			});
		}
		else {
			MetricService.Connection.getInstance().getComments(value.getDate(), value.getMetric().getId(), new AsyncCallback<List<Comment>>() {
				
				@Override
				public void onSuccess(List<Comment> result) {
					dataprovider.setList(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}
			});
		}

	}
}
