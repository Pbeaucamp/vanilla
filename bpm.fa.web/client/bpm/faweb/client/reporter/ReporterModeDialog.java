package bpm.faweb.client.reporter;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.panels.center.ICubeViewer;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.reporter.widgets.CrossTabWidget;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ReporterModeDialog extends Tab {

	private static ReporterModeDialogUiBinder uiBinder = GWT.create(ReporterModeDialogUiBinder.class);

	interface ReporterModeDialogUiBinder extends UiBinder<Widget, ReporterModeDialog> {
	}

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	AbsolutePanel rootPanel;

	@UiField
	HTMLPanel gridPanel, previewPanel, previewPanelContainer;

	@UiField
	Label lbReporter, lblReportPreview;

	@UiField
	TextBox txtTitle;

	@UiField
	SimplePanel panelBin;

	@UiField
	Button btnApply;

	private CrossTabWidget grid;
	private FaWebDragController dragCtrl;
	private MainPanel mainPanel;

	public ReporterModeDialog(TabManager tabManager, MainPanel mainPanel, FaWebDragController dragCtrl) {
		super(tabManager, FreeAnalysisWeb.LBL.ReporterToolBar(), true);
		add(uiBinder.createAndBindUi(this));
		this.dragCtrl = dragCtrl;
		this.mainPanel = mainPanel;

		grid = new CrossTabWidget(dragCtrl);

		lbReporter.setText(FreeAnalysisWeb.LBL.ReporterPresentation());
		gridPanel.add(grid);
		txtTitle.getElement().setAttribute("placeholder", FreeAnalysisWeb.LBL.Title());

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	@UiHandler("btnPreview")
	void onPreview(ClickEvent e) {
		if (grid.getSelectedCells().isEmpty() && grid.getSelectedCols().isEmpty() && grid.getSelectedRows().isEmpty()) {
			Window.alert(FreeAnalysisWeb.LBL.ProvideMeasureAndDimension());
		}
		generateCube(true);
	}

	@UiHandler("btnApply")
	void onApply(ClickEvent event) {
		if (txtTitle.getText().isEmpty()) {
			Window.alert(FreeAnalysisWeb.LBL.ProvideName());
		}
		else {
			if (grid.getSelectedCells().isEmpty() && grid.getSelectedCols().isEmpty() && grid.getSelectedRows().isEmpty()) {
				Window.alert(FreeAnalysisWeb.LBL.ProvideMeasureAndDimension());
			}
			generateCube(false);
		}

	}

	@UiHandler("btnClear")
	void onClear(ClickEvent e) {
		grid.getSelectedRows().clear();
		grid.getSelectedCells().clear();
		grid.getSelectedCols().clear();
		grid.removeFromParent();
		grid = new CrossTabWidget(dragCtrl);
		gridPanel.add(grid);
		previewPanel.clear();
		previewPanel.add(lblReportPreview);
	}

	private void generateCube(final boolean isPreview) {
		List<String> rows = new ArrayList<String>();
		List<String> cols = new ArrayList<String>();

		for (DraggableTreeItem item : grid.getSelectedRows()) {
			rows.add(item.getUname());
		}

		for (DraggableTreeItem item : grid.getSelectedCols()) {
			cols.add(item.getUname());
		}

		for (DraggableTreeItem item : grid.getSelectedCells()) {
			rows.add(item.getUname());
		}

		FaWebService.Connect.getInstance().addMultiple(mainPanel.getKeySession(), rows, cols, new ArrayList<String>(), new AsyncCallback<InfosReport>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(InfosReport result) {
				if (isPreview) {
					GridCube gc = ((InfosReport) result).getGrid();
					previewPanel.clear();
					previewPanel.add((CubeView) mainPanel.setGridFromLocal((ICubeViewer) mainPanel.setGridFromRCP(result), false, gc));
				}
				else {
					Label title = new Label(txtTitle.getText());
					title.addStyleName("reportTitle");
					mainPanel.setGridFromRCP(result);
					mainPanel.getDisplayPanel().selectCubeViewer();

					for (Widget w : mainPanel.getDisplayPanel().getCubeViewerTab()) {
						if (w instanceof Label) {
							w.removeFromParent();
						}
					}
					mainPanel.getDisplayPanel().getCubeViewerTab().add(title);
				}

			}
		});
	}
}
