package template.world_representation.action;

import logist.simulation.Vehicle;

public class DeliverAction extends StateAction {
	
	public DeliverAction(Vehicle vehicle) {
		super(vehicle, ActionType.DELIVER);
	}

}
