package template.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Path<E extends Edge, N extends Node> {
	
	private final List<N> nodes;
	private final List<E> transitions;
	private final double weight;
	
	private Path(List<N> nodes, List<E> transitions, double weight) {
		if (transitions.size() + 1 != nodes.size()) {
			throw new IllegalArgumentException("Tried to construct an invalid Path, number of nodes: " + nodes.size() + " and number of edges: " + transitions.size());
		}
		this.nodes = nodes;
		this.transitions = transitions;
		this.weight = weight;
	}
	
	public N getStartNode() {
		return nodes.get(0);
	}
	
	public double getWeight() {
		return weight;
	}
	
	public List<E> getTransitions() {
		return transitions;
	}
	
	@Override
	public String toString() {
		String rString = nodes.get(0).toString();
		
		for (int i = 0; i < transitions.size(); ++i) {
			rString += "\n -- (" + transitions.get(i) + ") >>  \n" + nodes.get(i+1);
		}
		
		return rString;
	}
	
	public static final class PathBuilder<E extends Edge, N extends Node> {
		private final List<N> nodes;
		private final List<E> edges;
		private double weightStack;
		
		public PathBuilder() {
			nodes = new ArrayList<>();
			edges = new ArrayList<>();
			weightStack = 0;
		}
		
		public void addStep(E usedEdge, N arrivalNode) {
			nodes.add(arrivalNode);
			edges.add(usedEdge);
			weightStack += usedEdge.getWeight();
		}
		
		public void addInitial(N initialNode) {
			nodes.add(initialNode);
		}
		
		public double getWeight() {
			return weightStack;
		}
		
		public Path<E, N> build() {
			Collections.reverse(nodes);
			Collections.reverse(edges);
			
			return new Path<>(nodes, edges, weightStack);
		}
		
	}

}
