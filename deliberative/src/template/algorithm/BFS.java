package template.algorithm;

import java.util.Set;
import java.util.Queue;

import template.algorithm.Path.PathBuilder;
import template.utils.Tuple;

import java.util.HashSet;
import java.util.LinkedList;

public final class BFS<E extends Edge, N extends Node> {
	
	// TODO explorationDepth functionality is not implemented yet
	private final int explorationDepth;
	private final Explorer<E, N> explorer;
	
	public BFS(Explorer<E, N> explorer, int explorationDepth) {
		this.explorationDepth = explorationDepth;
		this.explorer = explorer;
	}
	
	public Path<E, N> getGoalPathFrom(N node) {
		Queue<ExplorationNode<E, N>> nonExploredNodes = new LinkedList<>();
		nonExploredNodes.add(new ExplorationNode<>(null, node, null, explorer, 0));
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
			if (bestPath == null || path.getWeight() > bestPath.getWeight()) {
				bestPath = path;
			} 
		}	
		
		return bestPath;
	}
	
	private static class ExplorationNode<E extends Edge, N extends Node> implements Node {
		
		private final ExplorationNode<E, N> parentNode;
		private final N currentNode;
		private final E linkEdge;
		private final Explorer<E, N> explorer;
		private final int depth;
		
		public ExplorationNode(ExplorationNode<E, N> parentNode, N currentNode, E linkEdge, Explorer<E, N> explorer, int depth) {
			this.parentNode = parentNode;
			this.currentNode = currentNode;
			this.linkEdge = linkEdge;
			this.explorer = explorer;
			this.depth = depth;
		}
		
		public Set<ExplorationNode<E, N>> next() {
			Set<Tuple<E, N>> transitions = explorer.getReachableNodesFrom(currentNode);
			
			Set<ExplorationNode<E, N>> reachableNodes = new HashSet<>();
			
			for (Tuple<E, N> transition: transitions) {
				reachableNodes.add(new ExplorationNode<>(this, transition.getRight(), transition.getLeft(), explorer, depth + 1));
			}
			
			return reachableNodes;
		}
		
		public Node getCurrentNode() {
			return currentNode;
		}
		
		public boolean hasParent() {
			return parentNode != null;
		}
		
		public Path<E, N> pathFromRoot() {
			PathBuilder<E, N> bPath = new PathBuilder<>();
			
			ExplorationNode<E, N> cursor = this;
			
			while (cursor.hasParent()) {
				bPath.addStep(cursor.linkEdge, cursor.currentNode);
				cursor = cursor.parentNode;
			}
			bPath.addInitial(cursor.currentNode);
			
			return bPath.build();
		}

		@Override
		public boolean isGoal() {
			return currentNode.isGoal();
		}
		
	}

}
