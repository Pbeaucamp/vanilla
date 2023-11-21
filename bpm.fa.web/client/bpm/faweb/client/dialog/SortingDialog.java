package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.DynamicSelectionCell;
import bpm.faweb.shared.SortElement;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class SortingDialog extends AbstractDialogBox {

	private static SortingDialogUiBinder uiBinder = GWT.create(SortingDialogUiBinder.class);

	interface SortingDialogUiBinder extends UiBinder<Widget, SortingDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel, sortPanel;
	
	@UiField
	Image imgAdd, imgDel, imgUp, imgDown;
	
	private DataGrid<SortElement> datagrid;
	private ListDataProvider<SortElement> dataprovider;
	private SingleSelectionModel<SortElement> selectionModel;
	
	List<SortElement> elements;
	
	private List<String> levels;
	
	private boolean confirm = false;

	private DynamicSelectionCell elementCell;
	
	public SortingDialog() {
		super(FreeAnalysisWeb.LBL.sorting(), false, false);
		
		elements = new ArrayList<SortElement>(MainPanel.getInstance().getInfosReport().getSortElements());
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		showWaitPart(true);
		
		FaWebService.Connect.getInstance().getLevels(MainPanel.getInstance().getKeySession(), new AsyncCallback<LinkedHashMap<String, LinkedHashMap<String, String>>>() {
			public void onSuccess(LinkedHashMap<String, LinkedHashMap<String, String>> result) {
				showWaitPart(false);
				
				List<String> elements = new ArrayList<String>();
				
				for(String dim : result.keySet()) {
					elements.addAll(result.get(dim).keySet());
				}
				for(ItemMes measure : MainPanel.getInstance().getInfosReport().getMeasures()) {
					elements.add(measure.getUname());
				}
				
				levels = elements;
				
				createSortGrid(elements);
				
				setList();
			}

			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();
			}
		});
	}

	private void createSortGrid(List<String> elements) {
		datagrid = new DataGrid<SortElement>();
		datagrid.setSize("100%", "100%");
		
		dataprovider = new ListDataProvider<SortElement>();
		dataprovider.addDataDisplay(datagrid);
		
		selectionModel = new SingleSelectionModel<SortElement>();
		datagrid.setSelectionModel(selectionModel);
		
		SelectionCell elementCell = new SelectionCell(elements);
//		elementCell = new DynamicSelectionCell(this);
		
		Column<SortElement, String> colElement = new Column<SortElement, String>(elementCell) {
			@Override
			public String getValue(SortElement object) {
				return object.getUname();
			}
		};
		colElement.setFieldUpdater(new FieldUpdater<SortElement, String>() {
			
			@Override
			public void update(int index, SortElement object, String value) {
				object.setUname(value);
			}
		});
		
		SelectionCell sortCell = new SelectionCell(SortElement.TYPES);
		
		Column<SortElement, String> colSort = new Column<SortElement, String>(sortCell) {
			@Override
			public String getValue(SortElement object) {
				return object.getType();
			}
		};
		colSort.setFieldUpdater(new FieldUpdater<SortElement, String>() {
			
			@Override
			public void update(int index, SortElement object, String value) {
				object.setType(value);
			}
		});
		
		datagrid.addColumn(colElement, FreeAnalysisWeb.LBL.element());
		datagrid.addColumn(colSort, FreeAnalysisWeb.LBL.sortType());
		
		sortPanel.add(datagrid);
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			MainPanel.getInstance().getInfosReport().setSortElements(new ArrayList<SortElement>( dataprovider.getList()));
			SortingDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			SortingDialog.this.hide();
		}
	};
	
	@UiHandler("imgAdd")
	public void onAdd(ClickEvent event) {
		SortElement elem = new SortElement();
		elem.setUname(levels.get(0));
		elem.setType(SortElement.TYPE_ASC);
		elements.add(elem);
		setList();
	}
	
	@UiHandler("imgDel")
	public void onDel(ClickEvent event) {
		SortElement elem = selectionModel.getSelectedObject();
		elements.remove(elem);
		setList();
	}
	
	@UiHandler("imgUp")
	public void onUp(ClickEvent event) {
		SortElement elem = selectionModel.getSelectedObject();
		int index = elements.indexOf(elem);
		if(index > 0) {
			elements.remove(index);
			elements.add(index - 1, elem);
		}
		setList();
	}
	
	@UiHandler("imgDown")
	public void onDown(ClickEvent event) {
		SortElement elem = selectionModel.getSelectedObject();
		int index = elements.indexOf(elem);
		if(index < elements.size() - 1) {
			elements.remove(index);
			elements.add(index+1, elem);
		}
		setList();
	}
	
	public boolean isConfirm() {
		return confirm;
	}
	
	private void setList() {
//		handleOptions();

		dataprovider.setList(elements);
	}

	public void handleOptions() {
		elementCell.clearOptions();
		
		List<String> lvls = new ArrayList<String>(levels);
		for(int i = 0 ; i < elements.size() ; i++) {
			if(i > 0) {
				lvls.remove(elements.get(i - 1).getUname());
			}
			elementCell.addOption(lvls, i);
			elementCell.setViewData(elements.get(i), elements.get(i).getUname());
		}
	}
}
