import java.util.ArrayList;
import java.util.List;

import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author Théo Nikles (250624)
 * @author Amaury Combes (235400)
 */

public class RabbitsGrassSimulationSpace {
	private Object2DGrid grassSpace;
	private Object2DGrid agentSpace;
	private int gridSize;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public RabbitsGrassSimulationSpace(int gridSize) {
		this.gridSize = gridSize;
		grassSpace = new Object2DGrid(gridSize, gridSize);
		agentSpace = new Object2DGrid(gridSize, gridSize);

		for (int x = 0; x < gridSize; x++) {
			for (int y = 0; y < gridSize; y++) {
				grassSpace.putObjectAt(x, y, new Integer(0));
			}
		}
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	public int getGrassSize() {
		int count = 0;
		for (int y = 0; y < grassSpace.getSizeY(); y++) {
			for (int x = 0; x < grassSpace.getSizeX(); x++) {
				if ((int)grassSpace.getObjectAt(x, y) > 0) {
					count++;
				}
			}
		}
		return count;
	}

	public void spreadGrass(int grass) {
		List<List<Integer>> freeSpots = getFreeSpots();
		
		if(freeSpots.size() > 0) {
			for (int i = 0; i < grass; ++i) {
				
				int chosenSpot = (int) (Math.random() * freeSpots.size());
				List<Integer> freeSpot = freeSpots.get(chosenSpot);
				int x = freeSpot.get(0);
				int y = freeSpot.get(1);

				// Grass cannot spread if there is a rabbit on this cell
				if (agentSpace.getObjectAt(x, y) == null) {
					int currentAmount = ((Integer) grassSpace.getObjectAt(x, y)).intValue();
					grassSpace.putObjectAt(x, y, new Integer(currentAmount + 1));
				} 
			}
		}		
	}
	
	public List<List<Integer>> getFreeSpots() {
		List<List<Integer>> freeSpots = new ArrayList<>();

		// Find free spots
		for (int i = 0; i < gridSize; ++i) {
			for (int j = 0; j < gridSize; ++j) {
				if(!isCellOccupied(i, j)) {
					List<Integer> freeSpot = new ArrayList<>();
					freeSpot.add(i);
					freeSpot.add(j);
					freeSpots.add(freeSpot);
				}
			}
		}
		
		return freeSpots;
	}

	public boolean addAgent(RabbitsGrassSimulationAgent agent) {
		List<List<Integer>> freeSpots = getFreeSpots();
		
		if(freeSpots.size() == 0) {
			return false;
		} else {
			int freeSpotChoice = (int) (Math.random() * freeSpots.size());
			
			List<Integer> chosenFreeSpot = freeSpots.get(freeSpotChoice);
			int x = chosenFreeSpot.get(0);
			int y = chosenFreeSpot.get(1);
			
			agentSpace.putObjectAt(x, y, agent);
			agent.setXY(x, y);
			agent.setRabbitsGrassSpace(this);
			
			return true;
		}
	}

	public boolean moveAgentAt(int x, int y, int newX, int newY) {
		if (!isCellOccupied(newX, newY)) {
			RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
			removeAgentAt(x, y);
			agent.setXY(newX, newY);
			agentSpace.putObjectAt(newX, newY, agent);
			return true;
		}
		return false;
	}

	public int eatGrassAt(int x, int y) {
		Integer grassNumber = (Integer) grassSpace.getObjectAt(x, y);
		grassSpace.putObjectAt(x, y, new Integer(0));
		// grassNumber indicates how much grass there is on the cell
		return grassNumber;
	}

	public void removeAgentAt(int x, int y) {
		agentSpace.putObjectAt(x, y, null);
	}

	public Object2DGrid getCurrentGrassSpace() {
		return grassSpace;
	}

	public Object2DGrid getCurrentAgentSpace() {
		return agentSpace;
	}
	
	public boolean isCellOccupied(int x, int y) {
		return agentSpace.getObjectAt(x, y) != null;
	}
	
	public RabbitsGrassSimulationAgent getAgentAt(int x, int y) {
		return (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
	}
	
}
