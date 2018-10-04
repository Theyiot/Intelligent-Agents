package template;

import logist.topology.Topology;
import logist.topology.Topology.City;
import static template.ActionType.MOVE;
import static template.ActionType.DELIVER;

public class State {
	private final String name;
	private final Topology topology;
	private final ActionType type;

	public State(String name, Topology topology, ActionType type) {
		this.name = name;
		this.topology = topology;
		this.type = type;
	}
	
	public State[] transition(StateAction action) {
		City destination = action.destination();
		if (name.equals(destination.name) || type == MOVE && action.type() == DELIVER) {
			throw new IllegalStateException("Cannot move from one state to the same");
		}
		State[] transitions = new State[2];
		transitions[0] = new State(destination.name, topology, MOVE);
		if (type != MOVE) {
			transitions[1] = new State(destination.name, topology, DELIVER);
		}
		
		return transitions;
	}
	
	public ActionType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "In " + name + type.toString();
	}
}
