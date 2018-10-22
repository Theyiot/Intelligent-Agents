package template.algorithm;

import java.util.Set;

import template.utils.Tuple;

public interface Explorer<E extends Edge, N extends Node> {
	
	public Set<Tuple<E, N>> getReachableNodesFrom(N node);
	
	public double h(N node);

}
