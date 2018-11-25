package auction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import centralized.Planner;
import logist.plan.Plan;
import logist.task.Task;
import util.Tuple;

public class Bidder {
	private double greedFactor;
	private int roundNumber;
	private Set<Task> ownedTasks;
	private Planner planner;
	private int agentId;
	private long lastBid;

	public Bidder(Planner planner, int agentId) {
		this.greedFactor =  1.1;
		this.roundNumber = 0;
		this.ownedTasks = new HashSet<>();
		this.planner = planner;
		this.agentId = agentId;
	}

	public long bid(Task newTask, long startTime, long bidTimeout) {
		// Compute "best" plan with owned task and current biding task
		Set<Task> fullTasks = new HashSet<>();
		fullTasks.addAll(ownedTasks);
		fullTasks.add(newTask);
		Tuple<Tuple<Double, Double>, List<Plan>> fullPlanResult = planner.plan(fullTasks, roundNumber, (long) Math.floor(startTime + bidTimeout / 2.0), 100);
		double fullPlanCost = fullPlanResult.getLeft().getLeft();
		double fullPlanFutureCostEstimation = fullPlanResult.getLeft().getRight();
		List<Plan> fullPlan = fullPlanResult.getRight(); 

		// Compute "best" plan with currently owned task (room for optimization here)
		Set<Task> currentTasks = new HashSet<>();
		currentTasks.addAll(ownedTasks);
		Tuple<Tuple<Double, Double>, List<Plan>> planResult = planner.plan(currentTasks, roundNumber, (long) Math.floor(startTime + bidTimeout), 100);
		double planCost = planResult.getLeft().getLeft();
		double planFutureCostEstimation = planResult.getLeft().getRight();
		List<Plan> plan = planResult.getRight();
		
		if (fullPlanCost < planCost) {
			//System.out.println("Warning plan with new task (" + fullPlanCost + ") was cheaper than plan with only owned tasks (" + planCost + ")");
		}
		
		//double respectiveDiff = fullPlanCost - planCost + (fullPlanFutureCostEstimation - planFutureCostEstimation);
		double respectiveDiff = fullPlanCost - planCost;
		double bid = greedFactor * Math.max(respectiveDiff, 0);
		
		if (bid == 0) {
			bid = lastBid;
		}
		
		long finalBid = (long) Math.ceil(bid);
		lastBid = finalBid;
		
		return finalBid;
	}

	public void acknowledgeBidResult(Task previous, int winner, Long[] bids) {
		if (winner == agentId) {
			ownedTasks.add(previous);
		}
		
		long minBid = Long.MAX_VALUE;
		
		for (int i=0; i < bids.length; ++i) {
			minBid = Math.min(bids[i], minBid);
		}
		
		if (minBid != lastBid) {
			double bidRatio = ((double) minBid) / ((double) lastBid);
			greedFactor = (bidRatio - 0.01) * greedFactor;
		} else {
			greedFactor = greedFactor * 1.05;
		}	
		
		// Makes sure we do not have a deficit greed factor
		greedFactor = Math.max(1, greedFactor);
		greedFactor = Math.min(1.5, greedFactor);

		roundNumber += 1;
	}

}
