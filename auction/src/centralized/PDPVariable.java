package centralized;

import centralized.value.TaskValue;
import problem.csp.primitive.Domain;
import problem.csp.primitive.Variable;

public class PDPVariable extends Variable<TaskValue> {
	private final int stepTime;
	private final int capacity;
	
	public PDPVariable(Domain<TaskValue> domain, int stepTime, int capacity) {
		super(domain);
		this.stepTime = stepTime;
		this.capacity = capacity;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getStepTime() {
		return stepTime;
	}
	
}
