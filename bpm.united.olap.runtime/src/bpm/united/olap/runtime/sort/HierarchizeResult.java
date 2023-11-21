package bpm.united.olap.runtime.sort;

import java.util.Collections;
import java.util.List;

import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.RuntimeFactory;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * A class which orders sets as a hierarchy
 * @author Marc Lanquetin
 *
 */
public class HierarchizeResult implements ISorterResult {
	private IVanillaLogger logger;
	
	public HierarchizeResult(IVanillaLogger logger) {
		this.logger = logger;
	}
	
	@Override
	public RunResult sortResult(List<List<MdxSet>> sets) {
		RuntimeFactory factory = RuntimeFactory.eINSTANCE;
		RunResult result = factory.createRunResult();
		
		//sort elements
		List<List<MdxSet>> sortedSets = hierarchize(sets);
		
		result.setMdxSets(sortedSets);
		
		return result;
	}

	private List<List<MdxSet>> hierarchize(List<List<MdxSet>> sets) {
		
		for(List<MdxSet> axisSets : sets) {
			
			Collections.sort(axisSets, new HierarchizeComparator());
			
		}
		
		return sets;
	}
	
}
