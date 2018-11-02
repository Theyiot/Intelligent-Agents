package problem.csp.resolver;

import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;

public interface CSPResolver<V extends Value> {
	
	public Assignment<V> resolve(ConstraintSatisfaction<V> cspProblem);

}
