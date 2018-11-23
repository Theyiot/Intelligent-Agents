package template.world_representation.action;

import logist.simulation.Vehicle;
import logist.topology.Topology.City;

public class MoveAction extends StateAction {
	private final City destination;
	
	public MoveAction(Vehicle vehicle, City destination) {
		super(vehicle, ActionType.MOVE);
		this.destination = destination;
	}
	
	public City getDestination() {
		return destination;
	}

}
