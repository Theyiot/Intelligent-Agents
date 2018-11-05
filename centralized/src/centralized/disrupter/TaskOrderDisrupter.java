package centralized.disrupter;

import java.util.Set;

import centralized.PDPVariable;
import centralized.value.TaskValue;
import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.resolver.Disrupter;

public abstract class TaskOrderDisrupter extends Disrupter<PDPVariable, TaskValue> {

	public TaskOrderDisrupter(ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
		super(cspProblem);
	}

	@Override
	public Set<Assignment<PDPVariable, TaskValue>> disrupte(Assignment<PDPVariable, TaskValue> assignement) {
		return null;
	}
}
