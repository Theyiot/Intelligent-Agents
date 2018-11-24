package template.world_representation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;
import template.util.Tuple;
import template.world_representation.action.ActionType;
import template.world_representation.action.TaskAction;
import template.world_representation.state.AuctionedTask;
import template.world_representation.state.State;

public class Transitioner {
	private final List<State> states;
	private final List<City> cities;
	
	public Transitioner(List<State> states, List<City> cities) {
		this.states = new ArrayList<> (states);
		this.cities = new ArrayList<> (cities);
	}
	
	public Tuple<Double, Set<State>> transitions(State state, TaskAction action) {
		Set<State> newStates = new HashSet<> ();

		if (action.getType() == ActionType.PICKUP || action.getType() == ActionType.DELIVER) {
			AuctionedTask task = state.getAuctionedTask();
			Vehicle vehicle = action.getVehicle();
			List<Tuple<Vehicle, City>> newTuples = state.getTuples();
			for(int i = 0 ; i < newTuples.size() ; i++) {
				if(newTuples.get(i).getLeft().equals(vehicle)) {
					City newCity = action.getType() == ActionType.PICKUP ? task.getFromCity() : task.getToCity();
					newTuples.set(i, new Tuple<> (vehicle, newCity));
				}
			}
			for(State s : states) {
				if(s.getTuples().equals(newTuples)) {
					newStates.add(s);
				}
			}
		} else {
			throw new IllegalStateException("Switch case is invalid");
		}

		return new Tuple<> (reward(state, action), newStates);
	}
	
	public static Double reward(State state, TaskAction action) {
		for(Tuple<Vehicle, City> tuple: state.getTuples()) {
			if(tuple.getLeft().equals(action.getVehicle())) {
				return tuple.getRight().distanceTo(tuple.getRight());
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
