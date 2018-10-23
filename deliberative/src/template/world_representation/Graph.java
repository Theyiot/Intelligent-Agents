package template.world_representation;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Graph {
	
	private final List<GraphVertex> vertices;
	private final List<GraphEdge> edges;
	
	public Graph(List<GraphVertex> vertices, List<GraphEdge> edges) {
		this.vertices = vertices;
		this.edges = edges;
	}
	
	public double minSpanningTreeWeight(GraphVertex root) {
		double totalCost = 0;
		Set<GraphVertex> visitedVertices = new HashSet<> ();
		visitedVertices.add(root);
		while (visitedVertices.size() < vertices.size()) {
			double minCost = Double.MAX_VALUE;
			GraphEdge addedEdge = null;
			GraphVertex chosenVertex = null;
			for(GraphVertex v : visitedVertices) {
				for(GraphEdge e : edges) {
					if(e.getV().equals(v) || e.getU().equals(v)) {
						GraphVertex destination = e.getV().equals(v) ? e.getU() : e.getV();
						if(!visitedVertices.contains(destination) && e.cost() < minCost) {
							minCost = e.cost();
							addedEdge = e;
							chosenVertex = v;
						}
					}
				}
			}
			if(addedEdge == null || chosenVertex == null) {
				System.out.println("Not a connected graph, non-existing Minimum Spanning Tree");
				return 0d;
			}
			totalCost += minCost;
			GraphVertex addedVertex = addedEdge.getU().equals(chosenVertex) ? addedEdge.getV() : addedEdge.getU();
			visitedVertices.add(addedVertex);
		}
		
		return totalCost;
	}
}
