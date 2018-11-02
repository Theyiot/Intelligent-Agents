package problem.csp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import problem.csp.primitive.Assignment;
import problem.csp.primitive.Constraint;
import problem.csp.primitive.ObjectiveFunction;
import problem.csp.primitive.Variable;
import problem.csp.primitive.Variable.RealizedVariable;

public final class ConstraintSatisfaction {
	private final List<Variable> X;
	private final Set<Constraint> C;
	private final ObjectiveFunction objective;
	
	
	public ConstraintSatisfaction(List<Variable> X, Set<Constraint> C, ObjectiveFunction objective) {
		this.X = X;
		this.C = C;
		this.objective = objective;
	}
	
	public class CSPAssignment implements Assignment {
		private final List<RealizedVariable> realizations;
		
		public CSPAssignment(List<RealizedVariable> realizations) {
			if (!isValid(realizations)) {
				throw new IllegalArgumentException("Invalid assignement for current CSP problem");	
			}
			
			this.realizations = new ArrayList<>(realizations);
		}
		
		@Override
		public List<RealizedVariable> getRealizations() {
			return realizations;
		}
		
		@Override
		public double cost() {
			return objective.valueAt(this);
		}
		
		@Override
		public boolean isSolution() {
			for (Constraint constraint: C) {
				if (!constraint.valueAt(this)) {
					return false;
				}
			}
			
			return true;	
		}
		
		private boolean isValid(List<RealizedVariable> realizations) {
			if (X.size() != realizations.size()) {
				return false;
			}
			
			for (int i=0; i < realizations.size(); ++i) {
				if (!X.get(i).isRealization((realizations.get(i)))) {
					return false;
				}
			}
			
			return true;
		}
		
	}

}
