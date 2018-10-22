package template.world_representation.state;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;
import template.algorithm.Node;
import template.world_representation.action.Action.ActionType;

public final class State implements Node {
	
	public static enum StateType {
		NON_GOAL_STATE, GOAL_STATE
	}
	
	private final Set<Task> worldTasks;
	private final Set<Task> holdingTasks;
	private final City location;
	private final int weightCapacity;
	private final StateType type;
	
	
	public State(Set<Task> worldTasks, Set<Task> holdingTasks, City location, int weightCapacity) {
		this.worldTasks = worldTasks;
		this.holdingTasks = holdingTasks;
		this.location = location;
		this.weightCapacity = weightCapacity;
		
		if (worldTasks.isEmpty() && holdingTasks.isEmpty()) {
			this.type = StateType.GOAL_STATE;
		} else {
			this.type = StateType.NON_GOAL_STATE;
		}
	}
	
	public State(Set<Task> worldTasks, Set<Task> holdingTasks, City location, int weightCapacity, StateType type) {
		this.worldTasks = worldTasks;
		this.holdingTasks = holdingTasks;
		this.location = location;
		this.weightCapacity = weightCapacity;
		this.type = type;
	}
	
	public State(State state) {
		this(state.getWorldTasks(), state.getHoldingTasks(), state.getLocation(), state.getWeightCapacity());
	}
	
	public State(State state, City location) {
		this(state.worldTasks, state.holdingTasks, location, state.getWeightCapacity());
	}
	
	public State(State state, Task task, ActionType type) {
		this(stateContructor(state, task, type));
	}

	public Set<Task> getWorldTasks() {
		return new HashSet<>(worldTasks);
	}

	public Set<Task> getHoldingTasks() {
		return new HashSet<>(holdingTasks);
	}

	public City getLocation() {
		return location;
	}
	
	public int getWeightCapacity() {
		return weightCapacity;
	}
	
	public int getCarryingWeight() {
		int weight = 0;
		
		for (Task task: holdingTasks) {
			weight += task.weight;
		}
		
		return weight;
	}
	
	public boolean isGoal() {
		return type == StateType.GOAL_STATE;
	}
	
	@Override
	public String toString() {
		return "Location: " + location + "\n World tasks: " + worldTasks + "\n HoldingTasks: " + holdingTasks;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof State)) {
			return false;
		} else {
			State otherState = (State) other;
			
			boolean locationCheck = location.equals(otherState.location);
			boolean worldTasksCheck = worldTasks.equals(otherState.getWorldTasks());
			boolean holdingTasksCheck = holdingTasks.equals(otherState.getHoldingTasks());
			boolean weightCheck = weightCapacity == otherState.getWeightCapacity();
		
			return locationCheck && worldTasksCheck && holdingTasksCheck && weightCheck;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(worldTasks, holdingTasks, location, weightCapacity);
	}
	
	private static State stateContructor(State state, Task task, ActionType type) {

		if (type == ActionType.PICKUP) {

			Set<Task> newWorldTasks = state.getWorldTasks();
			newWorldTasks.remove(task);

			Set<Task> newHoldingTasks = state.getHoldingTasks();
			newHoldingTasks.add(task);

			return new State(newWorldTasks, newHoldingTasks, state.location, state.weightCapacity);

		} else if (type == ActionType.DELIVER) {

			Set<Task> newHoldingTasks = state.getHoldingTasks();
			newHoldingTasks.remove(task);
			
			if (state.getHoldingTasks().isEmpty() && newHoldingTasks.isEmpty()) {
				return new State(state.getWorldTasks(), newHoldingTasks, state.location, state.weightCapacity, StateType.GOAL_STATE);
			} else {
				return new State(state.getWorldTasks(), newHoldingTasks, state.location, state.weightCapacity);
			}

		} else {
			throw new IllegalStateException("Illegal branching");
		}

	}
	
}
