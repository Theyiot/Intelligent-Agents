package reactive;

import java.util.HashSet;
import java.util.List;

import logist.task.TaskDistribution;
import logist.topology.Topology.City;
import reactive.world_representation.state.State;

public class TrivialPartialStateEvaluator extends PartialStateEvaluator {
	
	public TrivialPartialStateEvaluator(TaskDistribution td) {
		super(new HashSet<State>(), td, 0);
	}
	
	public double valueAt(List<City> positionState) {
		return 0;
	}

}
