package template.world_representation.state;

import logist.topology.Topology.City;

public class AuctionedTask {
	private City fromCity;
	private City toCity;

	public AuctionedTask(City fromCity, City toCity) {
		this.fromCity = fromCity;
		this.toCity = toCity;
	}
	
	public City getFromCity() {
		return fromCity;
	}
	
	public City getToCity() {
		return toCity;
	}
}
