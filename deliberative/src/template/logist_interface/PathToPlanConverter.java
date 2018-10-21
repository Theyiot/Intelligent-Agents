package template.logist_interface;

import template.algorithm.Path;
import template.world_representation.action.Action;
import template.world_representation.state.State;

import logist.plan.Plan;

public class PathToPlanConverter {
	
	public static Plan convert(Path<Action, State> path) {
		Plan plan = new Plan(path.getStartNode().getLocation());
		
		for (Action transition: path.getTransitions()) {
			plan.append(transition.toLogistAction());
		}
		
		return plan;
	}

}
