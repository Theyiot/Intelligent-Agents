import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 * 
 * @author Théo Nikles (250624)
 * @author Amaury Combes (235400)
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	private int x;
	private int y;
	private int stepsToLive;
	private static int IDNumber = 0;
	private int ID;
	private RabbitsGrassSimulationSpace rgsSpace;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
		stepsToLive = (int) ((Math.random() * (maxLifespan - minLifespan)) + minLifespan);
		IDNumber++;
		ID = IDNumber;
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	public void draw(SimGraphics arg0) {
		arg0.drawFastRect(Color.white);
	}

	public void step() {
		
		move();
		
		stepsToLive += rgsSpace.eatGrassAt(x, y);
		stepsToLive--;
	}
	
	public void reproduce() {
		//Reproduction takes half of the energy
		stepsToLive /= 2;
	}
	
	private void move() {
		boolean foundNextPosition = false;
		Set<Movement> bannedMoves = new HashSet<>();

		while (!foundNextPosition) {
			Movement movement = Movement.getRandomMoveWithout(bannedMoves);
			
			int xIncrement = movement.getX();
			int yIncrement = movement.getY();
			
			Object2DGrid grid = rgsSpace.getCurrentAgentSpace();
			int newX = (x + xIncrement + grid.getSizeX()) % grid.getSizeX();
			int newY = (y + yIncrement + grid.getSizeY()) % grid.getSizeY();
			
			foundNextPosition = !rgsSpace.isCellOccupied(newX, newY) || rgsSpace.getAgentAt(newX, newY) == this;
			
			if (!foundNextPosition) {
				bannedMoves.add(movement);
			} else {
				rgsSpace.moveAgentAt(x, y, newX, newY);
				x = newX;
				y = newY;
			}
		}
	}

	/*****************************
	 * GETTER AND SETTER METHODS *
	 *****************************/

	public void setRabbitsGrassSpace(RabbitsGrassSimulationSpace rgsSpace) {
		this.rgsSpace = rgsSpace;
	}

	public void setXY(int newX, int newY) {
		x = newX;
		y = newY;
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