package bpm.inlinedatas.oda.driver.ui.impl.datasource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import bpm.inlinedatas.oda.driver.runtime.impl.Connection;
import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieColumn;
import bpm.inlinedatas.oda.driver.ui.Activator;
import bpm.inlinedatas.oda.driver.ui.dialog.DialogAddColumn;
import bpm.inlinedatas.oda.driver.ui.dialog.DialogDelColumn;
import bpm.inlinedatas.oda.driver.ui.icons.IconNames;
import bpm.inlinedatas.oda.driver.ui.model.CellDescription;
import bpm.inlinedatas.oda.driver.ui.model.ColumnsDescription;
import bpm.inlinedatas.oda.driver.ui.tableViewer.DataTableViewer;

public class BuildDataSourcePage extends DataSourceWizardPage {

	private IConnection conn;

	private ArrayList<PropertieColumn> listDS;
	private ArrayList<ColumnsDescription> listColumns;
	private List<ArrayList<CellDescription>> listInput;

	private Button itemColAdd, itemColDel, itemLineDel, itemLineAdd;

	private Properties prop;

	private Composite compoTable;

	private DataTableViewer viewer;
	private Table table;

	public BuildDataSourcePage(String pageName) {
		super(pageName);
		setPingButtonVisible(false);

	}

	public BuildDataSourcePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createPageCustomControl(Composite parent) {

		setPingButtonVisible(false);
		setDescription("To build your own Data Source, use buttons to add Columns. Specify Names and types. Then, add rows, and values.");

		listColumns = new ArrayList<ColumnsDescription>();
		listInput = new ArrayList<ArrayList<CellDescription>>();

		// Fill datas with properties id needed
		if(prop != null) {

			try {
				conn = new Connection();
				conn.open(prop);

			} catch(Exception e) {
				e.printStackTrace();
			}

			listDS = ((Connection) conn).getListeProperties();

			// Columns
			for(PropertieColumn col : listDS) {

				listColumns.add(new ColumnsDescription(col.getPropertieColName(), col.getPropertieColType(), listColumns.size()));
			}

			// Rows
			for(int i = 0; i < listDS.get(0).getListPropertieData().size(); i++) {

				ArrayList<CellDescription> listCellsTemp = new ArrayList<CellDescription>();

				for(int j = 0; j < listColumns.size(); j++) {

					CellDescription c = new CellDescription(listColumns.get(j).getColType(), listColumns.get(j).getIndexCol());

					c.setValueCell(listDS.get(j).getListPropertieData().get(i).getPropertieDataValue().toString());

					listCellsTemp.add(c);
				}

				listInput.add(listCellsTemp);

			}

		}

		// Items add/del col
		parent.setLayout(new GridLayout(2, false));

		// ****** Control Toolbar to manage Columns

		Composite compo = new Composite(parent, SWT.BORDER);
		compo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		compo.setLayout(new GridLayout(5, false));

		itemColAdd = new Button(compo, SWT.PUSH);
		itemColAdd.setImage(Activator.getDefault().getImageRegistry().get(IconNames.ADD_COL));
		itemColAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		itemColDel = new Button(compo, SWT.PUSH);
		itemColDel.setImage(Activator.getDefault().getImageRegistry().get(IconNames.DEL_COL));
		itemColAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		Label l = new Label(compo, SWT.NONE);
		l.setText("   ");
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		itemLineAdd = new Button(compo, SWT.PUSH);
		itemLineAdd.setImage(Activator.getDefault().getImageRegistry().get(IconNames.ADD_LINE));
		itemColAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		itemLineDel = new Button(compo, SWT.PUSH);
		itemLineDel.setImage(Activator.getDefault().getImageRegistry().get(IconNames.DEL_LINE));
		itemColAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		PShelf shelf = new PShelf(parent, SWT.SMOOTH | SWT.BORDER);
		shelf.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		shelf.setRenderer(new RedmondShelfRenderer());

		PShelfItem item = new PShelfItem(shelf, SWT.NONE);
		item.setImage(Activator.getDefault().getImageRegistry().get(IconNames.DATASOURCE));
		item.setText("Data source");

		compoTable = item.getBody();
		compoTable.setLayout(new GridLayout(1, true));

		builtViewer();

	}

	private void builtViewer() {

		viewer = new DataTableViewer(compoTable, listColumns);

		// Table which use the viewer
		table = viewer.getTable();

		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setInput(listInput);
		viewer.refresh();

		// Listenners BTS
		initListenerBtn();

	}

	// ******************************* Listeners tool bar
	private void initListenerBtn() {

		// ---------Option ADD a columns
		itemColAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// Show the dialog box to specify a column
				DialogAddColumn dialogAdd = new DialogAddColumn(getShell(), "InlineDatas - Build DataSource", "Enter Name and select Type", "Column's Caracteristics");

				// If fields are correct, and user click on "OK" Button
				if(dialogAdd.open() == Window.OK) {

					ColumnsDescription newCol = new ColumnsDescription(dialogAdd.getTempName(), dialogAdd.getTempType(), listColumns.size());
					updateAddingTable(newCol);
				}
			}
		});

		// -------- Option REMOVE a column
		itemColDel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// Show the dialog box to choose and del a column
				DialogDelColumn dialogAdd = new DialogDelColumn(getShell(), "InlineDatas - Build DataSource", "Please, choose a Column", "Column's Caracteristics", listColumns);

				if(dialogAdd.open() == Window.OK) {

					// remove the col of array list
					int i = 0;
					for(ColumnsDescription current : listColumns) {

						if(current.getColName().equals(dialogAdd.getTempColDelName())) {
							ColumnsDescription.removeColumn(listColumns, i);

							for(int j = 0; j < listInput.size(); j++) {
								listInput.get(j).remove(i);
							}

							updateDeletingTable(dialogAdd.getTempColDelName());
						}

						i++;
					}
				}
			}
		});

		// -------- Option ADD a line
		itemLineAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// Add one cell for each Columns
				ArrayList<CellDescription> l = new ArrayList<CellDescription>();

				for(ColumnsDescription currentCol : listColumns) {
					CellDescription c = new CellDescription(currentCol.getColType(), currentCol.getIndexCol());
					if(c.getTypeCell() == ColumnsDescription.TYPES_NAMES[ColumnsDescription.INDEX_INTEGER] || 
							c.getTypeCell() == ColumnsDescription.TYPES_NAMES[ColumnsDescription.INDEX_DOUBLE] || 
							c.getTypeCell() == ColumnsDescription.TYPES_NAMES[ColumnsDescription.INDEX_FLOAT] || 
							c.getTypeCell() == ColumnsDescription.TYPES_NAMES[ColumnsDescription.INDEX_LONG]) {
						c.setValueCell("1");
					}
					else if(c.getTypeCell() == ColumnsDescription.TYPES_NAMES[ColumnsDescription.INDEX_CHAR] || 
							c.getTypeCell() == ColumnsDescription.TYPES_NAMES[ColumnsDescription.INDEX_STRING]) {
						c.setValueCell("value");
					}
					else {
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
						c.setValueCell(format.format(new Date()));
					}
					l.add(c);
				}

				listInput.add(l);
				viewer.refresh();

			}

		});

		// -------- Option REMOVE a line

		itemLineDel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// delete one cell for each coll

				int indexLastElement = listInput.size() - 1;

				listInput.remove(indexLastElement);
				viewer.refresh();

			}

		});

	}

	private void updateAddingTable(ColumnsDescription colAdded) {

		// Add the new col into the ArrayList
		listColumns.add(colAdded);

		// Add the new col into the viewer
		viewer.structureAddCol(colAdded, getShell());

		// add lines for this new col if needed
		for(int i = 0; i < listInput.size(); i++) {

			CellDescription c = new CellDescription(colAdded.getColType(), colAdded.getIndexCol());
			listInput.get(i).add(c);
		}

		// Refresh the viewer
		viewer.setInput(listInput);
		viewer.refresh();

	}

	// ******************** Method to update viewer when user delete a column
	private void updateDeletingTable(String colDel) {

		viewer.getControl().dispose();

		builtViewer();

		compoTable.layout();

	}

	@Override
	public Properties collectCustomProperties() {

		prop = new Properties();

		// ----------- Number of columns
		String stringNumberCol = String.valueOf(listColumns.size());

		// ----------- Columns Names
		String stringNames = "";
		for(ColumnsDescription current : listColumns) {
			stringNames += current.getColName() + ";";
		}

		// ----------- Columns Type
		String stringTypes = "";
		for(ColumnsDescription current : listColumns) {
			stringTypes += current.getColType().getSimpleName() + ";";
		}

		// ----------- Columns values
		String stringValues = "";

		for(int i = 0; i < listInput.size(); i++) {

			for(CellDescription currentCell : listInput.get(i)) {
				stringValues += currentCell.getValueCell() + ";";
			}
		}

		prop.setProperty(ColumnsDescription.PROPERTIES_COUNT_COL, stringNumberCol);
		prop.setProperty(ColumnsDescription.PROPERTIES_NAME_COL, stringNames);
		prop.setProperty(ColumnsDescription.PROPERTIES_VALUES_COL, stringValues);
		prop.setProperty(ColumnsDescription.PROPERTIES_TYPE_COL, stringTypes);

		return prop;
	}

	@Override
	public void setInitialProperties(Properties dataSourceProp) {
		prop = dataSourceProp;
	}

}
