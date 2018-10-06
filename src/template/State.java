package template;

import static template.StateType.EMPTY;
import static template.StateType.NON_EMPTY;

import java.util.List;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;


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
	
	public Tuple<EmptyState, List<TaskState>> transition(StateAction action) {
		if (!isLegal(action)) {
			throw new IllegalStateException("Illegal action " + action + " for current state " + this);
		}

		City destination = action.destination();

		EmptyState achievableEmptyState = new EmptyState(destination, topology, td);
		List<TaskState> newTaskStates = TaskState.generateTaskStates(destination, topology, td);
		
		return new Tuple<EmptyState, List<TaskState>>(achievableEmptyState, newTaskStates);
	}
	
	public double T(StateAction action, State otherState) {
		if(!isLegal(action)) {
			return 0;
		}
		
		List<TaskState> achievableTaskStates = otherState.transition(action).getRight();
		
		if(otherState.getType() == EMPTY) {
			
			double probability = 1;
			
			for (TaskState state: achievableTaskStates) {
				probability *= (1 - td.probability(state.getFromCity(), state.getToCity()));
			}
			
			return probability;
			
		} else if (otherState.getType() == NON_EMPTY) {
			
		}
		return 0;
	}
	
	/*public double T(StateAction action, State otherState) {
		if (!isLegal(action)) {
			return 0;
		}
		
		if ()
		
		City nextCity = otherState.getStateLocation();
		
		List<City> cities = topology.cities();
		
		double proba = 1;
		
		for(City city: cities) {
			proba *= (1 - td.probability(otherState.getCity(), city));
		}
		
		if (otherState.getType() == EMPTY) {
			return proba;
		} else if (otherState.getType() == NON_EMPTY) {
			return 1 - proba;
		} else {
			throw new IllegalStateException();
		}
		
	}*/
	
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
