package template.world_representation.state;

import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;
import static template.world_representation.state.StateType.EMPTY;

import template.util.Tuple;
import template.world_representation.action.MoveAction;

import java.util.ArrayList;
import java.util.List;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.world_representation.action.StateAction;

public class EmptyState extends State {
	private City stateCity;
	
	public EmptyState(City stateCity, Topology topology, TaskDistribution td) {
		super(topology, td, EMPTY);
		this.stateCity = stateCity;
	}
	
	@Override
	public boolean isLegal(StateAction action) {
		if (action.type() == MOVE) {
			MoveAction mAction = (MoveAction) action;
			return stateCity.hasNeighbor(mAction.getDestination());
		} else if (action.type() == DELIVER) {
			return false;
		} else {
			throw new IllegalStateException("Illegal branching");
		}
	}
	
	@Override
	public Tuple<EmptyState, List<TaskState>> transition(StateAction action) {
		if (!isLegal(action)) {
			throw new IllegalStateException("Illegal action " + action + " for current state " + this);
		}
		
		City destination = ((MoveAction) action).getDestination();
		
		EmptyState achievableEmptyState = new EmptyState(destination, topology, td);
		List<TaskState> newTaskStates = TaskState.generateTaskStates(destination, topology, td);
		
		return new Tuple<EmptyState, List<TaskState>>(achievableEmptyState, newTaskStates);
	}
	
	@Override
	public double reward(StateAction action) {
		if(isLegal(action)) {
			MoveAction mAction = (MoveAction) action;
			return stateCity.distanceTo(mAction.getDestination());
		} else {
			throw new IllegalStateException("Illegal action " + action + " for current state " + this);	
		}
	}
	
	@Override
	public City getStateLocation() {
		return stateCity;
	}
	
	@Override
	public String toString() {
		return "Empty state with location " + stateCity;
	}
	
	static public List<EmptyState> generateEmptyStates(List<City> cities, Topology topology, TaskDistribution td) {
		List<EmptyState> newStates = new ArrayList<>();
		
		for(City city: cities) {
			newStates.add(new EmptyState(city, topology, td));
		}
		
		return newStates;	
	}

}
