package template;

//the list of imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import logist.LogistSettings;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;
import problem.csp.ConstraintSatisfaction;
import problem.csp.ConstraintSatisfaction.CSPAssignment;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.primitive.Value;
import problem.csp.primitive.Variable;
import problem.csp.resolver.CSPResolver;
import problem.csp.resolver.SLS;
import centralized.value.PDPConstraintFactory;
import centralized.value.PDPVariable;
import centralized.value.PDPVariable.VariableType;
import centralized.value.TaskValue.ValueType;
import centralized.value.TaskValue;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class CentralizedTemplate implements CentralizedBehavior {

    private Topology topology;
    private TaskDistribution distribution;
    private Agent agent;
    private long timeout_setup;
    private long timeout_plan;
    
    @Override
    public void setup(Topology topology, TaskDistribution distribution,
            Agent agent) {
        
        // this code is used to get the timeouts
        LogistSettings ls = null;
        try {
            ls = Parsers.parseSettings("config\\settings_default.xml");
        }
        catch (Exception exc) {
            System.out.println("There was a problem loading the configuration file.");
        }
        
        // the setup method cannot last more than timeout_setup milliseconds
        timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
        // the plan method cannot execute more than timeout_plan milliseconds
        timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
        
        this.topology = topology;
        this.distribution = distribution;
        this.agent = agent;
    }

    @Override
    public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
        long time_start = System.currentTimeMillis();
        
        /* Beginning of our code */
        
        // Domains creation
        Set<Value> values = new HashSet<>();
        for (Task task: tasks) {
        		values.add(new TaskValue(task, ValueType.PICKUP));
        		values.add(new TaskValue(task, ValueType.DELIVER));
        }
        Domain actionDomain = new Domain(values);
        
       
        // Variables creation
        final List<Variable> planVariables = new ArrayList<>();
    		for (int i=0; i < 2*tasks.size(); ++i) {
    			planVariables.add(new PDPVariable(actionDomain, i));
    		}
    		
    		List<Variable> plansVariables = new ArrayList<>();
    		for (Vehicle vehicle: vehicles) {
    			plansVariables.addAll(planVariables);
    		}
       
        
        // Constraints creation
    		PDPConstraintFactory constraintFactory = new PDPConstraintFactory(planVariables.size(), vehicles.size());
    		Set<Constraint> constraints =  constraintFactory.getAllConstraints();
    		
    		// Objective function creation
    		ObjectiveFunction pdpObjectiveFunction = new ObjectiveFunction() {
    			@Override
    			public double valueAt(Assignment point) {
    				// TODO Implement objective function
    				return 0;
    			}
    		};
    		
    		
    		// CSP creation
    		ConstraintSatisfaction pdpConstraintSatisfaction = new ConstraintSatisfaction(plansVariables, constraints, pdpObjectiveFunction);
    		
    		
    		// SLS creation 
    		CSPResolver initialResolver = new CSPResolver() {
    			public CSPAssignment resolve(ConstraintSatisfaction cspProblem) {
    				
    				
    			}
    		};
    		
    		SLS resolver = new SLS(initialResolver, );
          
        
        /* End of our code */
        
//		System.out.println("Agent " + agent.id() + " has tasks " + tasks);
        Plan planVehicle1 = naivePlan(vehicles.get(0), tasks);

        List<Plan> plans = new ArrayList<Plan>();
        plans.add(planVehicle1);
        while (plans.size() < vehicles.size()) {
            plans.add(Plan.EMPTY);
        }
        
        long time_end = System.currentTimeMillis();
        long duration = time_end - time_start;
        System.out.println("The plan was generated in "+duration+" milliseconds.");
        
        return plans;
    }

    private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
        City current = vehicle.getCurrentCity();
        Plan plan = new Plan(current);

        for (Task task : tasks) {
            // move: current city => pickup location
            for (City city : current.pathTo(task.pickupCity)) {
                plan.appendMove(city);
            }

            plan.appendPickup(task);

            // move: pickup location => delivery location
            for (City city : task.path()) {
                plan.appendMove(city);
            }

            plan.appendDelivery(task);

            // set current city
            current = task.deliveryCity;
        }
        return plan;
    }
}
