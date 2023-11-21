package bpm.fwr.client;

import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.draggable.dragcontrollers.BinColumnDragController;
import bpm.fwr.client.draggable.dragcontrollers.BinDragController;
import bpm.fwr.client.draggable.dragcontrollers.DataDragController;
import bpm.fwr.client.draggable.dragcontrollers.PaletteDragController;
import bpm.fwr.client.panels.ReportViewer;
import bpm.fwr.client.panels.ToolPanel;
import bpm.fwr.client.panels.TopToolbar;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WysiwygPanel extends Composite {

	private static final int CLASSIC_NAVIGATION_PANEL = 400;
	private static final int CLASSIC_NAVIGATION = 395;

	private static final int NAVIGATION_COLLAPSE = 35;
	private static final int NAVIGATION_PANEL_COLLAPSE = 30;

	private static WysiwygPanelUiBinder uiBinder = GWT.create(WysiwygPanelUiBinder.class);

	interface WysiwygPanelUiBinder extends UiBinder<Widget, WysiwygPanel> {
	}

	@UiField
	AbsolutePanel absolutePanelRoot;

	@UiField
	DockLayoutPanel dockPanel;

	@UiField
	SimplePanel panelToolbar, leftPanel, reportContentPanel;

	private ToolPanel toolPanel;

	private ReportViewer reportViewer;
	
	private InfoUser infoUser;

	private PickupDragController paletteDragController, dragController, dataDragController;
	private PickupDragController groupDragController, resourceDragController, reportWidgetDragController;
	private PickupDragController detailDragController, cellsDragController;

	// Number of new report for this session
	private int indexNewReport = 0;
	private List<FwrMetadata> metadatas;

	public WysiwygPanel(boolean openReport, InfoUser infoUser, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		init(infoUser, keycloak);
		createTabReportPanel(openReport);
	}

	public WysiwygPanel(boolean openReport, InfoUser infoUser, int metadataid, String modelName, String packageName, String queryName, Boolean formatted, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		init(infoUser, keycloak);
		initFromQuery(metadataid, modelName, packageName, queryName, formatted);

	}

	private void init(InfoUser infoUser, Keycloak keycloak) {
		this.infoUser = infoUser;
		
		TopToolbar topToolbar = new TopToolbar(this, infoUser, keycloak);
		topToolbar.addStyleName(VanillaCSS.MENU_HEAD);
		panelToolbar.setWidget(topToolbar);

		paletteDragController = new PaletteDragController(absolutePanelRoot, false);
		dragController = new BinDragController(absolutePanelRoot, false);
		reportWidgetDragController = new BinColumnDragController(absolutePanelRoot, false);
		dataDragController = new DataDragController(absolutePanelRoot, false);
		resourceDragController = new DataDragController(absolutePanelRoot, false);
		groupDragController = new BinColumnDragController(absolutePanelRoot, false);
		detailDragController = new BinColumnDragController(absolutePanelRoot, false);
		cellsDragController = new BinColumnDragController(absolutePanelRoot, false);

		toolPanel = new ToolPanel(this, paletteDragController, dataDragController, resourceDragController);
		leftPanel.setWidget(toolPanel);

		initFromLoad();
	}
	
	public InfoUser getInfoUser() {
		return infoUser;
	}

	private void createTabReportPanel(boolean openReport) {
		reportViewer = new ReportViewer(this);
		reportContentPanel.setWidget(reportViewer);

		if (!openReport) {
			String name = buildNameReport();
			addTabReport(name, null);
		}
	}

	public String buildNameReport() {
		String reportName = Bpm_fwr.LBLW.NewReport() + " " + (indexNewReport + 1);
		indexNewReport++;
		return reportName;
	}

	public void openTemplate(Template<IReport> result) {
		addTabReport(result.getName(), (FWRReport) result.getItem());
	}

	public void addTabReport(String nameReport, FWRReport report) {
		reportViewer.openViewer(this, nameReport, report, reportWidgetDragController, paletteDragController, dragController, dataDragController, groupDragController, detailDragController, resourceDragController, cellsDragController);
	}

	public void addTabReportWithList(String nameReport, FWRReport report, DataSet dataset, Boolean formatted) {
		reportViewer.openViewerFromQuery(this, nameReport, report, reportWidgetDragController, paletteDragController, dragController, dataDragController, groupDragController, detailDragController, resourceDragController, cellsDragController, dataset, formatted);
	}

	public void setBinVisibility(boolean visibility) {
		// imgBin.setVisible(visibility);
	}

	public void setMetadatas(List<FwrMetadata> metadatas) {
		this.metadatas = metadatas;
	}

	/**
	 * We use that method to get the default value for each metadata
	 * 
	 * @return hashmap with metadata id for key and key language for value
	 */
	public String getLanguageDefaultForMetadataWithId() {
		// HashMap<Integer, String> defaultLanguages = new HashMap<Integer,
		// String>();
		if (metadatas != null) {
			for (FwrMetadata metadata : metadatas) {

				if (metadata.getLocales() != null && !metadata.getLocales().isEmpty()) {
					for (String key : metadata.getLocales().keySet()) {
						return key;
					}
				}
			}
		}
		return "";
	}

	public void importReport(FWRReport report) {
		addTabReport(report.getSaveOptions().getName(), report);
	}

	public void updateDatasetTreePart(String selectedLanguage) {
		toolPanel.getDatasetTree().setLanguages(selectedLanguage);
		toolPanel.getDatasetTree().buildTree(false);
	}

	public void initFromLoad() {
		toolPanel.showWaitPart(true);

		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

		FwrServiceConnection.Connect.getInstance().getMetadatas(infoUser.getGroup().getName(), new AsyncCallback<List<FwrMetadata>>() {
			public void onSuccess(List<FwrMetadata> metadatas) {

				toolPanel.showWaitPart(false);

				WysiwygPanel.this.metadatas = metadatas;
				toolPanel.getDatasetTree().setMetadatas(metadatas);
			}

			public void onFailure(Throwable caught) {

				toolPanel.showWaitPart(false);

				caught.printStackTrace();
				metadatas = null;
				toolPanel.getDatasetTree().setMetadatas(null);
			}
		});
	}

	public List<FwrMetadata> getMetadatas() {
		return metadatas;
	}

	public FwrBusinessModel searchedModel(String modelName) {

		for (FwrMetadata metadata : getMetadatas()) {
			if (metadata.isBrowsed()) {
				for (FwrBusinessModel model : metadata.getBusinessModels()) {
					if (model.getName().equals(modelName)) {
						return model;
					}
				}
			}
		}

		return null;
	}

	public void collapseNavigationPanel(boolean isCollapse) {
		if (isCollapse) {
			dockPanel.setWidgetSize(leftPanel, CLASSIC_NAVIGATION_PANEL);
			toolPanel.adaptSize(CLASSIC_NAVIGATION, isCollapse);
		} else {
			dockPanel.setWidgetSize(leftPanel, NAVIGATION_COLLAPSE);
			toolPanel.adaptSize(NAVIGATION_PANEL_COLLAPSE, isCollapse);
		}
	}

	public void initFromQuery(int metadataid, String modelName, String packageName, String queryName, final Boolean formatted) {
		FwrServiceMetadata.Connect.getInstance().loadDatasetFromQuery(metadataid, modelName, packageName, queryName, new AsyncCallback<DataSet>() {

			@Override
			public void onSuccess(DataSet result) {
				reportViewer = new ReportViewer(WysiwygPanel.this);
				reportContentPanel.setWidget(reportViewer);
				String name = buildNameReport();
				addTabReportWithList(name, null, result, formatted);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

}
