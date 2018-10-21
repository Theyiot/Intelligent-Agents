package template.world_representation.action;

import logist.topology.Topology.City;

abstract public class StateAction {
	private final ActionType type;
	
	public StateAction(ActionType type) {
		this.type = type;
	}
	
	public ActionType type() {
		return type;
	}
	
	@Override
	public String toString() {
		return "Action: " + type.toString();
	}
}
