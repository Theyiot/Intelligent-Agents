package template.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import template.algorithm.Path.PathBuilder;
import template.utils.Tuple;

public class AStar<E extends Edge, N extends Node> {
	
	private final Explorer<E, N> explorer;
	
	public AStar(Explorer<E, N> explorer) {
		this.explorer = explorer;
	}
	
	public Path<E, N> getGoalPathFrom(N node) {
		List<ExplorationNode<E, N>> nonExploredNodes = new ArrayList<>();
		nonExploredNodes.add(new ExplorationNode<>(null, node, null, explorer, 0));
		Map<Node, ExplorationNode<E, N>> visitedNodes = new HashMap<>();
		
		
		
		Set<Path<E, N>> candidatePaths = new HashSet<>();
		
		while (!nonExploredNodes.isEmpty()) {
			ExplorationNode<E, N> currentExplorationNode = nonExploredNodes.remove(0);
			
			if (currentExplorationNode.isGoal()) {
				ExplorationNode<E, N> goalNode = currentExplorationNode;
				candidatePaths.add(goalNode.pathFromRoot());
			} else if (!visitedNodes.containsKey(currentExplorationNode.getCurrentNode()) ||
					visitedNodes.get(currentExplorationNode.currentNode).f() > currentExplorationNode.f()) {
				visitedNodes.put(currentExplorationNode.getCurrentNode(), currentExplorationNode);
				Set<ExplorationNode<E, N>> nextNodes = currentExplorationNode.next();
				nonExploredNodes.addAll(nextNodes);
				nonExploredNodes.sort((x, y) -> x.f() < y.f() ? -1 : x.f() == y.f() ? 0 : 1);
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
		
		public double f() {
			return g() + h();
		}
		
		public double g() {
			return linkEdge.getWeight();
		}
		
		public double h() {
			return 0;
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
