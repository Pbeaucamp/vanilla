package bpm.fm.designer.web.client.panel;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.MetricLinkedItem;
import bpm.fm.api.model.utils.MetricSqlQueries;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.dialog.ActionDialog;
import bpm.fm.designer.web.client.dialog.AssociationDialog;
import bpm.fm.designer.web.client.dialog.CreateQueryDialog;
import bpm.fm.designer.web.client.dialog.LinkedObjectDialog;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.fm.designer.web.client.utils.ActionDatagrid;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.ItemLinkedDialog;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.ConditionKpi;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class MetricPropertiesPanel extends Composite {

	private static MetricPropertiesPanelUiBinder uiBinder = GWT.create(MetricPropertiesPanelUiBinder.class);

	interface MetricPropertiesPanelUiBinder extends UiBinder<Widget, MetricPropertiesPanel> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel, alertPanel, valueContentPanel, actionPanel;//, mapPanel;

	@UiField
	TextBox txtName, txtOwner, txtResp;
	
	@UiField(provided = true)
	ListBox lstLinked;
	
	@UiField
	TextArea txtDescription;

	@UiField
	Image imgSave, imgInit, imgAssos, imgAssosEdit, imgAssosDelete, imgDelete, imgRefresh, imgCreate, imgAddAction, imgDeleteAction, imgRefreshAction, imgEditAction;//, imgAddMap, imgDeleteMap, imgRefreshMap, imgEditMap;
	
	@UiField
	Button btnLinked;
	
	@UiField
	TabLayoutPanel tabPanel;

	private Metric metric;
	
	private ValuePanel valuePanel;

	private ActionDatagrid actionDatagrid;
//	private MapDatagrid mapDatagrid;

	private MetricPanel metricPanel;

	private DataGrid<Alert> alertDatagrid;
	private ListDataProvider<Alert> alertDataProvider;

	private SingleSelectionModel<Alert> alertSelectionModel;

	public MetricPropertiesPanel(Metric metric, MetricPanel metricPanel) {
		lstLinked = new ListBox(true);
		initWidget(uiBinder.createAndBindUi(this));
		
		this.metricPanel = metricPanel;
		
		this.metric = metric;
		if(metric != null) {
			if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_CLASSIC))) {
				valuePanel = new ClassicValuePanel(metric);
				valueContentPanel.clear();
				valueContentPanel.add(valuePanel);
				
				imgAssos.getElement().getStyle().setDisplay(Display.INLINE);
				imgAssosEdit.getElement().getStyle().setDisplay(Display.INLINE);
				imgAssosDelete.getElement().getStyle().setDisplay(Display.INLINE);
				imgCreate.getElement().getStyle().setDisplay(Display.INLINE);
			}
			else {
				valuePanel = new CalculatedValuePanel(metric);
				valueContentPanel.clear();
				valueContentPanel.add(valuePanel);
				
				imgAssos.getElement().getStyle().setDisplay(Display.NONE);
				imgAssosEdit.getElement().getStyle().setDisplay(Display.NONE);
				imgAssosDelete.getElement().getStyle().setDisplay(Display.NONE);
				imgCreate.getElement().getStyle().setDisplay(Display.NONE);
			}
			
			fillData();	
		}
		createAlertDatagrid();
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				refresh();
			}
		});

		
		alertPanel.add(alertDatagrid);
		
		actionDatagrid = new ActionDatagrid();
		actionPanel.add(actionDatagrid);
		
//		mapDatagrid = new MapDatagrid();
//		mapPanel.add(mapDatagrid);
	}
	
	public void createAlertDatagrid() {
		alertDatagrid = new DataGrid<Alert>(15);
		alertDatagrid.setWidth("100%");
		alertDatagrid.setHeight("100%");

		TextCell cell = new TextCell();

		Column<Alert, String> colName = new Column<Alert, String>(cell) {
			@Override
			public String getValue(Alert object) {
				return object.getName();
			}
		};

		Column<Alert, String> colFormula = new Column<Alert, String>(cell) {
			@Override
			public String getValue(Alert object) {
				String result = getFormula(object);
				if(result.isEmpty()){
					return "No formula";
				} else {
					return result;
				}
				
			}
		};

		Column<Alert, String> colEvent = new Column<Alert, String>(cell) {
			@Override
			public String getValue(Alert object) {
				return object.getAction().getActionType().getLabel();
			}
		};

		alertDatagrid.addColumn(colName, Messages.lbl.name());
		alertDatagrid.addColumn(colFormula, Messages.lbl.formula());
		alertDatagrid.addColumn(colEvent, Messages.lbl.eventType());
		
		alertDatagrid.setEmptyTableWidget(new Label(Messages.lbl.noAlerts()));

		alertDataProvider = new ListDataProvider<Alert>();
		alertDataProvider.addDataDisplay(alertDatagrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(alertDatagrid);

		alertSelectionModel = new SingleSelectionModel<Alert>();
		alertDatagrid.setSelectionModel(alertSelectionModel);
	}

	public void refresh() {
		if (metric != null) {
			
			MetricService.Connection.getInstance().getMetric(metric.getId(), new AsyncCallback<Metric>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					
				}

				@Override
				public void onSuccess(Metric result) {
					metric = result;
					valuePanel.refresh(metric);
					alertDataProvider.setList(metric.getAlerts());
					actionDatagrid.setMetric(result);
//					mapDatagrid.setMetric(result);
				}
			});
			
		}
	}

	private void fillData() {
		if(metric != null) {	
			txtName.setText(metric.getName());
			
			txtDescription.setText(metric.getDescription());
//			txtEtl.setText(metric.getEtlItemName());
			lstLinked.clear();
			for(MetricLinkedItem it : metric.getLinkedItems()) {
				lstLinked.addItem(it.getItemName(), it.getItemId() + "");
			}
			
			txtOwner.setText(metric.getOwner());
			txtResp.setText(metric.getResponsible());
			
			valuePanel.fillData();
		}
	}

	@UiHandler("imgSave")
	public void onSave(ClickEvent e) {
		save();
	}

	@UiHandler("imgInit")
	public void onRefresh(ClickEvent e) {
		if (metric != null) {
			
			MetricService.Connection.getInstance().getMetric(metric.getId(), new AsyncCallback<Metric>() {

				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.GetMetricError(), caught.getMessage(), caught);
					dial.center();
				}

				@Override
				public void onSuccess(Metric result) {
					metric = result;
					fillData();
				}
			});
		}
	}

	@UiHandler("imgAssos")
	public void onAssos(ClickEvent e) {
		if (metric != null) {
			AssociationDialog dial = new AssociationDialog(metric, null);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {			
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refresh();
				}
			});
			dial.center();
		}
	}
	@UiHandler("imgAssosEdit")
	public void onAssosEdit(ClickEvent e) {
		if (metric != null) {
			FactTableAxis axis = ((ClassicValuePanel)valuePanel).getSelectedAxis();
			AssociationDialog dial = new AssociationDialog(metric, axis);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {			
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refresh();
				}
			});
			dial.center();
		}
	}
	@UiHandler("imgAssosDelete")
	public void onAssosDelete(ClickEvent e) {
		if (metric != null) {
			FactTableAxis axis = ((ClassicValuePanel)valuePanel).getSelectedAxis();
			((FactTable)metric.getFactTable()).getFactTableAxis().remove(axis);
			MetricService.Connection.getInstance().updateMetric(metric, new AsyncCallback<Void>() {		
				@Override
				public void onSuccess(Void result) {
					refresh();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					refresh();
				}
			});
		}
	}

	@UiHandler("imgLink")
	public void onLink(ClickEvent e) {
		if (metric != null && metric.getFactTable() != null && metric.getFactTable() instanceof FactTable) {
			String login = ClientSession.getInstance().getLogin();
			String password = ClientSession.getInstance().getPassword();
			String vanillaUrl = ClientSession.getInstance().getVanillaUrl();
			
			//TODO: To change with the real group
			Group group = ClientSession.getInstance().getGroup();
			
			ItemLinkedDialog dial = new ItemLinkedDialog(login, password, vanillaUrl, group, metric);
			dial.center();
		}
	}

	private void save() {
		if (metric != null) {
			
			metric = valuePanel.getMetric();
			
			metric.setName(txtName.getText());
			
			metric.setDescription(txtDescription.getText());
			metric.setOwner(txtOwner.getText());
			metric.setResponsible(txtResp.getText());
			
			MetricService.Connection.getInstance().updateMetric(metric, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.ProblemSaveMetric(), caught.getMessage(), caught);
					dial.center();
				}

				@Override
				public void onSuccess(Void result) {
					
					metricPanel.refreshMetric(metric);
					
					InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.MetricCreatedSuccess(), false);
					dial.center();
				}
			});
		}
	}
	
	@UiHandler("imgDelete")
	public void onDelete(ClickEvent e) {
		MetricService.Connection.getInstance().deleteAlert(alertSelectionModel.getSelectedObject(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
			}

			@Override
			public void onSuccess(Void result) {
				refresh();			
			}
		});
	}
	
	@UiHandler("imgCreate")
	public void onCreate(ClickEvent e) {
		MetricService.Connection.getInstance().getCreateQueries(metric, new AsyncCallback<MetricSqlQueries>() {

			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.ProblemCreateQueries(), caught.getMessage(), caught);
				dial.center();
			}

			@Override
			public void onSuccess(MetricSqlQueries result) {
				CreateQueryDialog dial = new CreateQueryDialog(result);
				dial.center();
			}
			
		});
	}
	
//	@UiHandler("btnEtl")
//	public void onEtl(ClickEvent event) {
//		final RepositoryDialog dial = new RepositoryDialog(metric, IRepositoryApi.GTW_TYPE);
//		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
//			
//			@Override
//			public void onClose(CloseEvent<PopupPanel> event) {
//				if(dial.isConfirm()) {
//					txtEtl.setText(metric.getEtlItemName());
//				}
//			}
//		});
//		dial.center();
//	}
	
	@UiHandler("btnLinked")
	public void onLinked(ClickEvent event) {
		final LinkedObjectDialog dial = new LinkedObjectDialog(metric);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(dial.isConfirm()) {
					lstLinked.clear();
					for(MetricLinkedItem it : metric.getLinkedItems()) {
						lstLinked.addItem(it.getItemName(), it.getItemId() + "");
					}
				}
			}
		});
		dial.center();
	}
	
	@UiHandler("imgAddAction")
	public void onAddAction(ClickEvent event) {
		ActionDialog dial = new ActionDialog(metric);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});
		dial.center();
	}
	
	@UiHandler("imgEditAction")
	public void onEditAction(ClickEvent event) {
		ActionDialog dial = new ActionDialog(metric, actionDatagrid.getSelection());
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});
		dial.center();
	}
	
	@UiHandler("imgDeleteAction")
	public void onDeleteAction(ClickEvent event) {
		final MetricAction ds = actionDatagrid.getSelection();
		if(ds != null) {
			final InformationsDialog dial = new InformationsDialog("Delete Action", Messages.lbl.Ok(), Messages.lbl.Cancel(), "Are you sure you want to delete this action : " + ds.getName(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						MetricService.Connection.getInstance().deleteMetricAction(ds, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
								
							}

							@Override
							public void onSuccess(Void result) {
								refresh();
							}
						});
						
					}
				}
			});
			dial.center();
		}
	}
	
	@UiHandler("imgRefreshAction")
	public void onRefreshAction(ClickEvent event) {
		refresh();
	}

//	@UiHandler("imgAddMap")
//	public void onAddMap(ClickEvent event) {
//		MapDialog dial = new MapDialog(metric, null);
//		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
//			@Override
//			public void onClose(CloseEvent<PopupPanel> event) {
//				refresh();
//			}
//		});
//		dial.center();
//	}
//	
//	@UiHandler("imgEditMap")
//	public void onEditMap(ClickEvent event) {
//		MapDialog dial = new MapDialog(metric, mapDatagrid.getSelection());
//		dial.addCloseHandler(new CloseHandler<PopupPanel>() {		
//			@Override
//			public void onClose(CloseEvent<PopupPanel> event) {
//				refresh();
//			}
//		});
//		dial.center();
//	}
//	
//	@UiHandler("imgDeleteMap")
//	public void onDeleteMap(ClickEvent event) {
//		final MetricMap ds = mapDatagrid.getSelection();
//		if(ds != null) {
//			final InformationsDialog dial = new InformationsDialog("Delete Map", Messages.lbl.Ok(), Messages.lbl.Cancel(), "Are you sure you want to delete this map : " + ds.getName(), true);
//			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
//				@Override
//				public void onClose(CloseEvent<PopupPanel> event) {
//					if (dial.isConfirm()) {
//						MetricService.Connection.getInstance().deleteMetricMap(ds, new AsyncCallback<Void>() {
//							@Override
//							public void onFailure(Throwable caught) {
//								caught.printStackTrace();
//								
//							}
//
//							@Override
//							public void onSuccess(Void result) {
//								refresh();
//							}
//						});
//						
//					}
//				}
//			});
//			dial.center();
//		}
//	}
//	
//	@UiHandler("imgRefreshMap")
//	public void onRefreshMap(ClickEvent event) {
//		refresh();
//	}
	
	private String getFormula(Alert alert){
		String result = "";
		boolean first = true;
		for(Condition cond : alert.getConditions()){
			if(first){
				first = false;
			} else {
				result += " " + alert.getOperator() + " ";
			}
			
			ConditionKpi ckpi = (ConditionKpi) cond.getConditionObject();
			if(ckpi.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.STATE_TYPE])) {
				result += ckpi.getStateType();
			}
			else if(ckpi.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.MISSING_TYPE])) {
				result += ckpi.getMissingType();
			}
			else if(ckpi.getType().equals(ConditionKpi.ALERT_TYPES[ConditionKpi.VALUE_TYPE])) {
				result += cond.getLeftOperand() + " " + cond.getOperator() + " " + cond.getRightOperand();
			}
			
		}
		return result;
	}
}
