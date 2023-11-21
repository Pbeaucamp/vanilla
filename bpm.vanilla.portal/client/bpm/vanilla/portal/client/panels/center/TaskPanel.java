package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.viewer.FormsDTO;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.services.PerFormsService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class TaskPanel extends Tab {

	private static TaskPanelUiBinder uiBinder = GWT.create(TaskPanelUiBinder.class);

	interface TaskPanelUiBinder extends UiBinder<Widget, TaskPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelToolbar;
	
	@UiField
	SimplePanel panelTask;
	
	private ContentDisplayPanel mainPanel;
	private ListDataProvider<FormsDTO> dataProviderTask;

	public TaskPanel(ContentDisplayPanel mainPanel) {
		super(mainPanel, ToolsGWT.lblCnst.Task(), true);
		this.add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		
		panelTask.add(createTaskGrid());
		
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
		
		getForms();
	}
	
	private DataGrid<FormsDTO> createTaskGrid() {
		ImageResourceCell imgCell = new ImageResourceCell();
		Column<FormsDTO, ImageResource> logoColumn = new Column<FormsDTO, ImageResource>(imgCell) {

			@Override
			public ImageResource getValue(FormsDTO object) {
				return PortalImage.INSTANCE.forms();
			}
		};
		
		TextCell txtCell = new TextCell();
		Column<FormsDTO, String> nameColumn = new Column<FormsDTO, String>(txtCell) {

			@Override
			public String getValue(FormsDTO object) {
				return object.getName();
			}
		};
		
		Column<FormsDTO, String> originColumn = new Column<FormsDTO, String>(txtCell) {

			@Override
			public String getValue(FormsDTO object) {
				return object.getOriginName();
			}
		};
		
		DateCell dateCell = new DateCell();
		Column<FormsDTO, Date> dateColumn = new Column<FormsDTO, Date>(dateCell) {

			@Override
			public Date getValue(FormsDTO object) {
				return object.getCreatedOn();
			}
		};
		
		ButtonCell btnCell = new ButtonCell();
		Column<FormsDTO, String> btnColumn = new Column<FormsDTO, String>(btnCell) {

			@Override
			public String getValue(FormsDTO object) {
				if (object.isVanillaFormSubmit()) {
					return ToolsGWT.lblCnst.formsButtonVanillaSubmit();
				}
				else if (object.isVanillaFormValidate()){
					return ToolsGWT.lblCnst.formsButtonVanillaValidate();
				}
				else if (object.isWorkflowForm()) {
					return ToolsGWT.lblCnst.formsButtonWorkflowAccess();
				}
				return "";
			}
		};
		btnColumn.setFieldUpdater(new FieldUpdater<FormsDTO, String>() {
			
			@Override
			public void update(int index, FormsDTO object, String value) {
				access(object);
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<FormsDTO> dataGrid = new DataGrid<FormsDTO>(10000, resources);
//		dataGrid.addStyleName(CSS_DATA_GRID_TASK);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(logoColumn);
		dataGrid.addColumn(nameColumn);
		dataGrid.addColumn(originColumn);
		dataGrid.addColumn(dateColumn);
		dataGrid.addColumn(btnColumn);
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoTask()));

	    dataProviderTask = new ListDataProvider<FormsDTO>();
	    dataProviderTask.addDataDisplay(dataGrid);
	    dataProviderTask.setList(new ArrayList<FormsDTO>());
	    
	    SelectionModel<FormsDTO> selectionModel = new SingleSelectionModel<FormsDTO>();
	    dataGrid.setSelectionModel(selectionModel);
	    
	    return dataGrid;
	}

	private void getForms() {
		showWaitPart(true);
		
		PerFormsService.Connect.getInstance().getForms(new AsyncCallback<List<FormsDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}
			
			@Override
			public void onSuccess(List<FormsDTO> result) {
				showWaitPart(false);
				
				dataProviderTask.setList(result);
			}
		});
	}
	
	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		getForms();
	}
	
	private void access(FormsDTO dto) {
		mainPanel.openViewer(dto);
	}

}
