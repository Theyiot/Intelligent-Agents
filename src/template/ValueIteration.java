package template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public final class ValueIteration {
	private final List<State> states;
	private final List<StateAction> actions;
	private final double convergenceCriteria;
	private final double discountFactor;
	
	public ValueIteration(List<State> states, List<StateAction> actions, double convergenceCriteria, double discountFactor) {
		this.states = states;
		this.actions = actions;
		this.convergenceCriteria = convergenceCriteria;
		this.discountFactor = discountFactor;
	}
	
	public List<State> valueIteration() {
		double delta = Double.MAX_VALUE;
		
		while (delta > convergenceCriteria) {
			delta = iteration();
		}
		
		return states;
	}
	
	private double iteration() {
		
		Map<State, Double> snapshot = getSnapshot();
		List<Double> deltas = new ArrayList<>();
		
		for (State state: states) {
			Tuple<StateAction, Double> iterationReport = stateIteration(state);
			deltas.add(Math.abs(iterationReport.getRight()) - snapshot.get(state));
		}	
		
		return max(deltas);
	}
	
	private Tuple<StateAction, Double> stateIteration(State state) {
		
		double maxQSA = Double.MIN_VALUE;
		StateAction bestAction = null;
		
		for(StateAction action: actions) {
			
			double qSA = computeQSA(state, action);
			
			if (qSA > maxQSA) {
				maxQSA = qSA;
				bestAction = action;
			}
		}
		
		state.setBestAction(bestAction);
		state.setV(maxQSA);
		
		return new Tuple<StateAction, Double>(bestAction, maxQSA);
	}
	
	private double computeQSA(State state, StateAction action) {
		
		double sum = 0;
		
		Tuple<EmptyState, List<TaskState>> nextStatesT = state.transition(action);
		
		List<State> nextStates = new ArrayList<>();
		nextStates.add(nextStatesT.getLeft());
		nextStates.addAll(nextStatesT.getRight());
		
		for (State nextState: nextStates) {
			sum += state.T(action, nextState) * nextState.V();
		}
		
		double qSA = state.reward(action) + discountFactor * sum;
		
		return qSA;
		
	}
	
	private Map<State, Double> getSnapshot() {
		Map<State, Double> snapshot = new HashMap<State, Double>();
		
		for(State state: states) {
			snapshot.put(state, state.V());
		}
		
		return snapshot;
	}
	
	public static double max(List<Double> l) {
		double max = Double.MIN_VALUE;
		
		for(Double v: l) {
			max = Math.max(max, v);
		}
		
		return max;
	}

}
