package template.world_representation.state;

import java.util.Objects;

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
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof AuctionedTask) {
			AuctionedTask other = (AuctionedTask)o;
			return other.fromCity.equals(fromCity) && other.toCity.equals(toCity);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fromCity, toCity);
	}
}
