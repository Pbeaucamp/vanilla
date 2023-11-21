package bpm.gateway.runtime2.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.gateway.runtime2.internal.Row;

/**
 * Used to sort data from a lookup secondary input
 * If we browse all data for lookup it's too slow
 * @author Marc Lanquetin
 *
 */
public class LookupDataSorter {

//	private static int i = 0;
//	
//	public static synchronized HashMap<Integer, List<LookUpRow>> sortData(List<Row> rows, final Integer[] masterMap) {
//		
//		HashMap<Integer, List<LookUpRow>> map = new HashMap<Integer, List<LookUpRow>>();
//		
//		for(i = 0 ; i < masterMap.length ; i++) {
//			
//			if(masterMap[i] == null) {
//				continue;
//			}
//			
//			Collections.sort(rows, new Comparator<Row>() {
//				@Override
//				public int compare(Row o1, Row o2) {
//					try {
//						return o1.get(masterMap[i]).toString().compareTo(o2.get(masterMap[i]).toString());
//					} catch (Exception e) {
//						e.printStackTrace();
//						return o1.get(masterMap[i]).toString().compareTo(o2.get(masterMap[i]).toString());
//
//					}
//					
//				}
//			});
//			
//			List<LookUpRow> l = new ArrayList<LookUpRow>();
//			for(Row r : rows) {
//				l.add(new LookUpRow(r.get(masterMap[i]), r));
//			}
//			map.put(masterMap[i], l);
//		}
//		
//		return map;
//	}

	public static List<Row> sortDatas(List<Row> secondaryInputDatas, final Integer[] masterMap) {
		Collections.sort(secondaryInputDatas, new Comparator<Row>() {
			@Override
			public int compare(Row o1, Row o2) {
				int res = 0;
				for(int i = 0 ; i < masterMap.length ; i++) {
					if(masterMap[i] == null) {
						continue;
					}
					
					res = o1.get(masterMap[i]).toString().compareTo(o2.get(masterMap[i]).toString());
					
					if(res != 0) {
						return res;
					}
				}
				
				return res;
				
			}
		});
		
		return secondaryInputDatas;
	}
	
}
