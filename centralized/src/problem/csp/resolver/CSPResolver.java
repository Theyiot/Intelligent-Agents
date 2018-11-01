package problem.csp.resolver;

import problem.csp.ConstraintSatisfaction;
import problem.csp.ConstraintSatisfaction.CSPAssignment;

public interface CSPResolver {
	
	public CSPAssignment resolve(ConstraintSatisfaction cspProblem);

}
