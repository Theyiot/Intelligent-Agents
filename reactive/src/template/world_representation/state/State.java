package template.world_representation.state;

import java.util.List;

import static template.world_representation.state.StateType.EMPTY;
import static template.world_representation.state.StateType.NON_EMPTY;
import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;

import java.util.ArrayList;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.util.Tuple;
import template.world_representation.action.StateAction;


public abstract class State {
	protected final Topology topology;
	protected final TaskDistribution td; 
	private final StateType type;
	private double value = 0;
	private StateAction bestAction = null;

	public State(Topology topology, TaskDistribution td, StateType type) {
		this.topology = topology;
		this.td = td;
		this.type = type;
	}
	
	public abstract double reward(StateAction action);
	
	public abstract boolean isLegal(StateAction action);
	
	public abstract City getStateLocation();
	
	public abstract Tuple<EmptyState, List<TaskState>> transition(StateAction action);
	
	public double T(StateAction action, State otherState) {
		if(!isLegal(action)) {
			return 0;
		}
		
		List<TaskState> achievableTaskStates = this.transition(action).getRight();
		
		if(otherState.getType() == EMPTY) {
			
			double probability = 1;
			
			for (TaskState state: achievableTaskStates) {
				probability *= (1 - td.probability(state.getFromCity(), state.getToCity()));
			}
			
			return probability;
			
		} else if (otherState.getType() == NON_EMPTY) {
			
			double upperPart = 1;
			double downPart = 0;
			
			// Compute up part
			for(TaskState state: achievableTaskStates) {
				
				if (state.equals(otherState)) {
					upperPart *= td.probability(state.getFromCity(), state.getToCity());
				} else {
					upperPart *=  (1 - td.probability(state.getFromCity(), state.getToCity()));
				}	
			}
			
			// Compute down part
			for(TaskState success: achievableTaskStates) {
				List<TaskState> fails = new ArrayList<>(achievableTaskStates);
				fails.remove(success);
				Double currentProba = td.probability(success.getFromCity(), success.getToCity());
				
				for(TaskState fail: fails) {
					currentProba *= (1 - td.probability(fail.getFromCity(), fail.getToCity()));
				}
				
				downPart += currentProba;
			}
			
			return upperPart / downPart;
			
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
