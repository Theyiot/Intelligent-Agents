import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;

/**
 * Class that implements the simulation model for the rabbits grass simulation.
 * This is the first class which needs to be setup in order to run Repast
 * simulation. It manages the entire RePast environment and the simulation.
 *
 * @author Th�o Nikles (250624)
 * @author Amaury Combes (235400)
 */

public class RabbitsGrassSimulationModel extends SimModelImpl {
	// Default Values
	public static final int POSSIBLE_DIRECTIONS = 4;
	private static final int GRID_SIZE = 20;
	private static final int RABBITS_NUMBER = 10;
	private static final int BIRTH_THRESHOLD = 150;
	private static final int GRASS_GROWTH_RATE = 100;
	private static final int AGENT_MIN_LIFESPAN = 90;
	private static final int AGENT_MAX_LIFESPAN = 110;

	private int gridSize = GRID_SIZE;
	private int rabbitsNumber = RABBITS_NUMBER;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int grassGrowthRate = GRASS_GROWTH_RATE;
	private int agentMinLifespan = AGENT_MIN_LIFESPAN;
	private int agentMaxLifespan = AGENT_MAX_LIFESPAN;

	private RabbitsGrassSimulationSpace rgsSpace;

	private Schedule schedule;

	private ArrayList<RabbitsGrassSimulationAgent> agentList;

	private DisplaySurface displaySurf;

	private OpenSequenceGraph amountOfGrassAndRabbitInSpace;

	public static void main(String[] args) {
		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		init.loadModel(model, "", false);
	}

	/********************
	 * REQUIRED METHODS *
	 ********************/

	public void begin() {
		buildModel();
		buildSchedule();
		buildDisplay();

		displaySurf.display();
		amountOfGrassAndRabbitInSpace.display();
	}

	public String[] getInitParam() {
		String[] initParams = { "GridSize", "RabbitsNumber", "BirthThreshold", "GrassGrowthRate", "AgentMinLifespan",
				"AgentMaxLifespan", "GrassEnergy" };
		return initParams;
	}

	public String getName() {
		return "Rabbit Grass Simulation";
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setup() {
		rgsSpace = null;
		agentList = new ArrayList<>();
		schedule = new Schedule(1);

		if (displaySurf != null) {
			displaySurf.dispose();
		}
		displaySurf = null;

		if (amountOfGrassAndRabbitInSpace != null) {
			amountOfGrassAndRabbitInSpace.dispose();
		}
		amountOfGrassAndRabbitInSpace = null;

		displaySurf = new DisplaySurface(this, "Rabbits Grass Simulation Window");
		amountOfGrassAndRabbitInSpace = new OpenSequenceGraph("Amount Of Rabbits In Space", this);

		registerDisplaySurface("Rabbits Grass Simulation Window", displaySurf);
		this.registerMediaProducer("Plot", amountOfGrassAndRabbitInSpace);
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	private void buildModel() {
		rgsSpace = new RabbitsGrassSimulationSpace(gridSize);

		for (int i = 0; i < rabbitsNumber; i++) {
			addNewAgent();
		}
	}

	private void buildSchedule() {
		class RabbitsGrassSimulationStep extends BasicAction {
			public void execute() {
				SimUtilities.shuffle(agentList);
				for (int i = 0; i < agentList.size(); i++) {
					agentList.get(i).step();
				}

				reapDeadAgents();
				int newAgents = countNewAgents();
				for (int i = 0; i < newAgents; i++) {
					// We don't want to add an agent if there is no place left
					if (gridSize * gridSize > agentList.size()) {
						addNewAgent();
					}
				}

				rgsSpace.spreadGrass(grassGrowthRate);

				displaySurf.updateDisplay();
			}
		}

		schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());

		class UpdateRabbitsAndGrassInSpace extends BasicAction {
			public void execute() {
				amountOfGrassAndRabbitInSpace.step();
			}
		}

		schedule.scheduleActionAtInterval(10, new UpdateRabbitsAndGrassInSpace());
	}

	private void buildDisplay() {
		
		ColorMap map = new ColorMap();
		
		map.mapColor(0, Color.black);
		for (int i = 1; i < 16; i++) {
			map.mapColor(i, new Color(0, 127 + i*8 - 1, 0));
		}

		Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);

		Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
		displayAgents.setObjectList(agentList);

		// We decided not to make the displays probeable.
		displaySurf.addDisplayable(displayGrass, "Grass");
		displaySurf.addDisplayable(displayAgents, "Agents");

		amountOfGrassAndRabbitInSpace.addSequence("Rabbit In Space", new RabbitsInSpace());
		amountOfGrassAndRabbitInSpace.addSequence("Grass In Space", new GrassInSpace());
	}

	private int countNewAgents() {
		int count = 0;
		for (int i = agentList.size() - 1; i >= 0; i--) {
			RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentList.get(i);
			if (agent.getStepsToLive() >= birthThreshold) {
				agent.reproduce();
				count++;
			}
		}
		return count;
	}

	private void reapDeadAgents() {
		for (int i = (agentList.size() - 1); i >= 0; i--) {
			RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentList.get(i);
			if (agent.getStepsToLive() < 1) {
				rgsSpace.removeAgentAt(agent.getX(), agent.getY());
				agentList.remove(i);
			}
		}
	}

	private void addNewAgent() {
		RabbitsGrassSimulationAgent agent = new RabbitsGrassSimulationAgent(agentMinLifespan, agentMaxLifespan);
		boolean isAdded = rgsSpace.addAgent(agent);
		if (isAdded) {
			agentList.add(agent);
		}
	}

	/*****************************
	 * GETTER AND SETTER METHODS *
	 *****************************/

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = Math.max(1, gridSize);
	}

	public int getRabbitsNumber() {
		return rabbitsNumber;
	}

	public void setRabbitsNumber(int rabbitsNumber) {
		this.rabbitsNumber = Math.max(0, rabbitsNumber);
	}

	public int getBirthThreshold() {
		return birthThreshold;
	}

	public void setBirthThreshold(int birthThreshold) {
		this.birthThreshold = Math.max(0, birthThreshold);
	}

	public int getGrassGrowthRate() {
		return grassGrowthRate;
	}

	public void setGrassGrowthRate(int grassGrowthRate) {
		this.grassGrowthRate = Math.max(0, grassGrowthRate);
	}

	public int getAgentMaxLifespan() {
		return agentMaxLifespan;
	}

	public void setAgentMaxLifespan(int agentMaxLifespan) {
		this.agentMaxLifespan = Math.max(0, agentMaxLifespan);
	}

	public int getAgentMinLifespan() {
		return agentMinLifespan;
	}

	public void setAgentMinLifespan(int agentMinLifespan) {
		this.agentMinLifespan = Math.max(0, agentMinLifespan);
	}

	/**********************
	 * INNER CONSTRUCTORS *
	 **********************/

	class RabbitsInSpace implements DataSource, Sequence {
		public Object execute() {
			return new Double(getSValue());
		}

		public double getSValue() {
			return (double) agentList.size();
		}
	}

	class GrassInSpace implements DataSource, Sequence {
		public Object execute() {
			return new Double(getSValue());
		}

		public double getSValue() {
			return (double) rgsSpace.getGrassSize();
		}
	}
}
