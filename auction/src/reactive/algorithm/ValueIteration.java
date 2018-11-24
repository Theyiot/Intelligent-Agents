package reactive.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reactive.util.Tuple;
import reactive.world_representation.Transitioner;
import reactive.world_representation.action.TaskAction;
import reactive.world_representation.state.State;

public final class ValueIteration {
	private final List<State> states;
	private final List<TaskAction> actions;
	private final Transitioner transitioner;
	private final double convergenceCriteria;
	private final double discountFactor;
	
	public ValueIteration(List<State> states, List<TaskAction> actions, Transitioner transitioner, 
			double convergenceCriteria, double discountFactor) {
		this.states = states;
		this.actions = actions;
		this.transitioner = transitioner;
		this.convergenceCriteria = convergenceCriteria;
		this.discountFactor = discountFactor;
	}
	
	public List<State> valueIteration() {
		double delta = Double.MAX_VALUE;
		
		int numberOfIteration = 0;
		
		while (delta > convergenceCriteria) {
			++numberOfIteration;
			delta = iteration();
		}
		
		System.out.println("Value iteration converged in " + numberOfIteration + " steps.");
		
		return states;
	}
	
	private double iteration() {
		
		Map<State, Double> snapshot = getSnapshot();
		List<Double> deltas = new ArrayList<>();
		
		for (State state: states) {
			Tuple<TaskAction, Double> iterationReport = stateIteration(state);
			deltas.add(Math.abs(iterationReport.getRight()) - snapshot.get(state));
		}
		
		return max(deltas);
	}
	
	private Tuple<TaskAction, Double> stateIteration(State state) {
		
		double maxQSA = Double.MIN_VALUE;
		TaskAction bestAction = null;
		
		for(TaskAction action: actions) {
			if (state.isLegal(action)) {
				double qSA = computeQSA(state, action);
				
				if (qSA > maxQSA) {
					maxQSA = qSA;
					bestAction = action;
				}
			}
		}
		
		state.setBestAction(bestAction);
		state.setV(maxQSA);
		
		return new Tuple<TaskAction, Double>(bestAction, maxQSA);
	}
	
	private double computeQSA(State state, TaskAction action) {
		
		double sum = 0;
		
		Tuple<State, List<State>> nextStatesT = transitioner.getPossibleStatesFrom(state);
		
		List<State> nextStates = new ArrayList<>();
		
		for (State cState: states) {
			if (cState.equals(nextStatesT.getLeft())) {
				nextStates.add(cState);
			}
			
			for (State tState: nextStatesT.getRight()) {
				if (cState.equals(tState)) {
					nextStates.add(cState);
				}
			}
		}
		
		for (State nextState: nextStates) {
			sum += state.T(action, nextState) * nextState.V();
		}
		
		double qSA = Transitioner.reward(state, action) + discountFactor * sum;
		
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
