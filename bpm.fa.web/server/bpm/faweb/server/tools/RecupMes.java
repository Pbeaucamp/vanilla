package bpm.faweb.server.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.faweb.server.beans.InfosOlap;
import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.faweb.shared.infoscube.ItemMesGroup;

public class RecupMes {
	
	public static List<ItemMesGroup> recupMes(OLAPCube c, FaWebSession session, InfosOlap infos){	
//		System.out.println("debug LCA recupMes, c: " + c.toString());
//		System.out.println("debug LCA recupMes, c.getMeasures(): " + c.getMeasures().toArray().toString());
		
		List<ItemMesGroup> lroot = new ArrayList<ItemMesGroup>();
		List<String> measures = new ArrayList<String>();
		
		Collection<MeasureGroup> measGr = c.getMeasures();
		
		for(MeasureGroup gr : measGr) {
			ItemMesGroup measureGroup = new ItemMesGroup(gr.getName(), gr.getUniqueName());
			
			Collection<Measure> meas = gr.getMeasures();
			
			for(Measure mes : meas) {
    			measures.add(mes.getName());
    			measureGroup.addChild(new ItemMes(mes.getName(), mes.getUniqueName()));
    		}
    		
			lroot.add(measureGroup);
		}
		
		infos.setMeasures(measures);
		return lroot;
	}
}


