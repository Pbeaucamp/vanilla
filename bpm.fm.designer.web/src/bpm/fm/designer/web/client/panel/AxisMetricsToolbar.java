package bpm.fm.designer.web.client.panel;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.dialog.AxisDialog;
import bpm.fm.designer.web.client.dialog.LinkThemeDialog;
import bpm.fm.designer.web.client.dialog.MetricDialog;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AxisMetricsToolbar extends Composite {

	public static final int METRIC_TOOLBAR = 0;
	public static final int AXE_TOOLBAR = 1;
	
	private static AxesMetricsToolbarUiBinder uiBinder = GWT.create(AxesMetricsToolbarUiBinder.class);

	interface AxesMetricsToolbarUiBinder extends UiBinder<Widget, AxisMetricsToolbar> {
	}
	
	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label lblTitle;
	
	@UiField
	Image imgAdd, imgDelete, imgRefresh, imgEdit, imgLink;
	
	private int type;
	private Composite composite;
	
	public AxisMetricsToolbar(int type, Composite composite) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.type = type;
		this.composite = composite;
	}
	
	public void setText(String title) {
		lblTitle.setText(title);
	}

	@UiHandler("imgAdd")
	public void onAdd(ClickEvent e) {
		if(type == AXE_TOOLBAR) {
			AxisDialog dial = new AxisDialog(refreshAddCloseHandler);
			dial.addCloseHandler(refreshAddCloseHandler);
			dial.center();
		}
		else if(type == METRIC_TOOLBAR) {
			MetricDialog dial = new MetricDialog(null);
			dial.addCloseHandler(refreshAddCloseHandler);
			dial.center();
		}
	}
	
	@UiHandler("imgDelete")
	public void onDelete(ClickEvent e) {
		if(type == AXE_TOOLBAR) {
			final Axis ds = ((AxisPanel)composite).getSelectedAxe();
			if(ds != null) {
				final InformationsDialog dial = new InformationsDialog(Messages.lbl.DeleteAxis(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.DeleteAxisPartOne() + " " + ds.getName() + " " + Messages.lbl.DeleteAxisPartTwo(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							MetricService.Connection.getInstance().deleteAxe(ds, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.DeleteAxisSuccess(), false);
									dial.center();
									
									refresh();
								}

								@Override
								public void onFailure(Throwable caught) {
									InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.DeleteAxisError(), caught.getMessage(), caught);
									dial.center();
								}
							});
						}
					}
				});
				dial.center();
			}
		}
		else if(type == METRIC_TOOLBAR) {
			final Metric ds = ((MetricPanel)composite).getSelectedMetric();
			if(ds != null) {
				final InformationsDialog dial = new InformationsDialog(Messages.lbl.DeleteMetric(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.DeleteMetricPartOne() + " " + ds.getName() + " " + Messages.lbl.DeleteMetricPartTwo(), true);
				
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
				
						if (dial.isConfirm()) {
							MetricService.Connection.getInstance().deleteMetric(ds, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.DeleteMetricSuccess(), false);
									dial.center();
									MainPanel.instance.selectionChanged(null);
									refresh();
								}

								@Override
								public void onFailure(Throwable caught) {
									InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.DeleteMetricError(), caught.getMessage(), caught);
									dial.center();
								}
							});
						}
					}
				});
				dial.center();
			}
		}
	}
	
	@UiHandler("imgRefresh")
	public void onRefresh(ClickEvent e) {
		refresh();
	}
	
	@UiHandler("imgEdit")
	public void onEdit(ClickEvent e) {
		if(type == AXE_TOOLBAR) {
			AxisDialog dial = new AxisDialog(((AxisPanel)composite).getSelectedAxe(), refreshAddCloseHandler);
			dial.addCloseHandler(refreshCloseHandler);
			dial.center();
		}
		else if(type == METRIC_TOOLBAR) {
			MetricDialog dial = new MetricDialog(((MetricPanel)composite).getSelectedMetric());
			dial.addCloseHandler(refreshCloseHandler);
			dial.center();
		}
	}
	
	@UiHandler("imgLink")
	public void onLink(ClickEvent e) {
		if(type == AXE_TOOLBAR) {
			LinkThemeDialog dial = new LinkThemeDialog(((AxisPanel)composite).getSelectedAxe());
			dial.addCloseHandler(refreshCloseHandler);
			dial.center();
		}
		else if(type == METRIC_TOOLBAR) {
			LinkThemeDialog dial = new LinkThemeDialog(((MetricPanel)composite).getSelectedMetric());
			dial.addCloseHandler(refreshCloseHandler);
			dial.center();
		}
	}

	private void refresh() {
		if(type == AXE_TOOLBAR) {
			((AxisPanel)composite).refresh();
		}
		else if(type == METRIC_TOOLBAR) {
			((MetricPanel)composite).refresh();
		}
	}
	
	private CloseHandler<PopupPanel> refreshCloseHandler = new CloseHandler<PopupPanel>() {
		
		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			refresh();
		}
	};
	
	private CloseHandler<PopupPanel> refreshAddCloseHandler = new CloseHandler<PopupPanel>() {
		
		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			refresh();
		}
	};
}
