package centralized.disrupter;

import java.util.List;
import java.util.Set;

import centralized.PDPVariable;
import centralized.value.TaskValue;
import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.resolver.Disrupter;

public class VehicleDisrupter extends Disrupter<PDPVariable, TaskValue> {
	
	public VehicleDisrupter(ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
		super(cspProblem);
		// Pas besoin d'ajouter tous les paramètres du CSP. Il suffit juste d'avoir le CSP. Tu peux rajouter des méthodes pour avoir ce qui t'intéresse plus facilement dans Disrupter.java
	}

	@Override
	public Set<Assignment<PDPVariable, TaskValue>> disrupte(Assignment<PDPVariable, TaskValue> assignement) {
		// Pour choper les variables. Tu peux ajouter d'autres getter si tu as besoin.   
		List<PDPVariable> variables = super.problem.getVariables();
		
		return null;
	}
}
