package template.world_representation.action;

import logist.topology.Topology.City;

public class MoveAction extends StateAction {
	private final City destination;
	
	public MoveAction(City destination) {
		super(ActionType.MOVE);
		this.destination = destination;
	}
	
	public City getDestination() {
		return destination;
	}

}
