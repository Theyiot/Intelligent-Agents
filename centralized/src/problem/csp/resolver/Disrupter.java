package problem.csp.resolver;

import java.util.Set;

import problem.csp.ConstraintSatisfaction;
import problem.csp.ConstraintSatisfaction.CSPAssignment;

public abstract class Disrupter {
	private final ConstraintSatisfaction problem;
	
	public Disrupter(ConstraintSatisfaction problem) {
		this.problem = problem;
	}
	
	abstract public Set<CSPAssignment> disrupte(CSPAssignment assignement);

}
