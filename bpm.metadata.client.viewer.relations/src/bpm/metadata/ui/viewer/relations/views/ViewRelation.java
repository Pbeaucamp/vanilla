package bpm.metadata.ui.viewer.relations.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreePackage;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;






import bpm.database.ui.viewer.relations.model.Column;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;
import bpm.database.ui.viewer.relations.model.Table;
import bpm.database.ui.viewer.relations.views.AbstractViewRelation;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.ui.viewer.relations.Activator;



public class ViewRelation extends AbstractViewRelation{

	private static RGB TABLE_COLOR = new RGB(160, 160, 160);
	private static RGB TABLE_REL_COLOR = new RGB(224, 224, 224);
	
	public ViewRelation() {
		super();
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	protected void initContent(Object o){
		model = new Node();
		this.editor.setContents(model);
		
		if (o == null){
			return;
		}
		
		if (o instanceof TreeDataSource){
			initIDatasourceContent(((TreeDataSource)o).getDataSource(), null);
		}
		else if(o instanceof TreePackage) {
			initPackageContent(((TreePackage)o).getPackage());
		}
		else if(o instanceof TreeDataStream) {
			initDatastreamContent(((TreeDataStream)o).getDataStream());
		}
		
	}
	
	
	
	
	
	private void initDatastreamContent(IDataStream iDataStream) {
		
		IDataSource datasource = iDataStream.getDataSource();
		
		initIDatasourceContent(datasource, iDataStream);
		
	}
	private void initPackageContent(IBusinessPackage bpackage) {
		HashMap<String, Column> columnsMap = new HashMap<String, Column>();
		
		List <Table> listTable= new ArrayList<Table>();
		/*
		 * create Tables
		 */
		for(IBusinessTable table : bpackage.getBusinessTables("none")) {
			Table t = new Table();
			t.setLayout(table.getPositionX(), table.getPositionY(), 100, 100);
			t.setName(table.getName());
			t.setBTable(table);
			for(IDataStreamElement col : table.getColumns("none")){
				Column c = new Column();
				c.setName(col.getName());
				c.setDatastreamElement(col);
				t.addChild(c);
				columnsMap.put(col.getName(), c);
			}
			
			// model.addChild(t);
			listTable.add(t);
		}
		
		/*
		 * create Relations
		 */
		for(Relation r : ((BusinessModel)bpackage.getBusinessModel()).getRelations()){
			List<Column> lefts = new ArrayList<Column>();
			List<Column> rights = new ArrayList<Column>();
			
			for(Join j : r.getJoins()){
				
				IDataStreamElement leftColumn = j.getLeftElement();
				IDataStreamElement rightColumn = j.getRightElement();
				 
			//	LOOK:for(Node node : model.getChildren()) {
				LOOK:for(Node node :  listTable) {
					boolean tableLeftFinded = false;
					boolean tableRightFinded = false;
					for(Node colNode : node.getChildren()) {
						Column col = (Column) colNode;
						IDataStreamElement element = (IDataStreamElement) col.getDatastreamElement();
						if(element.getDataStream().getName().equals(leftColumn.getDataStream().getName())) {
							tableLeftFinded = true;
							if(element.getName().equals(leftColumn.getName())) {
								lefts.add(col);
								continue LOOK;
							}
						}
						else if(element.getDataStream().getName().equals(rightColumn.getDataStream().getName())) {
							tableRightFinded = true;
							if(element.getName().equals(rightColumn.getName())) {
								rights.add(col);
								continue LOOK;
							}
						}
					}
					if(tableLeftFinded) {
						Column col = new Column();
						col.setDatastreamElement(leftColumn);
						col.setName(leftColumn.getName());
						col.setExists(false);
						node.addChild(col);
						lefts.add(col);
					}
					else if(tableRightFinded) {
						Column col = new Column();
						col.setDatastreamElement(rightColumn);
						col.setName(rightColumn.getName());
						col.setExists(false);
						node.addChild(col);
						rights.add(col);
					}
				}
				

				
			}
			if(!lefts.isEmpty() && !rights.isEmpty()) {
				
				for(int i = 0 ; i < lefts.size() ; i++) {
					for(int j = 0 ; j < rights.size() ; j++) {
						JoinConnection con = new JoinConnection(lefts.get(i), rights.get(j), r.getCardinality().name());
						lefts.get(i).addConnection(con);
						rights.get(j).addConnection(con);
					}
				}
			}	
		}
		
		if (testInit(listTable))
			defineTablePosition(listTable);
		
		for (Table t :listTable)
			model.addChild(t);
		
		editor.setContents(model);
		
	}
	private  void initIDatasourceContent(IDataSource datasource, IDataStream datastream){

		HashMap<IDataStream, Table> tableMap = new HashMap<IDataStream, Table>();
		HashMap<IDataStreamElement, Column> columnsMap = new HashMap<IDataStreamElement, Column>();
		
		/*
		 * create Tables
		 */
		
		List <Table> listTable= new ArrayList<Table>();
		
		for(IDataStream ds : ((IDataSource)datasource).getDataStreams()){
			Table t = new Table();
			t.setLayout(ds.getPositionX(), ds.getPositionY(), 100, 100);
			t.setName(ds.getName());
			if(datastream != null && ds.equals(datastream)) {
				t.setColor(new Color(Display.getCurrent(), TABLE_COLOR));
			}
			tableMap.put(ds, t);
			for(IDataStreamElement col : ds.getElements()){
				Column c = new Column();
				c.setName(col.getName());
				if(datastream != null && ds.equals(datastream)) {
					c.setColor(new Color(Display.getCurrent(), TABLE_COLOR));
				}
				t.addChild(c);
				columnsMap.put(col, c);
			}
			
			final IDataStream f = ds;
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
		for(Relation r : ((IDataSource)datasource).getRelations()){
			
			for(Join j : r.getJoins()){
				Column source = columnsMap.get(j.getLeftElement());
				Column target = columnsMap.get(j.getRightElement());
				
				if(datastream != null && datastream.getElements().contains(j.getLeftElement())) { 
					Table table = (Table)target.getParent();
					table.setColor(new Color(Display.getCurrent(), TABLE_REL_COLOR));
					for(Node n : table.getChildren()) {
						((Column)n).setColor(new Color(Display.getCurrent(), TABLE_REL_COLOR));
					}
				}
				else if(datastream != null && datastream.getElements().contains(j.getRightElement())) { 
					Table table = (Table)source.getParent();
					table.setColor(new Color(Display.getCurrent(), TABLE_REL_COLOR));
					for(Node n : table.getChildren()) {
						((Column)n).setColor(new Color(Display.getCurrent(), TABLE_REL_COLOR));
					}
				}
				
				JoinConnection con = new JoinConnection(source, target, r.getCardinality().name());
				source.addConnection(con);
				target.addConnection(con);
				
			}
			
		}
		
		if (testInit(listTable))
			defineTablePosition(listTable);
		
		for (Table t :listTable)
			model.addChild(t);
		
		
		editor.setContents(model);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection)selection;
		initContent(ss.getFirstElement());

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
		//int initialMarginX = currentMarginX;
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
