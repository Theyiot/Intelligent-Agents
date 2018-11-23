package template.world_representation.action;

import logist.simulation.Vehicle;

abstract public class StateAction {
	private final ActionType type;
	private final Vehicle vehicle;
	
	public StateAction(Vehicle vehicle, ActionType type) {
		this.vehicle = vehicle;
		this.type = type;
	}
	
	public ActionType type() {
		return type;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}
	
	@Override
	public String toString() {
		return "Vehicle " + vehicle.name() + " did action: " + type.toString();
	}
}
