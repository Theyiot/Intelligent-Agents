package problem.csp.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;

public final class SLS<V extends Value> implements CSPResolver<V> {
	private final int depth;
	private final CSPResolver<V> initialResolver;
	private final double stochasticFactor;
	private final Disrupter<V> disrupter;
	
	public SLS(CSPResolver<V> initialResolver, Disrupter<V> disrupter, double stochasticFactor, int depth) {
		this.initialResolver = initialResolver;
		this.disrupter = disrupter;
		this.stochasticFactor = stochasticFactor;
		this.depth = depth;
	}
	
	@Override
	public Assignment<V> resolve(ConstraintSatisfaction<V> problem) {
		// TODO might want to add a compatibility check between problem and initialResolver
		Assignment<V> initialSolution = initialResolver.resolve(problem);
		
		return recursiveResolution(problem, 0, initialSolution);
	}
	
	private Assignment<V> recursiveResolution(ConstraintSatisfaction<V> problem, int depth, Assignment<V> currentSolution) {
		if (depth == this.depth) {
			return currentSolution;
		} else {
			// ChooseNeighbours()
			Set<Assignment<V>> newAssignments = disrupter.disrupte(currentSolution);
			// LocalChoice() - part 1
			Assignment<V> newAssignment = chooseBest(newAssignments, problem);
			
			// TODO WARNING should we check that the new assignment is better than the previous one ?
			// LocalChoice() - part 2 (stochasticity)
			if (new Random().nextDouble() <= stochasticFactor) {
				return recursiveResolution(problem, depth+1, newAssignment);
			} else {
				return recursiveResolution(problem, depth+1, currentSolution);
			}
		}
	}
	
	private Assignment<V> chooseBest(Set<Assignment<V>> assignments, ConstraintSatisfaction<V> objectiveFunction) {
		Set<Assignment<V>> minAssignmentByCost = null;
		
		double minCost = Double.MAX_VALUE;
		
		// Find smallest assignment assignment by cost
		for (Assignment<V> assignment: assignments) {
			double cost = objectiveFunction.cost(assignment);
			
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
