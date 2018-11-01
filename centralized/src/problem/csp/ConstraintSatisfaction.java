package problem.csp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.Domain;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.primitive.Value;

public final class ConstraintSatisfaction {
	private final List<Domain> D;
	private final Set<Constraint> C;
	private final ObjectiveFunction objective;
	
	
	public ConstraintSatisfaction(List<Domain> D, Set<Constraint> C, ObjectiveFunction objective) {
		this.D = D;
		this.C = C;
		this.objective = objective;
	}
	
	public class CSPAssignment implements Assignment {
		private final List<Value> values;
		
		public CSPAssignment(List<Value> values) {
			if (!isValid(values)) {
				throw new IllegalArgumentException("Invalid assignement for current CSP problem");	
			}
			
			this.values = new ArrayList<>(values);
		}
		
		public List<Value> getAssignment() {
			return values;
		}
		
		public double cost() {
			return objective.valueAt(this);
		}
		
		public boolean isSolution() {
			for (Constraint constraint: C) {
				if (!constraint.valueAt(this)) {
					return false;
				}
			}
			
			return true;	
		}
		
		private boolean isValid(List<Value> values) {
			if (D.size() != values.size()) {
				return false;
			}
			
			for (int i=0; i < values.size(); ++i) {
				if (!D.get(i).contains(values.get(i))) {
					return false;
				}
			}
			
			return true;
		}
		
	}

}
