package template;

import static template.ActionType.DELIVER;
import static template.ActionType.MOVE;
import static template.StateType.EMPTY;
import static template.StateType.NON_EMPTY;

import java.util.List;
import java.util.ArrayList;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

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
		if(!isLegal(action)) {
			throw new IllegalStateException("Illegal action " + action + " for current state " + this);
		} else {
			return stateCity.distanceTo(action.destination());	
		}
	}
	
	@Override
	public City getStateLocation() {
		return stateCity;
	}
	
	static public List<EmptyState> generateEmptyStates(List<City> cities, Topology topology, TaskDistribution td) {
		List<EmptyState> newStates = new ArrayList<>();
		
		for(City city: cities) {
			newStates.add(new EmptyState(city, topology, td));
		}
		
		return newStates;	
	}

}
