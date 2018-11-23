package template;

//the list of imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import auction.Bidder;
import centralized.PDPAssignmentConverter;
import centralized.PDPConstraintFactory;
import centralized.PDPVariable;
import centralized.Planner;
import centralized.disrupter.CombineDisrupter;
import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;
import logist.LogistSettings;
import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.config.Parsers;
import logist.agent.Agent;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;
import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.resolver.CSPResolver;
import problem.csp.resolver.SLS;
import util.Tuple;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 * 
 */
@SuppressWarnings("unused")
public class AuctionTemplate implements AuctionBehavior {

	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private Random random;
	private List<Vehicle> vehicles;

	private Bidder bidder;
	private Planner planner; 
	private long setupEnd;
	private long lastBidEnd;
	private Set<Task> ownedTasks;

	private long timeout_setup;
	private long timeout_plan;
	private long timeout_bid;
	private int roundNumber = 0;

	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {

		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
		this.vehicles = agent.vehicles();

		long seed = agent.id();
		this.random = new Random(seed);
		this.planner = new Planner(vehicles);
		this.bidder = new Bidder(planner);
		ownedTasks = new HashSet<>();

		// this code is used to get the timeouts
		LogistSettings ls = null;
		try {
			ls = Parsers.parseSettings("config/settings_default.xml");
		} catch (Exception exc) {
			System.out.println("There was a problem loading the configuration file.");
		}

		// the setup method cannot last more than timeout_setup milliseconds
		timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
		// the plan method cannot execute more than timeout_plan milliseconds
		timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
		// the biding method cannot execute more than timeout_plan
		timeout_bid = ls.get(LogistSettings.TimeoutKey.BID);

		// This lines should be the last setup line
		setupEnd = System.currentTimeMillis();
	}

	@Override
	public void auctionResult(Task previous, int winner, Long[] bids) {
		bidder.acknowledgeBidResult(previous, winner, bids);
	}

	@Override
	public Long askPrice(Task task) {
		long currentTime = System.currentTimeMillis();
		
		long bid = bidder.bid(task, currentTime, timeout_bid);
		
		return bid;
	}

	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		long timeout = System.currentTimeMillis() + timeout_plan;

		// Compute "best" plan with currently owned task (room for optimization here)
		Set<Task> currentTasks = new HashSet<>(tasks);
		// -1 convention for no future anticipation
		Tuple<Tuple<Double, Double>, List<Plan>> optimizedPlan = planner.plan(currentTasks, -1, timeout);
		
		return optimizedPlan.getRight();
	}
}
