package bpm.freematrix.reborn.web.client.main.alert;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.freematrix.reborn.web.client.dialog.ResolveAlertDialog;
import bpm.freematrix.reborn.web.client.dialog.WaitDialog;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.images.Images;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.ConditionKpi;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class AlertPanel extends Composite {

	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static AlertPanelUiBinder uiBinder = GWT.create(AlertPanelUiBinder.class);

	interface AlertPanelUiBinder extends UiBinder<Widget, AlertPanel> {
	}
	
	@UiField
	HTMLPanel gridPanel, commentPanel;
	
	private DataGrid<AlertRaised> dataGrid;

	private ListDataProvider<AlertRaised> dataProvider;

	private SingleSelectionModel<AlertRaised> selectionModel;

	public AlertPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.setSize("100%", "100%");
		
		createAlertGrid();
		
		gridPanel.add(dataGrid);
		
		refresh();
	}

	private void refresh() {
		WaitDialog.showWaitPart(true);
		MetricService.Connection.getInstance().getRaisedAlerts(false, new AsyncCallback<List<AlertRaised>>() {
			
			@Override
			public void onSuccess(List<AlertRaised> result) {
				
				//filter metrics by themes
				List<AlertRaised> toRm = new ArrayList<AlertRaised>();
				for(AlertRaised raised : result) {
					if(!FreeMatrixHeader.getInstance().isAllowed(raised.getMetric())) {
						toRm.add(raised);
					}
				}
				result.removeAll(toRm);
				
				commentPanel.clear();
				dataProvider.setList(result);
				WaitDialog.showWaitPart(false);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
			}
		});
	}

	private void createAlertGrid() {
		DataGrid.Resources resources = new CustomResources();
		
		dataGrid = new DataGrid<AlertRaised>(30, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		TextCell cell = new TextCell();
		
		Column<AlertRaised, ImageResource> colHandled = new Column<AlertRaised, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(AlertRaised object) {
				if(object.isHasBeenHandled()) {
					return Images.INSTANCE.green_flag();
				}
				else {
					return Images.INSTANCE.red_flag();
				}
				
			}
		};
		
		Column<AlertRaised, String> colName = new Column<AlertRaised, String>(cell) {
			@Override
			public String getValue(AlertRaised object) {
				return object.getMetric().getName();
			}
		};
		
		Column<AlertRaised, String> colOwner = new Column<AlertRaised, String>(cell) {
			@Override
			public String getValue(AlertRaised object) {
				return object.getMetric().getResponsible();
			}
		};
		
		Column<AlertRaised, String> colType = new Column<AlertRaised, String>(cell) {
			@Override
			public String getValue(AlertRaised object) {
				return getCause(object.getAlert());
			}
		};
		
		Column<AlertRaised, String> colDate = new Column<AlertRaised, String>(cell) {
			@Override
			public String getValue(AlertRaised object) {
				return dateFormat.format(object.getDate());
			}
		};
		
		Column<AlertRaised, String> colDateRes = new Column<AlertRaised, String>(cell) {
			@Override
			public String getValue(AlertRaised object) {
				if(object.getResolutionDate() != null) {
					return dateFormat.format(object.getResolutionDate());
				}
				return null;
			}
		};
		
		dataGrid.addColumn(colHandled);
		dataGrid.addColumn(colName, LabelConstants.lblCnst.Metric());
		dataGrid.addColumn(colOwner, LabelConstants.lblCnst.responsible());
		dataGrid.addColumn(colType, LabelConstants.lblCnst.type());
		dataGrid.addColumn(colDate, LabelConstants.lblCnst.date());
		dataGrid.addColumn(colDateRes, LabelConstants.lblCnst.dateRes());
		
		dataGrid.setEmptyTableWidget(new Label(LabelConstants.lblCnst.noAlerts()));
		
		dataProvider = new ListDataProvider<AlertRaised>();
		dataProvider.addDataDisplay(dataGrid);
		
	    selectionModel = new SingleSelectionModel<AlertRaised>();
	    dataGrid.setSelectionModel(selectionModel);
	    
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    
//	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {	
//			@Override
//			public void onSelectionChange(SelectionChangeEvent event) {
//				commentPanel.clear();
//				commentPanel.add(new CollaborationPanel(selectionModel.getSelectedObject()));
//			}
//		});
	}
	
	private String getCause(Alert alert) {
		String result = "";
		boolean first = true;
		for(Condition cond : alert.getConditions()){
			if(first){
				first = false;
			} else {
				result += " " + alert.getOperator() + " ";
			}
			ConditionKpi cki = (ConditionKpi) cond.getConditionObject();
			if(cki.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.MISSING_TYPE])) {
				if(cki.getMissingType().equals(ConditionKpi.MISSING_TYPES[ConditionKpi.MISSING_VAL_ONLY])) {
					return LabelConstants.lblCnst.missingValue();
				}
				else if(cki.getMissingType().equals(ConditionKpi.MISSING_TYPES[ConditionKpi.MISSING_OBJ_ONLY])) {
					return LabelConstants.lblCnst.missingObj();
				}
				else if(cki.getMissingType().equals(ConditionKpi.MISSING_TYPES[ConditionKpi.MISSING_VAL_OBJ])) {
					return LabelConstants.lblCnst.missingBoth();
				}
			}
			else if(cki.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.STATE_TYPE])) {
				if(cki.getStateType().equals(ConditionKpi.STATE_TYPES[ConditionKpi.STATE_ABOVE])) {
					return LabelConstants.lblCnst.stateOk();
				}
				else if(cki.getStateType().equals(ConditionKpi.STATE_TYPES[ConditionKpi.STATE_EQUAL])) {
					return LabelConstants.lblCnst.stateEqual();
				}
				else if(cki.getStateType().equals(ConditionKpi.STATE_TYPES[ConditionKpi.STATE_UNDER])) {
					return LabelConstants.lblCnst.stateNotOK();
				}
			}
			else {
				return cond.getLeftOperand() + " " + cond.getOperator() + " " + cond.getRightOperand();
			}
		}
		
		return null;
	}
	
	@UiHandler("imgResolve") 
	public void onResolve(ClickEvent event) {
		
		if(selectionModel.getSelectedObject() != null && !selectionModel.getSelectedObject().isHasBeenHandled()) {			
			final ResolveAlertDialog dial = new ResolveAlertDialog(selectionModel.getSelectedObject());
			dial.setGlassEnabled(true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if(dial.isConfirm()) {
						refresh();
					}
				}
			});
			dial.center();
		}
	}
//	@UiHandler("imgRefreshRaised") 
//	public void onRefreshRaised(ClickEvent event) {
//		refresh(false);
//	}
//	@UiHandler("imgRefreshResolved") 
//	public void onRefreshResolved(ClickEvent event) {
//		refresh(true);
//	}

	public void filterChange(Group group, Observatory obs, Theme theme) {
		refresh();
		
	}
} 
