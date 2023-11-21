package metadata.client.trees;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;

public class TreeDataSource extends TreeParent {

	private IDataSource dataSource;
	private TreeParent rootRelation;
	
	
	public TreeDataSource(IDataSource dataSource, boolean includeRelation) {
		super(dataSource.getName());
		this.dataSource = dataSource;
		buildChild(includeRelation, false);
	}
	
	public TreeDataSource(IDataSource dataSource) {
		super(dataSource.getName());
		this.dataSource = dataSource;
		buildChild(true, false);
	}

	public TreeDataSource(IDataSource dataSource, boolean includeRelation, boolean onlyDimensionMeasure) {
		super(dataSource.getName());
		this.dataSource = dataSource;
		buildChild(true, onlyDimensionMeasure);
	}

	@Override
	public String toString() {
		return dataSource.getName();
	}
	
	public IDataSource getDataSource(){
		return dataSource;
	}

	
	private void buildChild(boolean includeRelation, boolean onlyDimensionMeasure){
		
		if(onlyDimensionMeasure) {
			for(IDataStream t : dataSource.getDataStreams()){
				boolean toAdd = false;
				TreeDataStream tds = new TreeDataStream(t, false);
				
				for(IDataStreamElement elem : t.getElements()) {
					if(elem.getType().getParentType() == IDataStreamElement.Type.DIMENSION || elem.getType().getParentType() == IDataStreamElement.Type.GEO || elem.getType().getParentType() == IDataStreamElement.Type.DATE || elem.getType().getParentType() == IDataStreamElement.Type.MEASURE) {
						tds.addChild(new TreeDataStreamElement(elem));
						toAdd = true;
					}
				}
				
				if(toAdd) {
					addChild(tds);
				}
			}
		}
		
		else {
			for(IConnection c : dataSource.getConnections(null)){
				addChild(new TreeConnection(c));
			}
			
			for(IDataStream t : dataSource.getDataStreams()){
				addChild(new TreeDataStream(t));
			}
			
			
			
			if (includeRelation){
				rootRelation = new TreeParent("Relations"); //$NON-NLS-1$
				addChild(rootRelation);
				for(Relation r : dataSource.getRelations()){
					rootRelation.addChild(new TreeRelation(r));
				}
			}
		}
		
	}
	
	public void refresh() {
		this.removeAll();
		
		buildChild(true, false);
		
	}
	
	@Override
	public Object getContainedModelObject() {
		return dataSource;
	}
}
