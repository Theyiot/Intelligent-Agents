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
 * @author
 */

public class RabbitsGrassSimulationModel extends SimModelImpl {
	// Default Values
	// TODO : Best way to handle these values
	public static final int POSSIBLE_DIRECTIONS = 4;
	// TODO : Must be varying or not ?
	public static final int GRASS_ENERGY = 10;
	private static final int GRID_SIZE = 50;
	private static final int RABBITS_NUMBER = 150;
	private static final int BIRTH_THRESHOLD = 30;
	private static final int GRASS_GROWTH_RATE = 30;
	private static final int AGENT_MIN_LIFESPAN = 10;
	private static final int AGENT_MAX_LIFESPAN = 20;

	private int gridSize = GRID_SIZE;
	private int rabbitsNumber = RABBITS_NUMBER;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int grassGrowthRate = GRASS_GROWTH_RATE;
	private int initialGrass = GRASS_GROWTH_RATE;
	private int agentMinLifespan = AGENT_MIN_LIFESPAN;
	private int agentMaxLifespan = AGENT_MAX_LIFESPAN;

	private RabbitsGrassSimulationSpace rgsSpace;

	private Schedule schedule;

	private ArrayList<RabbitsGrassSimulationAgent> agentList;

	private DisplaySurface displaySurf;

	private OpenSequenceGraph amountOfGrassAndRabbitInSpace;

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

	public static void main(String[] args) {
		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		init.loadModel(model, "", false);
	}

	public void begin() {
		buildModel();
		buildSchedule();
		buildDisplay();

		displaySurf.display();
		amountOfGrassAndRabbitInSpace.display();
	}

	public void buildModel() {
		rgsSpace = new RabbitsGrassSimulationSpace(gridSize);
		rgsSpace.spreadGrass(initialGrass);

		for (int i = 0; i < rabbitsNumber; i++) {
			addNewAgent();
		}
		for (int i = 0; i < agentList.size(); i++) {
			RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent) agentList.get(i);
			cda.report();
		}
	}

	public void buildSchedule() {
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

				// TODO : Check that it is ok to have grass under a rabbit for one step (or
				// check why)
				rgsSpace.spreadGrass(initialGrass);

				displaySurf.updateDisplay();
			}
		}

		schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());

		class CarryDropCountLiving extends BasicAction {
			public void execute() {
				countLivingAgents();
			}
		}

		schedule.scheduleActionAtInterval(10, new CarryDropCountLiving());

	    class UpdateRabbitsInSpace extends BasicAction {
	      public void execute(){
	        amountOfGrassAndRabbitInSpace.step();
	      }
	    }

	    schedule.scheduleActionAtInterval(10, new UpdateRabbitsInSpace());
	}

	public void buildDisplay() {
		ColorMap map = new ColorMap();

		map.mapColor(0, Color.black);
		map.mapColor(1, Color.green);

		Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);

		Object2DDisplay displayAgents = new Object2DDisplay(rgsSpace.getCurrentAgentSpace());
		displayAgents.setObjectList(agentList);

		// We decided not to make the displays probeable.
		displaySurf.addDisplayableProbeable(displayGrass, "Grass");
		displaySurf.addDisplayableProbeable(displayAgents, "Agents");

	    amountOfGrassAndRabbitInSpace.addSequence("Rabbit In Space", new RabbitsInSpace());
	    amountOfGrassAndRabbitInSpace.addSequence("Grass In Space", new GrassInSpace());
	}

	public String[] getInitParam() {
		String[] initParams = { "GridSize", "RabbitsNumber", "BirthThreshold", "GrassGrowthRate", "AgentMinLifespan",
				"AgentMaxLifespan" };
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

	    if (amountOfGrassAndRabbitInSpace != null){
	    	amountOfGrassAndRabbitInSpace.dispose();
	    }
	    amountOfGrassAndRabbitInSpace = null;

		displaySurf = new DisplaySurface(this, "Rabbits Grass Simulation Window");
		amountOfGrassAndRabbitInSpace = new OpenSequenceGraph("Amount Of Rabbits In Space", this);

		registerDisplaySurface("Rabbits Grass Simulation Window", displaySurf);
	    this.registerMediaProducer("Plot", amountOfGrassAndRabbitInSpace);
	}

	private int countNewAgents() {
		int count = 0;
		for (int i = (agentList.size() - 1); i >= 0; i--) {
			RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent) agentList.get(i);
			if (cda.getStepsToLive() >= birthThreshold) {
				cda.reproduce();
				count++;
			}
		}
		return count;
	}

	private void reapDeadAgents() {
		for (int i = (agentList.size() - 1); i >= 0; i--) {
			RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent) agentList.get(i);
			if (cda.getStepsToLive() < 1) {
				rgsSpace.removeAgentAt(cda.getX(), cda.getY());
				agentList.remove(i);
			}
		}
	}

	private void addNewAgent() {
		RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(agentMinLifespan, agentMaxLifespan);
		agentList.add(a);
		rgsSpace.addAgent(a);
	}

	private int countLivingAgents() {
		int livingAgents = 0;
		for (int i = 0; i < agentList.size(); i++) {
			RabbitsGrassSimulationAgent cda = (RabbitsGrassSimulationAgent) agentList.get(i);
			if (cda.getStepsToLive() > 0)
				livingAgents++;
		}

		return livingAgents;
	}

	/*****************************
	 * * GETTER AND SETTER METHODS * *
	 *****************************/

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int getRabbitsNumber() {
		return rabbitsNumber;
	}

	public void setNumber(int rabbitsNumber) {
		this.rabbitsNumber = rabbitsNumber;
	}

	public int getBirthThreshold() {
		return birthThreshold;
	}

	public void setBirthThreshold(int birthThreshold) {
		this.birthThreshold = birthThreshold;
	}

	public int getGrassGrowthRate() {
		return grassGrowthRate;
	}

	public void setGrassGrowthRate(int grassGrowthRate) {
		this.grassGrowthRate = grassGrowthRate;
	}

	public int getAgentMaxLifespan() {
		return agentMaxLifespan;
	}

	public int getAgentMinLifespan() {
		return agentMinLifespan;
	}

	public void setAgentMaxLifespan(int agentMaxLifespan) {
		this.agentMaxLifespan = agentMaxLifespan;
	}

	public void setAgentMinLifespan(int agentMinLifespan) {
		this.agentMinLifespan = agentMinLifespan;
	}
}
