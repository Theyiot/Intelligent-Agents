package centralized;

import centralized.value.TaskValue;
import problem.csp.primitive.Domain;
import problem.csp.primitive.Variable;

public class PDPVariable extends Variable<TaskValue> {
	private final int stepTime;
	
	public PDPVariable(Domain<TaskValue> domain, int stepTime) {
		super(domain);
		this.stepTime = stepTime;
	}
	
	public int getStepTime() {
		return stepTime;
	}
	
}
