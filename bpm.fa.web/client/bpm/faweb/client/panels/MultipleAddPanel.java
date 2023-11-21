package bpm.faweb.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.MultipleAddDropController;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MultipleAddPanel extends VerticalPanel {

	private MainPanel mainPanel;

	private HorizontalPanel rowLstBoxPanel = new HorizontalPanel();
	private HorizontalPanel colLstBoxPanel = new HorizontalPanel();
	private HorizontalPanel filterLstBoxPanel = new HorizontalPanel();

	private HorizontalPanel rowBtnPanel = new HorizontalPanel();
	private HorizontalPanel colBtnPanel = new HorizontalPanel();
	private HorizontalPanel filterBtnPanel = new HorizontalPanel();

	private VerticalPanel rowLblBtnPanel = new VerticalPanel();
	private VerticalPanel colLblBtnPanel = new VerticalPanel();
	private VerticalPanel filterLblBtnPanel = new VerticalPanel();

	private Button btnAddMultipleCol;
	private Button btnAddMultipleRow;
	private Button btnDelMultipleCol;
	private Button btnDelMultipleRow;
	private Button btnAddFilter;
	private Button btnDelFilter;

	private List<String> rows = new ArrayList<String>();
	private List<String> cols = new ArrayList<String>();

	private ListBox lstRowMultipleAdd = new ListBox(true);
	private ListBox lstColMultipleAdd = new ListBox(true);
	private ListBox lstFiltersMultipleAdd = new ListBox(true);

	public MultipleAddPanel(MainPanel mainPanel) {
		super();
		this.mainPanel = mainPanel;

		this.getElement().getStyle().setMargin(5, Unit.PX);

		// ---------Fill the listbox with the actual grid datas----------
		refresh();
		fillLstBox();

		// ---------Create the add and del buttons with their actions-----
		btnAddMultipleCol = new Button("+");
		btnAddMultipleRow = new Button("+");
		btnDelMultipleCol = new Button("-");
		btnDelMultipleRow = new Button("-");
		btnAddFilter = new Button("+");
		btnDelFilter = new Button("-");

		btnAddMultipleRow.setPixelSize(30, 30);
		btnDelMultipleRow.setPixelSize(30, 30);
		btnAddMultipleCol.setPixelSize(30, 30);
		btnDelMultipleCol.setPixelSize(30, 30);
		btnAddFilter.setPixelSize(30, 30);
		btnDelFilter.setPixelSize(30, 30);

		addButtonsClick();

		// --------Create the row panel-----
		Label lblRow = new Label(FreeAnalysisWeb.LBL.Rows());
		lblRow.addStyleName("multAddLbl");

		lstRowMultipleAdd.setSize("400px", "140px");

		rowBtnPanel.add(btnAddMultipleRow);
		rowBtnPanel.add(btnDelMultipleRow);

		rowLstBoxPanel.add(rowLblBtnPanel);
		rowLstBoxPanel.add(lstRowMultipleAdd);

		mainPanel.getDragController().registerDropController(new MultipleAddDropController(mainPanel, lstRowMultipleAdd));

		rowBtnPanel.setCellVerticalAlignment(btnAddMultipleRow, ALIGN_MIDDLE);
		rowBtnPanel.setCellVerticalAlignment(btnDelMultipleRow, ALIGN_MIDDLE);
		rowBtnPanel.setCellHorizontalAlignment(btnAddMultipleRow, ALIGN_CENTER);
		rowBtnPanel.setCellHorizontalAlignment(btnDelMultipleRow, ALIGN_CENTER);
		rowBtnPanel.setSize("100%", "100%");
		rowBtnPanel.addStyleName("multPanelSideBorder");

		rowLblBtnPanel.add(lblRow);
		rowLblBtnPanel.add(rowBtnPanel);
		rowLblBtnPanel.setSize("150px", "100%");
		rowLblBtnPanel.setCellHorizontalAlignment(lblRow, ALIGN_CENTER);
		rowLblBtnPanel.setCellVerticalAlignment(lblRow, ALIGN_MIDDLE);

		rowLstBoxPanel.setHeight("150px");
		rowLstBoxPanel.addStyleName("chartDialogVp");
		rowLstBoxPanel.setCellVerticalAlignment(lblRow, ALIGN_MIDDLE);

		// --------Create the col panel-----
		Label lblCol = new Label(FreeAnalysisWeb.LBL.Cols());
		lblCol.addStyleName("multAddLbl");

		lstColMultipleAdd.setSize("400px", "140px");

		colBtnPanel.add(btnAddMultipleCol);
		colBtnPanel.add(btnDelMultipleCol);

		colLstBoxPanel.add(colLblBtnPanel);
		colLstBoxPanel.add(lstColMultipleAdd);

		mainPanel.getDragController().registerDropController(new MultipleAddDropController(mainPanel, lstColMultipleAdd));

		colBtnPanel.setCellVerticalAlignment(btnAddMultipleCol, ALIGN_MIDDLE);
		colBtnPanel.setCellVerticalAlignment(btnDelMultipleCol, ALIGN_MIDDLE);
		colBtnPanel.setCellHorizontalAlignment(btnAddMultipleCol, ALIGN_CENTER);
		colBtnPanel.setCellHorizontalAlignment(btnDelMultipleCol, ALIGN_CENTER);
		colBtnPanel.setSize("100%", "100%");
		colBtnPanel.addStyleName("multPanelSideBorder");

		colLblBtnPanel.add(lblCol);
		colLblBtnPanel.add(colBtnPanel);
		colLblBtnPanel.setSize("150px", "100%");
		colLblBtnPanel.setCellHorizontalAlignment(lblCol, ALIGN_CENTER);
		colLblBtnPanel.setCellVerticalAlignment(lblCol, ALIGN_MIDDLE);

		colLstBoxPanel.setHeight("150px");
		colLstBoxPanel.addStyleName("chartDialogVp");
		colLstBoxPanel.setCellVerticalAlignment(lblCol, ALIGN_MIDDLE);

		// --------Create the filter panel--
		Label lblFilter = new Label(FreeAnalysisWeb.LBL.Where());
		lblFilter.addStyleName("multAddLbl");

		lstFiltersMultipleAdd.setSize("400px", "140px");

		filterBtnPanel.add(btnAddFilter);
		filterBtnPanel.add(btnDelFilter);

		filterLstBoxPanel.add(filterLblBtnPanel);
		filterLstBoxPanel.add(lstFiltersMultipleAdd);

		mainPanel.getDragController().registerDropController(new MultipleAddDropController(mainPanel, lstFiltersMultipleAdd));

		filterBtnPanel.setCellVerticalAlignment(btnAddFilter, ALIGN_MIDDLE);
		filterBtnPanel.setCellVerticalAlignment(btnDelFilter, ALIGN_MIDDLE);
		filterBtnPanel.setCellHorizontalAlignment(btnAddFilter, ALIGN_CENTER);
		filterBtnPanel.setCellHorizontalAlignment(btnDelFilter, ALIGN_CENTER);
		filterBtnPanel.setSize("100%", "100%");
		filterBtnPanel.addStyleName("multPanelSideBorder");

		filterLblBtnPanel.add(lblFilter);
		filterLblBtnPanel.add(filterBtnPanel);
		filterLblBtnPanel.setSize("150px", "100%");
		filterLblBtnPanel.setCellHorizontalAlignment(lblFilter, ALIGN_CENTER);
		filterLblBtnPanel.setCellVerticalAlignment(lblFilter, ALIGN_MIDDLE);

		filterLstBoxPanel.setHeight("150px");
		filterLstBoxPanel.addStyleName("chartDialogVp");
		filterLstBoxPanel.setCellVerticalAlignment(lblFilter, ALIGN_MIDDLE);

		// ----Add all panels to the page---
		this.add(rowLstBoxPanel);
		this.add(new Space("1px", "10px"));
		this.add(colLstBoxPanel);
		this.add(new Space("1px", "10px"));
		this.add(filterLstBoxPanel);
	}

	private void addButtonsClick() {
		btnAddMultipleRow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<String> items = mainPanel.getNavigationPanel().getItemSelected();
				for (String s : items) {
					if (lstRowMultipleAdd.getItemCount() != 0) {
						boolean exists = false;
						for (int i = 0; i < lstRowMultipleAdd.getItemCount(); i++) {
							if (lstRowMultipleAdd.getValue(i).equalsIgnoreCase(s)) {
								exists = true;
							}
						}

						if (exists) {
							MessageHelper.openMessageDialog("Error", s + " : " + FreeAnalysisWeb.LBL.AlreadyExist());
						}
						else {
							lstRowMultipleAdd.addItem(s, s);
						}
					}
					else {
						lstRowMultipleAdd.addItem(s, s);
					}
				}
				mainPanel.getNavigationPanel().clearItemSelected();
				mainPanel.deselect();
				
				refreshTooltip();
			}
		});

		btnAddMultipleCol.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<String> items = mainPanel.getNavigationPanel().getItemSelected();
				for (String s : items) {
					if (lstColMultipleAdd.getItemCount() != 0) {
						boolean exists = false;
						for (int i = 0; i < lstColMultipleAdd.getItemCount(); i++) {
							if (lstColMultipleAdd.getValue(i).equalsIgnoreCase(s)) {
								exists = true;
							}
						}

						if (exists) {
							MessageHelper.openMessageDialog("Error", s + " : " + FreeAnalysisWeb.LBL.AlreadyExist());
						}
						else {
							lstColMultipleAdd.addItem(s, s);
						}
					}
					else {
						lstColMultipleAdd.addItem(s, s);
					}
				}
				mainPanel.getNavigationPanel().clearItemSelected();
				mainPanel.deselect();
				
				refreshTooltip();
			}
		});

		btnDelMultipleRow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<Integer> itemsSelected = new ArrayList<Integer>();
				for (int i = 0; i < lstRowMultipleAdd.getItemCount(); i++) {
					if (lstRowMultipleAdd.isItemSelected(i)) {
						itemsSelected.add(i - itemsSelected.size());
					}
				}
				for (int i : itemsSelected) {
					lstRowMultipleAdd.removeItem(i);
				}
				
				refreshTooltip();
			}
		});

		btnDelMultipleCol.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<Integer> itemsSelected = new ArrayList<Integer>();
				for (int i = 0; i < lstColMultipleAdd.getItemCount(); i++) {
					if (lstColMultipleAdd.isItemSelected(i)) {
						itemsSelected.add(i - itemsSelected.size());
					}
				}
				for (int i : itemsSelected) {
					lstColMultipleAdd.removeItem(i);
				}
				
				refreshTooltip();
			}
		});

		btnAddFilter.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<String> items = mainPanel.getNavigationPanel().getItemSelected();
				for (String s : items) {
					if (lstFiltersMultipleAdd.getItemCount() != 0) {
						boolean exists = false;
						for (int i = 0; i < lstFiltersMultipleAdd.getItemCount(); i++) {
							if (lstFiltersMultipleAdd.getValue(i).equalsIgnoreCase(s)) {
								exists = true;
							}
						}

						if (exists) {
							MessageHelper.openMessageDialog("Error", s + " : " + FreeAnalysisWeb.LBL.AlreadyExist());
						}
						else {
							lstFiltersMultipleAdd.addItem(s, s);
							mainPanel.getDisplayPanel().getCubeViewerTab().addFilterItem(s);
						}
					}
					else {
						lstFiltersMultipleAdd.addItem(s, s);
						mainPanel.getDisplayPanel().getCubeViewerTab().addFilterItem(s);
					}
				}
				mainPanel.getNavigationPanel().clearItemSelected();
				mainPanel.deselect();
				
				refreshTooltip();
			}
		});

		btnDelFilter.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<Integer> itemsSelected = new ArrayList<Integer>();
				for (int i = 0; i < lstFiltersMultipleAdd.getItemCount(); i++) {
					if (lstFiltersMultipleAdd.isItemSelected(i)) {
						itemsSelected.add(i - itemsSelected.size());
					}
				}

				for (int i : itemsSelected) {
					lstFiltersMultipleAdd.removeItem(i);
					mainPanel.getDisplayPanel().getCubeViewerTab().removeFilter(i);
				}
				
				refreshTooltip();
			}
		});
	}
	
	private void refreshTooltip() {
		SelectElement selectElement = SelectElement.as(lstRowMultipleAdd.getElement());
		NodeList<OptionElement> options = selectElement.getOptions();
	    for (int i = 0; i < options.getLength(); i++) {
	        options.getItem(i).setTitle(lstRowMultipleAdd.getItemText(i));
	    }

		
		selectElement = SelectElement.as(lstColMultipleAdd.getElement());
		options = selectElement.getOptions();
	    for (int i = 0; i < options.getLength(); i++) {
	        options.getItem(i).setTitle(lstColMultipleAdd.getItemText(i));
	    }

		
		selectElement = SelectElement.as(lstFiltersMultipleAdd.getElement());
		options = selectElement.getOptions();
	    for (int i = 0; i < options.getLength(); i++) {
	        options.getItem(i).setTitle(lstFiltersMultipleAdd.getItemText(i));
	    }
	}

	public void refresh() {
		lstColMultipleAdd.clear();
		lstFiltersMultipleAdd.clear();
		lstRowMultipleAdd.clear();
	}

	public void fillLstBox() {
		for (int i = 0; i < mainPanel.getInfosReport().getGrid().getNbOfRow(); i++) {
			if (i < mainPanel.getInfosReport().getIFirst()) {
				for (int j = mainPanel.getInfosReport().getJFirst(); j < mainPanel.getInfosReport().getGrid().getNbOfCol(); j++) {
					if ((mainPanel.getInfosReport().getGrid().getIJ(i, j)).getType().equalsIgnoreCase("ItemElement") && !(mainPanel.getInfosReport().getGrid().getIJ(i, j)).getUname().equalsIgnoreCase("")) {
						String item = (mainPanel.getInfosReport().getGrid().getIJ(i, j)).getUname();
						if (!cols.contains(item)) {
							lstColMultipleAdd.addItem(item, item);
							cols.add(item);
						}
					}
				}
			}
			else {
				for (int j = 0; j < mainPanel.getInfosReport().getJFirst(); j++) {
					if ((mainPanel.getInfosReport().getGrid().getIJ(i, j)).getType().equalsIgnoreCase("ItemElement") && !(mainPanel.getInfosReport().getGrid().getIJ(i, j)).getUname().equalsIgnoreCase("")) {
						String item = (mainPanel.getInfosReport().getGrid().getIJ(i, j)).getUname();
						if (!rows.contains(item)) {
							lstRowMultipleAdd.addItem(item, item);
							rows.add(item);
						}
					}
				}
			}
		}

		for (FaWebFilterHTML filterHtml : mainPanel.getDisplayPanel().getCubeViewerTab().getFilters()) {
			String filter = filterHtml.getFilter();
			lstFiltersMultipleAdd.addItem(filter);
		}
		
		refreshTooltip();
	}

	public void apply() {
		mainPanel.showWaitPart(true);

		List<String> rows = new ArrayList<String>();
		List<String> cols = new ArrayList<String>();
		List<String> filters = new ArrayList<String>();

		for (int i = 0; i < lstRowMultipleAdd.getItemCount(); i++) {
			rows.add(lstRowMultipleAdd.getValue(i));
		}

		for (int i = 0; i < lstColMultipleAdd.getItemCount(); i++) {
			cols.add(lstColMultipleAdd.getValue(i));
		}

		for (int i = 0; i < lstFiltersMultipleAdd.getItemCount(); i++) {
			filters.add(lstFiltersMultipleAdd.getValue(i));
		}

		FaWebService.Connect.getInstance().addMultiple(mainPanel.getKeySession(), rows, cols, filters, new AsyncCallback<InfosReport>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				mainPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(InfosReport result) {
				if (result == null) {
					MessageHelper.openMessageDialog("Error", FreeAnalysisWeb.LBL.ErrorMultipleAdd());
				}
				else {
					mainPanel.setGridFromRCP(result);
					mainPanel.getDisplayPanel().selectCubeViewer();
				}

				mainPanel.showWaitPart(false);
			}
		});
	}

}
