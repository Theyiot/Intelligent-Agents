package util;

import java.util.List;

public final class Math {
	
	private Math() {
		
	}
	
	public static double average(List<Double> values) {
		double stack = 0.0;
		double elementCount = (double) values.size();
		
		for (int i=0; i < elementCount; ++i) {
			stack += values.get(i);
		}
		
		return stack / elementCount;
	}

}
