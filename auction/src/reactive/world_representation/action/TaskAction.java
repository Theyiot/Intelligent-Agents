package reactive.world_representation.action;

import logist.simulation.Vehicle;

public class TaskAction {
	private final Vehicle vehicle;
	
	public TaskAction(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}
	
	@Override
	public String toString() {
		return "Vehicle " + vehicle.name() + " delivered the auctioned task.";
	}
}
