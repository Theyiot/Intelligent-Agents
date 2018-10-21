package template.algorithm;

import java.util.Set;

import template.utils.Tuple;

public interface Explorer<E extends Edge, T extends Node> {
	
	public Set<Tuple<E, T>> getReachableNodesFrom(T node);

}
