package template.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class ListOp {
	
	private ListOp() {
		
	}
	
	public static <T> void merge(List<T> l1, List<T> l2, Comparator<T> comparator) {
		for (int index1 = 0, index2 = 0; index2 < l2.size(); index1++) {
			if (index1 == l1.size() || comparator.compare(l1.get(index1), l2.get(index2)) == 1) {
				l1.add(index1, l2.get(index2++));
			}
		}

	}
	
	public static <T> List<T> fastMerge(Set<List<T>> lists, Comparator<T> comparator) {
	    int totalSize = 0; // every element in the set
	    for (List<T> l : lists) {
	        totalSize += l.size();
	    }

	    List<T> result = new ArrayList<T>(totalSize);

	    List<T> lowest;

	    while (result.size() < totalSize) { // while we still have something to add
	        lowest = null;

	        for (List<T> l : lists) {
	            if (! l.isEmpty()) {
	                if (lowest == null) {
	                    lowest = l;
	                } else if (comparator.compare(l.get(0), lowest.get(0)) <= 0) {
	                    lowest = l;
	                }
	            }
	        }

	        result.add(lowest.remove(0));
	    }

	    return result;
	}

}
