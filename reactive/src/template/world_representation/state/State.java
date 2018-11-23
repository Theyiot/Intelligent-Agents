package template.world_representation.state;

import static template.world_representation.action.ActionType.DELIVER;
import static template.world_representation.action.ActionType.PICKUP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import template.util.Tuple;
import template.world_representation.action.PickupAction;
import template.world_representation.action.TaskAction;


public class State {
	protected final Topology topology;
	protected final TaskDistribution td;
	protected final List<Tuple<Vehicle, City>> tuples;
	protected final AuctionedTask task;
	private double value;
	private TaskAction bestAction = null;

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
	
	public List<Tuple<Vehicle, City>> getTuples() {
		return tuples;
	}
	
	public boolean isLegal(TaskAction action) {
		for(Tuple<Vehicle, City> tuple : tuples) {
			if(tuple.getLeft().equals(action.getVehicle())) {
				if (action.getType() == PICKUP) {
					PickupAction mAction = (PickupAction) action;
					return tuple.getRight().hasNeighbor(mAction.getDestination());
				} else if (action.getType() == DELIVER) {
					return true;
				} else {
					throw new IllegalStateException("Illegal branching");
				}
			}
		}
		return false;
	}
	
	public double T(TaskAction action, State otherState) {
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
	
	public TaskAction getBestAction() {
		return bestAction;
	}
	
	public void setBestAction(TaskAction newBestAction) {
		this.bestAction = newBestAction;
	}

}
