package template.world_representation.action;

import logist.topology.Topology.City;

public class Move extends Action {
	
	private final City origin;
	private final City destination;
	
	
	public Move(City origin, City destination) {
		super(ActionType.MOVE);
		
		if (!origin.hasNeighbor(destination)) {
			throw new IllegalArgumentException("Tried to construct illegal move from " + origin + " to " + destination);
		}
		
		this.origin = origin;
		this.destination = destination;
	}
	
	public City getOrigin() {
		return origin;
	}
	
	public City getDestination() {
		return destination;
	}
	
	@Override
	public String toString() {
		return "Move action from " + origin + " to " + destination;
	}

	@Override
	public Double getWeight() {
		return origin.distanceTo(destination);
	}

	@Override
	public logist.plan.Action toLogistAction() {
		return new logist.plan.Action.Move(destination);
	}

}
