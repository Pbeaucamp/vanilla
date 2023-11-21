package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.SaveCubeDialog;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.HtmlFocusPanel;
import bpm.vanilla.platform.core.beans.fmdt.ColumnType;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CubeDesignerPanel extends Composite {

	private static CubeDesignerPanelUiBinder uiBinder = GWT.create(CubeDesignerPanelUiBinder.class);

	interface CubeDesignerPanelUiBinder extends UiBinder<Widget, CubeDesignerPanel> {
	}

	@UiField
	HTMLPanel panelColumns;

	@UiField
	HTMLPanel pColumns;

	@UiField
	HtmlFocusPanel panelDimensions, panelMeasures, pDimensions, pMeasures, dockPanel;

	@UiField
	TextBox txtTitle;

	@UiField
	TextArea areaInfo;

	@UiField
	Image btnlaunch;

	private IWait waitPanel;
	private QueryFMDTDrillerPanel drillerPanel;

	private FmdtQueryBuilder builder;
	private FmdtQueryDriller driller;

	private List<FmdtData> columns = new ArrayList<FmdtData>();
	private List<FmdtData> allcolumns = new ArrayList<FmdtData>();
	private List<FmdtData> measures = new ArrayList<FmdtData>();
	private List<FmdtDimension> dimensions = new ArrayList<FmdtDimension>();
	private Composite selected = null;

	public CubeDesignerPanel(FmdtQueryBuilder builder, FmdtQueryDriller driller, final IWait waitPanel, QueryFMDTDrillerPanel drillerPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.drillerPanel = drillerPanel;
		this.builder = initBuilder(builder);
		this.columns = new ArrayList<FmdtData>(builder.getListColumns());
		this.allcolumns = new ArrayList<FmdtData>(builder.getListColumns());
		this.driller = driller;
		initPanel();
		if(!builder.getPromptFilters().isEmpty())
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.CubePromptWarning());
	}

	private FmdtQueryBuilder initBuilder(FmdtQueryBuilder builder) {
		FmdtQueryBuilder newBuilder = new FmdtQueryBuilder();
		newBuilder.setDistinct(builder.isDistinct());
		newBuilder.setFilters(new ArrayList<FmdtFilter>(builder.getFilters()));
		newBuilder.setLimit(builder.isLimit());
		newBuilder.setName(builder.getName());
		newBuilder.setNbLimit(builder.getNbLimit());
		//newBuilder.setPromptFilters(builder.getPromptFilters());
		return newBuilder;
	}

	private void initPanel() {
		refreshColumnPanel();

		panelDimensions.addDragOverHandler(dragOverHandler);
		panelDimensions.addDragLeaveHandler(dragLeaveHandler);
		panelDimensions.addDropHandler(dropHandlerDimension);

		pDimensions.addDragOverHandler(dragOverHandler);
		pDimensions.addDragLeaveHandler(dragLeaveHandler);
		pDimensions.addDropHandler(dropHandlerDimension);

		panelMeasures.addDragOverHandler(dragOverHandler);
		panelMeasures.addDragLeaveHandler(dragLeaveHandler);
		panelMeasures.addDropHandler(dropHandlerMeasure);

		pMeasures.addDragOverHandler(dragOverHandler);
		pMeasures.addDragLeaveHandler(dragLeaveHandler);
		pMeasures.addDropHandler(dropHandlerMeasure);

		dockPanel.addDragOverHandler(dragOverHandler);
		dockPanel.addDragLeaveHandler(dragLeaveHandler);
		dockPanel.addDropHandler(dropHandlerColumn);

	}

	public void refreshColumnPanel() {
		pColumns.clear();
		for (FmdtData col : columns) {
			ColumnDraggable colDrag = new ColumnDraggable(col, CubeDesignerPanel.this);
			pColumns.add(colDrag);
		}
	}

	public void addToColumns(FmdtData level) {
		columns.add(level);
		ColumnDraggable colDrag = new ColumnDraggable(level, CubeDesignerPanel.this);
		pColumns.add(colDrag);
	}

	public void addAllToColumns(List<FmdtData> levels) {
		columns.addAll(levels);
		for (FmdtData col : levels) {
			if (col != null) {
				ColumnDraggable colDrag = new ColumnDraggable(col, CubeDesignerPanel.this);
				pColumns.add(colDrag);
			}
		}
	}

	@UiHandler("btnlaunch")
	public void onlaunchClick(ClickEvent event) {
		if (panelDimensions.getWidgetCount() > 1 && pMeasures.getWidgetCount() > 0) {
			List<FmdtData> listCols = new ArrayList<FmdtData>();

			dimensions = new ArrayList<FmdtDimension>();
			for (int i = 0; i < pDimensions.getWidgetCount(); i++) {
				if (pDimensions.getWidget(i) instanceof ObjectFmdtDimension) {
					dimensions.add(((ObjectFmdtDimension) pDimensions.getWidget(i)).getDimension());
					for (FmdtData level : ((ObjectFmdtDimension) pDimensions.getWidget(i)).getDimension().getLevels()) {
						listCols.add(level);
					}
				}
			}

			measures = new ArrayList<FmdtData>();
			for (int i = 0; i < pMeasures.getWidgetCount(); i++) {
				if (pMeasures.getWidget(i) instanceof Fmdtlevel) {
					measures.add(((Fmdtlevel) pMeasures.getWidget(i)).getLevel());
				}
			}

			builder.setListColumns(listCols);

			final String title = txtTitle.getText() != null && !txtTitle.getText().isEmpty() ? txtTitle.getText() : "Cube";
			String desc = areaInfo.getText() != null && !areaInfo.getText().isEmpty() ? areaInfo.getText() : "";

			FmdtServices.Connect.getInstance().createCube(dimensions, measures, driller, builder, title, desc, LocaleInfo.getCurrentLocale().getLocaleName(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
				}

				@Override
				public void onSuccess(String result) {
					final InformationsDialog asksave = new InformationsDialog(LabelsConstants.lblCnst.SaveCubeTitle(), LabelsConstants.lblCnst.Yes(), LabelsConstants.lblCnst.No(), LabelsConstants.lblCnst.SaveCubeMessage(), true);
					final String model = result;
					asksave.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (asksave.isConfirm()) {
								final SaveCubeDialog save = new SaveCubeDialog(waitPanel, drillerPanel.getAvailableGroups(), model, false);
								save.addCloseHandler(new CloseHandler<PopupPanel>() {
									@Override
									public void onClose(CloseEvent<PopupPanel> event) {
										if (save.getConfirm()) {

											ReportingService.Connect.getInstance().getForwardUrlForCubes(save.getItemId(), title, null, new AsyncCallback<String>() {
												public void onSuccess(String arg0) {

													showCube(arg0 + "&locale=" + LocaleInfo.getCurrentLocale().getLocaleName() + "&viewer=true&disco=true", save.getName());
												}

												public void onFailure(Throwable caught) {
													caught.printStackTrace();

													ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetUrl());
												}
											});
										}
									}
								});
								save.center();
							} else {
								HashMap<String, String> parameters = new HashMap<String, String>();
								parameters.put(FmdtConstant.FMDTWEB_CUBE, "true");

								FmdtServices.Connect.getInstance().generateCubeUrl(LocaleInfo.getCurrentLocale().getLocaleName(), parameters, FmdtConstant.DEST_FAWEB, new AsyncCallback<String>() {
									@Override
									public void onSuccess(String result) {
										showCube(result + "&viewer=true&disco=true", null);
									}

									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
										ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
										MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
									}
								});
							}
						}
					});
					asksave.center();
				}
			});
		} else
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.CubeDimMissing());
	}

	private void showCube(String cubeUrl, String name) {
		drillerPanel.getViewer().callFrame(cubeUrl, name);
	}

	public void launchFreeAnalysis() {
		if (panelDimensions.getWidgetCount() > 1 && pMeasures.getWidgetCount() > 0) {
			List<FmdtData> listCols = new ArrayList<FmdtData>();

			dimensions = new ArrayList<FmdtDimension>();
			for (int i = 0; i < pDimensions.getWidgetCount(); i++) {
				if (pDimensions.getWidget(i) instanceof ObjectFmdtDimension) {
					dimensions.add(((ObjectFmdtDimension) pDimensions.getWidget(i)).getDimension());
					for (FmdtData level : ((ObjectFmdtDimension) pDimensions.getWidget(i)).getDimension().getLevels()) {
						listCols.add(level);
					}
				}
			}

			measures = new ArrayList<FmdtData>();
			for (int i = 0; i < pMeasures.getWidgetCount(); i++) {
				if (pMeasures.getWidget(i) instanceof Fmdtlevel) {
					measures.add(((Fmdtlevel) pMeasures.getWidget(i)).getLevel());
				}
			}

			builder.setListColumns(listCols);

			final String title = txtTitle.getText() != null && !txtTitle.getText().isEmpty() ? txtTitle.getText() : "Cube";
			String desc = areaInfo.getText() != null && !areaInfo.getText().isEmpty() ? areaInfo.getText() : "";

			FmdtServices.Connect.getInstance().createCube(dimensions, measures, driller, builder, title, desc, LocaleInfo.getCurrentLocale().getLocaleName(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
				}

				@Override
				public void onSuccess(String result) {
					final InformationsDialog asksave = new InformationsDialog(LabelsConstants.lblCnst.SaveCubeTitle(), LabelsConstants.lblCnst.Yes(), LabelsConstants.lblCnst.No(), LabelsConstants.lblCnst.SaveCubeMessage(), true);
					final String model = result;
					asksave.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (asksave.isConfirm()) {
								final SaveCubeDialog save = new SaveCubeDialog(waitPanel, drillerPanel.getAvailableGroups(), model, false);
								save.addCloseHandler(new CloseHandler<PopupPanel>() {
									@Override
									public void onClose(CloseEvent<PopupPanel> event) {
										if (save.getConfirm()) {
											HashMap<String, String> parameters = new HashMap<String, String>();
											parameters.put(FmdtConstant.FASD_KEY, String.valueOf(save.getItemId()));
											parameters.put(FmdtConstant.CUBENAME_KEY, title);

											FmdtServices.Connect.getInstance().generateCubeUrl(LocaleInfo.getCurrentLocale().getLocaleName(), parameters, FmdtConstant.DEST_FAWEB, new AsyncCallback<String>() {
												@Override
												public void onSuccess(String result) {
													ToolsGWT.doRedirect(result);
												}

												@Override
												public void onFailure(Throwable caught) {
													caught.printStackTrace();
													ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
													MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
												}
											});
										}
									}
								});
								save.center();
							} else {
								HashMap<String, String> parameters = new HashMap<String, String>();
								parameters.put(FmdtConstant.FMDTWEB_CUBE, "true");

								FmdtServices.Connect.getInstance().generateCubeUrl(LocaleInfo.getCurrentLocale().getLocaleName(), parameters, FmdtConstant.DEST_FAWEB, new AsyncCallback<String>() {
									@Override
									public void onSuccess(String result) {
										ToolsGWT.doRedirect(result);
									}

									@Override
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
										ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
										MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
									}
								});
							}
						}
					});
					asksave.center();
				}
			});
		} else
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.CubeDimMissing());
	}

	private DropHandler dropHandlerDimension = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (column != null && column instanceof FmdtColumn && ((FmdtColumn) column).getType() == ColumnType.DIMENSION) {
				pDimensions.add(new ObjectFmdtDimension(column, CubeDesignerPanel.this));
				removeFromparent();
			}
		}
	};

	private DropHandler dropHandlerMeasure = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			addMeasures(data);
		}
	};

	public void addMeasures(String data) {
		FmdtData column = getData(data);
		if (column != null && column instanceof FmdtColumn && ((FmdtColumn) column).getType() == ColumnType.MEASURE) {
			pMeasures.add(new Fmdtlevel(column, CubeDesignerPanel.this));
			removeFromparent();
		}

	}

	private DropHandler dropHandlerColumn = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (column != null) {
				if (!columns.contains(column)) {
					columns.add(column);
					ColumnDraggable colDrag = new ColumnDraggable(column, CubeDesignerPanel.this);
					pColumns.add(colDrag);
					removeFromparent();
				}
			}
		}
	};

	public FmdtData getData(String data) {
		FmdtData column = null;
		if (data != null && !data.isEmpty()) {
			Integer id = Integer.parseInt(data);
			for (FmdtData col : allcolumns) {
				if (col.getId() == id) {
					column = col;
					break;
				}
			}
		}
		return column;
	}

	/*
	 * public void removeFromColumn(FmdtData data) { if (columns.contains(data))
	 * { columns.remove(data); refreshColumnPanel(); } }
	 */
	public void removeFromparent() {
		if (selected != null) {
			if (selected instanceof ColumnDraggable) {
				columns.remove(((ColumnDraggable) selected).getColumn());
				selected.removeFromParent();
			} else {
				if (selected instanceof Fmdtlevel) {
					((Fmdtlevel) selected).removeLevel();
				}
			}
			selected = null;
		}
	}

	private DragOverHandler dragOverHandler = new DragOverHandler() {

		@Override
		public void onDragOver(DragOverEvent event) {
		}
	};

	private DragLeaveHandler dragLeaveHandler = new DragLeaveHandler() {

		@Override
		public void onDragLeave(DragLeaveEvent event) {
		}
	};

	public List<FmdtData> getMeasures() {
		return measures;
	}

	public Composite getSelected() {
		return selected;
	}

	public void setSelected(Composite selected) {
		this.selected = selected;
	}

}
