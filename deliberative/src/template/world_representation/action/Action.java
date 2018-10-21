package template.world_representation.action;

import template.algorithm.Edge;

public abstract class Action implements Edge {
	
	public static enum ActionType {
		MOVE, PICKUP, DELIVER
	}
	
	private final ActionType type;
	
	
	public Action(ActionType type) {
		this.type = type;
	}
	
	public ActionType getType() {
		return type;
	}
	
	abstract public logist.plan.Action toLogistAction();

}
