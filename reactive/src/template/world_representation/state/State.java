package template.world_representation.state;

import java.util.List;

import static template.world_representation.state.StateType.EMPTY;
import static template.world_representation.state.StateType.NON_EMPTY;
import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;

import java.util.ArrayList;
import java.util.Random;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.util.Tuple;
import template.world_representation.action.StateAction;


public abstract class State {
	protected final Topology topology;
	protected final TaskDistribution td; 
	private final StateType type;
	private double value;
	private StateAction bestAction = null;

	public State(Topology topology, TaskDistribution td, StateType type) {
		this.topology = topology;
		this.td = td;
		this.type = type;
		this.value = new Random().nextDouble();
	}
	
	public abstract double reward(StateAction action);
	
	public abstract boolean isLegal(StateAction action);
	
	public abstract City getStateLocation();
	
	public abstract Tuple<EmptyState, List<TaskState>> transition(StateAction action);
	
	public double T(StateAction action, State otherState) {
		if(!isLegal(action)) {
			return 0;
		}
		
		if(otherState.getType() == EMPTY) {
			return td.probability(otherState.getStateLocation(), null);			
		} else if (otherState.getType() == NON_EMPTY) {
			TaskState tState = (TaskState) otherState;
			return td.probability(tState.getFromCity(), tState.getToCity());
		} else {
			throw new IllegalStateException("Illegal branching");
		}
	}
	
	public StateType getType() {
		return type;
	}
	
	public double V() {
		return value;
	}
	
	public void setV(double newValue) {
		this.value = newValue;
	}
	
	public StateAction getBestAction() {
		return bestAction;
	}
	
	public void setBestAction(StateAction newBestAction) {
		this.bestAction = newBestAction;
	}

}
