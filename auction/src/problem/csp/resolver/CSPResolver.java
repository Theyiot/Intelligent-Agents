package problem.csp.resolver;

import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;

public interface CSPResolver<B extends Variable<V>, V extends Value> {
	
	public Assignment<B, V> resolve(ConstraintSatisfaction<B, V> cspProblem);
	
}
