package reactive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logist.task.TaskDistribution;
import logist.topology.Topology.City;
import reactive.world_representation.state.AuctionedTask;
import reactive.world_representation.state.State;

public class PartialStateEvaluator {
	private final Map<PartialState, Double> mapping;
	
	public PartialStateEvaluator(Set<State> states, TaskDistribution tD, int cityCount) {
		mapping = new HashMap<>();
		
		for (State state: states) {
			PartialState partialState = new PartialState(state.getCities());
			AuctionedTask task = state.getAuctionedTask();
			
			if (!mapping.containsKey(partialState)) {
				mapping.put(partialState, tD.probability(task.getFromCity(), task.getToCity()) * state.V());
			} else {
				double valueToAdd = tD.probability(task.getFromCity(), task.getToCity()) * state.V();
				mapping.put(partialState, mapping.get(partialState) + valueToAdd);
			}
		}
		
		for (PartialState key: mapping.keySet()) {
			mapping.put(key, mapping.get(key) * (1.0 / (double) cityCount));
		}
	}
	
	public double valueAt(List<City> positionState) {
		return mapping.get(new PartialState(positionState));
	}
	
	public double valueAt(PartialState state) {
		return mapping.get(state);
	}

}
