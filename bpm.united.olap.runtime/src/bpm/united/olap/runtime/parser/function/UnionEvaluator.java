package bpm.united.olap.runtime.parser.function;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.mdx.parser.result.NodeEvaluator;
import bpm.mdx.parser.result.TermItem;
import bpm.mdx.parser.result.TopCountItem;
import bpm.mdx.parser.result.UnionItem;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.runtime.parser.MdxEvaluator;

/**
 * Evaluate Union function
 * @author Marc Lanquetin
 *
 */
public class UnionEvaluator implements IFunctionEvaluator {

	private MdxEvaluator parent;
	
	public UnionEvaluator(MdxEvaluator evaluator) {
		parent = evaluator;
	}
	 
	
	
	private void extractTermItems(NodeEvaluator node, List<TermItem> items){
		if (node instanceof UnionItem){
//			extractTermItems(((UnionItem)node).getLeftItem(), items);
////		extractTermItems(((UnionItem)node).getRightItem(), items);
			for(NodeEvaluator n : ((UnionItem)node).getUnionItems()) {
				extractTermItems(n, items);
			}
		}
		else if (node instanceof TermItem){
			
			boolean present = false;
			for(TermItem i : items){
				if (i.getUname().equals(((TermItem)node).getUname())){
					present = true;
					break;
				}
			}
			
			if (!present){
				items.add((TermItem)node);
			}
		}
		else if (node instanceof TopCountItem) {
			try {
				List<MdxSet> set = parent.evaluateItem(node);
				items.add(((TermItem)((TopCountItem)node).getSet()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			Logger.getLogger(getClass()).error("What the fuck am i doing here!!!!!!!!!!!!!!");
			throw new RuntimeException("What the fuck am i doing here!!!!!!!!!!!!!!" + node.getClass().getName());
		}
		
	}
	
	@Override
	public List<MdxSet> evaluate(NodeEvaluator node) throws Exception {
		
		//UnionItem item = (UnionItem) node;
		List<MdxSet> results = new ArrayList<MdxSet>();
		
		
		//super trick
		List<TermItem> l = new ArrayList<TermItem>();
		for(NodeEvaluator n : ((UnionItem)node).getUnionItems()) {
			extractTermItems(n, l);
		}
		
		for(TermItem i : l){
			results.addAll(evaluateItem(i));
		}
		
//		List<MdxSet> leftSets = new ArrayList<MdxSet>();
//		List<MdxSet> rightSets = new ArrayList<MdxSet>();
//		
//		leftSets.addAll(evaluateItem(item.getLeftItem()));
//		rightSets.addAll(evaluateItem(item.getRightItem()));
//		
//		
//		
//		//cross items
//		results.addAll(leftSets);
//		results.addAll(rightSets);
		
		return results;
	}

	private List<MdxSet> evaluateItem(NodeEvaluator item) throws Exception {
		
		List<MdxSet> set = parent.evaluateItem(item);
		
		return set;
	}

}
