package problem.csp.resolver;

import java.util.Set;

import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;

public abstract class Disrupter<V extends Value> {
	private final ConstraintSatisfaction<V> problem;
	
	public Disrupter(ConstraintSatisfaction<V> problem) {
		this.problem = problem;
	}
	
	abstract public Set<Assignment<V>> disrupte(Assignment<V> assignement);

}
