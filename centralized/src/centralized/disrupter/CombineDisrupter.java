package centralized.disrupter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;
import problem.csp.resolver.Disrupter;

public class CombineDisrupter extends Disrupter<Variable<B>, Value> {
	private final Assignment<Variable<V>, Value> assignmentOld;
	private final List<Variable<V>> variables;
	private final List<Domain<V>> domains;
	private final List<Constraint<Variable<V>, Value>> constraints;
	
	public CombineDisrupter(Assignment<Variable<V>, Value> assignmentOld, List<Variable<V>> variables,
			List<Domain<V>> domains, List<Constraint<Variable<V>, Value>> constraints) {
		this.assignmentOld = assignmentOld;
		this.variables = new ArrayList<> (variables);
		this.domains = new ArrayList<> (domains);
		this.constraints = new ArrayList<> (constraints);
	}

	@Override
	public Set<Assignment<Variable<B>, Value>> disrupte(Assignment<Variable<B>, Value> assignement) {
		return null;
	}
}
