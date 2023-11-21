package metadata.client.trees;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;

public class TreeModel extends TreeParent {

	private IBusinessModel model;
	
	public TreeModel(IBusinessModel model, String groupName) {
		super(model.getName());
		this.model = model;
		buildChilds(groupName, false);
	}
	
	public TreeModel(IBusinessModel model, String groupName, boolean onlyDimensionMeasure) {
		super(model.getName());
		this.model = model;
		buildChilds(groupName, onlyDimensionMeasure);
	}

	public IBusinessModel getModel(){
		return model;
	}
	
	@Override
	public String toString(){
		return model.getName();
	}
	
	private boolean findTreeBusinessTable(IBusinessTable t, String groupName, TreeParent bt) {
		TreeBusinessTable tbt = new TreeBusinessTable(t,false,groupName);
		
		for(IDataStreamElement e : t.getColumnsOrdered(groupName)){
			if(e.getType().getParentType() == IDataStreamElement.Type.DIMENSION || e.getType().getParentType() == IDataStreamElement.Type.MEASURE) {
				tbt.addChild(new TreeDataStreamElement(e));
			}
		}
		
		for(IBusinessTable table : t.getChilds(groupName)) {
			findTreeBusinessTable(table, groupName, tbt);
		}
		
		if(tbt.getChildren() != null && tbt.getChildren().size() > 0) {
			bt.addChild(tbt);
			return true;
		}
		return false;
	}
	
	private void buildChilds(String groupName, boolean onlyDimensionMeasure){
		
		if(onlyDimensionMeasure) {
			TreeParent bt = new TreeParent("Business Tables"); //$NON-NLS-1$
			addChild(bt);
			List<IBusinessTable> toAdd = new ArrayList<IBusinessTable>();
			for(IBusinessTable t : ((BusinessModel)model).getBusinessTables()){
				boolean needed = findTreeBusinessTable(t, groupName, bt);
				if(needed) {
					toAdd.add(t);
				}
			}
			
			TreeParent rp = new TreeParent("Business Packages"); //$NON-NLS-1$
			addChild(rp);
			for(IBusinessPackage p : model.getBusinessPackages(groupName)){
				TreePackage pack = new TreePackage(p, groupName, false);
				
				for(IBusinessTable t : p.getBusinessTables(groupName)) {
					if(toAdd.contains(t)) {
						pack.addChild(new TreeBusinessTable(t, false, groupName));
					}
				}
				
				if(pack.getChildren() != null && pack.getChildren().size() > 0) {
					rp.addChild(pack);
				}
			}
		}
		
		else {
			TreeParent bt = new TreeParent("Business Tables"); //$NON-NLS-1$
			addChild(bt);
			for(IBusinessTable t : ((BusinessModel)model).getBusinessTables()){
				bt.addChild(new TreeBusinessTable(t, groupName));
			}
			
			TreeParent rs = new TreeParent("Resources"); //$NON-NLS-1$
			addChild(rs);
			for(IResource t : ((BusinessModel)model).getResources()){
				if ( t instanceof ListOfValue){
					rs.addChild(new TreeLov((ListOfValue)t));
				}
				else if (t instanceof Filter){
					rs.addChild(new TreeFilter((Filter)t));
				}
				else if (t instanceof Prompt){
					rs.addChild(new TreePrompt((Prompt)t));
				}
				else if (t instanceof ComplexFilter){
					rs.addChild(new TreeComplexFilter((ComplexFilter)t));
				}
				else {
					rs.addChild(new TreeResource(t));
				}
			}
			
			TreeParent rl = new TreeParent("Relations"); //$NON-NLS-1$
			addChild(rl);
			for(Relation r : ((BusinessModel)model).getRelations()){
				rl.addChild(new TreeRelation(r));
			}
			
			TreeParent relationStrategies = new TreeParent("Relation Strategies"); //$NON-NLS-1$
			addChild(relationStrategies);
			for(RelationStrategy r : ((BusinessModel)model).getRelationStrategies()){
				relationStrategies.addChild(new TreeRelationStrategy(r));
			}
			
			
			TreeParent rp = new TreeParent("Business Packages"); //$NON-NLS-1$
			addChild(rp);
			for(IBusinessPackage p : model.getBusinessPackages(groupName)){
				rp.addChild(new TreePackage(p, groupName));
			}
		}

	}
	
	public void refresh(String groupName){
		removeAll();
		buildChilds(groupName, false);
	}
	
	@Override
	public Object getContainedModelObject() {
		return model;
	}

}
