package bpm.fasd.viewer.relation.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.olap.OLAPRelation;
import org.fasd.utils.trees.TreeDatabase;
import org.freeolap.FreemetricsPlugin;


import bpm.database.ui.viewer.relations.model.Column;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;
import bpm.database.ui.viewer.relations.model.Table;
import bpm.database.ui.viewer.relations.views.AbstractViewRelation;
import bpm.fasd.viewer.relation.Activator;



public class ViewRelation extends AbstractViewRelation {

	public static final String ID = "bpm.fasd.viewer.relation.views.ViewRelation";
	
	
	
	
	public ViewRelation() {
		super();
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}

	@Override
	protected void initContent(Object o) {
		model = new Node();
		this.editor.setContents(model);
		
		if (o == null){
			return;
		}
		
		DataSource dataSource = (DataSource)o;
		HashMap<DataObject, Table> tableMap = new HashMap<DataObject, Table>();
		HashMap<DataObjectItem, Column> columnsMap = new HashMap<DataObjectItem, Column>();
		
		List <Table> listTable= new ArrayList<Table>();

		for(DataObject ds : dataSource.getDataObjects()){
			Table t = new Table();
			t.setLayout(ds.getPositionX(), ds.getPositionY(), 100, 100);
			t.setName(ds.getName());
			tableMap.put(ds, t);
			for(DataObjectItem col : ds.getColumns()){
				Column c = new Column();
				c.setName(col.getName());
				t.addChild(c);
				columnsMap.put(col, c);
			}
			
			final DataObject f = ds;
			final Table tt = t;
			t.addPropertyChangeListener(new PropertyChangeListener(){

				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)){
						f.setPositionX(tt.getLayout().x);
						f.setPositionY(tt.getLayout().y);
					}
					
				}
				
			});
			
		//	model.addChild(t);
			
			listTable.add(t);
		}
		
		
		/*
		 * create Relations
		 */
		
		for(OLAPRelation r : FreemetricsPlugin.getDefault().getFAModel().getRelations()){
			
			if (r.getRightObject().getDataSource() != dataSource || r.getLeftObject().getDataSource() != dataSource ){
				continue;
			}
			
			
			
			Column source = columnsMap.get(r.getLeftObjectItem());
			Column target = columnsMap.get(r.getRightObjectItem());
			
			JoinConnection con = new JoinConnection(source, target, "");
			source.addConnection(con);
			target.addConnection(con);
				
			
			
		}
		
		if (testInit(listTable))
			defineTablePosition(listTable);
		
		for (Table t :listTable)
			model.addChild(t);
		
		editor.setContents(model);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection)selection;
		
		
		if (ss.getFirstElement() instanceof TreeDatabase){
			initContent(((TreeDatabase)ss.getFirstElement()).getDriver());
		}
		
	}

	
	private int margin =25;
	
	private boolean testInit(List<Table> listTable){
		for (Table t : listTable){
			if (t.getLayout().x!=10 || t.getLayout().y!=10)
				return false;	
		}
		return true;
	}
	
	private void defineTablePosition (List<Table> listTable){
		int marginX =10;
		int marginY =10;
		int i=0;
		int maxHeight=0;
		List<Table> tablesPositioned =new ArrayList<Table>();
		
		for(Table t :listTable){
			if (t.getAllChildrenConnections().isEmpty()){
				t.setLayout(marginX, marginY, t.getLayout().width, t.getLayout().height);
				tablesPositioned.add(t);
				if (t.getLayout().height>maxHeight)
					maxHeight=t.getLayout().height;
				
				if (i<4){
					marginX+=margin+t.getLayout().width;
					i++;
				}
				else {
					marginX=10;
					marginY+=maxHeight+margin; 
					i=0;
				}
			}	
		}
		
		for(Table t :listTable){
			if (t.getChildrenTargetConnections().isEmpty()){
				marginY=positionedTable(t, 10, marginY, tablesPositioned);
			}	
		}
		i=1;
		while (tablesPositioned.size()!=listTable.size()){
			for(Table t :listTable){
				if (t.getChildrenTargetConnections().size()<=i){
					if (!tablesPositioned.contains(t))
						marginY=positionedTable(t, 10, marginY, tablesPositioned);
				}	
			}
			i++;
		}
	}
	
	
	private int positionedTable (Table currentTable, int currentMarginX, int currentMarginY , List<Table> tablesPositioned){
		int initialMarginY = currentMarginY;
		int marginX = currentMarginX +margin+ currentTable.getLayout().width;
		int marginY = currentMarginY;
		
		if (!tablesPositioned.contains(currentTable)){	
			tablesPositioned.add(currentTable);
			
			List<JoinConnection> childConnection =currentTable.getChildrenSourceConnections();
			if (!childConnection.isEmpty()){
				for (JoinConnection connection : childConnection){
					Table targetTable = (Table)connection.getTarget().getParent();
					marginY = positionedTable(targetTable, marginX, marginY, tablesPositioned);
					marginY+=margin;
				}			
			}
			else 
				marginY+= currentTable.getLayout().height;
			
			
			if (marginY-initialMarginY<=currentTable.getLayout().height)
			{
				marginY=initialMarginY+currentTable.getLayout().height;
				currentTable.setLayout(currentMarginX, initialMarginY, currentTable.getLayout().width, currentTable.getLayout().height);
			}
			else{
				int posY = (((marginY-initialMarginY)-currentTable.getLayout().height)/2) + initialMarginY;
				currentTable.setLayout(currentMarginX, posY, currentTable.getLayout().width, currentTable.getLayout().height);
			}
			
						
			marginY+=margin;
			
		}
		
		return marginY;
	}

}
