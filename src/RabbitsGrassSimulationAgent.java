import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	private int x;
	private int y;
	private int vX;
	private int vY;
	private int stepsToLive;
	private static int IDNumber = 0;
	private int ID;
	private RabbitsGrassSimulationSpace rgsSpace;

	public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
		stepsToLive = (int) ((Math.random() * (maxLifespan - minLifespan)) + minLifespan);
		IDNumber++;
		ID = IDNumber;
	}

	public void setCarryDropSpace(RabbitsGrassSimulationSpace rgsSpace) {
		this.rgsSpace = rgsSpace;
	}

	private void setVxVy() {
		vX = vY = 0;
		switch((int)(Math.random() * RabbitsGrassSimulationModel.POSSIBLE_DIRECTIONS)) {
		case 0:				//EAST
			vX = 1;
			break;
		case 1:				//NORTH
			vY = -1;
			break;
		case 2:				//WEST
			vX = -1;
			break;
		case 3: default:	//SOUTH
			vY = 1;
			break;
		}
	}

	public void step() {
		setVxVy();
		Object2DGrid grid = rgsSpace.getCurrentAgentSpace();
		int newX = (x + vX + grid.getSizeX()) % grid.getSizeX();
		int newY = (y + vY + grid.getSizeY()) % grid.getSizeY();
		
		tryMove(newX, newY);
		
		stepsToLive += rgsSpace.eatGrassAt(x, y);
		stepsToLive--;
	}
	
	public void reproduce() {
		//Reproduction takes half of the energy
		stepsToLive /= 2;
	}

	private boolean tryMove(int newX, int newY) {
		return rgsSpace.moveAgentAt(x, y, newX, newY);
	}

	public void draw(SimGraphics arg0) {
		arg0.drawHollowFastOval(Color.white);
	}

	public void setXY(int newX, int newY) {
		x = newX;
		y = newY;
	}

	public void report() {
		System.out.println(getID() + " at " + x + ", " + y + " has " + getStepsToLive() + " steps to live.");
	}

	public String getID() {
		return "A-" + ID;
	}

	public int getStepsToLive() {
		return stepsToLive;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
