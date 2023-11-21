package bpm.faweb.client.projection.panel;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.RepositoryContentDialog;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.center.ICubeViewer;
import bpm.faweb.client.panels.center.ProjectionTab;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.data.DataField;
import bpm.faweb.client.projection.data.DataGridWithScrollEvent;
import bpm.faweb.client.projection.dialog.DialogCreateProjection;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.TreeParentDTO;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.images.CommonImages;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * A panel which contains projection data Can show original data to compare
 * values
 * 
 * @author Marc Lanquetin
 * 
 */
public class UpdateProjectionDataPanel extends Composite {

	private static final String CSS_PAGER = "pageGrid";

	private static UpdateProjectionDataPanelUiBinder uiBinder = GWT.create(UpdateProjectionDataPanelUiBinder.class);

	interface UpdateProjectionDataPanelUiBinder extends UiBinder<Widget, UpdateProjectionDataPanel> {
	}

	@UiField
	Image imgSave, imgCompare, imgCube, imgEdit;

	DataGridWithScrollEvent dataGrid;

	@UiField
	HTMLPanel dataGridPanel;

	// @UiField
	// Label lblTitle;

	private MainPanel mainPanel;
	private DataGridWithScrollEvent projGrid, origGrid;

	private int columnIndex;

	private ICubeViewer cubeViewer;
	private ProjectionCubeView previousCubeView;

	private int row;
	private int cell;

	public UpdateProjectionDataPanel(MainPanel mainPanel, ICubeViewer cubeViewer, String name, List<List<DataField>> data, ProjectionCubeView previousCubeView, int row, int cell) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.cubeViewer = cubeViewer;

		this.row = row;
		this.cell = cell;

		this.previousCubeView = previousCubeView;

		imgSave.setResource(CommonImages.INSTANCE.viewer_report_history());
		imgCube.setResource(FaWebImage.INSTANCE.cube());

		imgSave.setTitle(FreeAnalysisWeb.LBL.Save());
		imgCube.setTitle(FreeAnalysisWeb.LBL.ProjectionCubeViewBtn());

		imgSave.addClickHandler(clickHandler);
		imgCube.addClickHandler(clickHandler);
		imgEdit.setResource(CommonImages.INSTANCE.edit_24());
		imgEdit.setTitle(FreeAnalysisWeb.LBL.ProjectionEdit());
		imgEdit.addClickHandler(clickHandler);

		if (mainPanel.getActualProjection().getType().equals(Projection.TYPE_WHATIF)) {
			imgCompare.setResource(FaWebImage.INSTANCE.projection_comparer_24());
			imgCompare.setTitle(FreeAnalysisWeb.LBL.ProjectionCompareBtn());
			imgCompare.addClickHandler(clickHandler);
		}
		else {
			imgCompare.removeFromParent();
		}

		if (data != null) {
			refresh(data, true);
		}
	}

	private ClickHandler clickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(imgSave)) {
				mainPanel.showWaitPart(true);
				FaWebService.Connect.getInstance().getRepositories(mainPanel.getKeySession(), FaWebService.PROJECTION, new AsyncCallback<TreeParentDTO>() {

					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						mainPanel.showWaitPart(false);
					}

					public void onSuccess(TreeParentDTO result) {
						if (result != null && result instanceof TreeParentDTO) {
							RepositoryContentDialog dial = new RepositoryContentDialog(mainPanel, result, null);
							dial.center();
							mainPanel.showWaitPart(false);
						}
					}
				});
			}
			else if (event.getSource().equals(imgCompare)) {
				if (origGrid == null) {
					createGrid(false);
				}
				else {
					dataGridPanel.clear();

					projGrid.getElement().getStyle().setWidth(100, Unit.PCT);

					dataGridPanel.add(projGrid);
					origGrid = null;
				}
			}
			else if (event.getSource().equals(imgCube)) {
				ProjectionCubeView panel = null;
				if (previousCubeView != null) {
					panel = previousCubeView;
					panel.setPreviousValuePanel(UpdateProjectionDataPanel.this);
				}
				else {
					panel = new ProjectionCubeView(mainPanel, cubeViewer, UpdateProjectionDataPanel.this);
				}

				((ProjectionTab)cubeViewer).setProjectionView(panel);
			}
			else if (event.getSource().equals(imgEdit)) {
				DialogCreateProjection dial = new DialogCreateProjection(mainPanel, mainPanel.getActualProjection());
				dial.center();
			}
		}
	};

	private HTMLPanel fillDataGrid(List<List<DataField>> data, boolean isProjection) {

		HTMLPanel gridPanel = new HTMLPanel("");

		Label lblTitle = new Label();
		lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);

		if (isProjection) {
			lblTitle.setText(FreeAnalysisWeb.LBL.ProjectionProjValues());
		}
		else {
			lblTitle.setText(FreeAnalysisWeb.LBL.ProjectionOriginalValues());
		}

		gridPanel.add(lblTitle);

		dataGrid = new DataGridWithScrollEvent(50);

		dataGrid.setEmptyTableWidget(new Label("No data selected"));
		dataGrid.setMinimumTableWidth(45, Unit.PCT);
		dataGrid.setHeight(Window.getClientHeight() - 200 + "px");

		List<DataField> fields = data.get(0);

		columnIndex = 0;

		for (DataField field : fields) {

			Cell<String> cell = null;
			cell = new TextCell();

			Column<List<DataField>, String> column = new Column<List<DataField>, String>(cell) {
				private int index = columnIndex;

				@Override
				public String getValue(List<DataField> object) {
					return object.get(index).getValue();
				}
			};
			columnIndex++;

			dataGrid.addColumn(column, field.getName());

			dataGrid.setColumnWidth(column, 100, Unit.PX);
		}

		ListDataProvider<List<DataField>> provider = new ListDataProvider<List<DataField>>();

		provider.addDataDisplay(dataGrid);
		provider.setList(data);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(CSS_PAGER);
		pager.setDisplay(dataGrid);

		gridPanel.add(dataGrid);
		gridPanel.add(pager);

		if (isProjection) {
			gridPanel.getElement().getStyle().setFloat(Float.LEFT);
			projGrid = dataGrid;
			gridPanel.getElement().getStyle().setWidth(100, Unit.PCT);
		}
		else {
			gridPanel.getElement().getStyle().setFloat(Float.RIGHT);
			origGrid = dataGrid;
			gridPanel.getElement().getStyle().setWidth(45, Unit.PCT);
			projGrid.getParent().getElement().getStyle().setWidth(45, Unit.PCT);
		}

		addScrollHandler(dataGrid);

		return gridPanel;
	}

	protected void refresh(List<List<DataField>> result, final boolean isProjection) {
		HTMLPanel grid = fillDataGrid(result, isProjection);

		if (!isProjection) {

			dataGridPanel.add(createSeparator());

			dataGridPanel.add(grid);
		}

		else {
			dataGridPanel.clear();
			dataGridPanel.add(grid);
		}
	}

	protected Widget createSeparator() {
		SimplePanel pan = new SimplePanel();
		pan.getElement().getStyle().setBorderWidth(2, Unit.PX);
		pan.getElement().getStyle().setBorderColor("grey");
		pan.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		pan.setSize("1px", Window.getClientHeight() - 160 + "px");
		pan.getElement().getStyle().setMarginLeft(5, Unit.PCT);
		pan.getElement().getStyle().setMarginTop(10, Unit.PX);
		pan.getElement().getStyle().setMarginBottom(10, Unit.PX);
		pan.getElement().getStyle().setFloat(Float.LEFT);
		return pan;
	}

	public void createGrid(final boolean isProjection) {
		FaWebService.Connect.getInstance().drillThroughProjection(mainPanel.getKeySession(), row, cell, null, new AsyncCallback<List<List<DataField>>>() {
			@Override
			public void onSuccess(List<List<DataField>> result) {
				refresh(result, isProjection);
			}

			@Override
			public void onFailure(Throwable caught) {
				ExceptionManager.getInstance().handleException(caught, "Error while retriving data.");
			}
		});
	}

	/**
	 * When the original values are shown, the two grids will vertically scroll
	 * together to compare data
	 * 
	 * @param grid
	 */
	public void addScrollHandler(DataGridWithScrollEvent grid) {

		if (grid == projGrid) {
			dataGrid.addScrollHandler(new ScrollHandler() {
				@Override
				public void onScroll(ScrollEvent event) {

					if (origGrid != null) {
						origGrid.getScrollPanel().setVerticalScrollPosition(projGrid.getScrollPanel().getVerticalScrollPosition());
					}
				}
			});
		}
		else {
			dataGrid.addScrollHandler(new ScrollHandler() {
				@Override
				public void onScroll(ScrollEvent event) {

					if (projGrid != null) {
						projGrid.getScrollPanel().setVerticalScrollPosition(origGrid.getScrollPanel().getVerticalScrollPosition());
					}
				}
			});
		}

	}

	public void setPreviousCubeView(ProjectionCubeView projectionCubeView) {
		this.previousCubeView = projectionCubeView;
	}
}
