package auction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import centralized.Planner;
import logist.plan.Plan;
import logist.task.Task;
import util.Tuple;

public class Bidder {
	private float greedFactor;
	private int roundNumber;
	private Set<Task> ownedTasks;
	private Planner planner;

	public Bidder(Planner planner) {
		this.greedFactor = 1;
		this.roundNumber = 0;
		this.ownedTasks = new HashSet<>();
		this.planner = planner;
	}

	public long bid(Task newTask, long startTime, long bidTimeout) {
		// Compute "best" plan with owned task and current biding task
		Set<Task> fullTasks = new HashSet<>();
		fullTasks.addAll(ownedTasks);
		fullTasks.add(newTask);
		Tuple<Tuple<Double, Double>, List<Plan>> fullPlanResult = planner.plan(fullTasks, roundNumber, (long) Math.floor(startTime + bidTimeout / 2.0));
		double fullPlanCost = fullPlanResult.getLeft().getLeft();
		double fullPlanFutureCostEstimation = fullPlanResult.getLeft().getRight();
		List<Plan> fullPlan = fullPlanResult.getRight(); 

		// Compute "best" plan with currently owned task (room for optimization here)
		Set<Task> currentTasks = new HashSet<>();
		currentTasks.addAll(ownedTasks);
		Tuple<Tuple<Double, Double>, List<Plan>> planResult = planner.plan(currentTasks, roundNumber, (long) Math.floor(startTime + bidTimeout));
		double planCost = planResult.getLeft().getLeft();
		double planFutureCostEstimation = planResult.getLeft().getRight();
		List<Plan> plan = planResult.getRight();
		
		if (fullPlanCost < planCost) {
			System.out.println("Warning plan with new task (" + fullPlanCost + ") was cheaper than plan with only owned tasks (" + planCost + ")");
		}
		
		double respectiveDiff = (fullPlanCost - planCost) + (fullPlanFutureCostEstimation - planFutureCostEstimation);
		double bid = greedFactor * Math.max(respectiveDiff, 0); 
		
		return (long) Math.ceil(bid);
	}

	public void acknowledgeBidResult(Task previous, int winner, Long[] bids) {

	}

}
