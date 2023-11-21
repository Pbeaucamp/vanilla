package bpm.united.olap.runtime.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.tools.AlphanumComparator;

/**
 * Used to sort members
 * @author Marc Lanquetin
 *
 */
public class MemberSorter implements IElementSorter {
	
	public static class ElementDefinitionComparator implements Comparator<ElementDefinition>{

		@Override
		public int compare(ElementDefinition o1, ElementDefinition o2) {
			
			return o1.getUname().compareTo(o2.getUname());
		}
		
	}
	

	@Override
	public List sort(List objects) {
		
		List<Member> members = (List<Member>) objects;
		
		Collections.sort(members, new MemberComparator());
		
		return members;
	}

	/**
	 * Compare two members and sort them
	 * @author Marc Lanquetin
	 *
	 */
	private class MemberComparator implements Comparator<Member> {

		private AlphanumComparator comparator = new AlphanumComparator();
		
		@Override
		public int compare(Member member1, Member member2) {
			
			String value1 = null;
			String value2 = null;
			
			if(member1.getOrderValue() != null) {
				value1 = member1.getOrderValue();
			}
			else {
				value1 = member1.getName();
			}
			
			if(member2.getOrderValue() != null) {
				value2 = member2.getOrderValue();
			}
			else {
				value2 = member2.getName();
			}
			
			return comparator.compare(value1, value2);
		}
		
	}
	
	
}
