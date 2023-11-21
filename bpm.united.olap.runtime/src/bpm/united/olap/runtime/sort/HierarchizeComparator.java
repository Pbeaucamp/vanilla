package bpm.united.olap.runtime.sort;

import java.util.Comparator;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.tools.AlphanumComparator;

public  class HierarchizeComparator implements Comparator<MdxSet> {

	private AlphanumComparator advancedComparator = new AlphanumComparator();
	
	@Override
	public int compare(MdxSet arg0, MdxSet arg1) {
		
		
		for(int i = 0 ; i < arg0.getElements().size() ; i++) {
			
			ElementDefinition elem0 = arg0.getElements().get(i);
			ElementDefinition elem1 = arg1.getElements().get(i);
			
			if (elem0.getUname().equals(elem1.getUname())){
				continue;
			}

			if(elem0.getUname().startsWith(elem1.getUname())) {
				return 1;
			}
			else if(elem1.getUname().startsWith(elem0.getUname())) {
				return -1;
			}
			
			if(elem0 instanceof Member) {
				Member member0 = (Member) elem0;
				Member member1 = (Member) elem1;
				
				String uname0 = "";
				String uname1 = "";
				if(member0.getOrderUname() != null) {
					uname0 = member0.getOrderUname();
				}
				else {
					uname0 = member0.getUname();
				}
				
				if(member1.getOrderUname() != null) {
					uname1 = member1.getOrderUname();
				}
				else {
					uname1 = member1.getUname();
				}
				
				String[] unameParts0 = uname0.split("\\]\\.\\[");
				String[] unameParts1 = uname1.split("\\]\\.\\[");
				
				for(int j = 0 ; j < unameParts0.length ; j++) {
					
					if(unameParts1.length > j ) {
						
						int res = advancedComparator.compare(unameParts0[j].replace("[","").replace("]", ""), unameParts1[j].replace("[","").replace("]", ""));
						if(res > 0) {
							return 1;
						}
						else if(res < 0) {
							return -1;
						}
					}
					
				}
				
				if(unameParts0.length > unameParts1.length) {
					return 1;
				}
				else if(unameParts1.length > unameParts0.length) {
					return -1;
				}
				
			}
			
			else if(elem0 instanceof Measure) {
				Measure member0 = (Measure) elem0;
				Measure member1 = (Measure) elem1;
				
				String uname0 = member0.getName();
				String uname1 = member1.getName();
				
				return advancedComparator.compare(uname0, uname1);
			}
		}
		
		if (arg0.getMeasure() != null && arg1.getMeasure() != null){
			String uname0 = arg0.getMeasure().getName();
			String uname1 = arg1.getMeasure().getName();
			return advancedComparator.compare(uname0, uname1);
//			return arg0.getMeasure().getName().compareTo(arg1.getMeasure().getName());
		}
		
		return 0;
	}
}
