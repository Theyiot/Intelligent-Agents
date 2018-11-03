package problem.csp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;

public final class ConstraintSatisfaction<V extends Value> {
	private final List<Variable<V>> X;
	private final Set<Constraint<V>> C;
	private final ObjectiveFunction<V> objective;
	
	
	public ConstraintSatisfaction(List<Variable<V>> X, Set<Constraint<V>> C, ObjectiveFunction<V> objective) {
		this.X = X;
		this.C = C;
		this.objective = objective;
	}
	
	public List<Variable<V>> getVariables() {
		return X;
	}
	
	public double cost(Assignment<V> assignment) {
		return objective.valueAt(assignment);
	}
	
	public boolean isSolution(Assignment<V> assignment) {
		if (!isValid(assignment)) {
			throw new IllegalArgumentException("Tried to check for an unvalid assignment");
		}
		
		for (Constraint<V> constraint: C) {
			if (!constraint.valueAt(assignment)) {
				return false;
			}
		}
		
		return true;	
	}
	
	private boolean isValid(Assignment<V> assignment) {
		if (X.size() != assignment.size()) {
			return false;
		}
		
		for (int i=0; i < assignment.size(); ++i) {
			if (!X.get(i).isRealization((assignment.get(i)))) {
				return false;
			}
		}
		
		return true;
	}
	
	private Set<Domain<V>> getDomains() {
		Set<Domain<V>> domains = new HashSet<>();
		for (Variable<V> variable: X) {
			domains.add(variable.getDomain());
		}
		return domains;
	}
}
