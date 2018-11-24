package centralized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import centralized.disrupter.CombineDisrupter;
import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;
import logist.plan.Action;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology.City;
import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.resolver.CSPResolver;
import problem.csp.resolver.SLS;
import reactive.PartialStateEvaluator;
import template.AuctionTemplate;
import util.Tuple;

public final class Planner {
	private final List<Vehicle> vehicles;
	private final PartialStateEvaluator evaluator;

	public Planner(final List<Vehicle> vehicles, PartialStateEvaluator evaluator) {
		this.vehicles = vehicles;
		this.evaluator = evaluator;
	}

	public Tuple<Tuple<Double, Double>, List<Plan>> plan(Set<Task> tasks, final int roundNumber, long timeout) {
		final List<Vehicle> vehicleList = new ArrayList<>(vehicles);

		final List<City> initialCities = new ArrayList<>();

		for (Vehicle vehicle : vehicles) {
			initialCities.add(vehicle.getCurrentCity());
		}

		// Domains creation
		Set<TaskValue> values = new HashSet<>();
		values.add(new TaskValue());
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

		// Objective function
		ObjectiveFunction<PDPVariable, TaskValue> pdpObjectiveFunction = new ObjectiveFunction<PDPVariable, TaskValue>() {
			private final List<Vehicle> vehiclesList = vehicles;
			private final float anticipationFactor = computeAnticipationFactor(roundNumber);

			@Override
			public double valueAt(Assignment<PDPVariable, TaskValue> point) {
				List<List<PDPVariable.RealizedVariable>> plans = point.getRealizations();

				double costStack = 0;
				for (int i = 0; i < vehiclesList.size(); ++i) {

					City currentCity = vehiclesList.get(i).getCurrentCity();
					for (PDPVariable.RealizedVariable realization : plans.get(i)) {
						TaskValue action = realization.getValue();

						if (action.getType() == ValueType.NONE) {
							break;
						} else if (action.getType() == ValueType.PICKUP) {
							costStack += currentCity.distanceTo(action.getTask().pickupCity)
									* vehiclesList.get(i).costPerKm();
							currentCity = action.getTask().pickupCity;
						} else if (action.getType() == ValueType.DELIVER) {
							costStack += currentCity.distanceTo(action.getTask().deliveryCity)
									* vehiclesList.get(i).costPerKm();
							currentCity = action.getTask().deliveryCity;
						}
					}
				}
				return costStack + anticipationFactor * expectedCostFor(point);
			}
		};

		// CSP creation
		ConstraintSatisfaction<PDPVariable, TaskValue> pdpConstraintSatisfaction = new ConstraintSatisfaction<PDPVariable, TaskValue>(
				plansVariables, constraints, pdpObjectiveFunction);

		// SLS creation
		final Set<Task> tasksSet = new HashSet<>(tasks);
		final List<Vehicle> cVehicles = new ArrayList<>(vehicles);
		final int variableCount = plansVariables.size();
		CSPResolver<PDPVariable, TaskValue> initialResolver = new CSPResolver<PDPVariable, TaskValue>() {
			private final int vehicleCount = cVehicles.size();

			public Assignment<PDPVariable, TaskValue> resolve(
					ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
				List<PDPVariable> cspVariables = cspProblem.getVariables();
				List<List<PDPVariable.RealizedVariable>> realizations = new ArrayList<>();

				for (int i = 0; i < vehicleCount; ++i) {
					realizations.add(new ArrayList<PDPVariable.RealizedVariable>());
				}

				Map<Integer, Integer> filledCount = new HashMap<>();
				for (int i = 0; i < vehicleCount; ++i) {
					filledCount.put(i, 0);
				}

				// Fill first vehicle plan by picking and delivering immediately tasks
				for (Task task : tasksSet) {
					int vehicleChoice = AuctionTemplate.RANDOM.nextInt(vehicleCount);
					realizations.get(vehicleChoice)
							.add(cspVariables.get(vehicleCount * vehicleChoice + filledCount.get(vehicleChoice))
									.realize(new TaskValue(task, ValueType.PICKUP)));
					realizations.get(vehicleChoice)
							.add(cspVariables.get(vehicleCount * vehicleChoice + filledCount.get(vehicleChoice))
									.realize(new TaskValue(task, ValueType.DELIVER)));

					filledCount.put(vehicleChoice, filledCount.get(vehicleChoice) + 2);
				}

				// Fill other vehicle plans with no tasks
				for (int vehicleIndex : filledCount.keySet()) {
					for (int i = filledCount.get(vehicleIndex); i < 2 * tasksSet.size(); ++i) {
						realizations.get(vehicleIndex)
								.add(cspVariables.get(vehicleCount * vehicleIndex + i).realize(new TaskValue()));
					}
				}

				return new Assignment<PDPVariable, TaskValue>(realizations);
			}
		};

		CombineDisrupter disrupter = new CombineDisrupter(pdpConstraintSatisfaction);

		SLS<PDPVariable, TaskValue> resolver = new SLS<PDPVariable, TaskValue>(initialResolver, disrupter, 0.3, 30000,
				timeout);

		Assignment<PDPVariable, TaskValue> solution = resolver.resolve(pdpConstraintSatisfaction);
		double planCost = pdpObjectiveFunction.valueAt(solution);
		double expectedCostAt = expectedCostFor(solution);
		Tuple<Double, Double> costTuple = new Tuple<>(planCost, expectedCostAt);

		List<Plan> logistPlans = PDPAssignmentConverter.toLogistPlan(solution, initialCities);

		return new Tuple<Tuple<Double, Double>, List<Plan>>(costTuple, logistPlans);
	}

	private float computeAnticipationFactor(int roundNumber) {
		if (roundNumber == -1) {
			return 0;
		} else {
			return 1;
		}
	}

	private double expectedCostFor(Assignment<PDPVariable, TaskValue> plan) {
		List<City> finalStateLocation = new ArrayList<>();

		List<List<PDPVariable.RealizedVariable>> plans = plan.getRealizations();

		for (int i = 0; i < plans.size(); ++i) {
			List<PDPVariable.RealizedVariable> vehiclePlan = plans.get(i);
			Vehicle vehicle = vehicles.get(i);

			City city = vehicle.getCurrentCity();

			for (int j = 0; j < vehiclePlan.size(); ++j) {
				PDPVariable.RealizedVariable action = vehiclePlan.get(j);
				if (action.getValue().getType() == ValueType.NONE) {
					break;
				} else if (action.getValue().getType() == ValueType.DELIVER) {
					city = action.getValue().getTask().deliveryCity;
				}
			}

			finalStateLocation.add(city);
		}
	
		// TODO add value iteration fetch
		return evaluator.valueAt(finalStateLocation);
	}

}
