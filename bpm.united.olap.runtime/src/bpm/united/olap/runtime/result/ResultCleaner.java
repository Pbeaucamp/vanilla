package bpm.united.olap.runtime.result;

import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.RunResult;

/**
 * Remove all non-visible elements in the result item and set them visible on the dimension for the next queries
 * @author Marc Lanquetin
 *
 */
public class ResultCleaner implements IResultCleaner {

	@Override
	public RunResult cleanResults(RunResult result) {
		
		for(List<MdxSet> sets : result.getMdxSets()) {
			
			List<ElementDefinition> toRm = new ArrayList<ElementDefinition>();
			
			int previousSize = -1;
			for(MdxSet set : sets) {
				
				for(ElementDefinition def : set.getElements()) {
					
					if(!def.isIsVisible()) {
						if(set.getElements().size() > previousSize || previousSize == -1) {
							toRm.add(def);
						}
						else {
							toRm.remove(def);
						}
						
						def.setIsVisible(true);
					}
					else if(set.getElements().size() == previousSize || previousSize == -1) {
						toRm.remove(def);
					}
				}
				
				for(ElementDefinition def : toRm) {
					
					set.getElements().remove(def);
				}
				
				previousSize = set.getElements().size();
			}
			
		}
		
		return result;
	}

}
