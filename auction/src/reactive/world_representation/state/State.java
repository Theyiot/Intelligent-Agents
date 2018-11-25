package reactive.world_representation.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import logist.simulation.Vehicle;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import reactive.world_representation.action.TaskAction;
import util.Tuple;


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
	
	public List<City> getCities() {
		List<City> cities = new ArrayList<>();
		
		for (Tuple<Vehicle, City> tuple: tuples) {
			cities.add(tuple.getRight());
		}
		
		return cities;
	}
	
	public boolean isLegal(TaskAction action) {
		for(Tuple<Vehicle, City> tuple : tuples) {
			if(tuple.getLeft().equals(action.getVehicle())) {
				return true;
			}
		}
		throw new IllegalStateException("Want to do action for an unknown vehicle");
	}
	
	public double T(TaskAction action, State otherState) {
		if(!isLegal(action)) {
			return 0;
		}

		AuctionedTask task = otherState.getAuctionedTask();
		return (1.0 / (double) topology.cities().size()) * td.probability(task.getFromCity(), task.getToCity());
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

	@Override
	public boolean equals(Object o) {
		if(o instanceof State) {
			State other = (State)o;
			List<Tuple<Vehicle, City>> oTuples = other.getTuples();
			if(oTuples == null || other.getTuples().size() != tuples.size() ||
					!other.getAuctionedTask().equals(task)) {
				return false;
			}
			for(int i = 0 ; i < this.getTuples().size() ; i++) {
				Tuple<Vehicle, City> tuple = oTuples.get(i);
				if(!tuple.getLeft().equals(tuples.get(i).getLeft()) ||
						!tuple.getRight().equals(tuples.get(i).getRight())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(tuples, task);
	}
}
