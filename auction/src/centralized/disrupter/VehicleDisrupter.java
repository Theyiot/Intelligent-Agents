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
import template.AuctionTemplate;

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
		do {
			pickupIndex = AuctionTemplate.RANDOM.nextInt(plan1.size());
		} while(((TaskValue)plan1.get(pickupIndex).getValue()).getType() != ValueType.PICKUP);
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
		Variable<TaskValue>.RealizedVariable plan2PickupNone = null;
		Variable<TaskValue>.RealizedVariable plan2DeliverNone = null;
		for(int i = 0 ; i < plan2.size() - 1 ; i++) {
			TaskValue taskValue = (TaskValue)plan2.get(i).getValue();
			if(taskValue.getType() == ValueType.NONE) {
				plan2PickupNone = plan2.get(i);
				plan2.set(i, plan1.get(pickupIndex));
				plan2DeliverNone = plan2.get(i + 1);
				plan2.set(i + 1, plan1.get(deliverIndex));
				break;
			}
		}
		
		//Setting new values in plan1
		int lastVariablePlaced = 0;
		for(int i = 0 ; i < plan1.size() ; i++) {
			TaskValue taskValue = (TaskValue)plan1.get(i).getValue();
			if(i != pickupIndex && i != deliverIndex && taskValue.getType() != ValueType.NONE) {
				plan1.set(lastVariablePlaced++, plan1.get(i));
			}
		}
		if(plan2PickupNone == null || plan2DeliverNone == null) {
			throw new IllegalStateException("Could not change task of vehicle");
		}
		plan1.set(lastVariablePlaced++, plan2PickupNone);
		plan1.set(lastVariablePlaced++, plan2DeliverNone);
		
		Set<Assignment<PDPVariable, TaskValue>> set = new HashSet<> ();
		set.add(newA);
		return super.validateAssignments(set);
	}
}
