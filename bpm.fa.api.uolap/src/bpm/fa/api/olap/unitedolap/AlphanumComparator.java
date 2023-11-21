package bpm.fa.api.olap.unitedolap;

/*
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

import java.util.Comparator;

/**
 * This is an updated version with enhancements made by Daniel Migowski, Andre Bogus, and David Koelle
 * 
 * To convert to use Templates (Java 1.5+): - Change "implements Comparator" to "implements Comparator<String>" - Change "compare(Object o1, Object o2)" to "compare(String s1, String s2)" - Remove the type checking and casting in compare().
 * 
 * To use this class: Use the static "sort" method from the java.util.Collections class: Collections.sort(your list, new AlphanumComparator());
 */
public class AlphanumComparator implements Comparator {
	private final boolean isDigit(char ch) {
		return ch >= 48 && ch <= 57;
	}

	/** Length of string is passed in for improved efficiency (only need to calculate it once) **/
	private final String getChunk(String s, int slength, int marker) {
		StringBuilder chunk = new StringBuilder();
		char c = s.charAt(marker);
		chunk.append(c);
		marker++;
		if(isDigit(c)) {
			while(marker < slength) {
				c = s.charAt(marker);
				if(!isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		}
		else {
			while(marker < slength) {
				c = s.charAt(marker);
				if(isDigit(c))
					break;
				chunk.append(c);
				marker++;
			}
		}
		return chunk.toString();
	}

	public int compare(Object o1, Object o2) {
		if(!(o1 instanceof String) || !(o2 instanceof String)) {
			if(o1 == null) {
				o1 = "NULL";
			}
			if(o2 == null) {
				o2 = "NULL";
			}
			return ((Comparable) o1).compareTo(((Comparable) o2));
		}
		String s1 = (String) o1;
		String s2 = (String) o2;

		String regex = "\\d+";
		if(s1.matches(regex) && s2.matches(regex)) {

			return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
		}
		else {
			return s1.compareToIgnoreCase(s2);
		}
	}
}
