package metadata.client.trees;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;

public class TreePackage extends TreeParent {

	private IBusinessPackage pack;
	
	public TreePackage(IBusinessPackage pack, String groupName) {
		super(pack.getName());
		this.pack = pack;
		buildChilds(groupName);
	}
	
	public TreePackage(IBusinessPackage pack, String groupName, boolean buildChild) {
		super(pack.getName());
		this.pack = pack;
		if(buildChild) {
			buildChilds(groupName);
		}
	}

	public IBusinessPackage getPackage(){
		return pack;
	}
	
	@Override
	public String toString(){
		return pack.getName();
	}
	
	private void buildChilds(String groupName){
		TreeParent bt = new TreeParent("Business Tables"); //$NON-NLS-1$
		addChild(bt);
		for(IBusinessTable t : pack.getOrderedTables(groupName)){
			bt.addChild(new TreeBusinessTable(t, false, groupName));
		}
		
		TreeParent rs = new TreeParent("Resources"); //$NON-NLS-1$
		addChild(rs);
		for(IResource t : pack.getResources()){
			if ( t instanceof ListOfValue){
				rs.addChild(new TreeLov((ListOfValue)t));
			}
			else if ( t instanceof Filter){
				rs.addChild(new TreeFilter((Filter)t));
			}
			else if ( t instanceof ComplexFilter){
				rs.addChild(new TreeComplexFilter((ComplexFilter)t));
			}
			else if ( t instanceof Prompt){
				rs.addChild(new TreePrompt((Prompt)t));
			}
			else {
				rs.addChild(new TreeResource(t));
			}
		}
		
		TreeParent qu = new TreeParent("Queries"); //$NON-NLS-1$
		addChild(qu);
		if(pack.getSavedQueries() != null) {
			for(SavedQuery query : pack.getSavedQueries()){
				qu.addChild(new TreeSavedQuery(query));
			}
		}
	}
	
	public void refresh(String groupName){
		removeAll();
		buildChilds(groupName);
	}
	@Override
	public Object getContainedModelObject() {
		return pack;
	}
}
