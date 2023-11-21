package bpm.faweb.client.projection.panel;

import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.center.ICubeViewer;
import bpm.faweb.client.panels.center.ProjectionTab;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.projection.data.DataField;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.infoscube.GridCube;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionCubeView extends Composite {

	private static ProjectionCubeViewUiBinder uiBinder = GWT.create(ProjectionCubeViewUiBinder.class);

	interface ProjectionCubeViewUiBinder extends UiBinder<Widget, ProjectionCubeView> {
	}
	
	@UiField
	HTMLPanel cubePanel;
	
	private MainPanel mainPanel;
	private ICubeViewer cubeViewer;
	
	private UpdateProjectionDataPanel previousValuePanel;
	
	private HTMLPanel projectionGrid;
	private HTMLPanel originalGrid;
	
	int height;
	
	public ProjectionCubeView(MainPanel mainPanel, ICubeViewer cubeViewer, UpdateProjectionDataPanel previousValuePanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.previousValuePanel = previousValuePanel;
		this.cubeViewer = cubeViewer;
		
		CubeView projGrid = new CubeView(mainPanel, cubeViewer, mainPanel.getInfosReport().getGrid(), false, true);

		projectionGrid = new HTMLPanel("");
		cubePanel.add(projectionGrid);
		
		Label lblTitle = new Label();
		lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);
		lblTitle.setText("Cube projection");
		projectionGrid.add(lblTitle);

		height = cubePanel.getOffsetHeight();
		
		projectionGrid.getElement().getStyle().setFloat(Float.LEFT);
		projectionGrid.getElement().getStyle().setWidth(100, Unit.PCT);
		projectionGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		
		cubePanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		cubePanel.getElement().getStyle().setTop(0, Unit.PX);
		cubePanel.getElement().getStyle().setBottom(0, Unit.PX);
		cubePanel.getElement().getStyle().setWidth(100, Unit.PCT);
		
		projectionGrid.getElement().getStyle().setOverflow(Overflow.AUTO);
		projectionGrid.add(projGrid);
	}
	
	public void setDrillThrough(List<List<DataField>> result, int row, int cell) {
		UpdateProjectionDataPanel panel = new UpdateProjectionDataPanel(mainPanel, cubeViewer, "Projection", result, ProjectionCubeView.this, row, cell);
		((ProjectionTab)cubeViewer).setUpdateProjectionView(panel);
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

	public void createOriginalGrid() {
		mainPanel.showWaitPart(true);
		FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), false, false, new AsyncCallback<GridCube>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				mainPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(GridCube result) {
				CubeView projGrid = new CubeView(mainPanel, cubeViewer, result, false, true);
				originalGrid = new HTMLPanel("");
				Label lblTitle = new Label();
				lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);
				lblTitle.setText("Cube original");
				originalGrid.add(lblTitle);
				
				originalGrid.getElement().getStyle().setFloat(Float.RIGHT);
				originalGrid.getElement().getStyle().setWidth(45, Unit.PCT);
				originalGrid.getElement().getStyle().setOverflow(Overflow.AUTO);
				originalGrid.getElement().getStyle().setHeight(100, Unit.PCT);
				originalGrid.add(projGrid);
				projectionGrid.getElement().getStyle().setWidth(45, Unit.PCT);
				
				cubePanel.add(originalGrid);
				mainPanel.showWaitPart(false);
			}

		});

	}

	public void setPreviousValuePanel(UpdateProjectionDataPanel updateProjectionDataPanel) {
		this.previousValuePanel = updateProjectionDataPanel;
	}
	
	public void setProjectionGrid(CubeView view) {
		projectionGrid.clear();
		Label lblTitle = new Label();
		lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);
		lblTitle.setText("Cube projection");
		projectionGrid.add(lblTitle);
		projectionGrid.add(view);
		
		if(cubePanel.getWidgetCount() > 2) {
			mainPanel.showWaitPart(true);
			FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), false, false, new AsyncCallback<GridCube>() {

				@Override
				public void onFailure(Throwable caught) {
					mainPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(GridCube result) {
					CubeView projGrid = new CubeView(mainPanel, cubeViewer, result, false, true);
					
					originalGrid.clear();
					Label lblTitle = new Label();
					lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);
					lblTitle.setText("Cube original");
					originalGrid.add(lblTitle);
					originalGrid.add(projGrid);
					mainPanel.showWaitPart(false);
				}

			});
		}
	}

	public void setOriginalGrid(CubeView view) {
		mainPanel.showWaitPart(true);
		originalGrid.clear();
		Label lblTitle = new Label();
		lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);
		lblTitle.setText("Cube original");
		originalGrid.add(lblTitle);
		originalGrid.add(view);
		
		FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), true, false, new AsyncCallback<GridCube>() {

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(GridCube result) {
				CubeView projGrid = new CubeView(mainPanel, cubeViewer, result, false, true);
				
				projectionGrid.clear();
				Label lblTitle = new Label();
				lblTitle.getElement().getStyle().setFontSize(14, Unit.PX);
				lblTitle.setText("Cube projection");
				projectionGrid.add(lblTitle);
				projectionGrid.add(projGrid);
				mainPanel.showWaitPart(false);
			}

		});
	}
	
	public CubeView getProjectionCube() {
		return (CubeView) projectionGrid.getWidget(1);
	}
	
	public CubeView getOriginCube() {
		return (CubeView) originalGrid.getWidget(1);
	}

	public void createCubeSeparator() {
		cubePanel.add(createSeparator());
	}

	public UpdateProjectionDataPanel getPreviousValuePanel() {
		return previousValuePanel;
	}

	public ComplexPanel getProjectionGrid() {
		return projectionGrid;
	}
}
