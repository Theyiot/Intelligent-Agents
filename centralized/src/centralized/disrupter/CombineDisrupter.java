package centralized.disrupter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import centralized.PDPVariable;
import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;
import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.resolver.Disrupter;

public class CombineDisrupter extends Disrupter<PDPVariable, TaskValue> {
	private final VehicleDisrupter vehicleDisrupter;
	private final TaskOrderDisrupter taskOrderDisruper;
	
	public CombineDisrupter(ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
		super(cspProblem);
		this.vehicleDisrupter = new VehicleDisrupter(cspProblem);
		this.taskOrderDisruper = new TaskOrderDisrupter(cspProblem);
	}

	@Override
	public Set<Assignment<PDPVariable, TaskValue>> disrupte(Assignment<PDPVariable, TaskValue> assignment) {
		Set<Assignment<PDPVariable, TaskValue>> neighbours = new HashSet<>();
		
		//Choosing a random vehicle
		int vehicleIndex;
		do {
			vehicleIndex = new Random().nextInt(assignment.size());
		} while(((TaskValue)assignment.get(0, vehicleIndex).getValue()).getType() == ValueType.NONE);
		
		vehicleDisrupter.setIndex1(vehicleIndex);
		//Applying change vehicle operator
		for(int i = 0 ; i < assignment.size() ; i++) {
			if(i != vehicleIndex) {
				vehicleDisrupter.setIndex2(i);
				neighbours.addAll(vehicleDisrupter.disrupte(assignment));
			}
		}
		
		//Applying change task order operator
		int length = 0;
		while(length < assignment.getPlan(vehicleIndex).size() &&
				((TaskValue)(assignment.get(length, vehicleIndex).getValue())).getType() != ValueType.NONE) {
			length++;
		}
		
		//We have to double the 2, since we split each task in two subtasks (picking up and delivering it)
		if(length >= 4) {
			taskOrderDisruper.setIndex(vehicleIndex);
			for(int idx1 = 0 ; idx1 < length - 1 ; idx1++) {
				taskOrderDisruper.setIdx1(idx1);
				for(int idx2 = idx1 + 1 ; idx2 < length ; idx2++) {
					taskOrderDisruper.setIdx2(idx2);
					neighbours.addAll(taskOrderDisruper.disrupte(assignment));
				}
			}
		}
		
		return neighbours;
	}
}
