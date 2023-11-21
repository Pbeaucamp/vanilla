package bpm.smart.web.client.panels.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.CollapseWidget;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.dialogs.ImportProjectDialog;
import bpm.smart.web.client.dialogs.ProjectDialog;
import bpm.smart.web.client.dialogs.ShareDialog;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class NavigationPanel extends CollapseWidget {

	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);

	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> {
	}

	interface MyStyle extends CssResource {
		String childCell();

		String selectedchildCell();

		String groupHeaderCell();
		
		String mainRow();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, panelToolbarScript;

	@UiField
	SimplePanel panelGrid, panelPager;

	@UiField
	Image btnEditProject, btnDeleteProject, btnRefresh, btnAddProject, btnShareProject, btnImportProject, btnExportProject;

	@UiField
	Image btnAddScript, btnDeleteScript, imgExpand, imgCollapse;

	@UiField
	HTMLPanel navigationPanel;

	private AirPanel airPanel;
	private CollapsePanel collapsePanel;

	private ListDataProvider<AirProject> dataProvider;
	private SingleSelectionModel<AirProject> selectionModel;
	private ListHandler<AirProject> sortNameHandler;
	private ListHandler<AirProject> sortDateHandler;
	private ListHandler<AirProject> sortAuthorHandler;
	private DataGrid<AirProject> datagrid;

	private Set<Integer> showingScripts = new HashSet<Integer>();
	private List<RScript> scriptList = new ArrayList<RScript>();
	private List<RScriptModel> modelsList = new ArrayList<RScriptModel>();

	private AirProject currentProject;
	private RScript currentScript;
	private RScriptModel currentModel;

	private int idUser;
	
	private List<Widget> collapseWidgets;

	public NavigationPanel(AirPanel airPanel, CollapsePanel collapsePanel) {
		initWidget(uiBinder.createAndBindUi(this));
		initJs(this);
		this.airPanel = airPanel;
		this.collapsePanel = collapsePanel;
		this.idUser = airPanel.getUser().getId();

		imgExpand.setVisible(false);

		this.datagrid = createGridData();
		panelGrid.add(datagrid);

		btnAddScript.setVisible(false);
		btnDeleteScript.setVisible(false);
		btnShareProject.setVisible(false);
		btnExportProject.setVisible(false);
		loadEvent();
		
		collapseWidgets = new ArrayList<>();
		collapseWidgets.add(panelToolbar);
		collapseWidgets.add(panelGrid);
		collapseWidgets.add(panelPager);
	}

	private void loadEvent() {
		airPanel.showWaitPart(true);
		
		SmartAirService.Connect.getInstance().loadAllRScripts(new AsyncCallback<List<RScript>>() {

			@Override
			public void onFailure(Throwable caught) {
				airPanel.showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadScripts());
			}

			@Override
			public void onSuccess(List<RScript> result) {
				//airPanel.showWaitPart(false);
				Collections.sort(result, new Comparator<RScript>() {
					@Override
					public int compare(RScript script1, RScript script2) {
						return script1.getName().compareToIgnoreCase(script2.getName());
					}
				});
				scriptList = result;

				SmartAirService.Connect.getInstance().getLastScriptModels(result, new AsyncCallback<List<RScriptModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						airPanel.showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadScriptModels());
					}

					@Override
					public void onSuccess(List<RScriptModel> result) {
						//airPanel.showWaitPart(false);

						modelsList = result;
						SmartAirService.Connect.getInstance().getProjects(idUser, new AsyncCallback<List<AirProject>>() {

							@Override
							public void onFailure(Throwable caught) {
								airPanel.showWaitPart(false);

								caught.printStackTrace();

								ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadProjects());
							}

							@Override
							public void onSuccess(List<AirProject> result) {
								airPanel.showWaitPart(false);
								
								Collections.sort(result, new Comparator<AirProject>() {
									@Override
									public int compare(AirProject p1, AirProject p2) {
										return p1.getName().compareToIgnoreCase(p2.getName());
									}
								});
								dataProvider.setList(result);
								sortNameHandler.setList(dataProvider.getList());
								sortDateHandler.setList(dataProvider.getList());
								sortAuthorHandler.setList(dataProvider.getList());
								
								if(result.contains(currentProject)){
									selectionModel.setSelected(currentProject, true);
								} else {
									currentProject = null;
									selectionModel.clear();
								}
								

							}
						});
					}
				});
			}
		});
	}

	private DataGrid<AirProject> createGridData() {
		TextCell cell = new TextCell();
		final Column<AirProject, String> nameColumn = new Column<AirProject, String>(cell) {

			@Override
			public String getValue(AirProject object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		final Column<AirProject, String> dateColumn = new Column<AirProject, String>(cell) {

			@Override
			public String getValue(AirProject object) {
				return object.getDate().toString().substring(0, 10);
			}
		};
		dateColumn.setSortable(true);
		final Column<AirProject, String> authorColumn = new Column<AirProject, String>(cell) {

			@Override
			public String getValue(AirProject object) {
				return object.getAuthor();
			}
		};
		authorColumn.setSortable(true);
		ImageCell imageCell = new ImageCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) {
					// The template will sanitize the URI.
					sb.appendHtmlConstant("<img src = '" + value + "' height = '30px' width = '30px' />");
				}
			}
		};

		final Column<AirProject, String> iconColumn = new Column<AirProject, String>(imageCell) {

			@Override
			public String getValue(AirProject object) {

				return object.getUrlIcon();
			}

		};

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<AirProject> dataGrid = new DataGrid<AirProject>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(dateColumn, LabelsConstants.lblCnst.Date());
		dataGrid.addColumn(authorColumn, LabelsConstants.lblCnst.Author());
		dataGrid.addColumn(iconColumn, LabelsConstants.lblCnst.Icon());
		dataGrid.setColumnWidth(nameColumn, 35.0, Unit.PCT);
		dataGrid.setColumnWidth(dateColumn, 25.0, Unit.PCT);
		dataGrid.setColumnWidth(authorColumn, 25.0, Unit.PCT);
		dataGrid.setColumnWidth(iconColumn, 15.0, Unit.PCT);

		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<AirProject>();
		dataProvider.addDataDisplay(dataGrid);

		sortNameHandler = new ListHandler<AirProject>(new ArrayList<AirProject>());
		sortNameHandler.setComparator(nameColumn, new Comparator<AirProject>() {

			@Override
			public int compare(AirProject m1, AirProject m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		sortDateHandler = new ListHandler<AirProject>(new ArrayList<AirProject>());
		sortDateHandler.setComparator(dateColumn, new Comparator<AirProject>() {

			@Override
			public int compare(AirProject m1, AirProject m2) {
				return m1.getDate().compareTo(m2.getDate());
			}
		});
		sortAuthorHandler = new ListHandler<AirProject>(new ArrayList<AirProject>());
		sortAuthorHandler.setComparator(authorColumn, new Comparator<AirProject>() {

			@Override
			public int compare(AirProject m1, AirProject m2) {
				return m1.getAuthor().compareTo(m2.getAuthor());
			}
		});

		dataGrid.addColumnSortHandler(sortNameHandler);
		dataGrid.addColumnSortHandler(sortDateHandler);
		dataGrid.addColumnSortHandler(sortAuthorHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<AirProject>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		// pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		this.datagrid = dataGrid;
		/**
		 * Renders the data rows that display each contact in the table.
		 */
		class CustomTableBuilder extends AbstractCellTableBuilder<AirProject> {

			private final String childCell = " " + style.childCell();
			private final String selectedchildCell = " " + style.selectedchildCell();
			private final String rowStyle;
			private final String selectedRowStyle;
			private final String cellStyle;
			private final String selectedCellStyle;

			public CustomTableBuilder() {
				super(datagrid);

				// Cache styles for faster access.
				Style style = datagrid.getResources().style();
				rowStyle = style.evenRow();
				selectedRowStyle = " " + style.selectedRow();
				cellStyle = style.cell() + " " + style.evenRowCell();
				selectedCellStyle = " " + style.selectedRowCell();
			}

			@Override
			public void buildRowImpl(AirProject rowValue, int absRowIndex) {
				buildProjectRow(rowValue, absRowIndex);

				// Display list of scripts.
				if (showingScripts.contains(rowValue.getId())) {
					List<RScript> scripts = getScriptsbyProject(rowValue);
					for (RScript script : scripts) {
						buildScriptRow(script, getModelbyScript(script), absRowIndex);
					}
				}
			}

			private void buildProjectRow(AirProject rowValue, int absRowIndex) {
				// Calculate the row styles.
				SelectionModel<? super AirProject> selectionModel = datagrid.getSelectionModel();
				boolean isSelected = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
				StringBuilder trClasses = new StringBuilder(rowStyle+ " " + NavigationPanel.this.style.mainRow());
				if (isSelected) {
					trClasses.append(selectedRowStyle);
				}

				// Calculate the cell styles.
				String cellStyles = cellStyle;
				if (isSelected) {
					cellStyles += selectedCellStyle;
				}

				TableRowBuilder row = startRow();

				row.className(trClasses.toString());
				row.attribute("onclick", "setTimeout(function(){clickProject(" + rowValue.getId() + "," + absRowIndex + ");},100)");

				// Name column.
				TableCellBuilder td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(0), nameColumn, rowValue);

				td.endTD();

				// Date column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(1), dateColumn, rowValue);

				td.endTD();

				// Author column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(1), authorColumn, rowValue);
				td.endTD();

				// Icon column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(1), iconColumn, rowValue);
				td.endTD();

				row.endTR();
			}

			private void buildScriptRow(RScript rowValue, RScriptModel rowModel, int absRowIndex) {
				// Calculate the row styles.

				boolean isSelected = (currentScript == null) ? false : (rowValue.getId() == currentScript.getId());

				StringBuilder trClasses = new StringBuilder(rowStyle);

				// Calculate the cell styles.
				String cellStyles = cellStyle;
				if (isSelected) {
					cellStyles += selectedchildCell;
				}
				else {
					cellStyles += childCell;
				}

				TableRowBuilder row = startRow();
				row.className(trClasses.toString());
				row.attribute("onclick", "clickScript(" + rowValue.getId() + "," + absRowIndex + ");");

				// Name column.
				TableCellBuilder td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text(rowValue.getName());

				td.endTD();

				// Date column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text(rowModel.getDateVersion().toString().substring(0, 10));

				td.endTD();

				// author column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();

				// icon column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();

				row.endTR();
			}
		}
		dataGrid.setTableBuilder(new CustomTableBuilder());

		return dataGrid;
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadEvent();
	}

	@UiHandler("btnAddProject")
	public void onAddProjectClick(ClickEvent event) {
		ProjectDialog dial = new ProjectDialog(this);
		dial.center();
	}

	public void addProject(AirProject project) {
		airPanel.showWaitPart(true);

		SmartAirService.Connect.getInstance().addOrEditProject(project, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				airPanel.showWaitPart(false);

				loadEvent();

				// airPanel.updateViewerPanel();

			}

			@Override
			public void onFailure(Throwable caught) {
				airPanel.showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToAddAProject());
			}
		});
	}

	@UiHandler("btnEditProject")
	public void onEditClick(ClickEvent event) {
		AirProject selectedProject = selectionModel.getSelectedObject();
		if (selectedProject == null) {
			return;
		}

		ProjectDialog dial = new ProjectDialog(this, selectedProject);
		dial.center();
	}

	@UiHandler("btnDeleteProject")
	public void onDeleteClick(ClickEvent event) {
		deleteProject();

	}

	@UiHandler("btnShareProject")
	public void onShareClick(ClickEvent event) {
		AirProject selectedProject = selectionModel.getSelectedObject();
		if (selectedProject == null) {
			return;
		}
		ShareDialog dial = new ShareDialog(this, currentProject);
		dial.center();

	}

	@UiHandler("btnAddScript")
	public void onAddScriptClick(ClickEvent event) {
		AirProject selectedProject = selectionModel.getSelectedObject();
		airPanel.refresh(selectedProject, new RScript(), new RScriptModel(), new ArrayList<RScriptModel>());
	}

	@UiHandler("btnDeleteScript")
	public void onDeleteScriptClick(ClickEvent event) {
		if (currentScript == null) {
			return;
		}
		final InformationsDialog dialConfirm = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeleteScript(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					airPanel.showWaitPart(true);

					SmartAirService.Connect.getInstance().deleteRScript(currentScript, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							airPanel.showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToDeleteScript());
						}

						@Override
						public void onSuccess(Void result) {
							airPanel.showWaitPart(false);
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DeleteScriptSuccessfull());

							loadEvent();
							airPanel.onScriptDelete(currentScript);
						}
					});
				}
			}
		});
		dialConfirm.center();

	}

	@UiHandler("btnImportProject")
	public void onImportProjectClick(ClickEvent event) {
		final ImportProjectDialog dial = new ImportProjectDialog(this);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isSuccessed()) {
					onRefreshClick(null);
				}

			}
		});

	}

	@UiHandler("btnExportProject")
	public void onExportProjectClick(ClickEvent event) {
		if (currentProject == null) {
			return;
		}
		final InformationsDialog dialConfirm = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.AllVersions(), LabelsConstants.lblCnst.OnlyTheLast(), LabelsConstants.lblCnst.ExportProjectVersions(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					airPanel.showWaitPart(true);

					SmartAirService.Connect.getInstance().zipProject(currentProject, true, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							airPanel.showWaitPart(false);
							caught.printStackTrace();
							ExceptionManager.getInstance().handleException(caught, caught.getMessage());

						}

						@Override
						public void onSuccess(String result) {
							airPanel.showWaitPart(false);
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + "vanillaair";
							ToolsGWT.doRedirect(fullUrl);

						}
					});
				}
				else {
					airPanel.showWaitPart(true);

					SmartAirService.Connect.getInstance().zipProject(currentProject, false, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							airPanel.showWaitPart(false);
							caught.printStackTrace();
							ExceptionManager.getInstance().handleException(caught, caught.getMessage());

						}

						@Override
						public void onSuccess(String result) {
							airPanel.showWaitPart(false);
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + "vanillaair";
							ToolsGWT.doRedirect(fullUrl);

						}
					});
				}
			}
		});
		dialConfirm.center();

	}

	private void deleteProject() {
		final AirProject selectedProject = selectionModel.getSelectedObject();
		if (selectedProject == null) {
			return;
		}

		final InformationsDialog dialConfirm = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeleteProject(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					airPanel.showWaitPart(true);

					SmartAirService.Connect.getInstance().deleteProject(selectedProject, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							airPanel.showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToDeleteProject());
						}

						@Override
						public void onSuccess(Void result) {
							airPanel.showWaitPart(false);
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.DeleteProjectSuccessfull());
							
							loadEvent();
							for(RScript script : scriptList){
								if(script.getIdProject() == selectedProject.getId()){
									airPanel.onScriptDelete(script);
								}
							}
						}
					});
				}
			}
		});
		dialConfirm.center();
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			AirProject selectedProject = selectionModel.getSelectedObject();
			btnEditProject.setVisible(selectedProject != null);
			btnDeleteProject.setVisible(selectedProject != null);
			btnExportProject.setVisible(selectedProject != null);
			if (selectedProject != null) {
				if (selectedProject.getIdUserCreator() == idUser) {
					btnShareProject.setVisible(true);
				}
				else {
					btnShareProject.setVisible(false);
				}
			}

			currentProject = selectedProject;
		}
	};

	public DataGrid<AirProject> getDatagrid() {
		return datagrid;
	}

	private List<RScript> getScriptsbyProject(AirProject project) {
		List<RScript> result = new ArrayList<RScript>();
		for (RScript script : scriptList) {
			if (script.getIdProject() == project.getId()) {
				result.add(script);
			}
		}
		return result;
	}

	private RScriptModel getModelbyScript(RScript script) {
		RScriptModel result = new RScriptModel();
		for (RScriptModel model : modelsList) {
			if (model.getIdScript() == script.getId()) {
				result = model;
			}
		}
		return result;
	}

	public void handleProjectClick(String id, String index) {
		//List<Integer> wraps = new ArrayList<Integer>(showingScripts);
		if (showingScripts.contains(Integer.parseInt(id))) {
			showingScripts.clear();
		}
		else {
			showingScripts.clear();
			showingScripts.add(Integer.parseInt(id));
		}
		
		
		// Redraw the modified row.
//		for(int old : wraps){
//			datagrid.redrawRow(old);
//		}
//		datagrid.redrawRow(Integer.parseInt(index));
		datagrid.redraw();

		btnAddScript.setVisible(true);
		btnDeleteScript.setVisible(false);
//		airPanel.refresh(currentProject, new RScript(), new RScriptModel(), new ArrayList<RScriptModel>());
	}

	public void handleScriptClick(String id, String index) {
		// RScript selecScript = null;
		// AirProject selectedProject = null;
		// RScriptModel selecModel = null;
		for (RScript script : scriptList) {
			if (script.getId() == Integer.parseInt(id)) {
				currentScript = script;
			}
		}
		for (AirProject project : dataProvider.getList()) {
			if (project.getId() == currentScript.getIdProject()) {
				currentProject = project;
			}
		}
		for (RScriptModel model : modelsList) {
			if (model.getIdScript() == Integer.parseInt(id)) {
				currentModel = model;
			}
		}
		airPanel.showWaitPart(true);

		SmartAirService.Connect.getInstance().getModelsbyScript(currentScript.getId(), new AsyncCallback<List<RScriptModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				airPanel.showWaitPart(false);
				airPanel.refresh(currentProject, currentScript, currentModel, new ArrayList<RScriptModel>());

				btnAddScript.setVisible(true);
				btnDeleteScript.setVisible(true);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadScriptModels());
			}

			@Override
			public void onSuccess(List<RScriptModel> result) {
				airPanel.showWaitPart(false);
				airPanel.refresh(currentProject, currentScript, currentModel, result);

				btnAddScript.setVisible(true);
				btnDeleteScript.setVisible(true);
			}
		});

		datagrid.redraw();
	}

	public List<RScript> getScriptList() {
		return scriptList;
	}

	public int getIdUser() {
		return idUser;
	}

	public AirProject getCurrentProject() {
		return currentProject;
	}

	private final native void initJs(NavigationPanel navigationPanel) /*-{
		var navigationPanel = navigationPanel;
		$wnd.clickProject = function(id, index){
			navigationPanel.@bpm.smart.web.client.panels.resources.NavigationPanel::handleProjectClick(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		$wnd.clickScript = function(id, index){
			navigationPanel.@bpm.smart.web.client.panels.resources.NavigationPanel::handleScriptClick(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		
	}-*/;

	@Override
	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(false);
		airPanel.onCollapse(false);
	}

	@Override
	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(true);
		airPanel.onCollapse(true);
	}
	
	@Override
	public Image getImgExpand() {
		return imgExpand;
	}

	@Override
	public List<Widget> getCollapseWidgets() {
		return collapseWidgets;
	}

	@Override
	protected void additionnalCollapseTreatment(boolean isCollapse) { }

	public RScript getCurrentScript() {
		return currentScript;
	}

	public void setCurrentScript(RScript currentScript) {
		this.currentScript = currentScript;
	}

	public RScriptModel getCurrentModel() {
		return currentModel;
	}

	public void setCurrentModel(RScriptModel currentModel) {
		this.currentModel = currentModel;
	}

	public void setCurrentProject(AirProject currentProject) {
		this.currentProject = currentProject;
	}
	
	
}
