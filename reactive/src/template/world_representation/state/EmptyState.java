package template.world_representation.state;

import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;
import static template.world_representation.state.StateType.EMPTY;

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
			return stateCity.hasNeighbor(action.destination());
		} else if (action.type() == DELIVER) {
			return false;
		} else {
			throw new IllegalStateException("Illegal branching");
		}
	}
	
	@Override
	public double reward(StateAction action) {
		if(isLegal(action)) {
			return stateCity.distanceTo(action.destination());
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
