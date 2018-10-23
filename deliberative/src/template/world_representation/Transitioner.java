package template.world_representation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logist.task.Task;
import logist.topology.Topology.City;
import template.algorithm.Explorer;
import template.utils.IllegalTransitionException;
import template.utils.Tuple;
import template.world_representation.action.Action;
import template.world_representation.action.Action.ActionType;
import template.world_representation.action.Deliver;
import template.world_representation.action.Move;
import template.world_representation.action.Pickup;
import template.world_representation.state.State;

public class Transitioner implements Explorer<Action, State> {

	public Tuple<Double, State> transition(State state, Action action)
			throws IllegalStateException, IllegalTransitionException {
		Tuple<Double, State> transition = null;

		switch (action.getType()) {

		case MOVE:
			transition = transition(state, (Move) action);
			break;

		case PICKUP:
			transition = transition(state, (Pickup) action);
			break;

		case DELIVER:
			transition = transition(state, (Deliver) action);
			break;

		default:
			throw new IllegalStateException("Switch case is invalid");

		}

		return transition;
	}
	
	@Override
	public Set<Tuple<Action, State>> getReachableNodesFrom(State state) {
		Set<Action> legalActions = getLegalActionsAt(state);
		
		Set<Tuple<Action, State>> transitions = new HashSet<>();
		
		for (Action legalAction: legalActions) {
			State nextState;
			try {
				nextState = nextStateWith(state, legalAction);
			} catch (Exception e) {
				throw new Error("Unexpected behavior. getLegalActions didn't give legal actions only. \n Action: " + 
			legalAction + "\n State: " + state + ". Exception: " + e); 
			} 
			transitions.add(new Tuple<Action, State>(legalAction, nextState));
		}
		
		return transitions;
	}
	
	@Override
	public double h(State node) {
		Set<Task> tasks = node.getWorldTasks();
		Set<Task> holdingTasks = node.getHoldingTasks();
		
		double inferiorCostBound = 0;
		
		for (Task task: tasks) {
			inferiorCostBound = Math.max(task.pathLength() + node.getLocation().distanceTo(task.pickupCity), inferiorCostBound);
		}
		
		for (Task holdingTask: holdingTasks) {
			inferiorCostBound = Math.max(node.getLocation().distanceTo(holdingTask.deliveryCity), inferiorCostBound);
		}
		
		return inferiorCostBound;
	}
	
	public Set<Action> getLegalActionsAt(State state) {
		Set<Action> legalMoves = getLegalMovesAt(state);
		Set<Action> legalPickups = getLegalPickupsAt(state);
		Set<Action> legalDeliveries = getLegalDeliveriesAt(state);
		
		Set<Action> legalActions = new HashSet<>();
		
		legalActions.addAll(legalMoves);
		legalActions.addAll(legalPickups);
		legalActions.addAll(legalDeliveries);
		
		return legalActions;
	}

	public Double rewardWith(State state, Action action) throws IllegalStateException, IllegalTransitionException {
		Tuple<Double, State> transition = transition(state, action);
		return transition.getLeft();
	}

	public State nextStateWith(State state, Action action) throws IllegalStateException, IllegalTransitionException {
		Tuple<Double, State> transition = transition(state, action);
		return transition.getRight();
	}

	private Tuple<Double, State> transition(State state, Move action) throws IllegalTransitionException {
		City location = state.getLocation();
		City destination = action.getDestination();

		if (location.hasNeighbor(destination)) {
			State newState = new State(state, destination);
			Double reward = action.getWeight();
			return new Tuple<Double, State>(reward, newState);
		} else {
			throw new IllegalTransitionException(
					"Tried to perform illegal move from " + location + " to " + destination);
		}

	}

	private Tuple<Double, State> transition(State state, Pickup action) throws IllegalTransitionException {
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

	private Tuple<Double, State> transition(State state, Deliver action) throws IllegalTransitionException {
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
	
	private Set<Action> getLegalMovesAt(State state) {
		List<City> neighborCities = state.getLocation().neighbors();
		Set<Action> legalMoves = new HashSet<>();
		
		for (City city: neighborCities) {
			legalMoves.add(new Move(state.getLocation(), city));
		}
		
		return legalMoves;
	}
	
	private Set<Action> getLegalPickupsAt(State state) {
		Set<Task> worldTasks = state.getWorldTasks();
		Set<Action> legalPickups = new HashSet<>();
		
		for (Task task: worldTasks) {
			if (task.pickupCity.equals(state.getLocation()) && state.getCarryingWeight() + task.weight <= state.getWeightCapacity()) {
				legalPickups.add(new Pickup(task));
			}
		}
		
		return legalPickups;
	}
	
	private Set<Action> getLegalDeliveriesAt(State state) {
		Set<Task> holdingTasks = state.getHoldingTasks();
		Set<Action> legalDeliveries = new HashSet<>();
		
		for (Task task: holdingTasks) {
			if (task.deliveryCity.equals(state.getLocation())) {
				legalDeliveries.add(new Deliver(task));
			}
		}
		
		return legalDeliveries;
	}

}
