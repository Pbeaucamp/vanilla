package bpm.sqldesigner.query.palette;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.sqldesigner.query.commands.creation.NodeCreationFactory;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Schema;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.services.AbstractDatabaseService;
import bpm.sqldesigner.query.services.DatabaseServices;
import bpm.sqldesigner.query.services.FactoryDatabaseService;

public class SQLDesignerPalette extends PaletteViewer {

	private PaletteDrawer tablesGroup;
	private PaletteRoot paletteRoot;
	private DataBaseConnection dbc;
	private List<CreationToolEntry> hiddenToolEntries = new ArrayList<CreationToolEntry>();
	
	private DocumentGateway document;
	
	public SQLDesignerPalette(DataBaseConnection dbc, DocumentGateway document) {
		this.document = document;
		initalizePaletteRoot();
		getDBcontents(dbc);
		this.dbc = dbc;
		setPaletteRoot(paletteRoot);
	}
	
	public SQLDesignerPalette(Schema schema, DocumentGateway document) {
		this.document = document;
		initalizePaletteRoot();


		for (Node node :schema.getChildren()){
			Table table = (Table) node;
			createPanelTable(table);
		}

		setPaletteRoot(paletteRoot);
	}
	

	protected void initalizePaletteRoot() {

		paletteRoot = new PaletteRoot();
		PaletteGroup manipGroup = new PaletteGroup("Objects utils");
		paletteRoot.add(manipGroup);

		SelectionToolEntry selectionToolEntry = new SelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		manipGroup.add(new MarqueeToolEntry());

		paletteRoot.add(new PaletteSeparator());

		tablesGroup = new PaletteDrawer("Tables");
		paletteRoot.add(tablesGroup);

		paletteRoot.setDefaultEntry(selectionToolEntry);
		
	}

	
	public void loadColumns(Table table){
		AbstractDatabaseService dS =FactoryDatabaseService.getDataBaseService(dbc);

		try {
			dS.connect(document);
			List<Column> columns = dS.extractColumns(table.getName());
			Iterator<Column> it2 = columns.iterator();

			while (it2.hasNext()) {
				table.addChild(it2.next());
			}
			
			dS.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void getDBcontents(DataBaseConnection dbc) {

		AbstractDatabaseService dS = FactoryDatabaseService.getDataBaseService(dbc);

		try {
			dS.connect(document);
		} catch (ServerException e) {
			e.printStackTrace();
		}

		if (dS.isConnected()) {

			List<Table> tables = dS.extractTables();
			Iterator<Table> it = tables.iterator();

			while (it.hasNext()) {
				Table table = it.next();
				createPanelTable(table);

				

			}

			dS.disconnect();

		}//else
//			System.out.println("Connection failed");

	}

	protected void createPanelTable(Table table) {
		NodeCreationFactory creationFactory = new NodeCreationFactory(table);

		CreationToolEntry cte = new CreationToolEntry(table.getName(),
				"Creation of " + table.getName(), creationFactory, null, null);

		PropertyChangeListener listener = new CreationEntryListener(cte);
		table.addPropertyChangeListener(listener);
		tablesGroup.add(cte);

	}
	
	public void filterOnName(String tableName){
		for(Object o : tablesGroup.getChildren()){
			
			if (o instanceof CreationToolEntry){
				CreationToolEntry cte = (CreationToolEntry)o;
				cte.setVisible(!hiddenToolEntries.contains(cte) && cte.getLabel().startsWith(tableName));
				
			}
		}
	}

	public PaletteRoot getPaletteRoot() {
		return paletteRoot;
	}

	class CreationEntryListener implements PropertyChangeListener {

		private CreationToolEntry cte;

		public CreationEntryListener(CreationToolEntry cte) {
			this.cte = cte;
		}

		
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getPropertyName().equals(Table.CTE_VISIBLE)) {
				cte.setVisible(true);
				hiddenToolEntries.remove(cte);
			} else if (event.getPropertyName().equals(Table.CTE_NOVISIBLE)) {
				cte.setVisible(false);
				hiddenToolEntries.add(cte);
			}
		}

	}
	
}
