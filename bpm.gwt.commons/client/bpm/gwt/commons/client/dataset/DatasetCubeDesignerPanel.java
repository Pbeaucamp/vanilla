package bpm.gwt.commons.client.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.CubeDialog;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.fmdtdriller.ColumnDraggable;
import bpm.gwt.commons.client.viewer.fmdtdriller.Fmdtlevel;
import bpm.gwt.commons.client.viewer.fmdtdriller.ObjectFmdtDimension;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.HtmlFocusPanel;
import bpm.smart.core.model.AirCube;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasetCubeDesignerPanel extends Composite implements IWait{

	private static DatasetCubeDesignerUiBinder uiBinder = GWT.create(DatasetCubeDesignerUiBinder.class);

	interface DatasetCubeDesignerUiBinder extends UiBinder<Widget, DatasetCubeDesignerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String popup();
		String frame();
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
	
	@UiField
	ListBox lstOld;
	
	@UiField
	MyStyle style;
	
	private Dataset dts;
	private List<FmdtData> columns = new ArrayList<FmdtData>();
	private List<FmdtData> allcolumns = new ArrayList<FmdtData>();
	private List<FmdtData> measures = new ArrayList<FmdtData>();
	private List<FmdtDimension> dimensions = new ArrayList<FmdtDimension>();
	private Composite selected = null;
	private List<AirCube> aircubes = new ArrayList<AirCube>();

	public DatasetCubeDesignerPanel(Dataset dts) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.dts = dts;
		this.columns = dataColumnsToFmdtDatas(dts.getMetacolumns());
		this.allcolumns = new ArrayList<FmdtData>(this.columns);
		initPanel();
		fillExistingCubes();
	}
	
	private void fillExistingCubes() {
		showWaitPart(true);
		CommonService.Connect.getInstance().getCubesbyDataset(dts.getId(), new AsyncCallback<List<AirCube>>() {
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(List<AirCube> result) {
				showWaitPart(false);
				lstOld.addItem("","0");
				for(AirCube cube : result){
					lstOld.addItem(cube.getTitle(), String.valueOf(cube.getId()));
				}
				aircubes = result;
			}
		});
	}

	private List<FmdtData> dataColumnsToFmdtDatas(List<DataColumn> cols) {
		List<FmdtData> list = new ArrayList<FmdtData>();
		for(DataColumn col : cols){
			FmdtData fmd = new FmdtData(col.getColumnLabel(), col.getColumnName(), col.getDescription());
			list.add(fmd);
		}
		return list;
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
			ColumnDraggable colDrag = new ColumnDraggable(col, DatasetCubeDesignerPanel.this);
			pColumns.add(colDrag);
		}
	}

	public void addToColumns(FmdtData level) {
		columns.add(level);
		ColumnDraggable colDrag = new ColumnDraggable(level, DatasetCubeDesignerPanel.this);
		pColumns.add(colDrag);
	}

	public void addAllToColumns(List<FmdtData> levels) {
		columns.addAll(levels);
		for (FmdtData col : levels) {
			if (col != null) {
				ColumnDraggable colDrag = new ColumnDraggable(col, DatasetCubeDesignerPanel.this);
				pColumns.add(colDrag);
			}
		}
	}

	@UiHandler("btnlaunch")
	public void onlaunchClick(ClickEvent event) {
		if(!txtTitle.getText().equals("")){
				if (panelDimensions.getWidgetCount() > 1 && pMeasures.getWidgetCount() > 0) {
					/* création csv dans R */
					showWaitPart(true);
					CommonService.Connect.getInstance().generateCSVinR(dts, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							showWaitPart(false);
							caught.printStackTrace();
							ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
							MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
							
						}

						@Override
						public void onSuccess(final String csvUrl) {
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
		
		
							final String title = txtTitle.getText() != null && !txtTitle.getText().isEmpty() ? txtTitle.getText() : "Cube";
							String desc = areaInfo.getText() != null && !areaInfo.getText().isEmpty() ? areaInfo.getText() : "";
							StringBuilder buf = new StringBuilder();
							
							buf.append("<bpm.csv.oda.query>\n");
							buf.append("	<columns>\n");
							for(FmdtDimension dim : dimensions){
								for(int i=1; i<= allcolumns.size(); i++) {
									if(allcolumns.get(i-1).getName().equals(dim.getName())){
										buf.append("		<column>" + i + "</column>\n");
										break;
									}
									
								}
							}
							for(FmdtData mes : measures){
								for(int i=1; i<= allcolumns.size(); i++) {
									if(allcolumns.get(i-1).getName().equals(mes.getName())){
										buf.append("		<column>" + i + "</column>\n");
										break;
									}
									
								}
							}
							
							buf.append("	</columns>\n");
							buf.append("</bpm.csv.oda.query>");
							String request = buf.toString();
									
							CommonService.Connect.getInstance().generateCube(dimensions, measures, csvUrl, request, title, desc, new AsyncCallback<String>() {
								@Override
								public void onFailure(Throwable caught) {
									showWaitPart(false);
									caught.printStackTrace();
									ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
									MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
								}
		
								@Override
								public void onSuccess(String result) {
					
									HashMap<String, String> parameters = new HashMap<String, String>();
									parameters.put(FmdtConstant.FMDTWEB_CUBE, "true");
									final String XmlModel = result;
		
									FmdtServices.Connect.getInstance().generateCubeUrl(LocaleInfo.getCurrentLocale().getLocaleName(), parameters, FmdtConstant.DEST_FAWEB, new AsyncCallback<String>() {
										@Override
										public void onSuccess(String result) {
											showWaitPart(false);
											showCube(result + "&viewer=true&disco=true&bpm.vanilla.groupId=1&bpm.vanilla.repositoryId=1", title, csvUrl, XmlModel);
										}
		
										@Override
										public void onFailure(Throwable caught) {
											showWaitPart(false);
											caught.printStackTrace();
											ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
											MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
										}
									});
										
								}
							});
						}
					});	
				} else
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.CubeDimMissing());
		} else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.CubeTitleMissing());
		}
	}

	private void showCube(String cubeUrl, String name, final String csvFilePath, String xmlModel) {
		CubeDialog dial = new CubeDialog(cubeUrl, name, xmlModel, dts.getId(), Integer.parseInt(lstOld.getValue(lstOld.getSelectedIndex())));
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				CommonService.Connect.getInstance().deleteFile(csvFilePath, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
		
					}
				});
				
			}
		});
		dial.center();
	}


	private DropHandler dropHandlerDimension = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();

			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			FmdtData column = getData(data);
			if (column != null) {
				pDimensions.add(new ObjectFmdtDimension(column, DatasetCubeDesignerPanel.this));
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
		if (column != null) {
			pMeasures.add(new Fmdtlevel(column, DatasetCubeDesignerPanel.this));
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
					ColumnDraggable colDrag = new ColumnDraggable(column, DatasetCubeDesignerPanel.this);
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

	@Override
	public void showWaitPart(boolean visible) {}
	
	@UiHandler("lstOld")
	public void onOldCHange(ChangeEvent event){
		int idCube = Integer.parseInt(lstOld.getValue(lstOld.getSelectedIndex()));
		if(idCube == 0){
			txtTitle.setEnabled(true);
			txtTitle.setText("");
			areaInfo.setText("");
			columns.clear();
			columns.addAll(allcolumns);
			refreshColumnPanel();
			pDimensions.clear();
			pMeasures.clear();
		} else {
			showWaitPart(true);
			String model = "";
			for(AirCube cube : aircubes){
				if(cube.getId() == idCube){
					model = cube.getXmlModel();
					break;
				}
			}
			if(model == "") return;
			CommonService.Connect.getInstance().loadCube(model, new AsyncCallback<HashMap<String,Serializable>>() {
			
				@Override
				public void onFailure(Throwable caught) {
					showWaitPart(false);
					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
				}

				@Override
				public void onSuccess(HashMap<String, Serializable> result) {
					showWaitPart(false);
					txtTitle.setEnabled(false);
					txtTitle.setText(lstOld.getItemText(lstOld.getSelectedIndex()));
					areaInfo.setText((String)result.get("desc"));
					
					columns.clear();
					columns.addAll(allcolumns);
					refreshColumnPanel();
					pDimensions.clear();
					pMeasures.clear();
					
					List<FmdtData> tpmeasures = (List<FmdtData>)result.get("measures");
					List<FmdtDimension> tpdimensions = (List<FmdtDimension>)result.get("dimensions");
					for(FmdtData dat : tpmeasures){
						Fmdtlevel elem = null;
						for(FmdtData col : allcolumns){
							if(col.getName().equals(dat.getName())){
								FmdtData column = getData(col.getId()+"");
								column.setMeasOp(dat.getMeasOp());
								if (column != null) {
									elem = new Fmdtlevel(column, DatasetCubeDesignerPanel.this);
									//elem.getOperations().setValue(elem.getAggregList().indexOf(column.getMeasOp()));
									elem.getOperations().setValue(column.getMeasOp());
									pMeasures.add(elem);
									columns.remove(column);
									refreshColumnPanel();
								}
								break;
							}
						}
					}
					for(FmdtDimension dim : tpdimensions){
						ObjectFmdtDimension elem = null;
						for(FmdtData col : allcolumns){
							if(col.getName().equals(dim.getName())){
								String data = col.getId()+"";
								FmdtData column = getData(data);
								if (column != null) {
									elem = new ObjectFmdtDimension(column, DatasetCubeDesignerPanel.this);
									pDimensions.add(elem);
									columns.remove(column);
									refreshColumnPanel();
								}
							}
						}
						int i = 0;
						for(FmdtData lev : dim.getLevels()){
							if(i==0){
								i++;
								continue;
							}
							for(FmdtData col : allcolumns){
								if(col.getName().equals(lev.getName())){
									String data = col.getId()+"";
									FmdtData column = getData(data);
									if (column != null) {
										elem.addNextLevel(elem.getLevels().get(elem.getLevels().size()-1), column);
										columns.remove(column);
										refreshColumnPanel();
									}
								}
							}
							i++;
						}
					}
				
				}
			});
		}
	}
}
