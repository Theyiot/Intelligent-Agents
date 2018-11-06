package problem.csp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;

public final class ConstraintSatisfaction<B extends Variable<V>, V extends Value> {
	private final List<B> X;
	private final Set<Constraint<B, V>> C;
	private final ObjectiveFunction<B, V> objective;
	
	
	public ConstraintSatisfaction(List<B> X, Set<Constraint<B, V>> C, ObjectiveFunction<B, V> objective) {
		this.X = X;
		this.C = C;
		this.objective = objective;
	}
	
	public List<B> getVariables() {
		return X;
	}
	
	public double cost(Assignment<B, V> assignment) {
		return objective.valueAt(assignment);
	}
	
	public boolean isSolution(Assignment<B, V> assignment) {
		if (!isValid(assignment)) {
			throw new IllegalArgumentException("Tried to check for an unvalid assignment with size " + assignment.getTotalSize() + " expected " + X.size());
		}
		
		for (Constraint<B, V> constraint: C) {
			if (!constraint.valueAt(assignment)) {
				return false;
			}
		}
		
		return true;	
	}
	
	private boolean isValid(Assignment<B, V> assignment) {
		if (X.size() != assignment.getTotalSize()) {
			return false;
		}
		
		/*for (int i=0; i < assignment.size(); ++i) {
			if (!X.get(i).isRealization((assignment.get(i)))) {
				return false;
			}
		}*/
		
		return true;
	}
	
	private Set<Domain<V>> getDomains() {
		Set<Domain<V>> domains = new HashSet<>();
		for (B variable: X) {
			domains.add(variable.getDomain());
		}
		return domains;
	}
}
