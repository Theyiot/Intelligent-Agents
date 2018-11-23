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
		this.idx1 = newIdx1;
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
		
		plan.set(idx1, v2);
		plan.set(idx2, v1);
		


		Set<Assignment<PDPVariable, TaskValue>> set = new HashSet<> ();
		set.add(newA);
		return super.validateAssignments(set);
	}
}
