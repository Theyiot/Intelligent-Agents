package problem.csp.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;
import template.AuctionTemplate;
import util.CustomQueue;

public final class SLS<B extends Variable<V>, V extends Value> implements CSPResolver<B, V> {
	private final int depth;
	private final CSPResolver<B, V> initialResolver;
	private final double stochasticFactor;
	private final Disrupter<B, V> disrupter;
	private final long timeout;
	private final int convergenceScope = 40;
	private final double convergenceCriteria = 1e-10;

	public SLS(CSPResolver<B, V> initialResolver, Disrupter<B, V> disrupter, double stochasticFactor, int depth,
			long timeout) {
		this.initialResolver = initialResolver;
		this.disrupter = disrupter;
		this.stochasticFactor = stochasticFactor;
		this.depth = depth;
		this.timeout = timeout;
	}

	@Override
	public Assignment<B, V> resolve(ConstraintSatisfaction<B, V> problem) {
		CustomQueue lastDiffs = new CustomQueue(convergenceScope);
		
		for (int i = 0; i < convergenceScope; ++i) {
			lastDiffs.add(Double.MAX_VALUE);
		}
			
		Assignment<B, V> initialSolution = initialResolver.resolve(problem);
		
		System.out.println("Initial solution\n" + initialSolution);

		Assignment<B, V> currentSolution = initialSolution;
		int currentDepth = 0;
		long currentTime = System.currentTimeMillis();
		
		
		while (lastDiffs.average() > convergenceCriteria && timeout - currentTime > 500) {
			System.out.println("SLS current depth " + currentDepth);
			// ChooseNeighbours()
			Set<Assignment<B, V>> newAssignments = disrupter.disrupte(currentSolution);
			// LocalChoice() - part 1
			Assignment<B, V> newAssignment = chooseBest(newAssignments, problem);
			
			lastDiffs.add(problem.cost(currentSolution) - problem.cost(newAssignment));
			// LocalChoice() - part 2 (stochasticity)
			if (AuctionTemplate.RANDOM.nextDouble() <= stochasticFactor) {
				currentSolution = newAssignment;
			}
			
			++currentDepth;
		}

		return currentSolution;
	}

	private Assignment<B, V> recursiveResolution(ConstraintSatisfaction<B, V> problem, int depth,
			Assignment<B, V> currentSolution) {
		System.out.println("SLS depth: " + depth);
		if (depth == this.depth) {
			return currentSolution;
		} else {
			// ChooseNeighbours()
			Set<Assignment<B, V>> newAssignments = disrupter.disrupte(currentSolution);
			// LocalChoice() - part 1
			Assignment<B, V> newAssignment = chooseBest(newAssignments, problem);

			// TODO WARNING should we check that the new assignment is better than the
			// previous one ?
			// LocalChoice() - part 2 (stochasticity)
			if (AuctionTemplate.RANDOM.nextDouble() <= stochasticFactor) {
				return recursiveResolution(problem, depth + 1, newAssignment);
			} else {
				return recursiveResolution(problem, depth + 1, currentSolution);
			}
		}
	}

	private Assignment<B, V> chooseBest(Set<Assignment<B, V>> assignments,
			ConstraintSatisfaction<B, V> objectiveFunction) {
		Set<Assignment<B, V>> minAssignmentByCost = null;

		double minCost = Double.MAX_VALUE;

		// Find smallest assignment assignment by cost
		for (Assignment<B, V> assignment : assignments) {
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
		return new ArrayList<>(minAssignmentByCost).get(AuctionTemplate.RANDOM.nextInt(minAssignmentByCost.size()));
	}

}
