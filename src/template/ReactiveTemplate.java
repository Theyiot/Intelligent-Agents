package template;

import java.util.ArrayList;
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

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);
		
		cities = topology.cities();
		List<State> states = new ArrayList<>();
		List<StateAction> actions = new ArrayList<>();
		
		// Create all states and actions
		for(City city: cities) {
			
			// Create states
			states.add(new EmptyState(city, topology, td));
			states.addAll(TaskState.generateTaskStates(city, topology, td));
			
			// Create actions
			actions.add(new StateAction(city, ActionType.MOVE));
			actions.add(new StateAction(city, ActionType.DELIVER));
			
		}
		
		// Value iteration algorithm
		ValueIteration valueIterationAlgo = new ValueIteration(states, actions, 1e-10, discount);
		states = valueIterationAlgo.valueIteration();
		
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
}
