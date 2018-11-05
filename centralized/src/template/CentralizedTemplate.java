package template;

//the list of imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import logist.LogistSettings;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
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
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;
import problem.csp.primitive.Variable.RealizedVariable;
import problem.csp.resolver.CSPResolver;
import problem.csp.resolver.SLS;
import centralized.PDPConstraintFactory;
import centralized.PDPVariable;
import centralized.value.TaskValue.ValueType;
import centralized.value.TaskValue;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class CentralizedTemplate implements CentralizedBehavior {

	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private long timeout_setup;
	private long timeout_plan;

	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {

		// this code is used to get the timeouts
		LogistSettings ls = null;
		try {
			ls = Parsers.parseSettings("config\\settings_default.xml");
		} catch (Exception exc) {
			System.out.println("There was a problem loading the configuration file.");
		}

		// the setup method cannot last more than timeout_setup milliseconds
		timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
		// the plan method cannot execute more than timeout_plan milliseconds
		timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);

		this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;
	}

	@Override
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		long time_start = System.currentTimeMillis();

		/* Beginning of our code */

		// Domains creation
		Set<TaskValue> values = new HashSet<>();
		values.add(new TaskValue(null, ValueType.NONE));
		for (Task task : tasks) {
			values.add(new TaskValue(task, ValueType.PICKUP));
			values.add(new TaskValue(task, ValueType.DELIVER));
		}
		Domain<TaskValue> actionDomain = new Domain<>(values);

		// Variables creation
		final List<PDPVariable> plansVariables = new ArrayList<>();
		for (Vehicle vehicle : vehicles) {
			List<PDPVariable> planVariables = new ArrayList<>();
			for (int i = 0; i < 2 * tasks.size(); ++i) {
				planVariables.add(new PDPVariable(actionDomain, i, vehicle.capacity()));
			}
			plansVariables.addAll(planVariables);
		}

		// Constraints creation
		PDPConstraintFactory constraintFactory = new PDPConstraintFactory(2 * tasks.size(), vehicles.size());
		Set<Constraint<PDPVariable, TaskValue>> constraints = constraintFactory.getAllConstraints();

		// Objective function creation
		ObjectiveFunction<PDPVariable, TaskValue> pdpObjectiveFunction = new ObjectiveFunction<PDPVariable, TaskValue>() {
			@Override
			public double valueAt(Assignment<PDPVariable, TaskValue> point) {
				// TODO Implement objective function
				return 0;
			}
		};

		// CSP creation
		ConstraintSatisfaction<PDPVariable, TaskValue> pdpConstraintSatisfaction = new ConstraintSatisfaction<PDPVariable, TaskValue>(
				plansVariables, constraints, pdpObjectiveFunction);

		// SLS creation
		final Set<Task> tasksSet = tasks.clone();
		final List<Vehicle> cVehicles = new ArrayList<>(vehicles);
		final int variableCount = plansVariables.size();
		CSPResolver<PDPVariable, TaskValue> initialResolver = new CSPResolver<PDPVariable, TaskValue>() {
			public Assignment<PDPVariable, TaskValue> resolve(ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
				List<PDPVariable> cspVariables = cspProblem.getVariables();
				List<List<PDPVariable.RealizedVariable>> realizations = new ArrayList<>();

				int i = 0;
				List<PDPVariable.RealizedVariable> realization = new ArrayList<>(2 * tasksSet.size());
				// Fill first vehicle plan by picking and delivering immediately tasks
				for (Task task : tasksSet) {
					realization.set(i, cspVariables.get(i).realize(
							new TaskValue(task, ValueType.PICKUP)));
					realization.set(i + 1, cspVariables.get(i + 1).realize(
							new TaskValue(task, ValueType.DELIVER)));
					i += 2;
				}
				realizations.add(realization);

				// Fill other vehicle plans with no tasks
				for (int rows = 0; rows < cVehicles.size() - 1; rows++) {
					realization = new ArrayList<>();
					for (int cols = 0; cols < 2 * tasksSet.size(); cols++) {
						realization.set(cols, cspVariables.get(cols).realize(
								new TaskValue(null, ValueType.NONE)));
					}
					realizations.add(realization);
				}

				return new Assignment<PDPVariable, TaskValue>(realizations);
			}
		};

		// SLS<TaskValue> resolver = new SLS<TaskValue>(initialResolver, );

		/* End of our code */

//		System.out.println("Agent " + agent.id() + " has tasks " + tasks);
		Plan planVehicle1 = naivePlan(vehicles.get(0), tasks);

		List<Plan> plans = new ArrayList<Plan>();
		plans.add(planVehicle1);
		while (plans.size() < vehicles.size()) {
			plans.add(Plan.EMPTY);
		}

		long time_end = System.currentTimeMillis();
		long duration = time_end - time_start;
		System.out.println("The plan was generated in " + duration + " milliseconds.");

		return plans;
	}

	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity)) {
				plan.appendMove(city);
			}

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path()) {
				plan.appendMove(city);
			}

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}
}
