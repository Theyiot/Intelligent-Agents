package template.world_representation;

public class GraphEdge {
	private final GraphVertex u;
	private final GraphVertex v;
	private final double cost;
	
	public GraphEdge(GraphVertex u, GraphVertex v, double cost) {
		this.u = u;
		this.v = v;
		this.cost = cost;
	}
	
	public GraphVertex getU() {
		return u;
	}
	
	public GraphVertex getV() {
		return v;
	}
	
	public double cost() {
		return cost;
	}
	
	@Override
	public boolean equals(Object edge) {
		if (!(edge instanceof GraphEdge)) {
			return false;
		}
		return (u.equals(((GraphEdge)edge).getU()) && v.equals(((GraphEdge)edge).getV())) ||
				(u.equals(((GraphEdge)edge).getV()) && v.equals(((GraphEdge)edge).getU()));
	}
}
