package bpm.es.web.client.panels.documents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.es.web.client.EsWeb.Layout;
import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.images.Images;
import bpm.es.web.shared.beans.Document;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DocumentsGrid extends Composite {
	
	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");

	private static DocumentsGridUiBinder uiBinder = GWT.create(DocumentsGridUiBinder.class);

	interface DocumentsGridUiBinder extends UiBinder<Widget, DocumentsGrid> {
	}
	
	interface MyStyle extends CssResource {
		String imgGrid();
		String imgPlanned();
		String pager();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelGrid;
	
	@UiField
	Image btnAddDocument;
	
	private DataGrid<Document> datagrid;
	private ListDataProvider<Document> dataProvider;
	private ListHandler<Document> sortHandler;
	
	private List<Document> persons;

	public DocumentsGrid() {
		initWidget(uiBinder.createAndBindUi(this));
		
		datagrid = buildGrid();
		panelGrid.setWidget(datagrid);
		
		loadDocuments();
	}
	
	public void setAddVisible(boolean visible) {
		btnAddDocument.setVisible(visible);
	}

	@UiHandler("btnAddDocument")
	public void onAddDocument(ClickEvent event) {
		
	}
	
	private void loadDocuments() {
//		parentPanel.showWaitPart(true);
//		
//		CommonService.Connect.getInstance().getDocuments(new GwtCallbackWrapper<List<Document>>(waitPanel, true) {
//
//			@Override
//			public void onSuccess(List<Document> result) {
//				loadDocuments(result);
//			}
//		}.getAsyncCallback());
	}

	public void loadDocuments(List<Document> result) {
		this.persons = result != null ? result : new ArrayList<Document>();
		dataProvider.setList(persons);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<Document> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<Document, String> colName = new Column<Document, String>(txtCell) {

			@Override
			public String getValue(Document object) {
				return object.getName() != null ? object.getName() : Labels.lblCnst.Unknown();
			}
		};
		colName.setSortable(true);

		Column<Document, String> colType = new Column<Document, String>(txtCell) {

			@Override
			public String getValue(Document object) {
				switch (object.getType()) {
				case BILL:
					return Labels.lblCnst.Bill();
				case ETIQUETTE:
					return Labels.lblCnst.Etiquette();
				case LETTER:
					return Labels.lblCnst.Letter();
				case PHOTO:
					return Labels.lblCnst.Photo();
				default:
					return Labels.lblCnst.Unknown();
				}
			}
		};
		colType.setSortable(true);

		Column<Document, String> colExt = new Column<Document, String>(txtCell) {

			@Override
			public String getValue(Document object) {
				switch (object.getType()) {
				case BILL:
					return "PDF";
				case ETIQUETTE:
					return "DOCX";
				case LETTER:
					return "DOCX";
				case PHOTO:
					return "PNG";
				default:
					return Labels.lblCnst.Unknown();
				}
			}
		};
		colExt.setSortable(true);

		Column<Document, String> creationDate = new Column<Document, String>(txtCell) {

			@Override
			public String getValue(Document object) {
				return object.getCreationDate() != null ? dtf.format(object.getCreationDate()) : Labels.lblCnst.Unknown();
			}
		};
		creationDate.setSortable(true);
		
		ButtonImageCell printCell = new ButtonImageCell(Images.INSTANCE.ic_print_black_27dp(), style.imgGrid());
		Column<Document, String> colPrint = new Column<Document, String>(printCell) {

			@Override
			public String getValue(Document object) {
				return "";
			}
		};
		colPrint.setFieldUpdater(new FieldUpdater<Document, String>() {

			@Override
			public void update(int index, Document object, String value) {
//				editPerson(object);
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Document> dataGrid = new DataGrid<Document>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(colName, Labels.lblCnst.Name());
		dataGrid.addColumn(colType, Labels.lblCnst.TypeOfDocument());
		dataGrid.addColumn(colExt, Labels.lblCnst.Extension());
		dataGrid.addColumn(creationDate, Labels.lblCnst.CreationDate());
		dataGrid.addColumn(colPrint);
		dataGrid.setColumnWidth(colPrint, "70px");
		dataGrid.setEmptyTableWidget(new Label("No Data"));
		
//		columns.put(surnameColumn, Labels.lblCnst.Surname());
//		columns.put(nameColumn, Labels.lblCnst.Name());
//		columns.put(mailColumn, Labels.lblCnst.Mail());
//		columns.put(creationDate, Labels.lblCnst.CreationDate());

		dataProvider = new ListDataProvider<Document>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Document>(new ArrayList<Document>());
		sortHandler.setComparator(colName, new Comparator<Document>() {

			@Override
			public int compare(Document o1, Document o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(colType, new Comparator<Document>() {

			@Override
			public int compare(Document o1, Document o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		sortHandler.setComparator(colExt, new Comparator<Document>() {

			@Override
			public int compare(Document o1, Document o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		sortHandler.setComparator(creationDate, new Comparator<Document>() {

			@Override
			public int compare(Document o1, Document o2) {
				if(o1.getCreationDate() == null) {
					return -1;
				}
				else if(o2.getCreationDate() == null) {
					return 1;
				}
				
				return o2.getCreationDate().before(o1.getCreationDate()) ? -1 : o2.getCreationDate().after(o1.getCreationDate()) ? 1 : 0;
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}

	public void editDocument(Document user) {
		
	}
	
	public void manageLayout(Layout layout) {
//		if (layout == Layout.MOBILE) {
//			if (columns != null) {
//				for (Entry<Column<Document, ?>, String> col : columns.entrySet()) {
//					if (datagrid.getColumnIndex(col.getKey()) > 0) {
//						datagrid.removeColumn(col.getKey());
//					}
//				}
//			}
//		}
//		else if (layout == Layout.TABLET || layout == Layout.COMPUTER) {
//			if (columns != null) {
//				int index = 2;
//				for (Entry<Column<Document, ?>, String> col : columns.entrySet()) {
//					if (datagrid.getColumnIndex(col.getKey()) < 0) {
//						datagrid.insertColumn(index, col.getKey(), col.getValue());
//						index++;
//					}
//				}
//			}
//		}
	}
}
