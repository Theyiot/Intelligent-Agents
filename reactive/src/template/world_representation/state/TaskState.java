package template.world_representation.state;

import java.util.List;
import java.util.ArrayList;

import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;
import static template.world_representation.state.StateType.*;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.world_representation.action.StateAction;

public class TaskState extends State {
	private City fromCity;
	private City toCity;
	
	public TaskState(City fromCity, City toCity, Topology topology, TaskDistribution td) {
		super(topology, td, NON_EMPTY);
		this.toCity = toCity;
		this.fromCity = fromCity;
	}
	
	@Override
	public boolean isLegal(StateAction action) {
		if (action.type() == MOVE) {
			return fromCity.hasNeighbor(action.destination());
		} else if (action.type() == DELIVER) {
			return true;
		} else {
			throw new IllegalStateException("Illegal branching");
		}
	}
	
	@Override
	public double reward(StateAction action) {
		if (!isLegal(action)) {
			throw new IllegalStateException("Illegal action " + action + " for current state " + this);
		}
		
		if (action.type() == MOVE) {
			return fromCity.distanceTo(action.destination());
		} else if (action.type() == DELIVER) {
			return super.td.reward(fromCity, toCity) - fromCity.distanceTo(action.destination());
		} else {
			throw new IllegalStateException("Illegal branching");
		}
	}
	
	@Override
	public City getStateLocation() {
		return fromCity;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof TaskState)) {
			return false;
		} else {
			TaskState otherState = (TaskState) other;
			return otherState.getFromCity().equals(getFromCity()) && otherState.getToCity().equals(getToCity());
		}
	}
	
	@Override
	public String toString() {
		return "Task state with location " + fromCity + " and direction " + toCity;
	}
	
	public static List<TaskState> generateTaskStates(City city, Topology topology, TaskDistribution td) {
		
		List<TaskState> newStates = new ArrayList<>();
		
		for(City cityPrime: topology.cities()) {
			if (!cityPrime.equals(city)) {
				newStates.add(new TaskState(city, cityPrime, topology, td));
			}
		}
		
		return newStates;	
	}
	
	
	
	public City getFromCity() {
		return fromCity;
	}
	
	public City getToCity() {
		return toCity;
	}

}
