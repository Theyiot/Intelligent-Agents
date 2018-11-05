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

public class TaskOrderDisrupter extends Disrupter<PDPVariable, TaskValue> {
	private int index;
	private int idx1;
	private int idx2;

	public TaskOrderDisrupter(ConstraintSatisfaction<PDPVariable, TaskValue> cspProblem) {
		super(cspProblem);
	}
	
	public void setIndex(int newIndex) {
		this.index = newIndex;
	}
	
	public void setIdx1(int newIdx1) {
		this.index = newIdx1;
	}
	
	public void setIdx2(int newIdx2) {
		this.idx2 = newIdx2;
	}

	@Override
	public Set<Assignment<PDPVariable, TaskValue>> disrupte(Assignment<PDPVariable, TaskValue> assignment) {
		Assignment<PDPVariable, TaskValue> newA = new Assignment<PDPVariable, TaskValue> (assignment);

		List<Variable<TaskValue>.RealizedVariable> plan = newA.getPlan(index);
		
		Variable<TaskValue>.RealizedVariable v1 = plan.get(idx1);
		Variable<TaskValue>.RealizedVariable v2 = plan.get(idx2);
		TaskValue t1 = (TaskValue)v1.getValue();
		TaskValue t2 = (TaskValue)v2.getValue();
		if(t1.getTask().equals(t2.getTask())) {
			return new HashSet<Assignment<PDPVariable, TaskValue>> ();
		}
		
		Variable<TaskValue>.RealizedVariable v1Prime = null;
		int searchDirection = t1.getType() == ValueType.PICKUP ? 1 : -1;
		int index1;
		for(index1 = idx1 + searchDirection ; index1 < plan.size() && index1 >= 0 ; index1 += searchDirection) {
			Variable<TaskValue>.RealizedVariable t = plan.get(index1);
			if(((TaskValue)t.getValue()).getTask().equals(t1.getTask())) {
				v1Prime = t;
				break;
			}
		}
		Variable<TaskValue>.RealizedVariable v2Prime = null;
		searchDirection = t2.getType() == ValueType.PICKUP ? 1 : -1;
		int index2;
		for(index2 = idx2 + searchDirection ; index2 < plan.size() && index2 >= 0 ; index2 += searchDirection) {
			Variable<TaskValue>.RealizedVariable t = plan.get(index2);
			if(((TaskValue)t.getValue()).getTask().equals(t2.getTask())) {
				v2Prime = t;
				break;
			}
		}
		if(idx1 < index1) {
			plan.set(idx1, idx2 < index2 ? v2 : v2Prime);
			plan.set(index1, idx2 < index2 ? v2Prime : v2);
			plan.set(Math.min(idx2, index2), v1);
			plan.set(Math.max(idx2, index2), v1Prime);
		} else {
			plan.set(idx1, idx2 < index2 ? v2Prime : v2);
			plan.set(index1, idx2 < index2 ? v2 : v2Prime);
			plan.set(Math.min(idx2, index2), v1Prime);
			plan.set(Math.max(idx2, index2), v1);
		}

		Set<Assignment<PDPVariable, TaskValue>> set = new HashSet<> ();
		set.add(newA);
		return super.validateAssignments(set);
	}
}
