package template;

//the list of imports
import java.util.ArrayList;
import java.util.Arrays;
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
import reactive.PartialStateEvaluator;
import reactive.TrivialPartialStateEvaluator;
import reactive.algorithm.ValueIteration;
import reactive.world_representation.Transitioner;
import reactive.world_representation.action.ActionType;
import reactive.world_representation.action.TaskAction;
import reactive.world_representation.state.AuctionedTask;
import reactive.world_representation.state.State;
import util.Tuple;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 * 
 */
@SuppressWarnings("unused")
public class AuctionTemplate implements AuctionBehavior {
	public static Random RANDOM;

	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
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
		long setupStart = System.currentTimeMillis();

		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
		this.vehicles = agent.vehicles();

		long seed = agent.id();
		//PartialStateEvaluator evaluator = new PartialStateEvaluator(valueIteration(0.9), distribution, topology.cities().size());
		PartialStateEvaluator evaluator = new TrivialPartialStateEvaluator(distribution);
		this.planner = new Planner(vehicles, evaluator);
		this.bidder = new Bidder(planner, agent.id());
		ownedTasks = new HashSet<>();

		RANDOM = new Random(seed);

		// this code is used to get the timeouts
		LogistSettings ls = null;
		try {
			ls = Parsers.parseSettings("./config/settings_auction.xml");
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
		Tuple<Tuple<Double, Double>, List<Plan>> optimizedPlan = planner.plan(currentTasks, -1, timeout, 300);
	
		return optimizedPlan.getRight();
	}

	private Set<State> valueIteration(double discount) {
		
		List<City> cities = topology.cities();
		List<State> states = new ArrayList<>();
		List<TaskAction> actions = new ArrayList<>();

		// Create all states
		List<List<Tuple<Vehicle, City>>> allTuples = generateAllStates(agent.vehicles(), cities);

		List<AuctionedTask> tasks = new ArrayList<>();
		for (City fromCity : cities) {
			for (City toCity : cities) {
				if (!fromCity.equals(toCity)) {
					tasks.add(new AuctionedTask(fromCity, toCity));
				}
			}
		}

		for (List<Tuple<Vehicle, City>> tuple : allTuples) {
			for (AuctionedTask task : tasks) {
				states.add(new State(topology, distribution, tuple, task));
			}
		}

		// Create all actions
		for (Vehicle vehicle : agent.vehicles()) {
			actions.add(new TaskAction(vehicle));
		}

		// Value iteration algorithm
		ValueIteration valueIterationAlgo = new ValueIteration(states, actions, new Transitioner(states), 1e-10,
				discount);
		valueIterationAlgo.valueIteration();
		
		return new HashSet<>(states);
	}
	
	private List<List<Tuple<Vehicle, City>>> generateAllStates(List<Vehicle> vehicles, List<City> cities) {
		List<List<Tuple<Vehicle, City>>> allCombinations = new ArrayList<> ();
		for(Vehicle vehicle: vehicles) {
			List<Tuple<Vehicle, City>> tuples = new ArrayList<> ();
			//Generates all the tuples
			for(City city: cities) {
				tuples.add(new Tuple<>(vehicle, city));
			}
			
			List<List<Tuple<Vehicle, City>>> combinationsTemp = new ArrayList<> (allCombinations);
			allCombinations = new ArrayList<> ();
			//Add all the tuples (of the new vehicle) for all the existing combinations
			for(List<Tuple<Vehicle, City>> combinations : combinationsTemp) {
				for(Tuple<Vehicle, City> tuple : tuples) {
					List<Tuple<Vehicle, City>> combinationsToAdd = new ArrayList<> (combinations);
					combinationsToAdd.add(tuple);
					allCombinations.add(combinationsToAdd);
				}
			}
			
			//First iteration needs to manually add the tasks
			if(allCombinations.size() == 0) {
				for(Tuple<Vehicle, City> tuple : tuples) {
					allCombinations.add(Arrays.asList(tuple));	
				}
			}
		}
		return allCombinations;
	}
}
