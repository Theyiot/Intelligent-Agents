package problem.csp.resolver;

import java.util.HashSet;
import java.util.Set;

import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;

public abstract class Disrupter<B extends Variable<V>, V extends Value> {
	protected final ConstraintSatisfaction<B, V> problem;
	
	public Disrupter(ConstraintSatisfaction<B, V> problem) {
		this.problem = problem;
	}
	
	abstract public Set<Assignment<B, V>> disrupte(Assignment<B, V> assignement);

	public Set<Assignment<B, V>> validateAssignments(Set<Assignment<B, V>> assignments) {
		Set<Assignment<B, V>> validAssignments = new HashSet<> ();
		for(Assignment<B, V> assignment : assignments) {
			if(problem.isSolution(assignment)) {
				validAssignments.add(new Assignment<B, V> (assignment));
			}
		}
		return validAssignments;
	}
}
