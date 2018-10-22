package template.algorithm;

import java.util.Set;
import java.util.Queue;

import java.util.HashSet;
import java.util.LinkedList;

public final class BFS<E extends Edge, N extends Node> extends ExplorationAlgorithm<E, N> {
	
	
	public BFS(Explorer<E, N> explorer) {
		super(explorer);
	}
	
	@Override
	public Path<E, N> getGoalPathFrom(N node) {
		Queue<ExplorationNode<E, N>> nonExploredNodes = new LinkedList<>();
		nonExploredNodes.add(new ExplorationNode<>(null, node, null, explorer, 0, 0));
		Set<Node> visitedNodes = new HashSet<>();
		
		
		
		Set<Path<E, N>> candidatePaths = new HashSet<>();
		
		while (!nonExploredNodes.isEmpty()) {
			ExplorationNode<E, N> currentExplorationNode = nonExploredNodes.remove();
			
			if (currentExplorationNode.isGoal()) {
				ExplorationNode<E, N> goalNode = currentExplorationNode;
				candidatePaths.add(goalNode.pathFromRoot());
			} else if (!visitedNodes.contains(currentExplorationNode.getCurrentNode())) {
				Set<ExplorationNode<E, N>> nextNodes = currentExplorationNode.next();
				visitedNodes.add(currentExplorationNode.getCurrentNode());
				nonExploredNodes.addAll(nextNodes);
			}
		}
		
		Path<E, N> bestPath = null;
		
		for (Path<E, N> path: candidatePaths) {
			if (bestPath == null || path.getWeight() < bestPath.getWeight()) {
				bestPath = path;
			} 
		}	
		
		System.out.println("Path cost: " + bestPath.getWeight());
		
		return bestPath;
	}

}
