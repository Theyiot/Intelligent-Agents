package template.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class AStar<E extends Edge, N extends Node> extends ExplorationAlgorithm<E, N> {
	
	private Comparator<ExplorationNode<E, N>> comparator = new Comparator<ExplorationNode<E, N>>() {
		
		@Override
		public int compare(ExplorationNode<E, N> e1, ExplorationNode<E, N> e2) {
			double x_FCost = explorer.h(e1.getCurrentNode()) + e1.getAccumulatedWeight();
			double y_FCost = explorer.h(e2.getCurrentNode()) + e2.getAccumulatedWeight();
			return x_FCost < y_FCost ? -1 : x_FCost == y_FCost ? 0 : 1;
		}
		
	};
	
	public AStar(Explorer<E, N> explorer) {
		super(explorer);
	}

	public Path<E, N> getGoalPathFrom(N node) {
		Queue<ExplorationNode<E, N>> nonExploredNodes = new PriorityQueue<ExplorationNode<E, N>>(comparator);
		nonExploredNodes.add(new ExplorationNode<>(null, node, null, explorer, 0, 0));
		Map<Node, ExplorationNode<E, N>> visitedNodes = new HashMap<>();

		while (true) {
	
			ExplorationNode<E, N> currentExplorationNode = nonExploredNodes.remove();
			
			//System.out.println("Current weight " + explorer.h(currentExplorationNode.getCurrentNode()) + currentExplorationNode.getAccumulatedWeight());

			if (currentExplorationNode.isGoal()) {
				
				ExplorationNode<E, N> goalNode = currentExplorationNode;
				System.out.println("Path cost: " + goalNode.pathFromRoot().getWeight());
				System.out.println("Path: " + goalNode.pathFromRoot());
				return goalNode.pathFromRoot();
				
			} else if (!visitedNodes.containsKey(currentExplorationNode.getCurrentNode()) || 
					currentExplorationNode.getAccumulatedWeight() < 
					visitedNodes.get(currentExplorationNode.getCurrentNode()).getAccumulatedWeight()) {
				
				visitedNodes.put(currentExplorationNode.getCurrentNode(), currentExplorationNode);
				List<ExplorationNode<E, N>> nextNodes = new ArrayList<>(currentExplorationNode.next());
				
				nextNodes.sort(comparator);
				nonExploredNodes.addAll(nextNodes);
			}
		}

	}

}
