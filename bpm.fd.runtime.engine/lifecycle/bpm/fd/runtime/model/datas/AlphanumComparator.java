package bpm.fd.runtime.model.datas;

import java.util.Comparator;

public class AlphanumComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		if(!(o1 instanceof String) || !(o2 instanceof String)) {
			return ((Comparable) o1).compareTo(((Comparable) o2));
		}
		String s1 = (String) o1;
		String s2 = (String) o2;

		String regex1 = "\\d+";
		String regex2 = "\\d+(\\.\\d+)?";
		if(s1.matches(regex1) && s2.matches(regex1)) {
			return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
		}
		else if(s1.matches(regex2) && s2.matches(regex2)) {
			return Float.valueOf(s1).compareTo(Float.valueOf(s2));
		}
		else {
			return s1.compareTo(s2);
		}
	}
}
