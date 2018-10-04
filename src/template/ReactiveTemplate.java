package template;

import java.util.List;
import java.util.Random;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class ReactiveTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;
	private List<City> cities;
	private Double[][] rewards;
	private State[] states;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		cities = topology.cities();
		int citiesNumber = cities.size(), citiesTimesTwo = citiesNumber * 2;
		rewards = new Double[citiesTimesTwo][citiesTimesTwo];
		states = new State[citiesTimesTwo];
		
		//Populating the tables
		for(int y = 0 ; y < citiesTimesTwo ; y++) {
			for(int x = 0 ; x < citiesTimesTwo ; x++) {
				City cityX = cities.get(x % citiesNumber), cityY = cities.get(y % citiesNumber);
				
				//Populating reward table
				if (y < citiesNumber) {
					if(x != y && cityX.hasNeighbor(cityY)) rewards[y][x] = - cityX.distanceTo(cityY);
					else rewards[y][x] = null;
				} else {
					if(x < citiesNumber || x == y) rewards[y][x] = null;
					else rewards[y][x] = td.reward(cityX, cityY) - cityX.distanceTo(cityY);
				}
				
				//Populating transition table
				for(int i = 0 ; i < citiesNumber ; i++) {
					for(ActionType type : ActionType.values()) {
						states[2 * i + type.ordinal()] = new State(cities.get(i).name, topology, type);
					}
				}
			}
		}
		
		double gamma = 1;
		double qPred = 0;
		double q = Double.MIN_VALUE;
		//Finding the best way, for every state
		while(Math.abs(q - qPred) > 1e-10) {
			qPred = q;
			for(int s = 0 ; s < states.length ; s++) {
				State state = states[s];
				for(int c = 0 ; c < citiesNumber ; c++) {
					City city = cities.get(c);
					
					//We skip going through the same city once again
					if(!city.name.equals(state.getName())) {
						for(ActionType type : ActionType.values()) {
							StateAction action = new StateAction(city, type);
							double val = rewards[c * (type.ordinal() + 1)][s * (state.getType().ordinal() + 1)];
							val += gamma * T(null, action, state) * V(null);
							if(val > q) q = val;
						}
					}
				}
			}
		}
		
		this.random = new Random();
		this.pPickup = discount;
		this.numActions = 0;
		this.myAgent = agent;
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;

		if (availableTask == null || random.nextDouble() > pPickup) {
			City currentCity = vehicle.getCurrentCity();
			action = new Move(currentCity.randomNeighbor(random));
		} else {
			action = new Pickup(availableTask);
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}
	
	private double T(State possibleState, StateAction action, State actualState) {
		return 0;
	}
	
	private double V(State possibleState) {
		return 0;
	}
}
