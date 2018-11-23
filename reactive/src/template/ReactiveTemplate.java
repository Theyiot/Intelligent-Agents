package template;

import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.algorithm.ValueIteration;
import template.util.Tuple;
import template.world_representation.action.DeliverAction;
import template.world_representation.action.MoveAction;
import template.world_representation.action.StateAction;
import template.world_representation.state.EmptyState;
import template.world_representation.state.State;
import template.world_representation.state.TaskState;

public class ReactiveTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;
	private List<City> cities;
	
	private Map<City, EmptyState> cityToEmptyState;
	private Map<City, Map<City, TaskState>> cityToTaskState;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		cities = topology.cities();
		List<State> states = new ArrayList<>();
		List<StateAction> actions = new ArrayList<>();
		
		List<List<Tuple<Vehicle, City>>> allTuples = generateAllStates(agent.vehicles(), cities);
		
		//Create all states and actions
		List<TaskState> tasks = new ArrayList<> ();
		for(City cityFrom: cities) {
			for(City cityTo: cities) {
				tasks.add(new TaskState(cityFrom, cityTo, topology, td));
			}
		}
		
		for(Vehicle vehicle: agent.vehicles()) {
			actions.add(new DeliverAction(vehicle));
		}
		
		// Value iteration algorithm
		ValueIteration valueIterationAlgo = new ValueIteration(states, actions, 1e-10, discount);
		valueIterationAlgo.valueIteration();
		
		// Create mapping from city to states. Required to link our model to the logist interface
		cityToEmptyState = new HashMap<>();
		cityToTaskState = new HashMap<>();
		
		for (State state: states) {
			TaskState cState = (TaskState) state;
			
			if (cityToTaskState.containsKey(cState.getStateLocation())) {
				cityToTaskState.get(cState.getStateLocation()).put(cState.getToCity(), cState);
			} else {
				Map<City, TaskState> destinationToState = new HashMap<City, TaskState>();
				destinationToState.put(cState.getToCity(), cState);
				cityToTaskState.put(cState.getStateLocation(), destinationToState);
			}
		}
		
		this.random = new Random();
		this.pPickup = discount;
		this.numActions = 0;
		this.myAgent = agent;
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;
		
		State currentState = null;
		
		if (availableTask == null) {
			currentState = cityToEmptyState.get(vehicle.getCurrentCity());
		} else {
			if (!cityToTaskState.containsKey(vehicle.getCurrentCity())) {
				throw new IllegalStateException("1rst stage failure");
				
			} else if (!cityToTaskState.get(vehicle.getCurrentCity()).containsKey(availableTask.deliveryCity)) {
				throw new IllegalStateException("2nd stage failure");
				
			}
			currentState = cityToTaskState.get(vehicle.getCurrentCity()).get(availableTask.deliveryCity);
		}
		
		StateAction bestAction = currentState.getBestAction();
		
		if (bestAction.type() == DELIVER) {
			action = new Pickup(availableTask);
		} else if (bestAction.type() == MOVE) {
			MoveAction mAction = (MoveAction) bestAction;
			action = new Move(mAction.getDestination());
		} else {
			throw new IllegalStateException();
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
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








