package reactive.world_representation.action;

import logist.simulation.Vehicle;

public class TaskAction {
	private final ActionType type;
	private final Vehicle vehicle;
	
	public TaskAction(Vehicle vehicle, ActionType type) {
		this.vehicle = vehicle;
		this.type = type;
	}
	
	public ActionType getType() {
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
