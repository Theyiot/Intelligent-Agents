package centralized;

import java.util.ArrayList;
import java.util.List;

import logist.plan.Action;
import logist.plan.Plan;
import problem.csp.primitive.Assignment;
import centralized.PDPVariable;
import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;

public class PDPAssignmentConverter {
	
	private PDPAssignmentConverter() {
		
	}
	
	public static List<Plan> toLogistPlan(Assignment<PDPVariable, TaskValue> solution) {
		List<List<PDPVariable.RealizedVariable>> cspPlans =  solution.getRealizations();
		List<Plan> logistPlans = new ArrayList<>();
		
		for (List<PDPVariable.RealizedVariable> cspPlan: cspPlans) {
			Plan currentPlan = new Plan(cspPlan.get(0).getValue().getTask().pickupCity);
			boolean firstCspAction = true;
			for (PDPVariable.RealizedVariable cspAction: cspPlan) {
				
				TaskValue cspActionValue = cspAction.getValue();
				Action logistAction = null;
				
				if (cspActionValue.getType() == ValueType.PICKUP) {
					logistAction = new Action.Pickup(cspActionValue.getTask());
					
					if (!firstCspAction) {
						currentPlan.append(new Action.Move(cspActionValue.getTask().pickupCity));
					}
				} else if (cspActionValue.getType() == ValueType.DELIVER) {
					logistAction = new Action.Delivery(cspActionValue.getTask());
					
					if (!firstCspAction) {
						currentPlan.append(new Action.Move(cspActionValue.getTask().deliveryCity));
					}
				} else {
					break;
				}
				
				firstCspAction = false;
				currentPlan.append(logistAction);
			}
		}
		
		return logistPlans;
	}

}
