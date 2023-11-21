package bpm.vanilla.portal.client.dialog.properties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.services.ActionsService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.shared.repository.LinkedDocumentDTO;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class LinkedDocumentView extends Composite {

	private static LinkedDocumentViewUiBinder uiBinder = GWT.create(LinkedDocumentViewUiBinder.class);

	interface LinkedDocumentViewUiBinder extends UiBinder<Widget, LinkedDocumentView> {
	}

	interface MyStyle extends CssResource {
		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelPager, panelContent;
	
	private PropertyDialog dial;
	private ContentDisplayPanel parent;

	private ListDataProvider<LinkedDocumentDTO> dataProvider;
	private ListHandler<LinkedDocumentDTO> sortHandler;

	private List<LinkedDocumentDTO> documents;
	private PortailRepositoryItem item;

	public LinkedDocumentView(PropertyDialog dial, ContentDisplayPanel parent, PortailRepositoryItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.dial = dial;
		this.parent = parent;
		this.item = item;

		panelContent.setWidget(createGridData());
		
		fillDocuments(item);
	}

	public void refreshResult() {
		if (documents != null) {
			this.dataProvider.setList(documents);
			sortHandler.setList(dataProvider.getList());
		}
	}

	private void loadComment(List<LinkedDocumentDTO> documents) {
		this.dataProvider.setList(documents);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<LinkedDocumentDTO> createGridData() {
		CustomCell cell = new CustomCell();
		Column<LinkedDocumentDTO, String> nameColumn = new Column<LinkedDocumentDTO, String>(cell) {

			@Override
			public String getValue(LinkedDocumentDTO object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		Column<LinkedDocumentDTO, String> formatColumn = new Column<LinkedDocumentDTO, String>(cell) {

			@Override
			public String getValue(LinkedDocumentDTO object) {
				return object.getFormat();
			}

		};
		formatColumn.setSortable(true);

		Column<LinkedDocumentDTO, String> commentColumn = new Column<LinkedDocumentDTO, String>(cell) {

			@Override
			public String getValue(LinkedDocumentDTO object) {
				return object.getComment();
			}
		};
		commentColumn.setSortable(true);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<LinkedDocumentDTO> dataGrid = new DataGrid<LinkedDocumentDTO>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.Name());
		dataGrid.addColumn(formatColumn, ToolsGWT.lblCnst.Format());
		dataGrid.addColumn(commentColumn, ToolsGWT.lblCnst.Comment());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoData()));

		dataProvider = new ListDataProvider<LinkedDocumentDTO>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<LinkedDocumentDTO>(new ArrayList<LinkedDocumentDTO>());
		sortHandler.setComparator(nameColumn, new Comparator<LinkedDocumentDTO>() {

			@Override
			public int compare(LinkedDocumentDTO o1, LinkedDocumentDTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(formatColumn, new Comparator<LinkedDocumentDTO>() {

			@Override
			public int compare(LinkedDocumentDTO o1, LinkedDocumentDTO o2) {
				return o1.getFormat().compareTo(o2.getFormat());
			}
		});
		sortHandler.setComparator(commentColumn, new Comparator<LinkedDocumentDTO>() {

			@Override
			public int compare(LinkedDocumentDTO o1, LinkedDocumentDTO o2) {
				return o1.getComment().compareTo(o2.getComment());
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	public void fillDocuments(PortailRepositoryItem dto) {

		ActionsService.Connect.getInstance().getLinkedDocuments(dto.getId(), new AsyncCallback<List<LinkedDocumentDTO>>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
			}

			public void onSuccess(List<LinkedDocumentDTO> result) {
				documents = result;

				if (result != null) {
					loadComment(result);
				}
				else {
					loadComment(new ArrayList<LinkedDocumentDTO>());
				}
			}
		});
	}

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			LinkedDocumentDTO linkedDoc = (LinkedDocumentDTO) context.getKey();
			if (event.getType().equals("dblclick")) {
				biPortal.get().showWaitPart(true);

				ActionsService.Connect.getInstance().getLinkedDocumentUrl(item.getId(), linkedDoc.getId(), new AsyncCallback<DisplayItem>() {

					@Override
					public void onSuccess(DisplayItem item) {
						biPortal.get().showWaitPart(false);

						LinkedDocumentView.this.parent.openViewer(item);
						
						dial.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						biPortal.get().showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
					}
				});
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

}
