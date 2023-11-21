package bpm.fa.ui.composite;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.ui.Messages;

public class ItemElementHelper {

	
	public static String getItemUniqueName(OLAPCube cube, Object item) throws Exception{
		
		OLAPMember mb = null;
		if (item instanceof Dimension){
			
			if (((Dimension) item).getHierarchies().size() == 1 ){
				mb = ((Dimension) item).getHierarchies().iterator().next().getDefaultMember();
			}
			else{
				throw new Exception(Messages.ItemElementHelper_0 + ((Dimension) item).getUniqueName());
			}
		}
		else if (item instanceof Hierarchy){
			mb = ((Hierarchy)item).getDefaultMember();
		}
		else if (item instanceof OLAPMember){
			mb = (OLAPMember)item;
		}
		else if (item instanceof Measure){
			return ((Measure)item).getUniqueName();
		}
		
		return mb.getUniqueName();
		
		
	}

	public static Hierarchy getHierarchy(OLAPCube cube, String uniqueMember) {
		if (uniqueMember.startsWith("[Measures]")){ //$NON-NLS-1$
			return null;
		}
		for(Dimension d  : cube.getDimensions()){
			for(Hierarchy h : d.getHierarchies()){
				if (uniqueMember.startsWith(h.getUniqueName())){
					return h;
				}
			}
		}
		
		return null;
	}
}
