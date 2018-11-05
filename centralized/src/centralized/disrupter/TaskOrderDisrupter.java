package centralized.disrupter;

import logist.simulation.Vehicle;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;
import problem.csp.resolver.Disrupter;

public abstract class TaskOrderDisrupter extends Disrupter<Variable<B>, Value> {
	private final Assignment<Variable<V>, Value> assignment;
	private final Vehicle v1;
	private final Vehicle v2;
	
	public TaskOrderDisrupter(Assignment<Variable<V>, Value> assignment, Vehicle v1, Vehicle v2) {
		this.assignment = assignment;
		this.v1 = v1;
		this.v2 = v2;
	}

	@Override
	public Set<Assignment<Variable<B>, Value>> disrupte(Assignment<Variable<B>, Value> assignement) {
		return null;
	}
}

}
