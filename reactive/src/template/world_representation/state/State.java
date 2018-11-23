package template.world_representation.state;

import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.MOVE;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.util.Tuple;
import template.world_representation.action.MoveAction;
import template.world_representation.action.StateAction;


public abstract class State {
	protected final Topology topology;
	protected final TaskDistribution td;
	protected final List<Tuple<Vehicle, City>> tuples;
	protected final AuctionedTask task;
	private double value;
	private StateAction bestAction = null;

	public State(Topology topology, TaskDistribution td, List<Tuple<Vehicle, City>> tuples, AuctionedTask task) {
		this.topology = topology;
		this.td = td;
		this.tuples = new ArrayList<> (tuples);
		this.task = task;
		this.value = new Random().nextDouble();
	}
	
	public AuctionedTask getAuctionedTask() {
		return task;
	}
	
	public boolean isLegal(StateAction action) {
		for(Tuple<Vehicle, City> tuple : tuples) {
			if(tuple.getLeft().equals(action.getVehicle())) {
				if (action.type() == MOVE) {
					MoveAction mAction = (MoveAction) action;
					return tuple.getRight().hasNeighbor(mAction.getDestination());
				} else if (action.type() == DELIVER) {
					return true;
				} else {
					throw new IllegalStateException("Illegal branching");
				}
			}
		}
		return false;
	}
	
	public double T(StateAction action, State otherState) {
		if(!isLegal(action)) {
			return 0;
		}

		AuctionedTask task = otherState.getAuctionedTask();
		return td.probability(task.getFromCity(), task.getToCity());
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
