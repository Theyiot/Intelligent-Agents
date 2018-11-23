package template.world_representation;

import java.util.ArrayList;
import java.util.List;

import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.util.Tuple;
import template.world_representation.action.TaskAction;
import template.world_representation.action.ActionType;
import template.world_representation.state.State;

public class Transitioner {
	
	public static Tuple<Double, State> transitions(State state, TaskAction action) {
		Tuple<Double, State> transition = null;

		if (action.getType() == ActionType.PICKUP) {
			
		} else if (action.getType() == ActionType.DELIVER) {
			
		} else {
			throw new IllegalStateException("Switch case is invalid");
		}

		return transition;
	}
	
	public static Double reward(State state, TaskAction action) {
		for(Tuple<Vehicle, City> tuple: state.getTuples()) {
			if(tuple.getLeft().equals(action.getVehicle())) {
				return tuple.getRight().distanceTo(tuple.getRight());
			}
		}
		throw new IllegalStateException("Trying to find reward of an unknown vehicle");
	}
	
	public static Tuple<State, List<State>> getPossibleStates(State state) {
		List<State> newStates = new ArrayList<> ();
		for(City city : new ArrayList<City> ()) {
			List<Tuple<Vehicle, City>> tuples = state.getTuples();
			for(int i = 0 ; i < tuples.size() ; i++) {
				if(tuples.get(i).getRight().hasNeighbor(city)) {
					List<Tuple<Vehicle, City>> newTuples = new ArrayList<> (tuples);
					newTuples.set(i, new Tuple<>(tuples.get(i).getLeft(), city));
				}
			}
		}
		return new Tuple<> (state, newStates);
	}

	private static Tuple<Double, State> transition(State state, PickupAction action) {
		City location = state.getLocation();
		Task task = action.getTask();
		City taskPickupLocation = task.pickupCity;

		if (state.getWorldTasks().contains(task) && location.equals(taskPickupLocation) && 
				state.getCarryingWeight() + task.weight <= state.getWeightCapacity()) {
			State newState = new State(state, task, ActionType.PICKUP);
			Double reward = action.getWeight();
			return new Tuple<Double, State>(reward, newState);
		} else {
			throw new IllegalTransitionException(
					"Tried to perform illegal pickup " + task + " at location " + state.getLocation());
		}

	}

	private static Tuple<Double, State> transition(State state, DeliverAction action) {
		City location = state.getLocation();
		Task task = action.getTask();
		City taskDeliveryLocation = task.deliveryCity;

		if (state.getHoldingTasks().contains(task) && location.equals(taskDeliveryLocation)) {
			State newState = new State(state, task, ActionType.DELIVER);
			Double reward = (double) action.getWeight();
			
			// This line to maximise the reward
			// return new Tuple<Double, State>(reward, newState);
			
			// This line to minimize the cost
			return new Tuple<Double, State>(reward, newState);
		} else {
			throw new IllegalTransitionException(
					"Tried to perform illegal delivery " + task + " at location " + state.getLocation());
		}
	}
}
