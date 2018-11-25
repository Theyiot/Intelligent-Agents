package reactive.world_representation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;
import util.Tuple;
import reactive.world_representation.action.ActionType;
import reactive.world_representation.action.TaskAction;
import reactive.world_representation.state.AuctionedTask;
import reactive.world_representation.state.State;

public class Transitioner {
	private final List<State> states;
	
	public Transitioner(List<State> states) {
		this.states = new ArrayList<> (states);
	}
	
	public Tuple<Double, Set<State>> transitions(State state, TaskAction action) {
		Set<State> newStates = new HashSet<> ();

		AuctionedTask task = state.getAuctionedTask();
		Vehicle vehicle = action.getVehicle();
		List<Tuple<Vehicle, City>> newTuples = state.getTuples();
		for(int i = 0 ; i < newTuples.size() ; i++) {
			if(newTuples.get(i).getLeft().equals(vehicle)) {
				newTuples.set(i, new Tuple<> (vehicle, task.getToCity()));
			}
		}
		for(State s : states) {
			if(s.getTuples().equals(newTuples)) {
				newStates.add(s);
			}
		}

		return new Tuple<> (reward(state, action), newStates);
	}
	
	public static Double reward(State state, TaskAction action) {
		for(Tuple<Vehicle, City> tuple: state.getTuples()) {
			if(tuple.getLeft().equals(action.getVehicle())) {
				AuctionedTask task = state.getAuctionedTask();
				return tuple.getRight().distanceTo(task.getFromCity()) + task.getFromCity().distanceTo(task.getToCity());
			}
		}
		throw new IllegalStateException("Trying to find reward of an unknown vehicle");
	}
	
	public Tuple<State, List<State>> getPossibleStatesFrom(State state) {
		List<State> legalStates = new ArrayList<> ();
		for(State s : states) {
			if(isValidNewState(state, s)) {
				legalStates.add(s);
			}
		}
		List<State> newStates = new ArrayList<> ();
		for(City city : new ArrayList<City> ()) {
			List<Tuple<Vehicle, City>> tuples = state.getTuples();
			for(int i = 0 ; i < tuples.size() ; i++) {
				if(tuples.get(i).getRight().hasNeighbor(city)) {
					List<Tuple<Vehicle, City>> newTuples = new ArrayList<> (tuples);
					newTuples.set(i, new Tuple<>(tuples.get(i).getLeft(), city));
				}
			}
		}
		return new Tuple<> (state, newStates);
	}
	
	private boolean isValidNewState(State initial, State end) {
		List<Tuple<Vehicle, City>> initTuples = initial.getTuples();
		List<Tuple<Vehicle, City>> endTuples = end.getTuples();
		int counter = 0;
		for(int i = 0 ; i < initTuples.size() ; i++) {
			if(!initTuples.get(i).getLeft().equals(endTuples.get(i).getLeft())) {
				System.out.println("Unordered tuples");
			} else if(!initTuples.get(i).getRight().equals(endTuples.get(i).getRight())) {
				if(!initTuples.get(i).getRight().hasNeighbor(endTuples.get(i).getRight())) {
					return false;
				}
				counter++;
			}
		}
		return counter == 1;
	}
}
