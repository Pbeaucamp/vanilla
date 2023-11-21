package bpm.data.viz.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.data.viz.core.preparation.PreparationRuleSort.SortType;

public class RuleUtils {
	
//	public static <T extends Comparable<T>> void keySort(final List<T> key, SortType type, List<?>... lists) {
//		keySort(key, type, Arrays.asList(lists));
//	}

	public static void keySort(final List<Serializable> key, final SortType type, Collection<List<Serializable>> lists) {
		// Create a List of indices
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < key.size(); i++)
			indices.add(i);

		// Sort the indices list based on the key
		Collections.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				if(type == SortType.ASC) {
					return ((Comparable)key.get(i)).compareTo(key.get(j));
				}
				return ((Comparable)key.get(j)).compareTo(key.get(i));
			}
		});

		// Create a mapping that allows sorting of the List by N swaps.
		Map<Integer, Integer> swapMap = new HashMap<Integer, Integer>(indices.size());

		// Only swaps can be used b/c we cannot create a new List of type <?>
		for(int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while(swapMap.containsKey(k))
				k = swapMap.get(k);

			swapMap.put(i, k);
		}

		// for each list, swap elements to sort according to key list
		for(Map.Entry<Integer, Integer> e : swapMap.entrySet())
			for(List<?> list : lists)
				Collections.swap(list, e.getKey(), e.getValue());
	}
	
}
