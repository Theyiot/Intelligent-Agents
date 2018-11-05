package centralized.disrupter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import centralized.PDPVariable;
import centralized.value.TaskValue;
import centralized.value.TaskValue.ValueType;
import problem.csp.ConstraintSatisfaction;
import problem.csp.primitive.Assignment;
import problem.csp.primitive.Variable;
import problem.csp.resolver.Disrupter;

public class VehicleDisrupter extends Disrupter<PDPVariable, TaskValue> {
	private int index1;
	private int index2;
	
	public VehicleDisrupter(ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
		super(cspProblem);
	}
	
	public void setIndex1(int newIndex) {
		this.index1 = newIndex;
	}
	
	public void setIndex2(int newIndex) {
		this.index2 = newIndex;
	}

	@Override
	public Set<Assignment<PDPVariable, TaskValue>> disrupte(Assignment<PDPVariable, TaskValue> assignment) {
		Assignment<PDPVariable, TaskValue> newA = new Assignment<> (assignment);
		
		List<Variable<TaskValue>.RealizedVariable> plan1 = newA.getPlan(index1);
		List<Variable<TaskValue>.RealizedVariable> plan2 = newA.getPlan(index2);
		
		int pickupIndex = 0;
		int deliverIndex;
		TaskValue t = (TaskValue)plan1.get(pickupIndex).getValue();
		//Finding corresponding deliver task
		for(deliverIndex = 1 ; deliverIndex < plan1.size() ; deliverIndex++) {
			TaskValue taskValue = (TaskValue)plan1.get(deliverIndex).getValue();
			if(taskValue.getType() == ValueType.DELIVER && taskValue.getTask().equals(t.getTask())) {
				break;
			}
		}
		
		//Setting new values in plan2
		Variable<TaskValue>.RealizedVariable plan2Pickup = null;
		Variable<TaskValue>.RealizedVariable plan2Deliver = null;
		for(int i = 0 ; i < plan2.size() - 2 ; i++) {
			TaskValue taskValue = (TaskValue)plan2.get(i).getValue();
			if(taskValue.getType() == ValueType.NONE) {
				plan2Pickup = plan2.get(i);
				plan2.set(i, plan1.get(pickupIndex));
				plan2Deliver = plan2.get(i + 1);
				plan2.set(i + 1, plan1.get(deliverIndex));
				break;
			}
		}
		
		//Setting new values in plan1
		int lastVariablePlaced = 0;
		for(int i = 1 ; i < plan1.size() ; i++) {
			TaskValue taskValue = (TaskValue)plan1.get(i).getValue();
			if(taskValue.getType() != ValueType.NONE) {
				plan1.set(lastVariablePlaced++, plan1.get(i));
			}
		}
		if(plan2Pickup == null || plan2Deliver == null) {
			throw new IllegalStateException("Could not change vehicle");
		}
		plan1.set(lastVariablePlaced++, plan2Pickup);
		plan1.set(lastVariablePlaced++, plan2Deliver);
		
		Set<Assignment<PDPVariable, TaskValue>> set = new HashSet<> ();
		set.add(newA);
		return set;
	}
}
