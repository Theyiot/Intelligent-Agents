package centralized.disrupter;

import java.util.HashSet;
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
	public Set<Assignment<PDPVariable, TaskValue>> disrupte(Assignment<PDPVariable, TaskValue> assignement) {
		Set<Assignment<PDPVariable, TaskValue>> neighbours = new HashSet<>();
		
		//Choosing a random vehicle
		int vehicleIndex;
		PDPVariable.RealizedVariable variable;
		do {
			vehicleIndex = (int)Math.random() * assignement.size();
		} while(((TaskValue)(variable = assignement.get(0, vehicleIndex)).getValue()).getType() == ValueType.NONE);
		TaskValue taskValue = (TaskValue)variable.getValue();
		
		vehicleDisrupter.setIndex1(vehicleIndex);
		//Applying change vehicle operator
		for(int i = 0 ; i < vehicleIndex ; i++) {
			if(((PDPVariable)(variable.getParent())).getCapacity() > taskValue.getTask().weight) {
				vehicleDisrupter.setIndex2(i);
				neighbours.addAll(vehicleDisrupter.disrupte(assignement));
			}
		}
		
		//Applying change task order operator
		int length = 0;
		while(length < assignement.getPlan(vehicleIndex).size() &&
				((TaskValue)(assignement.get(length, vehicleIndex).getValue())).getType() != ValueType.NONE) {
			length++;
		}
		
		//We have to double the 2, since we split each task in two subtasks (picking up and delivering it)
		if(length >= 4) {
			taskOrderDisruper.setIndex(vehicleIndex);
			for(int idx1 = 0 ; idx1 < length - 1 ; idx1++) {
				for(int idx2 = idx1 + 1 ; idx2 < length ; idx2++) {
					taskOrderDisruper.setIdx1(idx1);
					taskOrderDisruper.setIdx2(idx2);
					neighbours.addAll(taskOrderDisruper.disrupte(assignement));
				}
			}
		}
		
		return neighbours;
	}
}
