package template.world_representation;

import logist.topology.Topology.City;

public class GraphVertex {
	private final City city;
	
	public GraphVertex(City city) {
		this.city = city;
	}
	
	public City getCity() {
		return city;
	}
	
	@Override
	public boolean equals(Object vertex) {
		if (!(vertex instanceof GraphVertex)) {
			return false;
		}
		return ((GraphVertex)vertex).getCity().equals(city);
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
