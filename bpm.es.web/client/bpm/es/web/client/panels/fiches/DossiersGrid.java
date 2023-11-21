package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.es.web.client.EsWeb.Layout;
import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.dialogs.DossierHistoriqueDialog;
import bpm.es.web.client.images.Images;
import bpm.es.web.client.panels.DossierPanel;
import bpm.es.web.client.utils.ActivationImageCell;
import bpm.es.web.shared.beans.Person;
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
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DossiersGrid extends Composite {
	
	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");

	private static DossiersGridUiBinder uiBinder = GWT.create(DossiersGridUiBinder.class);

	interface DossiersGridUiBinder extends UiBinder<Widget, DossiersGrid> {
	}
	
	interface MyStyle extends CssResource {
		String imgGrid();
		String imgPlanned();
		String pager();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelGrid, panelPager;
	
	private DossierPanel parentPanel;
	
	private DataGrid<Person> datagrid;
	private ListDataProvider<Person> dataProvider;
	private ListHandler<Person> sortHandler;
	
	private List<Person> persons;

	public DossiersGrid(DossierPanel parentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentPanel = parentPanel;
		
		datagrid = buildGrid();
		panelGrid.setWidget(datagrid);
		
		loadPersons();
	}

	@UiHandler("btnAddRecord")
	public void onAddPerson(ClickEvent event) {
		parentPanel.displayFicheCreation();
	}
	
	private void loadPersons() {
//		parentPanel.showWaitPart(true);
//		
//		CommonService.Connect.getInstance().getPersons(new GwtCallbackWrapper<List<Person>>(waitPanel, true) {
//
//			@Override
//			public void onSuccess(List<Person> result) {
//				loadPersons(result);
//			}
//		}.getAsyncCallback());
	}

	public void loadPersons(List<Person> result) {
		this.persons = result != null ? result : new ArrayList<Person>();
		dataProvider.setList(persons);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<Person> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<Person, String> colRecordId = new Column<Person, String>(txtCell) {

			@Override
			public String getValue(Person object) {
				return object.getDossier() != null ? object.getDossier().getId() : Labels.lblCnst.Unknown();
			}
		};
		colRecordId.setSortable(true);

		Column<Person, String> colLastName = new Column<Person, String>(txtCell) {

			@Override
			public String getValue(Person object) {
				return object.getLastName() != null ? object.getLastName() : Labels.lblCnst.Unknown();
			}
		};
		colLastName.setSortable(true);

		Column<Person, String> colFirstName = new Column<Person, String>(txtCell) {

			@Override
			public String getValue(Person object) {
				return object.getFirstName() != null ? object.getFirstName() : Labels.lblCnst.Unknown();
			}
		};
		colFirstName.setSortable(true);

		Column<Person, String> colCreationDate = new Column<Person, String>(txtCell) {

			@Override
			public String getValue(Person object) {
				return object.getDossier() != null && object.getDossier().getCreationDate() != null ? dtf.format(object.getDossier().getCreationDate()) : Labels.lblCnst.Unknown();
			}
		};
		colCreationDate.setSortable(true);
		
		ActivationImageCell planificationCell = new ActivationImageCell(style.imgPlanned());
		Column<Person, String> colActivate = new Column<Person, String>(planificationCell) {

			@Override
			public String getValue(Person object) {
//				if (object.isSuperPerson()) {
//					return ActivationImageCell.DEACTIVATE;
//				}
//				else {
					return ActivationImageCell.ACTIVATE;
//				}
			}
		};
		colActivate.setFieldUpdater(new FieldUpdater<Person, String>() {

			@Override
			public void update(int index, Person object, String value) {
				if (value.equals(ActivationImageCell.DEACTIVATE)) {
					
				}
				else if (value.equals(ActivationImageCell.ACTIVATE)) {
					
				}
			}
		});
		
		ButtonImageCell histoCell = new ButtonImageCell(Images.INSTANCE.ic_list_black_27dp(), style.imgGrid());
		Column<Person, String> colHisto = new Column<Person, String>(histoCell) {

			@Override
			public String getValue(Person object) {
				return "";
			}
		};
		colHisto.setFieldUpdater(new FieldUpdater<Person, String>() {

			@Override
			public void update(int index, Person object, String value) {
				DossierHistoriqueDialog dial = new DossierHistoriqueDialog(object.getDossier());
				dial.center();
			}
		});
		
		ButtonImageCell editCell = new ButtonImageCell(Images.INSTANCE.ic_edit_black_18dp(), style.imgGrid());
		Column<Person, String> colEdit = new Column<Person, String>(editCell) {

			@Override
			public String getValue(Person object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<Person, String>() {

			@Override
			public void update(int index, Person object, String value) {
				editPerson(object);
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Person> dataGrid = new DataGrid<Person>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(colRecordId, Labels.lblCnst.RecordRef());
		dataGrid.setColumnWidth(colRecordId, "150px");
		dataGrid.addColumn(colLastName, Labels.lblCnst.LastName());
		dataGrid.addColumn(colFirstName, Labels.lblCnst.FirstName());
		dataGrid.addColumn(colCreationDate, Labels.lblCnst.CreationDate());
		dataGrid.addColumn(colActivate, Labels.lblCnst.Activation());
		dataGrid.setColumnWidth(colActivate, "150px");
		dataGrid.addColumn(colHisto);
		dataGrid.setColumnWidth(colHisto, "70px");
		dataGrid.addColumn(colEdit);
		dataGrid.setColumnWidth(colEdit, "70px");
		dataGrid.setEmptyTableWidget(new Label("No Data"));
		
//		columns.put(surnameColumn, Labels.lblCnst.Surname());
//		columns.put(nameColumn, Labels.lblCnst.Name());
//		columns.put(mailColumn, Labels.lblCnst.Mail());
//		columns.put(creationDate, Labels.lblCnst.CreationDate());

		dataProvider = new ListDataProvider<Person>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Person>(new ArrayList<Person>());
		sortHandler.setComparator(colRecordId, new Comparator<Person>() {

			@Override
			public int compare(Person o1, Person o2) {
				if(o1.getDossier() == null) {
					return -1;
				}
				else if(o2.getDossier() == null) {
					return 1;
				}
				
				return o1.getDossier().getId().compareTo(o2.getDossier().getId());
			}
		});
		sortHandler.setComparator(colLastName, new Comparator<Person>() {

			@Override
			public int compare(Person o1, Person o2) {
				return o1.getLastName().compareTo(o2.getLastName());
			}
		});
		sortHandler.setComparator(colFirstName, new Comparator<Person>() {

			@Override
			public int compare(Person o1, Person o2) {
				return o1.getFirstName().compareTo(o2.getFirstName());
			}
		});
		sortHandler.setComparator(colRecordId, new Comparator<Person>() {

			@Override
			public int compare(Person o1, Person o2) {
				if(o1.getDossier() == null) {
					return -1;
				}
				else if(o2.getDossier() == null) {
					return 1;
				}
				
				return o2.getDossier().getCreationDate().before(o1.getDossier().getCreationDate()) ? -1 : o2.getDossier().getCreationDate().after(o1.getDossier().getCreationDate()) ? 1 : 0;
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());
		
		// Create a Pager to control the table.
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName(style.pager());
	    pager.setDisplay(dataGrid);

	    panelPager.setWidget(pager);

		return dataGrid;
	}

	public void editPerson(Person user) {
		parentPanel.displayFicheCreation();
	}
	
	public void manageLayout(Layout layout) {
//		if (layout == Layout.MOBILE) {
//			if (columns != null) {
//				for (Entry<Column<Person, ?>, String> col : columns.entrySet()) {
//					if (datagrid.getColumnIndex(col.getKey()) > 0) {
//						datagrid.removeColumn(col.getKey());
//					}
//				}
//			}
//		}
//		else if (layout == Layout.TABLET || layout == Layout.COMPUTER) {
//			if (columns != null) {
//				int index = 2;
//				for (Entry<Column<Person, ?>, String> col : columns.entrySet()) {
//					if (datagrid.getColumnIndex(col.getKey()) < 0) {
//						datagrid.insertColumn(index, col.getKey(), col.getValue());
//						index++;
//					}
//				}
//			}
//		}
	}
}
