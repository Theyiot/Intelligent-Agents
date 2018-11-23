package centralized;

import java.util.ArrayList;
import java.util.List;

import logist.plan.Action;
import logist.plan.Plan;
import logist.topology.Topology.City;
import problem.csp.primitive.Assignment;
import centralized.PDPVariable;
import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;

public class PDPAssignmentConverter {

	private PDPAssignmentConverter() {

	}

	public static List<Plan> toLogistPlan(Assignment<PDPVariable, TaskValue> solution, List<City> initialCities) {
		List<List<PDPVariable.RealizedVariable>> cspPlans = solution.getRealizations();
		List<Plan> logistPlans = new ArrayList<>();

		for (int i=0; i<cspPlans.size(); ++i) {
			
			List<PDPVariable.RealizedVariable> cspPlan = cspPlans.get(i);
			
			if (cspPlan.get(0).getValue().getType() == ValueType.NONE) {
				logistPlans.add(Plan.EMPTY);
			} else {
				
				Plan currentPlan = new Plan(cspPlan.get(0).getValue().getTask().pickupCity);
				City previousCity = initialCities.get(i);
				
				for (PDPVariable.RealizedVariable cspAction : cspPlan) {

					TaskValue cspActionValue = cspAction.getValue();
					Action logistAction = null;

					if (cspActionValue.getType() == ValueType.PICKUP) {
						logistAction = new Action.Pickup(cspActionValue.getTask());

							List<City> path = previousCity.pathTo(cspActionValue.getTask().pickupCity);

							for (City city : path) {
								currentPlan.append(new Action.Move(city));
							}

						previousCity = cspActionValue.getTask().pickupCity;

					} else if (cspActionValue.getType() == ValueType.DELIVER) {
						logistAction = new Action.Delivery(cspActionValue.getTask());

							List<City> path = previousCity.pathTo(cspActionValue.getTask().deliveryCity);

							for (City city : path) {
								currentPlan.append(new Action.Move(city));
							}
						
						previousCity = cspActionValue.getTask().deliveryCity;
					} else {
						break;
					}

					currentPlan.append(logistAction);
				}
				logistPlans.add(currentPlan);
			}

		}

		return logistPlans;
	}

}
