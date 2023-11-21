package bpm.gwt.aklabox.commons.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class FilterDataGrid<T> extends Composite {

	private static FilterDataGridUiBinder uiBinder = GWT.create(FilterDataGridUiBinder.class);

	interface FilterDataGridUiBinder extends UiBinder<Widget, FilterDataGrid> {
	}
	
	interface MyStyle extends CssResource {
		String header();

	}

	@UiField MyStyle style;
	@UiField
	SimplePanel panelGrid, pager;
	@UiField
	LabelTextBox txtFilter;

	private DataGrid<T> grid;
	private List<T> data;
	
	private ListDataProvider<T> provider;

	public FilterDataGrid(final DataGrid<T> grid, final ListDataProvider<T> provider) {
		initWidget(uiBinder.createAndBindUi(this));

		this.grid = grid;
		this.provider = provider;
		this.data = new ArrayList<>(provider.getList());
		panelGrid.add(grid);
		pager.setVisible(false);
		
		grid.addStyleName(style.header());
		
//		provider = new ListDataProvider<>(data);
//		provider.addDataDisplay(grid);
		
		
		txtFilter.setPlaceHolder(LabelsConstants.lblCnst.Filter());
		txtFilter.setLabel("");
		txtFilter.setTitle(LabelsConstants.lblCnst.Filter());
		
		txtFilter.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String filter = txtFilter.getText().toLowerCase();
				List<T> filtered = new ArrayList();
				for(T row : data){
					boolean display = false;
					for(int i=0; i<grid.getColumnCount(); i++){
						
						if(grid.getColumn(i).getCell() instanceof TextCell && ((String)grid.getColumn(i).getValue(row)).toLowerCase().contains(filter)){
							display = true;
							break;
						}
					}
					if(display) filtered.add(row);
					
				}

				provider.setList(filtered);
				grid.redraw();
			}
		});
	}
	
	public FilterDataGrid(DataGrid<T> grid, ListDataProvider<T> provider, int pagination) {
		this(grid, provider);
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		// pager.addStyleName(style.pager());
		pager.setDisplay(grid);

		this.pager.setWidget(pager);
		this.pager.setVisible(true);
		grid.setPageSize(pagination);
	}

	
}