package util;

import java.util.ArrayList;
import java.util.List;

public class CustomQueue {
	private final List<Double> memory;
	private int load;
	private int capacity; 
	
	public CustomQueue(int size) {
		this.memory = new ArrayList<>(size);
		this.load = 0;
		this.capacity = size;
	}
	
	public void add(double element) {
		if (load < capacity) {
			memory.add(element);
			load += 1;
		} else {
			memory.remove(load - 1);
			memory.add(0, element);
		}
	}
	
	public double average() {
		return Math.average(memory);
	}

}
