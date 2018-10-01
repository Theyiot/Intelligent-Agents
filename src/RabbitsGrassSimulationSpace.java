import java.util.ArrayList;
import java.util.List;

import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author Shruti Goli (300136)  
 * @author Th�o Nikles (250624)
 * @author Amaury Combes (235400)
 */

public class RabbitsGrassSimulationSpace {
	private Object2DGrid grassSpace;
	private Object2DGrid agentSpace;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public RabbitsGrassSimulationSpace(int gridSize) {
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
				if (grassSpace.getObjectAt(x, y).equals(1)) {
					count++;
				}
			}
		}
		return count;
	}

	public void spreadGrass(int grass) {
		// Randomly place money in moneySpace
		for (int i = 0; i < grass; ++i) {

			// Choose coordinates
			int x = (int) (Math.random() * (grassSpace.getSizeX()));
			int y = (int) (Math.random() * (grassSpace.getSizeY()));

			// Grass cannot spread if there is a rabbit on this cell
			if (agentSpace.getObjectAt(x, y) == null) {
				int currentAmount = ((Integer) grassSpace.getObjectAt(x, y)).intValue();
				grassSpace.putObjectAt(x, y, new Integer(currentAmount + 1));
			}
		}
	}

	public boolean addAgent(RabbitsGrassSimulationAgent agent) {
		List<List<Integer>> freeSpots = new ArrayList<>();

		// Find free spots
		for (int i = 0; i < agentSpace.getSizeX(); ++i) {
			for (int j = 0; j < agentSpace.getSizeY(); ++j) {
				if(!isCellOccupied(i, j)) {
					List<Integer> freeSpot = new ArrayList<>();
					freeSpot.add(i);
					freeSpot.add(j);
					freeSpots.add(freeSpot);
				}
			}
		}
		
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
		Integer hasGrass = (Integer) grassSpace.getObjectAt(x, y);
		grassSpace.putObjectAt(x, y, new Integer(0));
		// hasGrass == 1 if the cell has grass on it, else hasGrass == 0 if not
		return hasGrass * RabbitsGrassSimulationModel.GRASS_ENERGY;
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

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	private boolean isCellOccupied(int x, int y) {
		return agentSpace.getObjectAt(x, y) != null;
	}
}
