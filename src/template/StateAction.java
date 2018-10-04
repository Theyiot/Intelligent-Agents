package template;

import logist.topology.Topology.City;

public class StateAction {
	private final City destination;
	private final ActionType type;
	
	public StateAction(City destination, ActionType type) {
		this.destination = destination;
		this.type = type;
	}
	
	public City destination() {
		return destination;
	}
	
	public ActionType type() {
		return type;
	}
	
	@Override
	public String toString() {
		return "To " + destination + type.toString();
	}
}
