package template.algorithm;

import java.util.HashSet;
import java.util.Set;

import template.algorithm.Path.PathBuilder;
import template.utils.Tuple;

public abstract class ExplorationAlgorithm<E extends Edge, N extends Node> {
	
	protected Explorer<E, N> explorer;
	
	public ExplorationAlgorithm(Explorer<E, N> explorer) {
		this.explorer = explorer;
	}
	
	abstract public Path<E, N> getGoalPathFrom(N node);

	
	protected static class ExplorationNode<E extends Edge, N extends Node> implements Node {

		private final ExplorationNode<E, N> parentNode;
		private final N currentNode;
		private final E linkEdge;
		private final Explorer<E, N> explorer;
		private final int depth;
		private final double accumulatedWeight;

		public ExplorationNode(ExplorationNode<E, N> parentNode, N currentNode, E linkEdge, Explorer<E, N> explorer,
				int depth, double accumulatedWeight) {
			this.parentNode = parentNode;
			this.currentNode = currentNode;
			this.linkEdge = linkEdge;
			this.explorer = explorer;
			this.depth = depth;
			this.accumulatedWeight = accumulatedWeight;
		}

		public Set<ExplorationNode<E, N>> next() {
			Set<Tuple<E, N>> transitions = explorer.getReachableNodesFrom(currentNode);

			Set<ExplorationNode<E, N>> reachableNodes = new HashSet<>();

			for (Tuple<E, N> transition : transitions) {
				reachableNodes.add(
						new ExplorationNode<>(this, transition.getRight(), transition.getLeft(), explorer, depth + 1, accumulatedWeight + transition.getLeft().getWeight()));
			}

			return reachableNodes;
		}

		public N getCurrentNode() {
			return currentNode;
		}
		
		public double getAccumulatedWeight() {
			return accumulatedWeight;
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
