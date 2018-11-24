package reactive;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import logist.topology.Topology.City;

public class PartialState {
	private List<City> positionState;
	
	public PartialState(List<City> positionState) {
		this.positionState = positionState;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PartialState)) {
			return false;
		} else {
			PartialState oPartialState = (PartialState) other;
			return positionState.equals(oPartialState.positionState);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(positionState);
	}

}
