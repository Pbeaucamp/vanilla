package bpm.united.olap.runtime.parser.function;

import java.util.ArrayList;
import java.util.List;

import bpm.mdx.parser.result.CrossjoinItem;
import bpm.mdx.parser.result.NodeEvaluator;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.ClosureHierarchy;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.united.olap.runtime.parser.MdxEvaluator;

/**
 * Evaluate Crossjoin function
 * @author Marc Lanquetin
 *
 */
public class CrossjoinEvaluator implements IFunctionEvaluator {

	private MdxEvaluator parent;
	
	public CrossjoinEvaluator(MdxEvaluator evaluator) {
		parent = evaluator;
	}
	
	@Override
	public List<MdxSet> evaluate(NodeEvaluator node) throws Exception {
		
		CrossjoinItem item = (CrossjoinItem) node;
		
//		List<MdxSet> leftSets = new ArrayList<MdxSet>();
//		List<MdxSet> rightSets = new ArrayList<MdxSet>();
		
//		leftSets.addAll(evaluateItem(item.getLeftItem()));
//		rightSets.addAll(evaluateItem(item.getRightItem()));
		
		List<List<MdxSet>> sets = new ArrayList<List<MdxSet>>();
		for(NodeEvaluator n : item.getCrossItems()) {
			sets.add(evaluateItem(n));
		}
		
		//cross items
		RuntimeFactory factory = RuntimeFactory.eINSTANCE;
		
		List<MdxSet> temp = null;
		List<MdxSet> results = new ArrayList<MdxSet>();
		
		for(List<MdxSet> s : sets) {
			
			temp = new ArrayList<MdxSet>(results);
			results.clear();
			
			if(temp.size() == 0) {
				results = s;
			}
			
			else {
				for(MdxSet se : s) {
					for(MdxSet tmpSet : temp) {
						
						if(isValidForCrossjoin(se, tmpSet)) {
							MdxSet res = factory.createMdxSet();
							res.getElements().addAll(tmpSet.getElements());
							res.getElements().addAll(se.getElements());
							if (se.getMeasure() != null){
								res.setMeasure(se.getMeasure());
							}
							else if (tmpSet.getMeasure() != null){
								res.setMeasure(tmpSet.getMeasure());
							}
							results.add(res);
						}
					}
				}
			}
		}
		
//		for(MdxSet left : leftSets) {
//			
//			for(MdxSet right : rightSets) {
//				
//				MdxSet res = factory.createMdxSet();
//				res.getElements().addAll(left.getElements());
//				res.getElements().addAll(right.getElements());
//				
//				
//				
//				if (right.getMeasure() != null){
//					res.setMeasure(right.getMeasure());
//				}
//				else if (left.getMeasure() != null){
//					res.setMeasure(left.getMeasure());
//				}
//				results.add(res);
//			}
//			
//		}
		
		return results;
	}

	/**
	 * Check if the crossjoin is valid
	 * If two members are from the same hierarchy, they have to be parent-child
	 * @param set1
	 * @param set2
	 * @return
	 */
	private boolean isValidForCrossjoin(MdxSet set1, MdxSet set2) {
		
		for(ElementDefinition elem1 : set1.getElements()) {
			if(elem1 instanceof Member) {
				Member member1 = (Member) elem1;
				if(member1.getParentLevel() != null) {
					for(ElementDefinition elem2 : set2.getElements()) {
						if(elem2 instanceof Member) {
							Member member2 = (Member) elem2;
							if(member2.getParentLevel() != null) {
								//look if they're from the same hierarchy
								if(member1.getParentHierarchy().getUname().equals(member2.getParentHierarchy().getUname())) {
									
									//look for the parent member
									int levelIndex1 = -1;
									int levelIndex2 = -1;
									if(member1.getParentHierarchy() instanceof ClosureHierarchy) {
										levelIndex1 = member1.getUname().split("\\]\\.\\[").length;
										levelIndex2 = member2.getUname().split("\\]\\.\\[").length;
									}
									
									else {
										levelIndex1 = member1.getParentHierarchy().getLevels().indexOf(member1.getParentLevel());
										levelIndex2 = member2.getParentHierarchy().getLevels().indexOf(member2.getParentLevel());
									}
									
									//check if they're parent-child
									if(levelIndex1 > levelIndex2) {
										if(!member1.getUname().startsWith(member2.getUname())) {
											return false;
										}
									}
									else {
										if(!member2.getUname().startsWith(member1.getUname())) {
											return false;
										}
									}
								}
							}
						}
					}
				}
				
			}
			
			
		}
		
		
		return true;
	}

	private List<MdxSet> evaluateItem(NodeEvaluator item) throws Exception {
		
		List<MdxSet> set = parent.evaluateItem(item);
		
		return set;
	}

}
