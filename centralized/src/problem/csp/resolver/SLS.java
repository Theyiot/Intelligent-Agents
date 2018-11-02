package problem.csp.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import problem.csp.ConstraintSatisfaction;
import problem.csp.ConstraintSatisfaction.CSPAssignment;

public final class SLS implements CSPResolver {
	private final int depth;
	private final CSPResolver initialResolver;
	private final double stochasticFactor;
	private final Disrupter disrupter;
	
	public SLS(CSPResolver initialResolver, Disrupter disrupter, double stochasticFactor, int depth) {
		this.initialResolver = initialResolver;
		this.disrupter = disrupter;
		this.stochasticFactor = stochasticFactor;
		this.depth = depth;
	}
	
	@Override
	public CSPAssignment resolve(ConstraintSatisfaction problem) {
		// TODO might want to add a compatibility check between problem and initialResolver
		CSPAssignment initialSolution = initialResolver.resolve(problem);
		
		return recursiveResolution(problem, 0, initialSolution);
	}
	
	private CSPAssignment recursiveResolution(ConstraintSatisfaction problem, int depth, CSPAssignment currentSolution) {
		if (depth == this.depth) {
			return currentSolution;
		} else {
			// ChooseNeighbours()
			Set<CSPAssignment> newAssignments = disrupter.disrupte(currentSolution);
			// LocalChoice() - part 1
			CSPAssignment newAssignment = chooseBest(newAssignments);
			
			// TODO WARNING should we check that the new assignment is better than the previous one ?
			// LocalChoice() - part 2 (stochasticity)
			if (new Random().nextDouble() <= stochasticFactor) {
				return recursiveResolution(problem, depth+1, newAssignment);
			} else {
				return recursiveResolution(problem, depth+1, currentSolution);
			}
		}
	}
	
	private CSPAssignment chooseBest(Set<CSPAssignment> assignments) {
		Set<CSPAssignment> minAssignmentByCost = null;
		
		double minCost = Double.MAX_VALUE;
		
		// Find smallest assignment assignment by cost
		for (CSPAssignment assignment: assignments) {
			double cost = assignment.cost();
			
			if (cost < minCost) {
				minAssignmentByCost = new HashSet<>();
				minAssignmentByCost.add(assignment);
				minCost = cost;
			} else if (cost == minCost) {
				minAssignmentByCost.add(assignment);
			} 
		}
		
		// Pick and return minimum assignment by cost
		return new ArrayList<>(minAssignmentByCost).get(new Random().nextInt(assignments.size()));
	}

}
